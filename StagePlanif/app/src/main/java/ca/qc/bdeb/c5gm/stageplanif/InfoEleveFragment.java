package ca.qc.bdeb.c5gm.stageplanif;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Classe qui s'occupe du Fragment fragment_demande_info_eleve
 */
public class InfoEleveFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    /**
     * L'image de l'etudiant
     */
    private ImageView imageView;
    /**
     * Bouton pour aller a l'activitee de photo
     */
    private FloatingActionButton btnPrendrePhoto;
    /**
     * Spinner affichant les noms
     */
    private Spinner spinnerNom;
    /**
     * Groupe radio qui permet de selectionner les priorites
     */
    private RadioGroup radioPriorite;
    /**
     * Lien avec la BD
     */
    private Stockage dbHelper;
    /**
     * Liste des comptes a afficher dans la BD
     */
    private ArrayList<Compte> comptes = new ArrayList<>();
    /**
     * Lien de la photo prise
     */
    private String lienPhotoActuel;
    /**
     * Instance du viewModel qui permet de communiquer avec l'activitee et l'autre fragment
     */
    private InfoStageViewModel viewModel;
    /**
     * Action lorsqu'un bouton du radio group est clique
     */
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
    /**
     * Propriete qui enregistre l'activitee de prendre une photo
     */
    final ActivityResultLauncher<Intent> envoyerPrendrePhotoIntentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    setPic();
                }
            });
    /**
     * Listener du bouton pour prendre une photo
     */
    private final View.OnClickListener prendrePhotoOnClickListener = view -> envoyerPrendrePhotoIntent();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = Stockage.getInstance(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(InfoStageViewModel.class);
        View view = inflater.inflate(R.layout.fragment_demande_info_eleve, container, false);
        imageView = view.findViewById(R.id.image_eleve_profile);
        btnPrendrePhoto = view.findViewById(R.id.btn_prendre_photo);
        spinnerNom = view.findViewById(R.id.nom_complet_entre_utilisateur);
        radioPriorite = view.findViewById(R.id.radio_group_drapeau);
        radioPriorite.setOnCheckedChangeListener(radioGroupOnClickListener);
        Stage stage = viewModel.getStage();
        if (stage != null) {
            comptes.add(stage.getEtudiant());
            setSpinner();
            setRadioButton(stage);
        } else {
            comptes = dbHelper.getEtudiantsSansStage();
            setSpinner();
        }
        setImage();
        return view;
    }

    /**
     * Methode qui coche automatiquement le radiobox de la priorite de l'eleve
     *
     * @param stage stage affiche
     */
    private void setRadioButton(Stage stage) {
        switch (stage.getPriorite()) {
            case MINIMUM:
                radioPriorite.check(R.id.drapeau_vert);
                break;
            case MOYENNE:
                radioPriorite.check(R.id.drapeau_jaune);
                break;
            case MAXIMUM:
                radioPriorite.check(R.id.drapeau_rouge);
                break;
        }
    }

    /**
     * Met les noms d'etudiants dans le spinner
     */
    private void setSpinner() {
        String[] arraySpinner = new String[comptes.size()];
        for (int i = 0; i < comptes.size(); i++) {
            Compte compte = comptes.get(i);
            arraySpinner[i] = compte.getPrenom() + " " + compte.getNom();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNom.setAdapter(adapter);
    }

    /**
     * Met l'image dans l'imageview
     */
    private void setImage() {
        if (comptes.size() > 0) {
            Compte compte = comptes.get(0);
            if (compte.getPhoto() != null) {
                Bitmap photoBitmap = Utils.getImage(compte.getPhoto());
                imageView.setImageBitmap(photoBitmap);
            } else {
                imageView.setImageResource(R.drawable.ic_baseline_person_24);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnPrendrePhoto.setOnClickListener(prendrePhotoOnClickListener);
        spinnerNom.setOnItemSelectedListener(this);
    }

    /**
     * Methode qui envoie l'utilisateur pour faire une photo
     * https://developer.android.com/training/camera/photobasics
     */
    private void envoyerPrendrePhotoIntent() {
        Intent prendrePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (prendrePhotoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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

    /**
     * Creer le fichier qui contiendra l'image
     * https://developer.android.com/training/camera/photobasics
     *
     * @return le fichier cree
     * @throws IOException si le fichier n'est pas cree
     */
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
        return image;
    }

    /**
     * Methode qui decode la photo et l'affiche dans l'imageview
     * https://developer.android.com/training/camera/photobasics
     */
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
        int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

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
            viewModel.setImage(photoBitmap);
            imageView.setImageBitmap(photoBitmap);
        } else {
            imageView.setImageResource(R.drawable.ic_baseline_person_24);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}