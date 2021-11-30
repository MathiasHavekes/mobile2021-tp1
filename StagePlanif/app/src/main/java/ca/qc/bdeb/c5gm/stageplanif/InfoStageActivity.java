package ca.qc.bdeb.c5gm.stageplanif;

import static java.time.temporal.ChronoUnit.MINUTES;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import ca.qc.bdeb.c5gm.stageplanif.data.Compte;
import ca.qc.bdeb.c5gm.stageplanif.data.Entreprise;
import ca.qc.bdeb.c5gm.stageplanif.data.Priorite;
import ca.qc.bdeb.c5gm.stageplanif.data.Stage;
import ca.qc.bdeb.c5gm.stageplanif.data.Stockage;

/**
 * Classe qui s'occupe de l'activite activity_demande_info_stage
 */
public class InfoStageActivity extends AppCompatActivity {
    /**
     * Propriete qui contient le fragment de l'information de stage
     */
    private FragmentContainerView demandeInfoStageFragment;
    /**
     * Toolbar contenant la toolbar
     */
    private Toolbar toolbar;
    /**
     * Bouton contenant le bouton pour cliquer sur suivant
     */
    private Button boutonSuivant;
    /**
     * Bouton contenant le bouton pour cliquer sur annuler
     */
    private Button boutonAnnuler;
    /**
     * Instance du viewModel qui permet de communiquer avec les fragments
     */
    private InfoStageViewModel viewModel;
    /**
     * Contient le contexte de l'activitee
     */
    private Context context;
    /**
     * Contient la priorite du stage
     */
    private Priorite priorite;
    /**
     * Contient la photo de l'eleve
     */
    private byte[] photo;
    /**
     * Contient l'entreprise de stage
     */
    private Entreprise entreprise;
    /**
     * Contient l'etudiant qui fait le stage
     */
    private Compte etudiant;
    /**
     * Contient le stage si celui-ci est fourni
     */
    private Stage stageStockage;
    /**
     * Liste des fragments affiches en ordre
     */
    private Fragment[] fragments;
    /**
     * Indice du fragment afficher dans la liste fragments
     */
    private int fragmentActuel;
    /**
     * Contient la logique du clique sur le bouton annuler
     */
    private final View.OnClickListener annulerClique = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (stageModifie()) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle(R.string.titre_avertissement);
                alertDialog.setMessage(getString(R.string.message_avertissement));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.annuler_message),
                        (dialogInterface, i) -> {
                            //on ne fait rien
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.message_oui),
                        (dialogInterface, i) -> {
                            setResult(RESULT_CANCELED);
                            finish();
                        });
                alertDialog.show();
                return;
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    };

    /**
     * Logique du bouton suivant
     */
    private final View.OnClickListener suivantClique = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (fragmentActuel) {
                case 0:
                    Boolean champsRempli = priorite != null && etudiant != null;
                    if(champsRempli) {
                        fragmentActuel++;
                        changerFragment(fragments[fragmentActuel]);
                        return;
                    }
                    afficherMessage(getString(R.string.message_erreur));
                    break;
                case 1:
                    fragmentActuel++;
                    changerFragment(fragments[fragmentActuel]);
                    break;
                case 2:
                    if(viewModel.getStage() == null) {
                        champsRempli = viewModel.getJourStage() != null;
                        if(champsRempli) {
                            champsRempli &= viewModel.getJourStage() != 0x00;
                        }
                        champsRempli &= viewModel.getHeureDebutStage() != null;
                        champsRempli &= viewModel.getHeureFinStage() != null;
                        champsRempli &= viewModel.getHeureDebutDiner() != null;
                        champsRempli &= viewModel.getHeureFinDiner() != null;
                        champsRempli &= viewModel.getTempsVisites() != null;
                        if (!champsRempli) {
                            afficherMessage(getString(R.string.message_erreur));
                            return;
                        }
                    }
                    fragmentActuel++;
                    boutonSuivant.setText(getResources().getString(R.string.btn_terminer));
                    changerFragment(fragments[fragmentActuel]);
                    break;
                case  3:
                    Stockage dbHelper = Stockage.getInstance(context);
                    Stage stage;
                    if(viewModel.getStage() == null) {
                        champsRempli = viewModel.getDispoTuteur() != null;
                        if(champsRempli) {
                            champsRempli &= viewModel.getDispoTuteur() != 0x00;
                        }
                        if (!champsRempli) {
                            afficherMessage(getString(R.string.message_erreur));
                            return;
                        } else {
                            stage = creerStage(dbHelper);
                        }
                    } else {
                        if (!photoEgal()) {
                            stageStockage.getEtudiant().setPhoto(photo);
                            dbHelper.changerPhotoCompte(stageStockage.getEtudiant());
                        }
                        stage = viewModel.getStage();
                        ArrayList<Compte> professeurs = dbHelper.getComptes(1);
                        stage.setPriorite(priorite);
                        stage.addEntreprise(entreprise);
                        stage.addEtudiant(etudiant);
                        stage.addProfesseur(professeurs.get(0));
                        stage.setCommentaire(viewModel.getCommentaire());
                        stage.setJournees(viewModel.getJourStage());
                        stage.setheureDebut(viewModel.getHeureDebutStage());
                        stage.setTempsStage((int) MINUTES.between(viewModel.getHeureDebutStage(), viewModel.getHeureFinStage()));
                        stage.setHeureDiner(viewModel.getHeureDebutDiner());
                        stage.setTempsDiner((int) MINUTES.between(viewModel.getHeureDebutDiner(), viewModel.getHeureFinDiner()));
                        stage.setDureeVisite(viewModel.getTempsVisites());
                        stage.setDisponibiliteTuteur(viewModel.getDispoTuteur());
                        dbHelper.modifierStage(stage);
                    }
                    creerIntent(stage);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demande_info_stage);
        demandeInfoStageFragment = findViewById(R.id.fragment_demande_info);
        boutonSuivant = findViewById(R.id.btn_suivant);
        boutonAnnuler = findViewById(R.id.btn_annuler);
        boutonAnnuler.setOnClickListener(annulerClique);
        boutonSuivant.setOnClickListener(suivantClique);
        fragments = new Fragment[]{new InfoEleveFragment(), new InfoEntrepriseFragment(), new InfoVisiteFragment(), new CommentairesVisiteFragment()};
        context = this;
        creationViewModel();
        Intent intent = getIntent();
        if (intent.hasExtra("stage")) {
            stageStockage = intent.getParcelableExtra("stage");
            viewModel.setStage(stageStockage);
        }
        changerFragment(fragments[0]);
        fragmentActuel = 0;
    }

    /**
     * Cree le viewModel et observe les donnees lives
     */
    private void creationViewModel() {
        viewModel = new ViewModelProvider(this).get(InfoStageViewModel.class);
        viewModel.getPriorite().observe(this, priorite -> this.priorite = priorite);
        viewModel.getCompte().observe(this, compte -> this.etudiant = compte);
        viewModel.getPhoto().observe(this, photo -> this.photo = photo);
        viewModel.getEntreprise().observe(this, entreprise -> this.entreprise = entreprise);
    }

    /**
     * Change le fragment
     *
     * @param fragment le fragment a afficher
     */
    private void changerFragment(Fragment fragment) {
        FragmentManager fragmentManger = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManger.beginTransaction();
        fragmentTransaction.replace(demandeInfoStageFragment.getId(), fragment);
        fragmentTransaction.commit();

        toolbar = findViewById((R.id.toolbar));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Envoie le resultat a l'activite
     */
    private void creerIntent(Stage stage) {
        Intent intent = new Intent();
        intent.putExtra("stage", stage);
        setResult(RESULT_OK, intent);
    }

    /**
     * Determine si la photo est egale a celle qui a ete mise
     *
     * @return true si les photos sont egal, false sinon
     */
    private Boolean photoEgal() {
        if (stageStockage != null) {
            return (photo == null && stageStockage.getEtudiant().getPhoto() != null) ^ Arrays.equals(photo, etudiant.getPhoto());
        }
        return photo == null;
    }

    /**
     * Determine si le stage a ete modifie
     *
     * @return true si le stage a ete modifier, false sinon
     */
    private Boolean stageModifie() {
        //Logique si c'est une modification de stage
        if (stageStockage != null) {
            boolean prioEgal = stageStockage.getPriorite() == priorite;
            if (entreprise != null) {
                boolean entrepriseEgal = stageStockage.getEntreprise().getId().equals(entreprise.getId());
                return !(entrepriseEgal && prioEgal && photoEgal());
            }
            return !(prioEgal && photoEgal());
        }
        //Logique si c'est un ajout de stage
        if (entreprise != null) {
            return !photoEgal() && entreprise != null && priorite != null;
        }
        //Logique si c'est dans le fragment d'info d'eleves
        return ! photoEgal() || priorite != null;
    }

    /**
     * Cree et ajoute un stage à la BD
     *
     * @param dbHelper lien vers la BD
     * @return Le stage cre
     */
    private Stage creerStage(Stockage dbHelper) {
        ArrayList<Compte> professeurs = dbHelper.getComptes(1);
        Stage stage = new Stage(priorite);
        etudiant.setPhoto(photo);
        stage.addEntreprise(entreprise);
        stage.addEtudiant(etudiant);
        stage.addProfesseur(professeurs.get(0));
        stage.setCommentaire(viewModel.getCommentaire());
        stage.setJournees(viewModel.getJourStage());
        stage.setheureDebut(viewModel.getHeureDebutStage());
        stage.setTempsStage((int) MINUTES.between(viewModel.getHeureDebutStage(), viewModel.getHeureFinStage()));
        stage.setHeureDiner(viewModel.getHeureDebutDiner());
        stage.setTempsDiner((int) MINUTES.between(viewModel.getHeureDebutDiner(), viewModel.getHeureFinDiner()));
        stage.setDureeVisite(viewModel.getTempsVisites());
        stage.setDisponibiliteTuteur(viewModel.getDispoTuteur());
        dbHelper.createStage(stage);
        dbHelper.changerPhotoCompte(etudiant);
        return stage;
    }

    /**
     * Affiche un message d'erreur
     *
     * @param message le message a afficher
     */
    private void afficherMessage(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(R.string.titre_erreur);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.btn_ok),
                (dialogInterface, i) -> {
                    //on ne fait rien
                });
        alertDialog.show();
    }

}