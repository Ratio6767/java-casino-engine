package gui;

import logic.CasinoGame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public abstract class GamePanel extends JPanel {

    protected CasinoGame game;
    protected JLabel balanceLabel;
    protected JButton backButton;

    public GamePanel(CasinoGame game, ActionListener backAction) {
        this.game = game;

        // 1. Set Layout for the whole screen
        this.setLayout(new BorderLayout());

        // 2. Create the Top Bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(50, 50, 50)); // Dark Gray
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Padding
        // Make sure it is visible
        topPanel.setOpaque(true);

        // 3. Balance Label (Top Left)
        balanceLabel = new JLabel("Balance: $" + game.getBalance());
        balanceLabel.setForeground(new Color(255, 215, 0)); // Gold Text
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        // 4. Back Button (Top Right)
        backButton = new JButton("Exit to Menu");
        backButton.addActionListener(backAction);
        backButton.setFocusPainted(false);
        backButton.setBackground(Color.WHITE);
        backButton.setFont(new Font("SansSerif", Font.BOLD, 12));

        // 5. Add items to the Top Bar
        topPanel.add(balanceLabel, BorderLayout.WEST);
        topPanel.add(backButton, BorderLayout.EAST);

        // 6. Add Top Bar to the main screen
        this.add(topPanel, BorderLayout.NORTH);
    }

    public void updateBalance() {
        balanceLabel.setText("Balance: $" + game.getBalance());
    }
}