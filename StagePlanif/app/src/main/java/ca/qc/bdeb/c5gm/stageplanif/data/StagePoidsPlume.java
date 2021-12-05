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
    private final String idEtudiant;
    private final String nomEtudiant;
    private final String prenomEtudiant;
    private final Integer dureeVisite;
    private final String id;

    public StagePoidsPlume(String id, Entreprise entreprise, Priorite priorite, String idEtudiant, String nomEtudiant, String prenomEtudiant, Integer dureeVisite) {
        this.id = id;
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
        id = in.readString();
        entreprise = in.readParcelable(Entreprise.class.getClassLoader());
        priorite = in.readParcelable(Priorite.class.getClassLoader());
        idEtudiant = in.readString();
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
        parcel.writeString(id);
        parcel.writeParcelable(entreprise, i);
        parcel.writeParcelable(priorite, i);
        parcel.writeString(idEtudiant);
        parcel.writeString(nomEtudiant);
        parcel.writeString(prenomEtudiant);
        parcel.writeInt(dureeVisite);
    }

    public Visite getVisite() {
        return new Visite(UUID.randomUUID().toString(), this, 0, this.dureeVisite, 0);
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public Priorite getPriorite() {
        return priorite;
    }

    public String getIdEtudiant() {
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

    public String getId() {
        return id;
    }
}
