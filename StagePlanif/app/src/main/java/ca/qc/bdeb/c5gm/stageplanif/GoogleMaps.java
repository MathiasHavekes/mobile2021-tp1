package ca.qc.bdeb.c5gm.stageplanif;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.qc.bdeb.c5gm.stageplanif.databinding.ActivityGoogleMapsBinding;

public class GoogleMaps extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private ArrayList<Stage> listeStages;
    private ArrayList<Stage> listeStagesMasques;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private ActivityGoogleMapsBinding binding;
    private Toolbar toolbar;
    private ItemViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoogleMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        geocoder = new Geocoder(this);

        listeStages = getIntent().getParcelableArrayListExtra("liste_des_stages");
        listeStagesMasques = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        toolbar = findViewById((R.id.toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        creationViewModel();
    }

    private void creationViewModel() {
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.getSelectedItem().observe(this, selection -> {
            mettreAJourlisteStages(selection);
            placerMarqueurs();
        });
    }

    private void mettreAJourlisteStages(int selection) {
        listeStages.addAll(listeStagesMasques);
        listeStagesMasques.clear();

        ArrayList<Integer> listePrioritesSelectionnees = Utils.calculerPrioritesSelectionnees(selection);
        listeStagesMasques = Utils.filtrerListeStages(listePrioritesSelectionnees, listeStages);
        listeStages.removeAll(listeStagesMasques);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_google_map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.passer_sur_carte) {
            Intent intent = new Intent(this, GoogleMaps.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        activerLaGeoLocatisation();
        setCoordonneesEntreprise();
        placerMarqueurs();
    }

    public void placerMarqueurs() {
        mMap.clear();
        for (Stage s : listeStages) {
            MarkerOptions marqueur = creerMarqueur(s.getEntreprise().getLatLng(), s.getPriorite(), s.getEntreprise().getNom());
            mMap.addMarker(marqueur);
        }
    }

    private void setCoordonneesEntreprise() {
        for (Stage s : listeStages) {
            StringBuilder adresseComplete = new StringBuilder();
            adresseComplete.append(s.getEntreprise().getAdresse() + ", " + s.getEntreprise().getVille() + ", " + s.getEntreprise().getProvince() + " " + s.getEntreprise().getCp());

            double[] coordonneesTrouvees = trouverAdressesParGeoCoding(adresseComplete.toString());
            s.getEntreprise().setLatLng(coordonneesTrouvees);
        }
    }

    private double[] trouverAdressesParGeoCoding(String adresse) {
        try {
            List<Address> listeAdresses = geocoder.getFromLocationName(adresse, 1);
            if (listeAdresses.size() > 0) {
                Address adresseTrouvee = listeAdresses.get(0);
                double[] coordonnees = {adresseTrouvee.getLatitude(), adresseTrouvee.getLongitude()};
                return  coordonnees;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MarkerOptions creerMarqueur(double[] latLng, Priorite priorite, String nomEntreprise) {
        LatLng coordonneesAdresse = new LatLng(latLng[0], latLng[1]);
        MarkerOptions marqueur = new MarkerOptions();
        marqueur.position(coordonneesAdresse);
        marqueur.title(nomEntreprise);
        marqueur.icon(BitmapDescriptorFactory.defaultMarker(renvoyerCouleur(priorite)));
        return marqueur;
    }

    private float renvoyerCouleur(Priorite priorite) {
        switch (priorite) {
            case MINIMUM:
                return BitmapDescriptorFactory.HUE_GREEN;
            case MOYENNE:
                return BitmapDescriptorFactory.HUE_YELLOW;
            case MAXIMUM:
                return BitmapDescriptorFactory.HUE_RED;
            default:
                return BitmapDescriptorFactory.HUE_BLUE;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        } else  {
            activerLaGeoLocatisation();
        }
    }

    @SuppressLint("MissingPermission")
    private void activerLaGeoLocatisation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        StringBuilder positionActuel = new StringBuilder();
        positionActuel.append("Position actuel:\n" + "Lat : " +  location.getLatitude() + " Lng : " + location.getLongitude());
        Toast.makeText(this, positionActuel.toString(), Toast.LENGTH_LONG).show();
    }
}
