package gui;

import logic.Blackjack;
import logic.Roulette;
import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel; // Holds all the screens

    public Window() {
        this.setTitle("Java Casino Project");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // Center on screen

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // --- Create Game Logic Instances ---
        Blackjack blackjackLogic = new Blackjack(1000);
        Roulette rouletteLogic = new Roulette(1000);

        // --- Create Screens ---

        // 1. Menu Screen
        MenuPanel menu = new MenuPanel(
                e -> cardLayout.show(mainPanel, "Blackjack"), // Action for btn 1
                e -> cardLayout.show(mainPanel, "Roulette")   // Action for btn 2
        );

        // 2. Blackjack Screen
        BlackjackPanel blackjackPanel = new BlackjackPanel(
                blackjackLogic,
                e -> cardLayout.show(mainPanel, "Menu") // Back button action
        );

        // 3. Roulette Screen
        RoulettePanel roulettePanel = new RoulettePanel(
                rouletteLogic,
                e -> cardLayout.show(mainPanel, "Menu") // Back button action
        );

        // Add screens to the card layout
        mainPanel.add(menu, "Menu");
        mainPanel.add(blackjackPanel, "Blackjack");
        mainPanel.add(roulettePanel, "Roulette");

        this.add(mainPanel);
        this.setVisible(true);
    }
}