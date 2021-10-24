package ca.qc.bdeb.c5gm.stageplanif;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;

public class DemandeInfoEleve extends AppCompatActivity {
    private ImageView imageView;
    private FloatingActionButton btnPrendrePhoto ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demande_info_eleve);
        imageView = findViewById(R.id.image_eleve_profile);
        btnPrendrePhoto = findViewById(R.id.btn_prendre_photo);

        btnPrendrePhoto.setOnClickListener(prendrePhotoOnClickListener);
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
            try {
                envoyerPrendrePhotoIntentLauncher.launch(prendrePhotoIntent);
            } catch (ActivityNotFoundException e) {
                // display error state to the user
            }

        }
    }

    ActivityResultLauncher<Intent> envoyerPrendrePhotoIntentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        imageView.setImageBitmap(imageBitmap);
                    }
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}