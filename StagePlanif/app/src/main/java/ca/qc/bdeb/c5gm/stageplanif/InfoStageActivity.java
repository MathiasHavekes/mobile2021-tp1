package ca.qc.bdeb.c5gm.stageplanif;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class InfoStageActivity extends AppCompatActivity {
    private FragmentContainerView demandeInfoFragment;
    private Toolbar toolbar;
    private Button boutonSuivant;
    private Button boutonAnnuler;
    private InfoStageViewModel viewModel;
    private Context context;
    private Priorite priorite;
    private byte[] photo;
    private Entreprise entreprise;
    private Compte etudiant;
    private Stage stageStockage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demande_info_stage);
        demandeInfoFragment = findViewById(R.id.fragment_demande_info);
        Fragment fragment = new InfoEleveFragment();
        context = this;
        creationViewModel();
        Intent intent = getIntent();
        if (intent.hasExtra("stage")) {
            stageStockage = intent.getParcelableExtra("stage");
            viewModel.setStage(stageStockage);
        }
        changerFragment(fragment);
        boutonSuivant = findViewById(R.id.btn_suivant);
        boutonAnnuler = findViewById(R.id.btn_annuler);
        boutonAnnuler.setOnClickListener(annulerClique);
        boutonSuivant.setOnClickListener(suivantClique);
    }

    private final View.OnClickListener annulerClique = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (stageModifie() || ! photoEgal()) {
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
            } else {
                finish();
            }
        }
    };

    private void creationViewModel() {
        viewModel = new ViewModelProvider(this).get(InfoStageViewModel.class);
        viewModel.getPriorite().observe(this, priorite -> {
            this.priorite = priorite;
        });
        viewModel.getCompte().observe(this, compte -> {
            this.etudiant = compte;
        });
        viewModel.getPhoto().observe(this, photo -> {
            this.photo = photo;
        });
        viewModel.getEntreprise().observe(this, entreprise -> {
            this.entreprise = entreprise;
        });
    }

    private void changerFragment(Fragment fragment) {
        FragmentManager fragmentManger = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManger.beginTransaction();
        fragmentTransaction.replace(demandeInfoFragment.getId(), fragment);
        fragmentTransaction.commit();

        toolbar = findViewById((R.id.toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private final View.OnClickListener suivantClique = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (boutonSuivant.getText() == getResources().getString(R.string.btn_suivant)) {
                if (priorite == null || etudiant == null) {
                    afficherMessage(getString(R.string.message_erreur));
                    return;
                }
                boutonSuivant.setText(getResources().getString(R.string.btn_terminer));
                Fragment fragment = new InfoEntrepriseFragment();
                changerFragment(fragment);
            } else if (boutonSuivant.getText() == getResources().getString(R.string.btn_terminer)) {
                Stockage dbHelper = Stockage.getInstance(context);
                if (stageStockage == null) {
                    ajouterStage(dbHelper);
                    finish();
                }
                if (!stageModifie() && photoEgal()) {
                    afficherMessage(getString(R.string.message_modification));
                    return;
                }
                if (! photoEgal()) {
                    stageStockage.getEtudiant().setPhoto(photo);
                    dbHelper.changerPhotoCompte(stageStockage.getEtudiant());
                }
                if (stageModifie()) {
                    stageStockage.setPriorite(priorite);
                    stageStockage.addEntreprise(entreprise);
                    dbHelper.modifierStage(stageStockage);
                }
                finish();
            }
        }
    };

    private Boolean photoEgal() {
        return (photo == null && stageStockage.getEtudiant().getPhoto() != null) ^ Arrays.equals(photo, etudiant.getPhoto());
    }

    private Boolean stageModifie() {
        boolean prioEgal = stageStockage.getPriorite() == priorite;
        if (entreprise != null) {
            boolean entrepriseEgal = stageStockage.getEntreprise().getId().equals(entreprise.getId());
            return ! (entrepriseEgal && prioEgal && photoEgal());
        }
        return ! (prioEgal && photoEgal());
    }

    private void ajouterStage(Stockage dbHelper) {
        ArrayList<Compte> professeurs = dbHelper.getComptes(1);
        Stage stage = new Stage(UUID.randomUUID().toString(), "2021-2022", priorite);
        etudiant.setPhoto(photo);
        stage.addEntreprise(entreprise);
        stage.addEtudiant(etudiant);
        stage.addProfesseur(professeurs.get(0));
        dbHelper.createStage(stage);
        dbHelper.changerPhotoCompte(etudiant);
    }

    private void afficherMessage(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(R.string.titre_erreur);
        alertDialog.setMessage(message);
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
}