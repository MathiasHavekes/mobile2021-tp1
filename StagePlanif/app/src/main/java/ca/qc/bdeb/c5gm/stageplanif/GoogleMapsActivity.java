package ca.qc.bdeb.c5gm.stageplanif;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleMapsActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float ZOOM_PAR_DEFAUT = 14f;
    private ArrayList<Stage> listeStages;
    private ArrayList<Stage> listeStagesMasques;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private boolean demandeLocalisationMiseAJour;
    private Toolbar toolbar;
    private SelectionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        demandeLocalisationMiseAJour = true;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this);

        listeStages = getIntent().getParcelableArrayListExtra("liste_des_stages");
        listeStagesMasques = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        toolbar = findViewById((R.id.toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        creationLocationRequest();
        creationViewModel();
        creationLocationCallBack();
    }

    private void creationLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void creationLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(ZOOM_PAR_DEFAUT).build();
                    CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    mMap.animateCamera(update);
                }
            }
        };

    }

    private void creationViewModel() {
        viewModel = new ViewModelProvider(this).get(SelectionViewModel.class);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
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
                return coordonnees;
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
        marqueur.icon(BitmapDescriptorFactory.defaultMarker(Utils.renvoyerCouleurHSV(priorite)));
        return marqueur;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        } else {
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
        mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_PAR_DEFAUT));
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        StringBuilder positionActuel = new StringBuilder();
        positionActuel.append("Position actuel:\n" + "Lat : " + location.getLatitude() + " Lng : " + location.getLongitude());
        Toast.makeText(this, positionActuel.toString(), Toast.LENGTH_LONG).show();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (demandeLocalisationMiseAJour) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

}
