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
    private byte journees;
    /**
     * Heure de debut du stage
     */
    private LocalTime heureDebut;
    /**
     * Duree du stage
     */
    private int tempsStage;
    /**
     * Heure de diner du stagiaire
     */
    private LocalTime heureDiner;
    /**
     * Temps du diner du stagiaire
     */
    private int tempsDiner;
    /**
     * Duree moyenne des visites
     */
    private int dureeVisite;
    /**
     * Disponibilites du tuteur
     */
    private byte disponibiliteTuteur;

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
        commentaire = in.readString();
        journees = in.readByte();
        heureDebut = LocalTime.ofSecondOfDay(in.readInt());
        tempsStage = in.readInt();
        heureDiner = LocalTime.ofSecondOfDay(in.readInt());
        tempsDiner = in.readInt();
        dureeVisite = in.readInt();
        disponibiliteTuteur = in.readByte();
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
        parcel.writeString(commentaire);
        parcel.writeByte(journees);
        parcel.writeInt(heureDebut.toSecondOfDay());
        parcel.writeInt(tempsStage);
        parcel.writeInt(heureDiner.toSecondOfDay());
        parcel.writeInt(tempsDiner);
        parcel.writeInt(dureeVisite);
        parcel.writeByte(disponibiliteTuteur);
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

    public byte getJournees() {
        return journees;
    }

    public void setJournees(byte journees) {
        this.journees = journees;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public void setheureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public int getTempsStage() {
        return tempsStage;
    }

    public void setTempsStage(int tempsStage) {
        this.tempsStage = tempsStage;
    }

    public LocalTime getHeureDiner() {
        return heureDiner;
    }

    public void setHeureDiner(LocalTime heureDiner) {
        this.heureDiner = heureDiner;
    }

    public int getTempsDiner() {
        return tempsDiner;
    }

    public void setTempsDiner(int tempsDiner) {
        this.tempsDiner = tempsDiner;
    }

    public int getDureeVisite() {
        return dureeVisite;
    }

    public void setDureeVisite(int dureeVisite) {
        this.dureeVisite = dureeVisite;
    }

    public byte getDisponibiliteTuteur() {
        return disponibiliteTuteur;
    }

    public void setDisponibiliteTuteur(byte disponibiliteTuteur) {
        this.disponibiliteTuteur = disponibiliteTuteur;
    }
}
