package ca.qc.bdeb.c5gm.stageplanif.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalTime;
import java.util.UUID;

public class Stage implements Parcelable {
    public static final Creator<Stage> CREATOR = new Creator<Stage>() {
        @Override
        public Stage createFromParcel(Parcel in) {
            return new Stage(in);
        }

        @Override
        public Stage[] newArray(int size) {
            return new Stage[size];
        }
    };
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
     * Professeur du stage
     */
    private Compte professeur;
    /**
     * Drapeau du stage
     */
    private Priorite priorite;
    /**
     * Commentaire sur le stage
     */
    private String commentaire;
    /**
     * Journees de stage
     */
    private int[] journees;
    /**
     * Heure de debut du stage
     */
    private LocalTime heure_debut;
    /**
     * Duree du stage
     */
    private int temps_stage;
    /**
     * Heure de diner du stagiaire
     */
    private LocalTime heure_diner;
    /**
     * Temps du diner du stagiaire
     */
    private int temps_diner;
    /**
     * Duree moyenne des visites
     */
    private int duree_visite;
    /**
     * Disponibilites du tuteur
     */
    private int[] disponibilite_tuteur;

    public Stage(String id, String anneeScolaire, Priorite priorite) {
        this.id = id;
        this.priorite = priorite;
    }

    public Stage(Priorite priorite) {
        this.id = UUID.randomUUID().toString();
        this.priorite = priorite;
    }

    protected Stage(Parcel in) {
        id = in.readString();
        etudiant = in.readParcelable(Compte.class.getClassLoader());
        professeur = in.readParcelable(Compte.class.getClassLoader());
        entreprise = in.readParcelable(Entreprise.class.getClassLoader());
        priorite = in.readParcelable(Priorite.class.getClassLoader());
    }

    public void addEtudiant(Compte etudiant) {
        this.etudiant = etudiant;
    }

    public void addProfesseur(Compte professeur) {
        this.professeur = professeur;
    }

    public void addEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public Compte getEtudiant() {
        return etudiant;
    }

    public Compte getProfesseur() {
        return professeur;
    }

    public Priorite getPriorite() {
        return priorite;
    }

    public void setPriorite(Priorite priorite) {
        this.priorite = priorite;
    }

    public String getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeParcelable(etudiant, i);
        parcel.writeParcelable(professeur, i);
        parcel.writeParcelable(entreprise, i);
        parcel.writeParcelable(priorite, i);
    }

    /**
     * Transforme un stage en une pure fabrication pour envoyer a Google Maps
     *
     * @return un objet google maps contenant les informations du stage
     */
    public StagePoidsPlume getGoogleMapsObject() {
        return new StagePoidsPlume(this.getEntreprise(), this.getPriorite());
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public int[] getJournees() {
        return journees;
    }

    public void setJournees(int[] journees) {
        this.journees = journees;
    }

    public LocalTime getHeure_debut() {
        return heure_debut;
    }

    public void setHeure_debut(LocalTime heure_debut) {
        this.heure_debut = heure_debut;
    }

    public int getTemps_stage() {
        return temps_stage;
    }

    public void setTemps_stage(int temps_stage) {
        this.temps_stage = temps_stage;
    }

    public LocalTime getHeure_diner() {
        return heure_diner;
    }

    public int getTemps_diner() {
        return temps_diner;
    }

    public int getDuree_visite() {
        return duree_visite;
    }

    public void setDuree_visite(int duree_visite) {
        this.duree_visite = duree_visite;
    }

    public int[] getDisponibilite_tuteur() {
        return disponibilite_tuteur;
    }

    public void setDisponibilite_tuteur(int[] disponibilite_tuteur) {
        this.disponibilite_tuteur = disponibilite_tuteur;
    }
}
