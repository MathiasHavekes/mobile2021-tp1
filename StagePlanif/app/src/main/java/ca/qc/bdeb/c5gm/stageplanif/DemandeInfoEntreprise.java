package ca.qc.bdeb.c5gm.stageplanif;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Locale;

public class DemandeInfoEntreprise extends Fragment {
    private ArrayList<Entreprise> entreprises;
    private Spinner spinnerEntreprise;
    private EditText texteAdresse;
    private EditText texteVille;
    private EditText texteCP;
    private EditText texteProvince;
    private InfoStageViewModel viewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stockage dbHelper = Stockage.getInstance(getActivity().getApplicationContext());
        entreprises = dbHelper.getEntreprises();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demande_info_entreprise, container, false);
        spinnerEntreprise = view.findViewById(R.id.spinner_entreprise);
        texteAdresse = view.findViewById(R.id.text_adresse);
        texteVille = view.findViewById(R.id.texte_ville);
        texteCP = view.findViewById(R.id.text_cp);
        texteProvince = view.findViewById(R.id.text_province);
        viewModel = new ViewModelProvider(requireActivity()).get(InfoStageViewModel.class);
        setSpinner();
        Stage stage = viewModel.getStage();
        if (stage != null) {
            Entreprise entrepriseStage = stage.getEntreprise();
            for (int i = 0; i < entreprises.size(); i++) {
                Entreprise entrepriseDansListe = entreprises.get(i);
                if(entrepriseStage.getId().equals(entrepriseDansListe.getId())) {
                    spinnerEntreprise.setSelection(i);
                    break;
                }
            }
        }
        return view;
    }

    private void setSpinner() {
        String[] arraySpinner = new String[entreprises.size()];
        for (int i = 0; i < entreprises.size(); i++) {
            Entreprise entreprise = entreprises.get(i);
            arraySpinner[i] = entreprise.getNom();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEntreprise.setAdapter(adapter);
        spinnerEntreprise.setOnItemSelectedListener(itemSelectionneListener);
    }

    private final AdapterView.OnItemSelectedListener itemSelectionneListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Entreprise entreprise = entreprises.get(i);
            texteAdresse.setText(entreprise.getAdresse());
            texteCP.setText(entreprise.getCp());
            texteProvince.setText(entreprise.getProvince());
            texteVille.setText(entreprise.getVille());
            viewModel.setEntreprise(entreprise);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
}