package ca.qc.bdeb.c5gm.stageplanif;

import java.util.UUID;

public class Stage {
    private String id;
    private Entreprise entreprise;
    private Compte eleve;
    private String anneeScolaire;
    private Compte enseignant;

    public Stage(Entreprise entreprise, Compte eleve, String anneeScolaire, Compte enseignant) {
        this.id = UUID.randomUUID().toString();
        this.entreprise = entreprise;
        this.eleve = eleve;
        this.anneeScolaire = anneeScolaire;
        this.enseignant = enseignant;
    }

    public Stage(String id, Entreprise entreprise, Compte eleve, String anneeScolaire, Compte enseignant) {
        this.id = id;
        this.entreprise = entreprise;
        this.eleve = eleve;
        this.anneeScolaire = anneeScolaire;
        this.enseignant = enseignant;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public Compte getEleve() {
        return eleve;
    }

    public String getAnneeScolaire() {
        return anneeScolaire;
    }

    public Compte getEnseignant() {
        return enseignant;
    }
}
