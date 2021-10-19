package ca.qc.bdeb.c5gm.stageplanif;

public enum TypeComptes {
    ADMIN(0),
    PROF(1),
    ELEVE(2);

    private final int value;
    private TypeComptes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
