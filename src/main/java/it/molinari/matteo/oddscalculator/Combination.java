package it.molinari.matteo.oddscalculator;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Combination {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private String card1;
    private String card2;
    private String card3;
    private String card4;
    private String card5;
    private String card6;
    private String card7;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    Combination(Card card1) {
        this.card1 = card1.toString();
    }
    Combination(Card card1, Card card2, Card card3, Card card4, Card card5, Card card6, Card card7) {
        this.card1 = card1.toString();
        this.card2 = card2.toString();
        this.card3 = card3.toString();
        this.card4 = card4.toString();
        this.card5 = card5.toString();
        this.card6 = card6.toString();
        this.card7 = card7.toString();
    }

    @Override
    public String toString() {
        return "Combination{" +
                card1 +
                ", " + card2 +
                ", " + card3 +
                ", " + card4 +
                ", " + card5 +
                ", " + card6 +
                ", " + card7 +
                '}';
    }

    @Override
    protected Combination clone() {
        Combination result = new Combination();
        result.card1 = this.card1;
        result.card2 = this.card2;
        result.card3 = this.card3;
        result.card4 = this.card4;
        result.card5 = this.card5;
        result.card6 = this.card6;
        result.card7 = this.card7;
        return result;
    }

    public boolean contains(Card card) {
        return Objects.equals(card1, card.toString()) ||
            Objects.equals(card2, card.toString()) ||
            Objects.equals(card3, card.toString()) ||
            Objects.equals(card4, card.toString()) ||
            Objects.equals(card5, card.toString()) ||
            Objects.equals(card6, card.toString()) ||
            Objects.equals(card7, card.toString());
    }

    public Combination newCombinationWithCard(Card card) {
        Combination result = this.clone();
        if(result.card1 == null) {
            result.card1 = card.toString();
        } else if(result.card2 == null) {
            result.card2 = card.toString();
        } else if(result.card3 == null) {
            result.card3 = card.toString();
        } else if(result.card4 == null) {
            result.card4 = card.toString();
        } else if(result.card5 == null) {
            result.card5 = card.toString();
        } else if(result.card6 == null) {
            result.card6 = card.toString();
        } else if(result.card7 == null) {
            result.card7 = card.toString();
        } else {
           throw new RuntimeException("The combination is already full.");
        }
        return result;
    }

}
