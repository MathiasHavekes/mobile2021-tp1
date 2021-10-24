package ca.qc.bdeb.c5gm.stageplanif;

import java.util.Comparator;

public class StageNomComparateur implements Comparator<Stage> {
    public int compare(Stage s1, Stage s2) {
        return s1.getEtudiant().getNom().compareToIgnoreCase(s2.getEtudiant().getNom());
    }
}