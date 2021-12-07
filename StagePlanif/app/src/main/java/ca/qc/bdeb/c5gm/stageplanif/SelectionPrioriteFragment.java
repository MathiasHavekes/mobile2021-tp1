package ca.qc.bdeb.c5gm.stageplanif;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ca.qc.bdeb.c5gm.stageplanif.data.Priorite;

public class SelectionPrioriteFragment extends Fragment {
    private final int VALEUR_DRAPEAU_VERT = Priorite.BASSE.getValeur();
    private final int VALEUR_DRAPEAU_JAUNE = Priorite.MOYENNE.getValeur();
    private final int VALEUR_DRAPEAU_ROUGE = Priorite.HAUTE.getValeur();
    private CheckBox drapeauVert, drapeauJaune, drapeauRouge;
    private int selection;
    private SelectionViewModel viewModel;
    private final View.OnClickListener drapeauVertOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            calculerValeurSelection(drapeauVert.isChecked(), VALEUR_DRAPEAU_VERT);
        }
    };
    private final View.OnClickListener drapeauJauneOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            calculerValeurSelection(drapeauJaune.isChecked(), VALEUR_DRAPEAU_JAUNE);
        }
    };
    private final View.OnClickListener drapeauRougeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            calculerValeurSelection(drapeauRouge.isChecked(), VALEUR_DRAPEAU_ROUGE);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_selection_priorite, container, false);
        drapeauVert = view.findViewById(R.id.drapeau_vert);
        drapeauJaune = view.findViewById(R.id.drapeau_jaune);
        drapeauRouge = view.findViewById(R.id.drapeau_rouge);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SelectionViewModel.class);
        drapeauVert.setOnClickListener(drapeauVertOnClickListener);
        drapeauJaune.setOnClickListener(drapeauJauneOnClickListener);
        drapeauRouge.setOnClickListener(drapeauRougeOnClickListener);
        selection = Priorite.getTotalValeursPriorites();
    }

    /**
     * @param estCoche
     * @param valeurDrapeau
     */
    private void calculerValeurSelection(boolean estCoche, int valeurDrapeau) {
        if (estCoche) {
            selection += valeurDrapeau;
        } else {
            selection -= valeurDrapeau;
        }
        viewModel.selectItem(selection);
    }
}