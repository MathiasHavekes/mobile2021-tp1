package ca.qc.bdeb.c5gm.stageplanif;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalTime;

import ca.qc.bdeb.c5gm.stageplanif.data.Stage;

/**
 * Classe qui s'occupe du Fragment fragment_demande_info_visite
 */
public class InfoVisiteFragment extends Fragment {
    /**
     * Valeur de lundi
     */
    private final byte LUNDI = 0x01;
    /**
     * Valeur de mardi
     */
    private final byte MARDI = 0x02;
    /**
     * Valeur de mercredi
     */
    private final byte MERCREDI = 0x04;
    /**
     * Valeur de jeudi
     */
    private final byte JEUDI = 0x08;
    /**
     * Valeur de vendredi
     */
    private final byte VENDREDI = 0x10;
    /**
     * Champs de texte du temps de debut de stage
     */
    private EditText txtTempsDebutStage;
    /**
     * Champs de texte de l'heure de fin de stage
     */
    private EditText txtTempsFinStage;
    /**
     * Champs de texte de temps de debut du diner
     */
    private EditText txtTempsDebutDiner;
    /**
     * Champs de texte du temps de fin du diner
     */
    private EditText txtTempsFinDiner;
    /**
     * Radio group du temps de visite
     */
    private RadioGroup radioGroup;
    /**
     * Dialogue de choix de temps
     */
    private TimePickerDialog dialogueChoixTemps;
    /**
     * Contexte du fragment
     */
    private Context context;
    /**
     * Instance du viewModel qui permet de communiquer avec l'activitee et l'autre fragment
     */
    private InfoStageViewModel viewModel;
    /**
     * Checkbox du lundi
     */
    private CheckBox checkBoxLundi;
    /**
     * Checkbox du mardi
     */
    private CheckBox checkBoxMardi;
    /**
     * Checkbox du mercredi
     */
    private CheckBox checkBoxMercredi;
    /**
     * Checkbox du jeudi
     */
    private CheckBox checkBoxJeudi;
    /**
     * Checkbox du vendredi
     */
    private CheckBox checkBoxVendredi;
    /**
     * Valeur des cases de jours coches
     */
    private byte coche = 0;

