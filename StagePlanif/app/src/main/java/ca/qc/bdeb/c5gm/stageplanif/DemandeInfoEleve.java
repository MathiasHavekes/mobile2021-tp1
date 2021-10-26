package ca.qc.bdeb.c5gm.stageplanif;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class DemandeInfoEleve extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ImageView imageView;
    private FloatingActionButton btnPrendrePhoto ;
    private Spinner spinnerNom;
    private Stockage dbHelper;
    private ArrayList<Compte> comptes = new ArrayList<>();
    private String lienPhotoActuel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = Stockage.getInstance(getApplicationContext());
        setContentView(R.layout.activity_demande_info_eleve);
        imageView = findViewById(R.id.image_eleve_profile);
        btnPrendrePhoto = findViewById(R.id.btn_prendre_photo);
        spinnerNom = findViewById(R.id.nom_complet_entre_utilisateur);
        btnPrendrePhoto.setOnClickListener(prendrePhotoOnClickListener);
        comptes = dbHelper.getComptes();
        String[] arraySpinner = new String[comptes.size()];
        for (int i = 0; i < comptes.size(); i++) {
            Compte compte = comptes.get(i);
            arraySpinner[i] = compte.getPrenom() + " " + compte.getNom();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNom.setAdapter(adapter);
    }

    private final View.OnClickListener prendrePhotoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            envoyerPrendrePhotoIntent();
        }
    };

    private void envoyerPrendrePhotoIntent() {
        Intent prendrePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(prendrePhotoIntent.resolveActivity(getPackageManager()) != null) {
            File fichierPhoto = null;
            try {
                fichierPhoto = creerFichierImage();
            } catch (IOException e) {
                // display error state to the user
            }
            if (fichierPhoto != null) {
                lienPhotoActuel = fichierPhoto.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(this,
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

    public void ajouterStage(View view) {
        int position = spinnerNom.getSelectedItemPosition();
        Compte compteSelection = comptes.get(position);
        Drawable drawable = imageView.getDrawable();
        if(drawable.getClass() == BitmapDrawable.class){
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            byte[] photoBytes = Utils.getBytes(bitmap);
            compteSelection.setPhoto(photoBytes);
            dbHelper.changerPhotoCompte(compteSelection);
        }
    }

    private File creerFichierImage() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "STAGE_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String currentPhotoPath = image.getAbsolutePath();
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

        Bitmap bitmap = BitmapFactory.decodeFile(lienPhotoActuel, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        /*compteSelection = comptes.get(i);
        Stage stageTrouve;
        ArrayList<Stage> stages = StockageConnu.getStages();
        for (Stage stage: stages) {
            if (compteSelection.getId() == stage.getEtudiant().getId()) {
                stageTrouve = stage;
                break;
            }
        }*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}