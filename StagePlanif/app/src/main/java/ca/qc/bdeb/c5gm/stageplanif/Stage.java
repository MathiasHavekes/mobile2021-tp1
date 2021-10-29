package ca.qc.bdeb.c5gm.stageplanif;

import android.os.Parcel;
import android.os.Parcelable;

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
}
