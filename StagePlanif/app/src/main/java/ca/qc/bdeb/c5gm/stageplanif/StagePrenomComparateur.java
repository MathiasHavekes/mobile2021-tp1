package ca.qc.bdeb.c5gm.stageplanif;

import java.util.Comparator;

/**
 * Comparteur de la classe stage : compare le prénom des étudiant entre eux
 */
public class StagePrenomComparateur implements Comparator<Stage> {
    public int compare(Stage s1, Stage s2) {
        return s1.getEtudiant().getPrenom().compareToIgnoreCase(s2.getEtudiant().getPrenom());
    }
}