package main;

import gui.Window; // Import the new name

public class Main {
    public static void main(String[] args) {
        // Run GUI in the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            new Window(); // Create the Window
        });
    }
}