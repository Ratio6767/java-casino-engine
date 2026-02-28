package logic;

public class Card {
    private String suit;  // "Hearts"
    private String rank;  // "King"
    private int value;    // 10

    public Card(String suit, String rank, int value) {
        this.suit = suit;
        this.rank = rank;               //constructor
        this.value = value;
    }

    public int getValue() {
        return value;                   //getter
    }

    @Override
    public String toString() {
        return rank + " of " + suit;   // king of hearts
    }
}