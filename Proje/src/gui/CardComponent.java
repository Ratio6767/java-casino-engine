package gui;

import logic.Card;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class CardComponent extends JPanel {

    private Card card;
    private boolean isHidden;

    // Dimensions: Taller to allow breathing room
    private final int CARD_WIDTH = 140;
    private final int CARD_HEIGHT = 200;

    public CardComponent(Card card) {
        this.card = card;
        this.isHidden = false;
        this.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        this.setOpaque(false);
    }

    public CardComponent() { // Hidden card constructor
        this.isHidden = true;
        this.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        this.setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // High quality rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Draw Card Background (The "White Part")
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(2, 2, w - 4, h - 4, 16, 16);
        g2.setColor(Color.LIGHT_GRAY); // Subtle border
        g2.drawRoundRect(2, 2, w - 4, h - 4, 16, 16);

        // 2. Draw Back if hidden
        if (isHidden) {
            drawBackPattern(g2, w, h);
            return;
        }

        // 3. Setup Colors
        String suit = getSuitName(card.toString());
        String rank = getRankName(card.toString());
        String symbol = getSuitSymbol(suit);

        if (suit.equalsIgnoreCase("Hearts") || suit.equalsIgnoreCase("Diamonds")) {
            g2.setColor(new Color(220, 20, 60)); // Crimson Red
        } else {
            g2.setColor(Color.BLACK);
        }

        // 4. Draw Corners (Rank & Small Symbol)
        drawCorners(g2, w, h, rank, symbol);

        // 5. Draw Center Pips (The shapes in the middle)
        if (isFaceCard(rank)) {
            drawFaceCard(g2, w, h, rank, symbol);
        } else {
            drawStandardPips(g2, w, h, getRankValue(rank), symbol);
        }
    }

    // --- DRAWING HELPERS ---

    private void drawCorners(Graphics2D g2, int w, int h, String rank, String symbol) {
        // Corner Font
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));

        // Top Left
        g2.drawString(rank, 12, 30);
        g2.drawString(symbol, 12, 52);

        // Bottom Right
        g2.drawString(rank, w - 30, h - 30);
        g2.drawString(symbol, w - 30, h - 10);
    }

    private void drawFaceCard(Graphics2D g2, int w, int h, String rank, String symbol) {
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(35, 45, w - 70, h - 90); // Inner box

        // Crown Drawing
        int cx = w / 2;
        int cy = h / 2 - 20;

        Path2D crown = new Path2D.Double();
        crown.moveTo(cx - 25, cy + 15);
        crown.lineTo(cx - 30, cy - 20);
        crown.lineTo(cx - 12, cy);
        crown.lineTo(cx, cy - 25);
        crown.lineTo(cx + 12, cy);
        crown.lineTo(cx + 30, cy - 20);
        crown.lineTo(cx + 25, cy + 15);
        crown.closePath();

        g2.fill(crown);

        // Large Center Symbol
        g2.setFont(new Font("Serif", Font.PLAIN, 60));
        FontMetrics fm = g2.getFontMetrics();
        int sw = fm.stringWidth(symbol);
        g2.drawString(symbol, cx - sw / 2, cy + 70);
    }

    private void drawStandardPips(Graphics2D g2, int w, int h, int val, String symbol) {
        // Reduced font size slightly so 9 and 10 don't overlap
        g2.setFont(new Font("SansSerif", Font.BOLD, 26));
        FontMetrics fm = g2.getFontMetrics();
        int fw = fm.stringWidth(symbol);

        // --- Columns ---
        int xL = 35 - fw/2;      // Left
        int xR = w - 35 - fw/2;  // Right
        int xC = w / 2 - fw/2;   // Center

        // --- Rows (Calculated to fit 140x200 cleanly) ---
        int yTop = 60;
        int yBot = 140;
        int yCtr = 100;          // Absolute Center (height / 2)

        // Mid-points for 9 and 10
        // We space them 30px apart approx
        int yUpperMid = 85;
        int yLowerMid = 115;

        switch (val) {
            case 1: draw(g2, symbol, xC, yCtr); break;
            case 2: draw(g2, symbol, xC, yTop); draw(g2, symbol, xC, yBot); break;
            case 3: draw(g2, symbol, xC, yTop); draw(g2, symbol, xC, yCtr); draw(g2, symbol, xC, yBot); break;
            case 4: draw(g2, symbol, xL, yTop); draw(g2, symbol, xR, yTop); draw(g2, symbol, xL, yBot); draw(g2, symbol, xR, yBot); break;
            case 5: draw(g2, symbol, xL, yTop); draw(g2, symbol, xR, yTop); draw(g2, symbol, xL, yBot); draw(g2, symbol, xR, yBot); draw(g2, symbol, xC, yCtr); break;
            case 6: draw(g2, symbol, xL, yTop); draw(g2, symbol, xR, yTop); draw(g2, symbol, xL, yCtr); draw(g2, symbol, xR, yCtr); draw(g2, symbol, xL, yBot); draw(g2, symbol, xR, yBot); break;
            case 7: draw(g2, symbol, xL, yTop); draw(g2, symbol, xR, yTop); draw(g2, symbol, xL, yCtr); draw(g2, symbol, xR, yCtr); draw(g2, symbol, xL, yBot); draw(g2, symbol, xR, yBot); draw(g2, symbol, xC, yUpperMid); break;
            case 8: draw(g2, symbol, xL, yTop); draw(g2, symbol, xR, yTop); draw(g2, symbol, xL, yCtr); draw(g2, symbol, xR, yCtr); draw(g2, symbol, xL, yBot); draw(g2, symbol, xR, yBot); draw(g2, symbol, xC, yUpperMid); draw(g2, symbol, xC, yLowerMid); break;

            // FIXED 9: Uses yTop, yUpperMid, yLowerMid, yBot (Evenly spaced)
            case 9:
                draw(g2, symbol, xL, yTop); draw(g2, symbol, xR, yTop);
                draw(g2, symbol, xL, yUpperMid); draw(g2, symbol, xR, yUpperMid);
                draw(g2, symbol, xL, yLowerMid); draw(g2, symbol, xR, yLowerMid);
                draw(g2, symbol, xL, yBot); draw(g2, symbol, xR, yBot);
                draw(g2, symbol, xC, yCtr);
                break;

            // FIXED 10:
            case 10:
                draw(g2, symbol, xL, yTop); draw(g2, symbol, xR, yTop);
                draw(g2, symbol, xL, yUpperMid); draw(g2, symbol, xR, yUpperMid);
                draw(g2, symbol, xL, yLowerMid); draw(g2, symbol, xR, yLowerMid);
                draw(g2, symbol, xL, yBot); draw(g2, symbol, xR, yBot);
                draw(g2, symbol, xC, yUpperMid - 10); // Slightly adjusted for center
                draw(g2, symbol, xC, yLowerMid + 10);
                break;
        }
    }

    private void draw(Graphics2D g2, String s, int x, int y) {
        g2.drawString(s, x, y);
    }

    private void drawBackPattern(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(150, 0, 0));
        g2.fillRect(5, 5, w - 10, h - 10);
        g2.setColor(Color.WHITE);
        for(int i=5; i<w-5; i+=10) g2.drawLine(i, 5, i, h-5);
    }

    // --- PARSING ---
    private String getSuitName(String text) { return text.split(" of ")[1]; }
    private String getRankName(String text) {
        String r = text.split(" of ")[0];
        if (Character.isDigit(r.charAt(0))) return r;
        return r.substring(0, 1);
    }
    private String getSuitSymbol(String suit) {
        switch (suit.toLowerCase()) {
            case "hearts": return "♥"; case "diamonds": return "♦";
            case "clubs": return "♣"; case "spades": return "♠"; default: return "?";
        }
    }
    private boolean isFaceCard(String rank) {
        return "J".equals(rank) || "Q".equals(rank) || "K".equals(rank);
    }
    private int getRankValue(String rank) {
        if (Character.isDigit(rank.charAt(0))) {
            int val = Integer.parseInt(rank);
            return (val == 10) ? 10 : val;
        }
        if (rank.equals("A")) return 1;
        return 0; // Face cards handled separately
    }
}