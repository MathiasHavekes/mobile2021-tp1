package ca.qc.bdeb.c5gm.stageplanif;

import android.os.Parcel;
import android.os.Parcelable;

public class Entreprise implements Parcelable {
    /**
     * ID de l'entreprise
     */
    private final String id;
    /**
     * Nom de l'entreprise
     */
    private final String nom;
    /**
     * Adresse de l'entreprise
     */
    private final String adresse;
    /**
     * Ville de l'entreprise
     */
    private final String ville;
    /**
     * Province de l'entreprise
     */
    private final String province;
    /**
     * Code postal de l'entreprise
     */
    private final String codePostal;
    /**
     * Les coordonnées GPS de l'entreprise
     */
    private double[] latLng;

    public Entreprise(String id, String nom, String adresse, String ville, String province, String codePostal) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
        this.province = province;
        this.codePostal = codePostal;
    }

    protected Entreprise(Parcel in) {
        id = in.readString();
        nom = in.readString();
        adresse = in.readString();
        ville = in.readString();
        province = in.readString();
        codePostal = in.readString();
        latLng = in.createDoubleArray();
    }

    public static final Creator<Entreprise> CREATOR = new Creator<Entreprise>() {
        @Override
        public Entreprise createFromParcel(Parcel in) {
            return new Entreprise(in);
        }

        @Override
        public Entreprise[] newArray(int size) {
            return new Entreprise[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getVille() {
        return ville;
    }

    public String getProvince() {
        return province;
    }

    public String getCp() {
        return codePostal;
    }

    public double[] getLatLng() {
        return latLng;
    }

    public void setLatLng(double[] latLng) {
        this.latLng = latLng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(nom);
        parcel.writeString(adresse);
        parcel.writeString(ville);
        parcel.writeString(province);
        parcel.writeString(codePostal);
        parcel.writeDoubleArray(latLng);
    }
}
