package logic;

import interfaces.Bettable;

public abstract class CasinoGame implements Bettable {

    protected double balance;
    protected double currentBet;

    public CasinoGame(double startBalance) {
        this.balance = startBalance;
    }



    @Override
    public boolean placeBet(double amount) {   //interface polymorphism
        if (amount > 0 && amount <= balance) {
            this.currentBet = amount;
            this.balance -= amount;
            return true;
        }
        return false;
    }

    @Override
    public double distributeWinnings(boolean playerWon, double odds) {
        if (playerWon) {
            double winnings = currentBet * odds;
            this.balance += (currentBet + winnings);
            return winnings;
        }
        return 0;
    }

    @Override
    public double getBalance() {
        return balance;
    }



    public abstract void startRound();
    public abstract void resetGame();
}