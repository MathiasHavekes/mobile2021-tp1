package ca.qc.bdeb.c5gm.stageplanif;

/**
 * Le compte sont le type de donnees utilise pour le recycler view
 */
public class Compte {
    /**
     * Le nom du compte
     */
    private final String nom;
    /**
     * Le prénom du compte
     */
    private final String prenom;
    /**
     * La photo du compte
     */
    private Byte[] photo;
    /**
     * Le type de compte
     */
    private final Integer typeCompte;

    private Priorite priorite;

    /**
     * Constructeur par défaut
     * @param nom Le nom du compte
     * @param prenom Le prénom du compte
     * @param photo La photo du compte (a modifier)
     * @param typeCompte Le type de compte
     */
    public Compte(String nom, String prenom, Byte[] photo, Integer typeCompte, Priorite priorite) {
        this.nom = nom;
        this.prenom = prenom;
        this.photo = photo;
        this.typeCompte = typeCompte;
        this.priorite = priorite;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public Integer getTypeCompte() {
        return typeCompte;
    }

    public Priorite getPriorite() {
        return priorite;
    }

    public Byte[] getPhoto() {
        return photo;
    }

    public void setPriorite(Priorite priorite) {
        this.priorite = priorite;
    }

    public void setPhoto(Byte[] photo) {
        this.photo = photo;
    }
}
