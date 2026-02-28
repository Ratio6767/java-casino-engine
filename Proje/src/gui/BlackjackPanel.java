package gui;

import logic.Blackjack;
import logic.Card;
import logic.CasinoGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BlackjackPanel extends GamePanel {

    private Blackjack blackjackLogic;

    // UI Components
    private JPanel dealerCardPanel;
    private JPanel playerCardPanel;
    private JLabel dealerScoreLabel;
    private JLabel playerScoreLabel;
    private JLabel statusLabel;

    private JTextField betInput;
    private JButton btnDeal, btnHit, btnStand, btnSimulate;

    // --- SIMULATION VARIABLES ---
    private Timer simTimer;
    private int simRoundsPlayed;
    private double simStartBalance;
    private final int MAX_SIM_ROUNDS = 100;

    public BlackjackPanel(CasinoGame game, ActionListener backAction) {
        super(game, backAction);

        this.blackjackLogic = (Blackjack) game;
        this.setBackground(new Color(39, 119, 20));

        // --- CENTER: The Table ---
        JPanel tablePanel = new JPanel(new GridLayout(2, 1));
        tablePanel.setOpaque(false);

        // 1. Dealer Area
        JPanel dealerArea = new JPanel(new BorderLayout());
        dealerArea.setOpaque(false);
        dealerScoreLabel = createScoreLabel("Dealer: 0");
        dealerArea.add(dealerScoreLabel, BorderLayout.NORTH);
        dealerCardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        dealerCardPanel.setOpaque(false);
        dealerArea.add(dealerCardPanel, BorderLayout.CENTER);

        // 2. Player Area
        JPanel playerArea = new JPanel(new BorderLayout());
        playerArea.setOpaque(false);
        playerScoreLabel = createScoreLabel("Player: 0");
        playerArea.add(playerScoreLabel, BorderLayout.NORTH);
        playerCardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        playerCardPanel.setOpaque(false);
        playerArea.add(playerCardPanel, BorderLayout.CENTER);

        tablePanel.add(dealerArea);
        tablePanel.add(playerArea);
        this.add(tablePanel, BorderLayout.CENTER);

        // --- SOUTH: Controls ---
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(30, 30, 30));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statusLabel = new JLabel("Place your bet!");
        statusLabel.setForeground(Color.YELLOW);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        betInput = new JTextField("10", 5);
        betInput.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        btnDeal = createButton("Deal");
        btnHit = createButton("Hit");
        btnStand = createButton("Stand");

        // Simulation Button
        btnSimulate = new JButton("Fast Forward (100x)");
        btnSimulate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSimulate.setBackground(new Color(255, 100, 100)); // Red
        btnSimulate.setFocusPainted(false);

        btnHit.setEnabled(false);
        btnStand.setEnabled(false);

        // Actions
        btnDeal.addActionListener(e -> startRound());
        btnHit.addActionListener(e -> playerHit());
        btnStand.addActionListener(e -> playerStand());
        btnSimulate.addActionListener(e -> startVisualSimulation());

        controlPanel.add(new JLabel("Bet: ") {{ setForeground(Color.WHITE); }});
        controlPanel.add(betInput);
        controlPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        controlPanel.add(btnDeal);
        controlPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        controlPanel.add(btnHit);
        controlPanel.add(btnStand);
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        controlPanel.add(btnSimulate);
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        controlPanel.add(statusLabel);

        this.add(controlPanel, BorderLayout.SOUTH);
    }

    // --- VISUAL FAST FORWARD LOGIC ---

    private void startVisualSimulation() {
        // Confirmation
        int response = JOptionPane.showConfirmDialog(this,
                "Watch 100 rounds play automatically.\nYou will see your balance change in real-time.\n\nReady to fast forward?",
                "Simulation Mode", JOptionPane.YES_NO_OPTION);

        if (response != JOptionPane.YES_OPTION) return;

        // Setup
        simRoundsPlayed = 0;
        simStartBalance = blackjackLogic.getBalance();

        // Disable Controls
        btnDeal.setEnabled(false);
        btnHit.setEnabled(false);
        btnStand.setEnabled(false);
        btnSimulate.setEnabled(false);
        betInput.setEnabled(false);
        backButton.setEnabled(false); // Don't leave while running

        // Create Timer (Runs every 100ms)
        simTimer = new Timer(100, e -> playOneSimulatedRound());
        simTimer.start();
    }

    private void playOneSimulatedRound() {
        double bet = 10; // Fixed bet for simulation

        // Stop Condition 1: Broke
        if (blackjackLogic.getBalance() < bet) {
            endSimulation("Broke! You lost all your money.");
            return;
        }

        // Stop Condition 2: 100 Rounds done
        if (simRoundsPlayed >= MAX_SIM_ROUNDS) {
            endSimulation("Simulation Complete (100 Rounds).");
            return;
        }

        // --- PLAY ROUND AUTOMATICALLY ---

        // 1. Place Bet & Deal
        blackjackLogic.placeBet(bet);
        blackjackLogic.startRound();

        // 2. Player Logic (Hit on 16 or less)
        while (blackjackLogic.playerTotal < 17) {
            blackjackLogic.hitPlayer();
        }

        // 3. Dealer Logic (Only if player didn't bust)
        if (blackjackLogic.playerTotal <= 21) {
            while (blackjackLogic.dealerTotal < 17) {
                blackjackLogic.hitDealer();
            }
        }

        // 4. Determine Winner & Payout
        boolean playerWon = false;
        boolean tie = false;

        if (blackjackLogic.playerTotal > 21) {
            // Player Bust (Loss)
        } else if (blackjackLogic.dealerTotal > 21) {
            playerWon = true;
        } else if (blackjackLogic.playerTotal > blackjackLogic.dealerTotal) {
            playerWon = true;
        } else if (blackjackLogic.playerTotal == blackjackLogic.dealerTotal) {
            tie = true;
        }

        if (playerWon) blackjackLogic.distributeWinnings(true, 1.0);
        if (tie) blackjackLogic.distributeWinnings(true, 0);

        // --- UPDATE VISUALS ---
        simRoundsPlayed++;
        updateBalance();
        updateTableUI(true); // Show everything
        statusLabel.setText("Simulating Round " + simRoundsPlayed + "...");

        // Visual flair: Red text if losing money, Green if winning (compared to start)
        if (blackjackLogic.getBalance() < simStartBalance) {
            balanceLabel.setForeground(Color.RED);
        } else {
            balanceLabel.setForeground(Color.GREEN);
        }
    }

    private void endSimulation(String reason) {
        simTimer.stop();

        double endBalance = blackjackLogic.getBalance();
        double profit = endBalance - simStartBalance;

        String msg = String.format("%s\n\nStart: $%.2f\nEnd: $%.2f\nTotal Profit/Loss: $%.2f",
                reason, simStartBalance, endBalance, profit);

        JOptionPane.showMessageDialog(this, msg);

        // Reset Controls
        backButton.setEnabled(true);
        btnDeal.setEnabled(true);
        btnSimulate.setEnabled(true);
        betInput.setEnabled(true);
        balanceLabel.setForeground(new Color(255, 215, 0)); // Reset Gold
        statusLabel.setText("Simulation Finished.");

        if (blackjackLogic.getBalance() <= 0) {
            statusLabel.setText("GAME OVER!");
            statusLabel.setForeground(Color.RED);
            btnDeal.setEnabled(false);
            btnSimulate.setEnabled(false);
        }
    }

    // --- STANDARD GAME LOGIC (Unchanged) ---

    private void startRound() {
        if (blackjackLogic.getBalance() <= 0) return;
        try {
            double bet = Double.parseDouble(betInput.getText());
            if (blackjackLogic.placeBet(bet)) {
                blackjackLogic.startRound();
                updateBalance();
                btnDeal.setEnabled(false);
                btnSimulate.setEnabled(false);
                betInput.setEnabled(false);
                btnHit.setEnabled(true);
                btnStand.setEnabled(true);
                statusLabel.setText("Your turn!");
                statusLabel.setForeground(Color.YELLOW);
                updateTableUI(false);
            } else {
                statusLabel.setText("Insufficient Funds!");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Enter valid number!");
        }
    }

    private void playerHit() {
        blackjackLogic.hitPlayer();
        updateTableUI(false);
        if (blackjackLogic.playerTotal > 21) endRound(false);
    }

    private void playerStand() {
        while (blackjackLogic.dealerTotal < 17) blackjackLogic.hitDealer();
        updateTableUI(true);

        if (blackjackLogic.dealerTotal > 21) endRound(true);
        else if (blackjackLogic.playerTotal > blackjackLogic.dealerTotal) endRound(true);
        else if (blackjackLogic.playerTotal == blackjackLogic.dealerTotal) {
            statusLabel.setText("Push! (Tie)");
            resetControls();
            game.placeBet(-1 * Double.parseDouble(betInput.getText()));
            updateBalance();
        } else endRound(false);
    }

    private void endRound(boolean playerWon) {
        if (playerWon) {
            double won = blackjackLogic.distributeWinnings(true, 1.0);
            statusLabel.setText("You Won $" + won + "!");
            statusLabel.setForeground(Color.GREEN);
        } else {
            statusLabel.setText("You Lost!");
            statusLabel.setForeground(Color.RED);
        }
        updateBalance();
        resetControls();
        if (blackjackLogic.getBalance() <= 0) {
            statusLabel.setText("GAME OVER!");
            statusLabel.setForeground(Color.RED);
            btnDeal.setEnabled(false);
            btnSimulate.setEnabled(false);
        }
    }

    private void resetControls() {
        btnDeal.setEnabled(true);
        btnSimulate.setEnabled(true);
        betInput.setEnabled(true);
        btnHit.setEnabled(false);
        btnStand.setEnabled(false);
    }

    private void updateTableUI(boolean showDealerFull) {
        playerScoreLabel.setText("Player: " + blackjackLogic.playerTotal);
        if (showDealerFull) dealerScoreLabel.setText("Dealer: " + blackjackLogic.dealerTotal);
        else dealerScoreLabel.setText("Dealer: ?");

        playerCardPanel.removeAll();
        dealerCardPanel.removeAll();

        for (Card c : blackjackLogic.playerHand) playerCardPanel.add(new CardComponent(c));

        if (blackjackLogic.dealerHand != null && !blackjackLogic.dealerHand.isEmpty()) {
            for (int i = 0; i < blackjackLogic.dealerHand.size(); i++) {
                if (i == 0 && !showDealerFull) dealerCardPanel.add(new CardComponent());
                else dealerCardPanel.add(new CardComponent(blackjackLogic.dealerHand.get(i)));
            }
        }
        this.revalidate();
        this.repaint();
    }

    private JLabel createScoreLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        return btn;
    }
}