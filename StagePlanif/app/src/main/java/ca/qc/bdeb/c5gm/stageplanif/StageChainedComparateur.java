package ca.qc.bdeb.c5gm.stageplanif;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class StageChainedComparateur implements Comparator<Stage> {
    private List<Comparator<Stage>> listComparators;

    public StageChainedComparateur(Comparator<Stage>... comparators) {
        this.listComparators = Arrays.asList(comparators);
    }

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
