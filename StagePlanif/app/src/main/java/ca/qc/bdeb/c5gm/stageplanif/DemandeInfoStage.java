package ca.qc.bdeb.c5gm.stageplanif;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DemandeInfoStage extends AppCompatActivity {
    private FragmentContainerView demandeInfoFragment;
    private Button boutonSuivant;
    private Button boutonAnnuler;
    private InfoStageViewModel viewModel;
    private Context context;
    private Priorite priorite;
    private Bitmap photo;
    private Compte compte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demande_info_stage);

        demandeInfoFragment = findViewById(R.id.fragment_demande_info);
        Fragment fragment = new DemandeInfoEleve();
        changerFragment(fragment);

        boutonSuivant = findViewById(R.id.btn_suivant);
        boutonAnnuler = findViewById(R.id.btn_annuler);
        boutonAnnuler.setOnClickListener(annulerClique);
        boutonSuivant.setOnClickListener(suivantClique);
        context = this;
        creationViewModel();
    }

    private final View.OnClickListener annulerClique = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(R.string.titre_avertissement);
            alertDialog.setMessage(getString(R.string.message_avertissement));
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.annuler_message),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //on ne fait rien
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.message_oui),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            alertDialog.show();
        }
    };

    private void creationViewModel() {
        viewModel = new ViewModelProvider(this).get(InfoStageViewModel.class);
        viewModel.getPriorite().observe(this, priorite -> {
            this.priorite = priorite;
        });
        viewModel.getCompte().observe(this, compte -> {
            this.compte = compte;
        });
        viewModel.getPhoto().observe(this, photo -> {
            this.photo = photo;
        });
    }

    private void changerFragment(Fragment fragment) {
        FragmentManager fragmentManger = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManger.beginTransaction();
        fragmentTransaction.replace(demandeInfoFragment.getId(), fragment);
        fragmentTransaction.commit();
    }

    private final View.OnClickListener suivantClique = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (boutonSuivant.getText() == getResources().getString(R.string.btn_suivant)) {
                if (priorite == null || compte == null) {
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle(R.string.titre_erreur);
                    alertDialog.setMessage(getString(R.string.message_erreur));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.btn_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //on ne fait rien
                                }
                            });
                    alertDialog.show();
                    return;
                }
                boutonSuivant.setText(getResources().getString(R.string.btn_terminer));
                Fragment fragment = new DemandeInfoEntreprise();
                changerFragment(fragment);
            } else if (boutonSuivant.getText() == getResources().getString(R.string.btn_terminer)) {

            }
        }
    };
}