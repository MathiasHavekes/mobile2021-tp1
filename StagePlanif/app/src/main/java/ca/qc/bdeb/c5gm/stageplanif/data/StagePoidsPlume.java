package ca.qc.bdeb.c5gm.stageplanif.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Les GoogleMapsObject sont des pures fabrications qui permettent d'envoyer plusieurs stages sans faire crasher le programme
 */
public class StagePoidsPlume implements Parcelable {
    /**
     * Implementation de parcel
     */
    public static final Creator<StagePoidsPlume> CREATOR = new Creator<StagePoidsPlume>() {
        @Override
        public StagePoidsPlume createFromParcel(Parcel in) {
            return new StagePoidsPlume(in);
        }

        @Override
        public StagePoidsPlume[] newArray(int size) {
            return new StagePoidsPlume[size];
        }
    };

    private final String id;
    /**
     * L'entreprise qui donne le stage
     */
    private final Entreprise entreprise;
    /**
     * La priorite du stage
     */
    private final Priorite priorite;
    /**
     * Id de l'etudiant associe au stage
     */
    private final String idEtudiant;
    /**
     * Nom de l'etudiant associe au stage
     */
    private final String nomEtudiant;
    /**
     * Prenom de l'etudiant associe au stage
     */
    private final String prenomEtudiant;
    /**
     * Duree de visite du stage
     */
    private final Integer dureeVisite;
    /**
     * Commentaire du stage
     */
    private final String commentaire;

    public StagePoidsPlume(String id, Entreprise entreprise, Priorite priorite, String idEtudiant, String nomEtudiant, String prenomEtudiant, Integer dureeVisite, String commentaire) {
        this.id = id;
        this.entreprise = entreprise;
        this.priorite = priorite;
        this.idEtudiant = idEtudiant;
        this.nomEtudiant = nomEtudiant;
        this.prenomEtudiant = prenomEtudiant;
        this.dureeVisite = dureeVisite;
        this.commentaire = commentaire;
    }

    /**
     * Implementation de parcel
     */
    protected StagePoidsPlume(Parcel in) {
        id = in.readString();
        entreprise = in.readParcelable(Entreprise.class.getClassLoader());
        priorite = in.readParcelable(Priorite.class.getClassLoader());
        idEtudiant = in.readString();
        nomEtudiant = in.readString();
        prenomEtudiant = in.readString();
        dureeVisite = in.readInt();
        commentaire = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeParcelable(entreprise, i);
        parcel.writeParcelable(priorite, i);
        parcel.writeString(idEtudiant);
        parcel.writeString(nomEtudiant);
        parcel.writeString(prenomEtudiant);
        parcel.writeInt(dureeVisite);
        parcel.writeString(commentaire);
    }

    public Visite getVisite() {
        return new Visite(UUID.randomUUID().toString(), this, this.dureeVisite, LocalDateTime.now());
    }

    public String getId() {
        return id;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public Priorite getPriorite() {
        return priorite;
    }

    public String getNomEtudiant() {
        return nomEtudiant;
    }

    public String getPrenomEtudiant() {
        return prenomEtudiant;
    }

    public String getNomCompletEtudiant() {
        return this.prenomEtudiant + " " + this.nomEtudiant;
    }

    public String getCommentaire() {
        return commentaire;
    }
}