    /**
     * OnClickListener pour choisir l'heure
     */
    private final View.OnClickListener choisirTemps = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialogueChoixTemps = new TimePickerDialog(context, (timePicker, heure, minute) -> mettreHeures(heure, minute, view),
                    0, 0, true);
            dialogueChoixTemps.show();
        }
    };
    /**
     * Action lors d'un clique de checkbox
     */
    private final View.OnClickListener checkboxClique = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Byte flag = 0;
            if (view.equals(checkBoxLundi)) {
                flag = LUNDI;
            } else if (view.equals(checkBoxMardi)) {
                flag = MARDI;
            } else if (view.equals(checkBoxMercredi)) {
                flag = MERCREDI;
            } else if (view.equals(checkBoxJeudi)) {
                flag = JEUDI;
            } else if (view.equals(checkBoxVendredi)) {
                flag = VENDREDI;
            }
            if (((CheckBox) view).isChecked()) {
                coche |= flag;
            } else {
                coche -= flag;
            }
            viewModel.setJourStage(coche);
        }
    };
    /**
     * Action lorsqu'un item du radio group est clique
     */
    private final RadioGroup.OnCheckedChangeListener radioGroupClique = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            switch (i) {
                case R.id.duree_30_minutes:
                    viewModel.setTempsVisites(30);
                    break;
                case R.id.duree_45_minutes:
                    viewModel.setTempsVisites(45);
                    break;
                case R.id.duree_60_minutes:
                    viewModel.setTempsVisites(60);
                    break;
            }
        }
    };

    /**
     * Met l'heure dans le viewmodel
     *
     * @param heure
     * @param minutes
     * @param view    view qui a ete utilisee
     */
    private void mettreHeures(int heure, int minutes, View view) {
        LocalTime temps = LocalTime.of(heure, minutes);
        if (view.equals(txtTempsDebutStage)) {
            viewModel.setHeureDebutStage(temps);
        } else if (view.equals(txtTempsFinStage)) {
            viewModel.setHeureFinStage(temps);
        } else if (view.equals(txtTempsDebutDiner)) {
            viewModel.setHeureDebutDiner(temps);
        } else if (view.equals(txtTempsFinDiner)) {
            viewModel.setHeureFinDiner(temps);
        }
        EditText tempsText = (EditText) view;
        String texteTemps = String.format("%02d:%02d", heure, minutes);
        tempsText.setText(texteTemps);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demande_info_visite, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(InfoStageViewModel.class);
        initView(view);
        Stage stage = viewModel.getStage();
        if (stage != null) {
            setChampsHeures(stage);
            setChampsDureeVisite(stage);
            setChampsJoursStage(stage);
        }
        return view;
    }

    /**
     * Set les checkbox des jours de stage
     *
     * @param stage instance du stage
     */
    private void setChampsJoursStage(Stage stage) {
        byte journees = stage.getJournees();
        if (journees != 0) {
            if ((journees & LUNDI) == LUNDI) {
                checkBoxLundi.setChecked(true);
            }
            if ((journees & MARDI) == MARDI) {
                checkBoxMardi.setChecked(true);
            }
            if ((journees & MERCREDI) == MERCREDI) {
                checkBoxMercredi.setChecked(true);
            }
            if ((journees & JEUDI) == JEUDI) {
                checkBoxJeudi.setChecked(true);
            }
            if ((journees & VENDREDI) == VENDREDI) {
                checkBoxVendredi.setChecked(true);
            }
        }

    }

    /**
     * Set les boutons radio de duree de visite
     *
     * @param stage instance du stage
     */
    private void setChampsDureeVisite(Stage stage) {
        int dureeVisite = stage.getDureeVisite();
        if (dureeVisite != 0) {
            switch (dureeVisite) {
                case 30:
                    radioGroup.check(R.id.duree_30_minutes);
                    break;
                case 45:
                    radioGroup.check(R.id.duree_45_minutes);
                    break;
                case 60:
                    radioGroup.check(R.id.duree_60_minutes);
                    break;
                default:
                    Log.d("Data", "setChampsDureeVisite() called with: stage = [" + stage + "] duree de visite different de 30,45 ou 60");
            }
        }
    }

    /**
     * Set les champs des heures de stage
     *
     * @param stage instance du stage
     */
    private void setChampsHeures(Stage stage) {
        LocalTime heureDebut = stage.getHeureDebut();
        if (heureDebut != null) {
            afficherTemps(txtTempsDebutStage, heureDebut);
            int tempsStage = stage.getTempsStage();
            if (tempsStage != 0) {
                LocalTime heureFin = heureDebut.plusMinutes(tempsStage);
                afficherTemps(txtTempsFinStage, heureFin);
            }
        }
        LocalTime heureDiner = stage.getHeureDiner();
        if (heureDiner != null) {
            afficherTemps(txtTempsDebutDiner, heureDiner);
            int tempsDiner = stage.getTempsDiner();
            if (tempsDiner != 0) {
                LocalTime heureFinDiner = heureDiner.plusMinutes(tempsDiner);
                afficherTemps(txtTempsFinDiner, heureFinDiner);
            }
        }
    }

    /**
     * Affiche un texte d'heure dans un champ de texte defini
     *
     * @param champs champs a remplir
     * @param heure  le temps qu'il faut afficher
     */
    private void afficherTemps(EditText champs, LocalTime heure) {
        String texteTemps = String.format("%02d:%02d", heure.getHour(), heure.getMinute());
        champs.setText(texteTemps);
    }

    /**
     * Initialise la vue
     *
     * @param view
     */
    private void initView(View view) {
        txtTempsDebutStage = view.findViewById(R.id.text_stage_debut);
        txtTempsDebutStage.setOnClickListener(choisirTemps);
        txtTempsFinStage = view.findViewById(R.id.text_stage_fin);
        txtTempsFinStage.setOnClickListener(choisirTemps);
        txtTempsDebutDiner = view.findViewById(R.id.text_diner_debut);
        txtTempsDebutDiner.setOnClickListener(choisirTemps);
        txtTempsFinDiner = view.findViewById(R.id.text_diner_fin);
        txtTempsFinDiner.setOnClickListener(choisirTemps);
        checkBoxLundi = view.findViewById(R.id.selection_lundi);
        checkBoxLundi.setOnClickListener(checkboxClique);
        checkBoxMardi = view.findViewById(R.id.selection_mardi);
        checkBoxMardi.setOnClickListener(checkboxClique);
        checkBoxMercredi = view.findViewById(R.id.selection_mercredi);
        checkBoxMercredi.setOnClickListener(checkboxClique);
        checkBoxJeudi = view.findViewById(R.id.selection_jeudi);
        checkBoxJeudi.setOnClickListener(checkboxClique);
        checkBoxVendredi = view.findViewById(R.id.selection_vendredi);
        checkBoxVendredi.setOnClickListener(checkboxClique);
        radioGroup = view.findViewById(R.id.radio_group_temps);
        radioGroup.setOnCheckedChangeListener(radioGroupClique);
    }
}