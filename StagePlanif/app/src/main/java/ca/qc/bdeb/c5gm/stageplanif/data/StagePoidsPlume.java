package ca.qc.bdeb.c5gm.stageplanif.data;

import android.os.Parcel;
import android.os.Parcelable;

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

    public StagePoidsPlume(Entreprise entreprise, Priorite priorite) {
        this.entreprise = entreprise;
        this.priorite = priorite;
    }

    /**
     * Implementation de parcel
     */
    protected StagePoidsPlume(Parcel in) {
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
