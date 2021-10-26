package ca.qc.bdeb.c5gm.stageplanif;

import java.util.ArrayList;

public class StockageConnu {
    private static ArrayList<Stage> stages = new ArrayList<>();

    public static ArrayList<Stage> getStages() {
        return stages;
    }

    public static void setStages(ArrayList<Stage> stages) {
        StockageConnu.stages = stages;
    }
}
