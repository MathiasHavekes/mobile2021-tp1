package ca.qc.bdeb.c5gm.stageplanif;

import android.os.Parcel;
import android.os.Parcelable;

public class GoogleMapsObject implements Parcelable {
    private final Entreprise entreprise;
    private final String nom;
    private final String prenom;
    private final Priorite priorite;

    public GoogleMapsObject(Entreprise entreprise, String nom, String prenom, Priorite priorite) {
        this.entreprise = entreprise;
        this.nom = nom;
        this.prenom = prenom;
        this.priorite = priorite;
    }

    protected GoogleMapsObject(Parcel in) {
        entreprise = in.readParcelable(Entreprise.class.getClassLoader());
        nom = in.readString();
        prenom = in.readString();
        priorite = in.readParcelable(Priorite.class.getClassLoader());
    }

    public static final Creator<GoogleMapsObject> CREATOR = new Creator<GoogleMapsObject>() {
        @Override
        public GoogleMapsObject createFromParcel(Parcel in) {
            return new GoogleMapsObject(in);
        }

        @Override
        public GoogleMapsObject[] newArray(int size) {
            return new GoogleMapsObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(entreprise, i);
        parcel.writeString(nom);
        parcel.writeString(prenom);
        parcel.writeParcelable(priorite, i);
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public Priorite getPriorite() {
        return priorite;
    }
}
