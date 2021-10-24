package ca.qc.bdeb.c5gm.stageplanif;

public enum Priorite {
    MAXIMUM(4),
    MOYENNE(2),
    MINIMUM(1);

    private int valeur;

    Priorite(int valeur) {
        this.valeur = valeur;
    }

    public int getValeur() {
        return valeur;
    }
}
