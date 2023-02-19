package it.molinari.matteo.oddscalculator;

import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class Card {

    private final SeedsEnum seed;
    private final CardValuesEnum value;

    @Override
    public String toString() {
        return seed.label + value.label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return seed == card.seed && value == card.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(seed, value);
    }
}
