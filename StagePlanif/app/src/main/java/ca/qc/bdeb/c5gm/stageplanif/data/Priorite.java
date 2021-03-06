package ca.qc.bdeb.c5gm.stageplanif.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enum qui définit une priorité de stage
 */
public enum Priorite implements Parcelable {
    BASSE(1),
    MOYENNE(2),
    HAUTE(4);

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
    /**
     * Valeurs possibles de priorité
     */
    private static final List<Priorite> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    /**
     * Valeur de la priorite
     */
    private final int valeur;


    Priorite(int valeur) {
        this.valeur = valeur;
    }

    /**
     * Trouver la priorite en fonction de sa valeur
     *
     * @param valeur valeur de la priorite voulue
     * @return une prioritee
     */
    public static Priorite getPriorite(Integer valeur) {
        for (Priorite priorite : VALUES) {
            if (priorite.getValeur() == valeur) {
                return priorite;
            }
        }
        return null;
    }

    /**
     * Renvoyer le total des valeurs de chaque prioritées
     *
     * @return le total (int)
     */
    public static int getTotalValeursPriorites() {
        int totalValeursPriorite = 0;

        for (Priorite p : Priorite.values()) {
            totalValeursPriorite += p.getValeur();
        }

        return totalValeursPriorite;
    }

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
