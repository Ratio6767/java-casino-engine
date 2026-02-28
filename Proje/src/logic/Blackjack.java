package logic;

import java.util.ArrayList;

public class Blackjack extends CasinoGame {

    private Deck deck;


    public int playerTotal;
    public int dealerTotal;
    public ArrayList<Card> playerHand;
    public ArrayList<Card> dealerHand;


    public Blackjack(double startBalance) {
        super(startBalance);
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
    }

    @Override
    public void startRound() {
        deck = new Deck();
        deck.shuffle();


        playerHand.clear();
        dealerHand.clear();
        playerTotal = 0;
        dealerTotal = 0;


        hitPlayer();
        hitPlayer();
        hitDealer();
    }

    @Override
    public void resetGame() {
        playerTotal = 0;
        dealerTotal = 0;
        playerHand.clear();
        dealerHand.clear();
    }

    public void hitPlayer() {
        Card c = deck.drawCard();
        playerHand.add(c);
        playerTotal += c.getValue();


        if (playerTotal > 21 && c.getValue() == 11) {
            playerTotal -= 10;
        }
    }

    public void hitDealer() {
        Card c = deck.drawCard();
        dealerHand.add(c); // <--- This saves the card so the GUI can draw it
        dealerTotal += c.getValue();


        if (dealerTotal > 21 && c.getValue() == 11) {
            dealerTotal -= 10;
        }
    }
}