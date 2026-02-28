package logic;

import java.util.Random;

public class Roulette extends CasinoGame {

    private int winningNumber; // 0 to 36, where 37 represents "00"
    private String winningColor;

    // Bet Info
    private String betType;   // Number, Color, Parity, Range, Dozen, Column
    private String betValue;  //  "Red", "Even", "1st 12", "Row 1"

    public Roulette(double startBalance) {
        super(startBalance);
    }

    public void setBetType(String type, String value) {
        this.betType = type;
        this.betValue = value;
    }

    @Override
    public void startRound() {
        // 0-36 are standard. We treat 37 as "00".
        Random rand = new Random();
        winningNumber = rand.nextInt(38); // 0 to 37

        // Determine Color
        if (winningNumber == 0 || winningNumber == 37) {
            winningColor = "Green";
        } else if ((winningNumber >= 1 && winningNumber <= 10) || (winningNumber >= 19 && winningNumber <= 28)) {
            winningColor = (winningNumber % 2 != 0) ? "Red" : "Black";
        } else {
            winningColor = (winningNumber % 2 != 0) ? "Black" : "Red";
        }
    }

    @Override
    public double distributeWinnings(boolean unused, double unusedOdds) {
        double winnings = 0;
        int num = winningNumber;

        // If 0 or 00 hits, usually all outside bets lose
        boolean isZero = (num == 0 || num == 37);

        switch (betType) {
            case "Number":
                // If betting on "00", value is "00". If "0", value "0".
                if (betValue.equals("00") && num == 37) winnings = currentBet * 35;
                else if (!betValue.equals("00") && Integer.parseInt(betValue) == num) winnings = currentBet * 35;
                break;

            case "Color":
                if (!isZero && betValue.equalsIgnoreCase(winningColor)) winnings = currentBet * 1;
                break;

            case "Parity": // Even / Odd
                if (!isZero) {
                    if (betValue.equals("Even") && num % 2 == 0) winnings = currentBet * 1;
                    if (betValue.equals("Odd") && num % 2 != 0) winnings = currentBet * 1;
                }
                break;

            case "Range": // 1-18 (Low) or 19-36 (High)
                if (!isZero) {
                    if (betValue.equals("Low") && num <= 18) winnings = currentBet * 1;
                    if (betValue.equals("High") && num >= 19) winnings = currentBet * 1;
                }
                break;

            case "Dozen": // 1st 12, 2nd 12, 3rd 12
                if (!isZero) {
                    if (betValue.equals("1st") && num <= 12) winnings = currentBet * 3;
                    else if (betValue.equals("2nd") && num >= 13 && num <= 24) winnings = currentBet * 3;
                    else if (betValue.equals("3rd") && num >= 25) winnings = currentBet * 3;
                }
                break;

            case "Column": // Top(3,6..), Middle(2,5..), Bottom(1,4..)
                if (!isZero) {
                    // Col 1 (Bottom row in UI) is 1, 4, 7 -> num % 3 == 1
                    // Col 2 (Middle row in UI) is 2, 5, 8 -> num % 3 == 2
                    // Col 3 (Top row in UI) is 3, 6, 9 -> num % 3 == 0
                    if (betValue.equals("Col1") && num % 3 == 1) winnings = currentBet * 2;
                    if (betValue.equals("Col2") && num % 3 == 2) winnings = currentBet * 2;
                    if (betValue.equals("Col3") && num % 3 == 0) winnings = currentBet * 2;
                }
                break;
        }

        if (winnings > 0) {
            balance += (currentBet + winnings);
            return winnings;
        }
        return 0;
    }

    @Override
    public void resetGame() {
        winningNumber = -1;
        winningColor = "";
    }

    public String getWinningNumberString() {
        if (winningNumber == 37) return "00";
        return String.valueOf(winningNumber);
    }

    public int getWinningNumberInt() { return winningNumber; }
    public String getWinningColor() { return winningColor; }
}