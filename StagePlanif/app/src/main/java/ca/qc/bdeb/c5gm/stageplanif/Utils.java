package ca.qc.bdeb.c5gm.stageplanif;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Utils {
    public static ArrayList<Integer> calculerPrioritesSelectionnees(int selection) {
        ArrayList<Integer> ListePrioritesSelectionnees = new ArrayList<>();

        for (Priorite p : Priorite.values()) {
            if ((selection & p.getValeur()) > 0) {
                ListePrioritesSelectionnees.add(p.getValeur());
            }
        }
        return ListePrioritesSelectionnees;
    }

    public static ArrayList<Stage> filtrerListeStages(ArrayList<Integer> ListePrioritesSelectionnees, ArrayList<Stage> listeStages) {
        ArrayList<Stage> listeStagesMasques = new ArrayList<>();

        for (Stage s : listeStages) {
            if (!ListePrioritesSelectionnees.contains(s.getPriorite().getValeur())) {
                listeStagesMasques.add(s);
            }
        }
        return listeStagesMasques;
    }

    public static ArrayList<Stage> trierListeStages(ArrayList<Stage> listeStages, Comparator<Stage>... comparators) {
        Collections.sort(listeStages, new StageChainedComparateur(comparators));
        return listeStages;
    }

    public static int renvoyerCouleur(Priorite priorite) {
        switch (priorite) {
            case MINIMUM:
                return R.color.green;
            case MOYENNE:
                return R.color.yellow;
            case MAXIMUM:
                return R.color.red;
            default:
                return R.color.black;
        }
    }
}
