package ca.qc.bdeb.c5gm.stageplanif.data;

/**
 * Enum qui contient les types de compte possible
 */
public enum TypeCompte {
    ETUDIANT(2),
    PROFESSEUR(1),
    ADMINISTRATEUR(0);

    TypeCompte(int valeur) {
        this.valeur = valeur;
    }

    private int valeur;

    public int getValeur(){
        return this.valeur;
    }
}
