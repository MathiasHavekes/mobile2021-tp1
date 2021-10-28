package ca.qc.bdeb.c5gm.stageplanif;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Les GoogleMapsObject sont des pures fabrications qui permettent d'envoyer plusieurs stages sans faire crasher le programme
 */
public class GoogleMapsObject implements Parcelable {
    /**
     * Implementation de parcel
     */
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
    /**
     * L'entreprise qui donne le stage
     */
    private final Entreprise entreprise;
    /**
     * La priorite du stage
     */
    private final Priorite priorite;

    public GoogleMapsObject(Entreprise entreprise, Priorite priorite) {
        this.entreprise = entreprise;
        this.priorite = priorite;
    }

    /**
     * Implementation de parcel
     */
    protected GoogleMapsObject(Parcel in) {
        entreprise = in.readParcelable(Entreprise.class.getClassLoader());
        priorite = in.readParcelable(Priorite.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(entreprise, i);
        parcel.writeParcelable(priorite, i);
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public Priorite getPriorite() {
        return priorite;
    }

}
