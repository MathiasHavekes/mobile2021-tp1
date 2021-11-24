package ca.qc.bdeb.c5gm.stageplanif;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.qc.bdeb.c5gm.stageplanif.data.Priorite;
import ca.qc.bdeb.c5gm.stageplanif.data.StagePoidsPlume;
import ca.qc.bdeb.c5gm.stageplanif.databinding.ActivityGoogleMapsBinding;

public class GoogleMapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_REQUEST_CODE = 1;
    private static final float ZOOM_PAR_DEFAUT = 16f;
    private static final String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private ActivityGoogleMapsBinding binding;
    private ArrayList<StagePoidsPlume> listeStages;
    private ArrayList<Marker> listeMarqueurs;
    private ArrayList<Integer> listeTagMarqueursSelectionnes;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean isLocationEnabled = false;
    private Toolbar toolbar;
    private SelectionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoogleMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initToolBar();
        initData();
        creationViewModel();
    }

    private void initToolBar() {
        toolbar = findViewById((R.id.toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
        listeStages = getIntent().getParcelableArrayListExtra("liste_des_stages");
        listeMarqueurs = new ArrayList<>();
        listeTagMarqueursSelectionnes = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this);
    }

    /**
     * Creer le viewModel qui permet de recevoir les informations necessaires a l'affichage des stages
     */
    private void creationViewModel() {
        viewModel = new ViewModelProvider(this).get(SelectionViewModel.class);
        viewModel.getSelectedItem().observe(this, selection -> {
            filtrerlisteStages(selection);
        });
    }

    /**
     * Filtre listeStages pour n'afficher que les stages selectionnes par l'utilisateur
     *
     * @param selection
     */
    private void filtrerlisteStages(int selection) {
        ArrayList<Integer> listePrioritesSelectionnees = Utils.calculerPrioritesSelectionnees(selection);

        for(int i = 0; i < listeStages.size(); i++) {
            listeMarqueurs.get(i).setVisible(listePrioritesSelectionnees.contains(listeStages.get(i).getPriorite().getValeur()));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ActivityCompat.requestPermissions(this, permissions, LOCATION_REQUEST_CODE);
        placerMarqueurs();

        mMap.setOnMarkerClickListener(marker -> {
            if(listeTagMarqueursSelectionnes.contains(marker.getTag())) {
                listeTagMarqueursSelectionnes.remove(marker.getTag());
                marker.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_location_on_24, listeStages.get((int) marker.getTag()).getPriorite()));
            } else {
                listeTagMarqueursSelectionnes.add((int) marker.getTag());
                marker.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_add_location_24, listeStages.get((int) marker.getTag()).getPriorite()));
            }
            return false;
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorId, Priorite prioriteMarqueur) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorId);
        vectorDrawable.setColorFilter(ContextCompat.getColor(context, Utils.renvoyerCouleur(prioriteMarqueur)), PorterDuff.Mode.SRC_IN);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * TODO: refaire commentaires et ajouter nom au marqueur
     */
    private void placerMarqueurs() {
        for (int i = 0; i < listeStages.size(); i++) {
            StringBuilder adresseComplete = new StringBuilder();
            adresseComplete.append(listeStages.get(i).getEntreprise().getAdresse() +
                    ", " + listeStages.get(i).getEntreprise().getVille() +
                    ", " + listeStages.get(i).getEntreprise().getProvince() +
                    " " + listeStages.get(i).getEntreprise().getCp());

            LatLng coordonneesTrouvees = trouverAdressesParGeoCoding(adresseComplete.toString());
            listeMarqueurs.add(mMap.addMarker(
                    new MarkerOptions()
                            .position(coordonneesTrouvees)
                            .title("TODO : REMETTRE NOM")
                            .snippet(listeStages.get(i).getEntreprise().getNom())
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_location_on_24, listeStages.get(i).getPriorite()))
            ));

            listeMarqueurs.get(i).setTag(i);
        }
    }

    /**
     * Trouve l'adresse liee a une adresse de type String, fait l'appel du geocoder et gere les exceptions
     *
     * @param adresse adresse de l'entreprise en String
     * @return l'adresse de l'entreprise en type Address avec sa localisation
     */
    private LatLng trouverAdressesParGeoCoding(String adresse) {
        try {
            List<Address> listeAdresses = geocoder.getFromLocationName(adresse, 1);
            if (listeAdresses.size() > 0) {
                Address adresseTrouvee = listeAdresses.get(0);
                LatLng coordonneesTrouvees = new LatLng(adresseTrouvee.getLatitude(), adresseTrouvee.getLongitude());
                return coordonneesTrouvees;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            isLocationEnabled = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }

        activerLocalisation();
    }

    @SuppressLint("MissingPermission")
    private void activerLocalisation() {
        if (isLocationEnabled && mMap != null) {
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, currLocation -> {
                if (currLocation != null) {
                    LatLng maLocation = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(maLocation, ZOOM_PAR_DEFAUT));
                }
            });
        }
    }
}
