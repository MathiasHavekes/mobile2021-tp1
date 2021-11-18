package ca.qc.bdeb.c5gm.stageplanif.comparateurs;

import java.util.Comparator;

import ca.qc.bdeb.c5gm.stageplanif.data.Stage;

/**
 * Comparteur de la classe stage : compare le nom des étudiant entre eux
 */
public class StageNomComparateur implements Comparator<Stage> {
    public int compare(Stage s1, Stage s2) {
        return s1.getEtudiant().getNom().compareToIgnoreCase(s2.getEtudiant().getNom());
    }
}