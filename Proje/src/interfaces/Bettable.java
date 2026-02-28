package interfaces;

public interface Bettable {
    // Defines that a game must be able to place a bet
    boolean placeBet(double amount);

    // Defines that a game must be able to payout
    double distributeWinnings(boolean playerWon, double odds);

    // Defines that we need to see the balance
    double getBalance();
}