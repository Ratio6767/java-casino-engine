package logic;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};

        for (int s = 0; s < suits.length; s++) {
            String suit = suits[s]; // Get the suit at the current index

            for (int i = 0; i < ranks.length; i++) {
                int val = i + 2;
                if (val > 10 && val < 14) val = 10; // Face cards
                if (val == 14) val = 11;            // Ace
                cards.add(new Card(suit, ranks[i], val));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) return null;
        return cards.remove(0);
    }
}