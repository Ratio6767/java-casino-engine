package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel {

    public MenuPanel(ActionListener playBlackjack, ActionListener playRoulette) {
        this.setLayout(new GridBagLayout()); // Centers everything

        JButton btnBlackjack = new JButton("Play Blackjack");
        JButton btnRoulette = new JButton("Play Roulette");

        // Make buttons big
        btnBlackjack.setPreferredSize(new Dimension(200, 50));
        btnRoulette.setPreferredSize(new Dimension(200, 50));

        // Add action listeners (what happens when clicked)
        btnBlackjack.addActionListener(playBlackjack);
        btnRoulette.addActionListener(playRoulette);

        this.add(btnBlackjack);
        this.add(Box.createRigidArea(new Dimension(20, 0))); // Space between
        this.add(btnRoulette);
    }
}