package ca.qc.bdeb.c5gm.stageplanif;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Hashtable;

import ca.qc.bdeb.c5gm.stageplanif.data.Stage;

/**
 * Classe qui s'occupe du Fragment fragment_commentaires_visite
 */
public class CommentairesVisiteFragment extends Fragment {
    /**
     * Valeur de chacun des moments de disponibilite du tuteur
     */
    public final int LUNDI_AM = 1;
    public final int LUNDI_PM = 2;
    public final int MARDI_AM = 4;
    public final int MARDI_PM = 8;
    public final int MERCREDI_AM = 16;
    public final int MERCREDI_PM = 32;
    public final int JEUDI_AM = 64;
    public final int JEUDI_PM = 128;
    public final int VENDREDI_AM = 256;
    public final int VENDREDI_PM = 512;
    /**
     * Instance du viewModel qui permet de communiquer avec l'activitee et l'autre fragment
     */
    private InfoStageViewModel viewModel;
    private final TextWatcher captureTexte = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            viewModel.setCommentaire(editable.toString());
        }
    };
    /**
     * Contient les checkboxes de jours et le moment qu'ils representent
     */
    private final Hashtable<String, CheckBox[]> checkBoxesDispoTuteur = new Hashtable<>();
    /**
     * Contient un commentaire par rapport au stage
     */
    private EditText editText;
    /**
     * Somme des valeurs des checkboxes coches
     */
    private Integer jours = 0;
    /**
     * Action lors d'un clique de checkbox
     */
    private final View.OnClickListener checkboxClique = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Integer flag = 0;
            if (view.equals(checkBoxesDispoTuteur.get("lundi")[0])) {
                flag = LUNDI_AM;
            } else if (view.equals(checkBoxesDispoTuteur.get("lundi")[1])) {
                flag = LUNDI_PM;
            } else if (view.equals(checkBoxesDispoTuteur.get("mardi")[0])) {
                flag = MARDI_AM;
            } else if (view.equals(checkBoxesDispoTuteur.get("mardi")[1])) {
                flag = MARDI_PM;
            } else if (view.equals(checkBoxesDispoTuteur.get("mercredi")[0])) {
                flag = MERCREDI_AM;
            } else if (view.equals(checkBoxesDispoTuteur.get("mercredi")[1])) {
                flag = MERCREDI_PM;
            } else if (view.equals(checkBoxesDispoTuteur.get("jeudi")[0])) {
                flag = JEUDI_AM;
            } else if (view.equals(checkBoxesDispoTuteur.get("jeudi")[1])) {
                flag = JEUDI_PM;
            } else if (view.equals(checkBoxesDispoTuteur.get("vendredi")[0])) {
                flag = VENDREDI_AM;
            } else if (view.equals(checkBoxesDispoTuteur.get("vendredi")[1])) {
                flag = VENDREDI_PM;
            }
            if (((CheckBox) view).isChecked()) {
                jours |= flag;
            } else {
                jours -= flag;
            }
            viewModel.setDispoTuteur(jours);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_commentaires_visite, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(InfoStageViewModel.class);
        editText = view.findViewById(R.id.txt_commentaire);
        stockerCheckboxes(view);
        activerChampsDispoTuteur();
        setChamps();
        editText.addTextChangedListener(captureTexte);
        return view;
    }

    /**
     * Met les valeurs enregistrees sur les checkboxes et le champs de commentaire
     */
    private void setChamps() {
        Stage stage = viewModel.getStage();
        if (stage != null) {
            setChampsDispoTuteur(stage);
            if (stage.getCommentaire() != null) {
                editText.setText(stage.getCommentaire());
                viewModel.setCommentaire(stage.getCommentaire());
            }
        }
    }

    /**
     * Coche les cases de disponibilite de tuteur en fonction de ce qui est enregistre dans le compte
     *
     * @param stage stage a afficher
     */
    private void setChampsDispoTuteur(Stage stage) {
        int dispoTuteur = stage.getDisponibiliteTuteur();
        if (dispoTuteur > 0) {
            if ((dispoTuteur & LUNDI_AM) == LUNDI_AM) {
                checkDispoTuteur(LUNDI_AM, checkBoxesDispoTuteur.get("lundi")[0]);
            }
            if ((dispoTuteur & LUNDI_PM) == LUNDI_PM) {
                checkDispoTuteur(LUNDI_PM, checkBoxesDispoTuteur.get("lundi")[1]);
            }
            if ((dispoTuteur & MARDI_AM) == MARDI_AM) {
                checkDispoTuteur(MARDI_AM, checkBoxesDispoTuteur.get("mardi")[0]);
            }
            if ((dispoTuteur & MARDI_PM) == MARDI_PM) {
                checkDispoTuteur(MARDI_PM, checkBoxesDispoTuteur.get("mardi")[1]);
            }
            if ((dispoTuteur & MERCREDI_AM) == MERCREDI_AM) {
                checkDispoTuteur(MERCREDI_AM, checkBoxesDispoTuteur.get("mercredi")[0]);
            }
            if ((dispoTuteur & MERCREDI_PM) == MERCREDI_PM) {
                checkDispoTuteur(MERCREDI_PM, checkBoxesDispoTuteur.get("mercredi")[1]);
            }
            if ((dispoTuteur & JEUDI_AM) == JEUDI_AM) {
                checkDispoTuteur(JEUDI_AM, checkBoxesDispoTuteur.get("jeudi")[0]);
            }
            if ((dispoTuteur & JEUDI_PM) == JEUDI_PM) {
                checkDispoTuteur(JEUDI_PM, checkBoxesDispoTuteur.get("jeudi")[1]);
            }
            if ((dispoTuteur & VENDREDI_AM) == VENDREDI_AM) {
                checkDispoTuteur(VENDREDI_AM, checkBoxesDispoTuteur.get("vendredi")[0]);
            }
            if ((dispoTuteur & VENDREDI_PM) == VENDREDI_PM) {
                checkDispoTuteur(VENDREDI_PM, checkBoxesDispoTuteur.get("vendredi")[1]);
            }
        }
        viewModel.setDispoTuteur(jours);
    }

    /**
     * Cocher le checkbox specifier en parametre
     *
     * @param valeurJour Valeur du jour coche
     * @param checkBox   checkbox a coche
     */
    private void checkDispoTuteur(int valeurJour, CheckBox checkBox) {
        if (checkBox.isEnabled()) {
            checkBox.setChecked(true);
            jours |= valeurJour;
        }
    }

    /**
     * Stocker les checkboxes dans le Hashmap
     */
    private void stockerCheckboxes(View view) {
        checkBoxesDispoTuteur.put("lundi", new CheckBox[]{
                view.findViewById(R.id.box_lundi_am),
                view.findViewById(R.id.box_lundi_pm)
        });
        checkBoxesDispoTuteur.put("mardi", new CheckBox[]{
                view.findViewById(R.id.box_mardi_am),
                view.findViewById(R.id.box_mardi_pm)
        });
        checkBoxesDispoTuteur.put("mercredi", new CheckBox[]{
                view.findViewById(R.id.box_mercredi_am),
                view.findViewById(R.id.box_mercredi_pm)
        });
        checkBoxesDispoTuteur.put("jeudi", new CheckBox[]{
                view.findViewById(R.id.box_jeudi_am),
                view.findViewById(R.id.box_jeudi_pm)
        });
        checkBoxesDispoTuteur.put("vendredi", new CheckBox[]{
                view.findViewById(R.id.box_vendredi_am),
                view.findViewById(R.id.box_vendredi_pm)
        });
    }

    /**
     * Activer la modification sur les checkboxes lorsque la journee est une journee de stage
     */
    private void activerChampsDispoTuteur() {
        if (viewModel.getJourStage() != null) {
            if ((viewModel.getJourStage() & viewModel.LUNDI) == viewModel.LUNDI) {
                activerCheckboxes(checkBoxesDispoTuteur.get("lundi"));
            }
            if ((viewModel.getJourStage() & viewModel.MARDI) == viewModel.MARDI) {
                activerCheckboxes(checkBoxesDispoTuteur.get("mardi"));
            }
            if ((viewModel.getJourStage() & viewModel.MERCREDI) == viewModel.MERCREDI) {
                activerCheckboxes(checkBoxesDispoTuteur.get("mercredi"));
            }
            if ((viewModel.getJourStage() & viewModel.JEUDI) == viewModel.JEUDI) {
                activerCheckboxes(checkBoxesDispoTuteur.get("jeudi"));
            }
            if ((viewModel.getJourStage() & viewModel.VENDREDI) == viewModel.VENDREDI) {
                activerCheckboxes(checkBoxesDispoTuteur.get("vendredi"));
            }
        } else {
            viewModel.setJourStage((byte) 0);
        }
    }

    /**
     * Active les checkboxes dans une liste de checkboxes
     */
    private void activerCheckboxes(CheckBox[] checkBoxes) {
        for (CheckBox checkbox : checkBoxes) {
            checkbox.setEnabled(true);
            checkbox.setOnClickListener(checkboxClique);
        }
    }
}