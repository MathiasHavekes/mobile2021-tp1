package ca.qc.bdeb.c5gm.stageplanif;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Comparteur enchainé de la classe stage : cette classe recoit des comparateur de stages
 * Les comparateurs sont ensuite placé dans une chaine de comparateur
 * L'ordre dans lequel les comparateurs sont recu dans le constructeur défini la facon dont les stages seront triée
 */
public class StageChainedComparateur implements Comparator<Stage> {
    private final List<Comparator<Stage>> listComparators;

    public StageChainedComparateur(Comparator<Stage>... comparators) {
        this.listComparators = Arrays.asList(comparators);
    }

    /**
     * Renvoie le résultat de tous les comparateurs passé en paramètre de classe
     * @param s1 : premier stage à comparer au second
     * @param s2 : deuxième stage à comparer au premier
     * @return le résultat int du trie
     */
    public int compare(Stage s1, Stage s2) {
        for (Comparator<Stage> comparator : listComparators) {
            int result = comparator.compare(s1, s2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
}
