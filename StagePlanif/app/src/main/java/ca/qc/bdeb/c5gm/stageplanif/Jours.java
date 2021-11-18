package ca.qc.bdeb.c5gm.stageplanif;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Jours implements Parcelable {
    MERCREDI(1),
    JEUDI(2),
    VENDREDI(4);

    private static final List<Jours> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private final int valeur;

    Jours(int valeur) {
        this.valeur = valeur;
    }


    public static final Parcelable.Creator<Jours> CREATOR = new Parcelable.Creator<Jours>() {
        @Override
        public Jours createFromParcel(Parcel in) {
            return Jours.values()[in.readInt()];
        }

        @Override
        public Jours[] newArray(int size) {
            return new Jours[size];
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

    /**
     * Trouver la priorite en fonction de sa valeur
     * @param valeur valeur de la priorite voulue
     * @return une prioritee
     */
    public static Jours getJours(Integer valeur) {
        for(Jours jours: VALUES) {
            if(jours.getValeur() == valeur){
                return jours;
            }
        }
        return null;
    }

    /**
     * Renvoyer le total des valeurs de chaque prioritées
     * @return le total (int)
     */
    public static int getTotalValeursPriorites() {
        int totalValeursPriorite = 0;

        for(Priorite p : Priorite.values()) {
            totalValeursPriorite += p.getValeur();
        }

        return totalValeursPriorite;
    }
}
