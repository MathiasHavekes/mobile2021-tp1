package ca.qc.bdeb.c5gm.stageplanif.data;

import android.os.Parcel;
import android.os.Parcelable;

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
    /**
     * L'entreprise qui donne le stage
     */
    private final Entreprise entreprise;
    /**
     * La priorite du stage
     */
    private final Priorite priorite;
    private final Integer idEtudiant;
    private final String nomEtudiant;
    private final String prenomEtudiant;
    private final Integer dureeVisite;

    public StagePoidsPlume(Entreprise entreprise, Priorite priorite, Integer idEtudiant, String nomEtudiant, String prenomEtudiant, Integer dureeVisite) {
        this.entreprise = entreprise;
        this.priorite = priorite;
        this.idEtudiant = idEtudiant;
        this.nomEtudiant = nomEtudiant;
        this.prenomEtudiant = prenomEtudiant;
        this.dureeVisite = dureeVisite;
    }

    /**
     * Implementation de parcel
     */
    protected StagePoidsPlume(Parcel in) {
        entreprise = in.readParcelable(Entreprise.class.getClassLoader());
        priorite = in.readParcelable(Priorite.class.getClassLoader());
        idEtudiant = in.readInt();
        nomEtudiant = in.readString();
        prenomEtudiant = in.readString();
        dureeVisite = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(entreprise, i);
        parcel.writeParcelable(priorite, i);
        parcel.writeInt(idEtudiant);
        parcel.writeString(nomEtudiant);
        parcel.writeString(prenomEtudiant);
        parcel.writeInt(dureeVisite);
    }

    //TODO:CHANGER TO STRING DE ID ETUDIANT
    public Visite getVisite() {
        return new Visite(UUID.randomUUID().toString(), UUID.randomUUID().toString(), this.nomEtudiant, this.prenomEtudiant, this.priorite, 0, this.dureeVisite, 0);
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public Priorite getPriorite() {
        return priorite;
    }

    public Integer getIdEtudiant() {
        return idEtudiant;
    }

    public String getNomEtudiant() {
        return nomEtudiant;
    }

    public String getPrenomEtudiant() {
        return prenomEtudiant;
    }

    public Integer getDureeVisite() {
        return dureeVisite;
    }
}
