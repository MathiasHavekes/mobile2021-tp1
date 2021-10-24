package ca.qc.bdeb.c5gm.stageplanif;

import java.util.Comparator;

public class ComptePrioriteComparateur implements Comparator<Compte> {
    public int compare(Compte c1, Compte c2) {
        return c1.getPriorite().getValeur() - c2.getPriorite().getValeur();
    }
}
