package ca.qc.bdeb.c5gm.stageplanif;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class DemandeInfoEleve extends Fragment implements AdapterView.OnItemSelectedListener {
    private ImageView imageView;
    private FloatingActionButton btnPrendrePhoto ;
    private Button btnSuivant;
    private Spinner spinnerNom;
    private RadioGroup radioPriorite;
    private Stockage dbHelper;
    private ArrayList<Compte> comptes = new ArrayList<>();
    private String lienPhotoActuel;
    private InfoStageViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = Stockage.getInstance(getActivity().getApplicationContext());
        comptes = dbHelper.getEtudiantsSansStage();
    }

    private final View.OnClickListener prendrePhotoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            envoyerPrendrePhotoIntent();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demande_info_eleve, container, false);
        imageView = view.findViewById(R.id.image_eleve_profile);
        btnPrendrePhoto = view.findViewById(R.id.btn_prendre_photo);
        btnSuivant = view.findViewById(R.id.btn_suivant);
        spinnerNom = view.findViewById(R.id.nom_complet_entre_utilisateur);
        radioPriorite = view.findViewById(R.id.radio_group_drapeau);
        radioPriorite.setOnCheckedChangeListener(radioGroupOnClickListener);
        if (comptes.size() > 0) {
            Compte compte = comptes.get(0);
            if (compte.getPhoto() != null) {
                Bitmap photoBitmap = Utils.getImage(compte.getPhoto());
                imageView.setImageBitmap(photoBitmap);
            } else {
                imageView.setImageResource(R.drawable.ic_baseline_person_24);
            }
        }
        String[] arraySpinner = new String[comptes.size()];
        for (int i = 0; i < comptes.size(); i++) {
            Compte compte = comptes.get(i);
            arraySpinner[i] = compte.getPrenom() + " " + compte.getNom();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNom.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(InfoStageViewModel.class);
        btnPrendrePhoto.setOnClickListener(prendrePhotoOnClickListener);
        spinnerNom.setOnItemSelectedListener(this);
        viewModel = new ViewModelProvider(requireActivity()).get(InfoStageViewModel.class);
    }

    private final RadioGroup.OnCheckedChangeListener radioGroupOnClickListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int selectedId = radioPriorite.getCheckedRadioButtonId();
            Priorite priorite;
            switch (selectedId) {
                case R.id.drapeau_vert:
                    priorite = Priorite.MINIMUM;
                    break;
                case R.id.drapeau_jaune:
                    priorite = Priorite.MOYENNE;
                    break;
                default:
                    priorite = Priorite.MAXIMUM;
                    break;
            }
            viewModel.setPriorite(priorite);
        }
    };

    private void envoyerPrendrePhotoIntent() {
        Intent prendrePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(prendrePhotoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File fichierPhoto = null;
            try {
                fichierPhoto = creerFichierImage();
            } catch (IOException e) {
                // display error state to the user
            }
            if (fichierPhoto != null) {
                lienPhotoActuel = fichierPhoto.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(getActivity().getBaseContext(),
                        "ca.qc.bdeb.c5gm.stageplanif", fichierPhoto);
                prendrePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                envoyerPrendrePhotoIntentLauncher.launch(prendrePhotoIntent);
            }
        }
    }

    ActivityResultLauncher<Intent> envoyerPrendrePhotoIntentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        setPic();
                    }
                }
            });


    private File creerFichierImage() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "STAGE_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(lienPhotoActuel, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(lienPhotoActuel, bmOptions);
        viewModel.setImage(bitmap);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Compte compteSelectionne = comptes.get(i);
        viewModel.setCompte(compteSelectionne);
        if (compteSelectionne.getPhoto() != null) {
            Bitmap photoBitmap = Utils.getImage(compteSelectionne.getPhoto());
            imageView.setImageBitmap(photoBitmap);
        } else {
            imageView.setImageResource(R.drawable.ic_baseline_person_24);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}