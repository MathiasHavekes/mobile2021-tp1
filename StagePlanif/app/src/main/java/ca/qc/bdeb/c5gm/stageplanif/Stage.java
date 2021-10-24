package ca.qc.bdeb.c5gm.stageplanif;

public class Stage {
    /**
     * ID du stage
     */
    private final String id;
    /**
     * Entreprise de stage
     */
    private Entreprise entreprise;
    /**
     * Etudiant du stage
     */
    private Compte etudiant;
    /**
     * Annee scolaire du stage
     */
    private final String anneeScolaire;
    /**
     * Professeur du stage
     */
    private Compte professeur;
    /**
     * Drapeau du stage
     */
    private Integer drapeau;

    public Stage(String id, String anneeScolaire, Integer drapeau) {
        this.id = id;
        this.anneeScolaire = anneeScolaire;
        this.drapeau = drapeau;
    }

    /**
     * Ajouter un etudiant au stage
     * @param etudiant etudiant du stage
     */
    public void addEtudiant(Compte etudiant) {
        this.etudiant = etudiant;
    }

    /**
     * Professeur du stage
     * @param professeur
     */
    public void addProfesseur(Compte professeur) {
        this.professeur = professeur;
    }

    /**
     * Entreprise du stage
     * @param entreprise
     */
    public void addEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public Compte getEtudiant() {
        return etudiant;
    }

    public String getAnneeScolaire() {
        return anneeScolaire;
    }

    public Compte getProfesseur() {
        return professeur;
    }

    public Integer getDrapeau() {
        return drapeau;
    }

    public String getId() {
        return id;
    }
}
