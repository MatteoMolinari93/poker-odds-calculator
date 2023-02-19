package it.molinari.matteo.oddscalculator;

public enum SeedsEnum {

    HEARTS("H"),
    DIAMONDS("D"),
    CLUBS("C"),
    SPADES("S");

    public final String label;

    SeedsEnum(String label) {
        this.label = label;
    }

}
