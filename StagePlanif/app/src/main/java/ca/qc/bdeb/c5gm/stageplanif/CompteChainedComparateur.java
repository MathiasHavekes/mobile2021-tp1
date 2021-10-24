package ca.qc.bdeb.c5gm.stageplanif;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class CompteChainedComparateur implements Comparator<Compte> {
    private List<Comparator<Compte>> listComparators;

    public CompteChainedComparateur(Comparator<Compte>... comparators) {
        this.listComparators = Arrays.asList(comparators);
    }

    public int compare(Compte c1, Compte c2) {
        for (Comparator<Compte> comparator : listComparators) {
            int result = comparator.compare(c1, c2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
}
