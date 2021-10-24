package ca.qc.bdeb.c5gm.stageplanif;

import java.util.Comparator;

public class ComptePrenomComparateur implements Comparator<Compte> {
    public int compare(Compte c1, Compte c2) {
        return c1.getPrenom().compareToIgnoreCase(c2.getPrenom());
    }
}