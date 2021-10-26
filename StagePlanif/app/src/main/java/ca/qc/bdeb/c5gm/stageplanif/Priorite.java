package ca.qc.bdeb.c5gm.stageplanif;

import android.os.Parcel;
import android.os.Parcelable;

public enum Priorite implements Parcelable {
    MAXIMUM(4),
    MOYENNE(2),
    MINIMUM(1);

    private int valeur;

    Priorite(int valeur) {
        this.valeur = valeur;
    }


    public static final Creator<Priorite> CREATOR = new Creator<Priorite>() {
        @Override
        public Priorite createFromParcel(Parcel in) {
            return Priorite.values()[in.readInt()];
        }

        @Override
        public Priorite[] newArray(int size) {
            return new Priorite[size];
        }
    };

    public int getValeur() {
        return valeur;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ordinal());
    }
}
