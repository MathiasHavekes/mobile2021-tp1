package ca.qc.bdeb.c5gm.stageplanif;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import ca.qc.bdeb.c5gm.stageplanif.data.Entreprise;
import ca.qc.bdeb.c5gm.stageplanif.data.Stage;
import ca.qc.bdeb.c5gm.stageplanif.data.Stockage;

/**
 * Classe qui s'occupe du Fragment fragment_demande_info_entreprise
 */
public class InfoEntrepriseFragment extends Fragment {
    /**
     * La liste d'entreprise qui donne des stages
     */
    private ArrayList<Entreprise> entreprises;
    /**
     * Spinner contenant le nom des entreprises donnant les stages
     */
    private Spinner spinnerEntreprise;
    /**
     * Texte qui affiche l'adresse de l'entreprise
     */
    private EditText texteAdresse;
    /**
     * Texte qui affiche la ville de l'entreprise
     */
    private EditText texteVille;
    /**
     * Texte qui affiche le code postal de l'entreprise
     */
    private EditText texteCP;
    /**
     * Texte qui affiche la province de l'entreprise
     */
    private EditText texteProvince;
    /**
     * Instance du viewModel qui permet de communiquer avec l'activitee et l'autre fragment
     */
    private InfoStageViewModel viewModel;
    /**
     * Propriete qui ecoute la selection d'item dans le spinner
     */
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stockage dbHelper = Stockage.getInstance(requireActivity().getApplicationContext());
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
        setSelection();
        return view;
    }

    /**
     * Methode qui choisi une entreprise par defaut dans le spinner
     */
    private void setSelection() {
        Stage stage = viewModel.getStage();
        if (stage != null) {
            Entreprise entrepriseStage = stage.getEntreprise();
            for (int i = 0; i < entreprises.size(); i++) {
                Entreprise entrepriseDansListe = entreprises.get(i);
                if (entrepriseStage.getId().equals(entrepriseDansListe.getId())) {
                    spinnerEntreprise.setSelection(i);
                    break;
                }
            }
        }
    }

    /**
     * Methode qui met les nom d'entreprises dans le spinner
     */
    private void setSpinner() {
        String[] arraySpinner = new String[entreprises.size()];
        for (int i = 0; i < entreprises.size(); i++) {
            Entreprise entreprise = entreprises.get(i);
            arraySpinner[i] = entreprise.getNom();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity().getBaseContext(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEntreprise.setAdapter(adapter);
        spinnerEntreprise.setOnItemSelectedListener(itemSelectionneListener);
    }

}