package it.molinari.matteo.oddscalculator;

public enum CardValuesEnum {

    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    J("J"),
    Q("Q"),
    K("K"),
    A("A");

    public final String label;

    CardValuesEnum(String label) {
        this.label = label;
    }

}
