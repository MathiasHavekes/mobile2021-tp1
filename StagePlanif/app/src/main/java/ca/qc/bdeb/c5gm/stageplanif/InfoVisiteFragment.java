package ca.qc.bdeb.c5gm.stageplanif;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.time.LocalTime;

/**
 *
 */
public class InfoVisiteFragment extends Fragment {
    private EditText tempsDebutStage;
    private EditText tempsFinStage;
    private EditText tempsDebutDiner;
    private EditText tempsFinDiner;
    private RadioGroup radioGroup;
    private TimePickerDialog dialogueChoixTemps;
    private Context context;
    private InfoStageViewModel viewModel;
    private CheckBox checkBoxMercredi;
    private CheckBox checkBoxJeudi;
    private CheckBox checkBoxVendredi;
    private View.OnClickListener choisirTemps = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialogueChoixTemps = new TimePickerDialog(context, (timePicker, heure, minute) -> mettreDate(heure, minute, view),
                    0, 0, true);
            dialogueChoixTemps.show();
        }
    };
    private View.OnClickListener checkboxClique = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.equals(checkBoxMercredi)) {

            } else if (view.equals(checkBoxJeudi)) {

            } else if (view.equals(checkBoxVendredi)) {

            }
        }
    };
    private RadioGroup.OnCheckedChangeListener radioGroupClique = new RadioGroup.OnCheckedChangeListener() {
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

    private void mettreDate(int heure, int minutes, View view) {
        LocalTime temps = LocalTime.of(heure, minutes);
        if (view.equals(tempsDebutStage)) {
            viewModel.setHeureDebutStage(temps);
        } else if(view.equals(tempsFinStage)) {
            viewModel.setHeureFinStage(temps);
        } else if(view.equals(tempsDebutDiner)) {
            viewModel.setHeureDebutDiner(temps);
        } else if (view.equals(tempsFinDiner)) {
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
        tempsDebutStage = view.findViewById(R.id.text_stage_debut);
        tempsDebutStage.setOnClickListener(choisirTemps);
        tempsFinStage = view.findViewById(R.id.text_stage_fin);
        tempsFinStage.setOnClickListener(choisirTemps);
        tempsDebutDiner = view.findViewById(R.id.text_diner_debut);
        tempsDebutDiner.setOnClickListener(choisirTemps);
        tempsFinDiner = view.findViewById(R.id.text_diner_fin);
        tempsFinDiner.setOnClickListener(choisirTemps);
        checkBoxMercredi = view.findViewById(R.id.selection_mercredi);
        checkBoxJeudi = view.findViewById(R.id.selection_jeudi);
        checkBoxVendredi = view.findViewById(R.id.selection_vendredi);
        radioGroup = view.findViewById(R.id.radio_group_temps);
        radioGroup.setOnCheckedChangeListener(radioGroupClique);
        return view;
    }
}