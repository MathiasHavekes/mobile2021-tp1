package ca.qc.bdeb.c5gm.stageplanif;

import java.util.Comparator;

public class StagePrioriteComparateur implements Comparator<Stage> {
    public int compare(Stage s1, Stage s2) {
        return s1.getPriorite().getValeur() - s2.getPriorite().getValeur();
    }
}
