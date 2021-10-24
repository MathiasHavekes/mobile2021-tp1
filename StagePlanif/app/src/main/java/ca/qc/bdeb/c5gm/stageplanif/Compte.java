package ca.qc.bdeb.c5gm.stageplanif;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Le compte sont le type de donnees utilise pour le recycler view
 */
public class Compte implements Parcelable {
    private final Integer Id;
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
    private byte[] photo;
    /**
     * Le type de compte
     */
    private final Integer typeCompte;

    /**
     * Constructeur par défaut
     * @param nom Le nom du compte
     * @param prenom Le prénom du compte
     * @param photo La photo du compte (a modifier)
     * @param typeCompte Le type de compte
     */
    public Compte(Integer id, String nom, String prenom, byte[] photo, Integer typeCompte) {
        this.Id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.photo = photo;
        this.typeCompte = typeCompte;
    }

    /**
     * Constructeur par défaut
     * @param nom Le nom du compte
     * @param prenom Le prénom du compte
     * @param photo La photo du compte (a modifier)
     * @param typeCompte Le type de compte
     */
    public Compte(String nom, String prenom, byte[] photo, Integer typeCompte) {
        this.Id = null;
        this.nom = nom;
        this.prenom = prenom;
        this.photo = photo;
        this.typeCompte = typeCompte;
    }

    protected Compte(Parcel in) {
        nom = in.readString();
        prenom = in.readString();
        if (in.readByte() == 0) {
            typeCompte = null;
        } else {
            typeCompte = in.readInt();
        }
        Id = null;
    }

    public static final Creator<Compte> CREATOR = new Creator<Compte>() {
        @Override
        public Compte createFromParcel(Parcel in) {
            return new Compte(in);
        }

        @Override
        public Compte[] newArray(int size) {
            return new Compte[size];
        }
    };

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public Integer getTypeCompte() {
        return typeCompte;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nom);
        parcel.writeString(prenom);
        if (typeCompte == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(typeCompte);
        }
    }

    public Integer getId() {
        return Id;
    }
}
