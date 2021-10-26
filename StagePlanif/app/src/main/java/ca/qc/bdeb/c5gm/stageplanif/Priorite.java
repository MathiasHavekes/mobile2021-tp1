package ca.qc.bdeb.c5gm.stageplanif;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Priorite {
    MINIMUM(1),
    MOYENNE(2),
    MAXIMUM(4);

    private static final List<Priorite> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();
    private int valeur;

    Priorite(int valeur) {
        this.valeur = valeur;
    }

    public int getValeur() {
        return valeur;
    }

    /**
     * Cree une priorite aleatoire
     * @return une priorite
     */
    public static Priorite randomPriorite() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    /**
     * Trouver la priorite en fonction de sa valeur
     * @param valeur valeur de la priorite voulue
     * @return une prioritee
     */
    public static Priorite getPriorite(Integer valeur) {
        for(Priorite priorite: VALUES) {
            if(priorite.getValeur() == valeur){
                return priorite;
            }
        }
        return null;
    }
}
