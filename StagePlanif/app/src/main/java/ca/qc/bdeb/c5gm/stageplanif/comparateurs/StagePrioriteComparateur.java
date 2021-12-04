package ca.qc.bdeb.c5gm.stageplanif.comparateurs;

import java.util.Comparator;

import ca.qc.bdeb.c5gm.stageplanif.data.Stage;

public class StagePrioriteComparateur implements Comparator<Stage> {
    public int compare(Stage s1, Stage s2) {
        return s2.getPriorite().getValeur() - s1.getPriorite().getValeur();
    }
}
