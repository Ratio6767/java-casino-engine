package gui;

import logic.CasinoGame;
import logic.Roulette;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

public class RoulettePanel extends GamePanel {

    private Roulette rouletteLogic;

    // UI Components
    private WheelComponent wheelDisplay;
    private JLabel statusLabel;
    private JLabel selectedBetLabel;
    private JTextField betInput;
    private JButton btnSpin;

    // Betting State
    private String currentBetType = "";
    private String currentBetValue = "";
    private JButton lastSelectedButton = null;
    private final Border defaultBorder = UIManager.getBorder("Button.border");
    private final Border selectedBorder = BorderFactory.createLineBorder(Color.YELLOW, 3);

    public RoulettePanel(CasinoGame game, ActionListener backAction) {
        super(game, backAction);
        this.rouletteLogic = (Roulette) game;
        this.setBackground(new Color(20, 20, 20));

        // --- MAIN LAYOUT ---
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // 1. LEFT SIDE: Wheel
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.45; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 20, 20, 10);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        wheelDisplay = new WheelComponent();
        leftPanel.add(wheelDisplay, BorderLayout.CENTER);
        mainContent.add(leftPanel, gbc);

        // 2. RIGHT SIDE: Betting Table
        gbc.gridx = 1;
        gbc.weightx = 0.55;
        gbc.insets = new Insets(20, 10, 20, 20);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        selectedBetLabel = new JLabel("Selected: None");
        selectedBetLabel.setForeground(Color.WHITE);
        selectedBetLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        selectedBetLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectedBetLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        rightPanel.add(selectedBetLabel, BorderLayout.NORTH);

        JPanel bettingGrid = createFullBettingTable();
        rightPanel.add(bettingGrid, BorderLayout.CENTER);

        mainContent.add(rightPanel, gbc);
        this.add(mainContent, BorderLayout.CENTER);

        // --- BOTTOM: Controls ---
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(40, 40, 40));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statusLabel = new JLabel("Place your bet!");
        statusLabel.setForeground(Color.YELLOW);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        betInput = new JTextField("100", 5);
        betInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnSpin = new JButton("SPIN");
        btnSpin.setBackground(new Color(255, 215, 0));
        btnSpin.setFocusPainted(false);
        btnSpin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSpin.addActionListener(e -> startSpinSequence());

        controlPanel.add(new JLabel("Bet: ") {{ setForeground(Color.WHITE); setFont(new Font("Segoe UI", Font.BOLD, 14)); }});
        controlPanel.add(betInput);
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        controlPanel.add(btnSpin);
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        controlPanel.add(statusLabel);

        this.add(controlPanel, BorderLayout.SOUTH);
    }

    private JPanel createFullBettingTable() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;

        Font numFont = new Font("Segoe UI", Font.BOLD, 14);
        Font textFont = new Font("Segoe UI", Font.BOLD, 10);
        Color green = new Color(0, 100, 0);
        Color red = new Color(200, 0, 0);
        Color black = Color.BLACK;

        // Define Gaps: Standard spacing vs. Gap spacing
        Insets stdInsets = new Insets(1, 1, 1, 1);
        Insets gapInsets = new Insets(1, 15, 1, 1); // 15px gap on the left

        // --- 1. DOZENS (TOP ROW) ---
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 4; // Each spans 4 columns

        // 1st 12 (No Gap)
        JButton doz1 = createBetButton("1st 12", green, numFont);
        doz1.addActionListener(e -> selectBet(doz1, "Dozen", "1st"));
        gbc.gridx = 1;
        gbc.insets = stdInsets;
        panel.add(doz1, gbc);

        // 2nd 12 (Add Gap)
        JButton doz2 = createBetButton("2nd 12", green, numFont);
        doz2.addActionListener(e -> selectBet(doz2, "Dozen", "2nd"));
        gbc.gridx = 5;
        gbc.insets = gapInsets; // <--- GAP HERE
        panel.add(doz2, gbc);

        // 3rd 12 (Add Gap)
        JButton doz3 = createBetButton("3rd 12", green, numFont);
        doz3.addActionListener(e -> selectBet(doz3, "Dozen", "3rd"));
        gbc.gridx = 9;
        gbc.insets = gapInsets; // <--- GAP HERE
        panel.add(doz3, gbc);

        // --- 2. ZERO & DOUBLE ZERO ---
        gbc.gridwidth = 1;
        gbc.insets = stdInsets; // Reset to standard

        JButton zeroBtn = createBetButton("0", green, numFont);
        zeroBtn.addActionListener(e -> selectBet(zeroBtn, "Number", "0"));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridheight = 1;
        panel.add(zeroBtn, gbc);

        JButton doubleZeroBtn = createBetButton("00", green, numFont);
        doubleZeroBtn.addActionListener(e -> selectBet(doubleZeroBtn, "Number", "00"));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridheight = 2;
        panel.add(doubleZeroBtn, gbc);

        // --- 3. NUMBERS 1-36 ---
        gbc.gridheight = 1;
        for (int col = 0; col < 12; col++) {
            // Apply GAP if this is the start of the 2nd (col 4) or 3rd (col 8) dozen block
            if (col == 4 || col == 8) {
                gbc.insets = gapInsets;
            } else {
                gbc.insets = stdInsets;
            }

            for (int row = 0; row < 3; row++) {
                int num = 3 * (col + 1) - row;
                Color bg = getRouletteColor(num).equals("Red") ? red : black;
                JButton btn = createBetButton(String.valueOf(num), bg, numFont);
                final String val = String.valueOf(num);
                btn.addActionListener(e -> selectBet(btn, "Number", val));

                gbc.gridx = col + 1;
                gbc.gridy = row + 1;
                panel.add(btn, gbc);
            }
        }

        // --- 4. COLUMNS ---
        gbc.gridx = 13;
        gbc.insets = stdInsets; // Reset to standard for the far right column
        String[] colNames = {"Col3", "Col2", "Col1"};
        for (int row = 0; row < 3; row++) {
            JButton btn = createBetButton("2 to 1", green, textFont);
            final String cName = colNames[row];
            btn.addActionListener(e -> selectBet(btn, "Column", cName));
            gbc.gridy = row + 1;
            panel.add(btn, gbc);
        }

        // --- 5. OUTSIDE BETS (BOTTOM) ---
        gbc.gridy = 4;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;

        // Group 1: 1-18 & Even (Under 1st 12) - No Left Gap
        gbc.insets = stdInsets;
        JButton lowBtn = createBetButton("1-18", green, numFont);
        lowBtn.addActionListener(e -> selectBet(lowBtn, "Range", "Low"));
        gbc.gridx = 1; panel.add(lowBtn, gbc);

        JButton evenBtn = createBetButton("EVEN", green, numFont);
        evenBtn.addActionListener(e -> selectBet(evenBtn, "Parity", "Even"));
        gbc.gridx = 3; panel.add(evenBtn, gbc);

        // Group 2: Red & Black (Under 2nd 12) - Add Left Gap
        gbc.insets = gapInsets;
        JButton redBtn = createBetButton("RED", red, numFont);
        redBtn.addActionListener(e -> selectBet(redBtn, "Color", "Red"));
        gbc.gridx = 5; panel.add(redBtn, gbc);

        gbc.insets = stdInsets; // Reset for the one next to it
        JButton blackBtn = createBetButton("BLACK", black, numFont);
        blackBtn.addActionListener(e -> selectBet(blackBtn, "Color", "Black"));
        gbc.gridx = 7; panel.add(blackBtn, gbc);

        // Group 3: Odd & 19-36 (Under 3rd 12) - Add Left Gap
        gbc.insets = gapInsets;
        JButton oddBtn = createBetButton("ODD", green, numFont);
        oddBtn.addActionListener(e -> selectBet(oddBtn, "Parity", "Odd"));
        gbc.gridx = 9; panel.add(oddBtn, gbc);

        gbc.insets = stdInsets; // Reset
        JButton highBtn = createBetButton("19-36", green, numFont);
        highBtn.addActionListener(e -> selectBet(highBtn, "Range", "High"));
        gbc.gridx = 11; panel.add(highBtn, gbc);

        return panel;
    }

    private JButton createBetButton(String text, Color bg, Font font) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(font);
        btn.setBorder(defaultBorder);
        return btn;
    }

    private void selectBet(JButton btn, String type, String value) {
        if (lastSelectedButton != null) lastSelectedButton.setBorder(defaultBorder);
        currentBetType = type;
        currentBetValue = value;
        lastSelectedButton = btn;
        btn.setBorder(selectedBorder);
        selectedBetLabel.setText("Selected: " + value + " (" + type + ")");
    }

    private void startSpinSequence() {
        if (currentBetType.isEmpty()) {
            statusLabel.setText("Please select a bet!");
            return;
        }
        if (rouletteLogic.getBalance() <= 0) {
            statusLabel.setText("Game Over!");
            return;
        }

        try {
            double bet = Double.parseDouble(betInput.getText());
            if (rouletteLogic.placeBet(bet)) {
                rouletteLogic.setBetType(currentBetType, currentBetValue);
                updateBalance();

                btnSpin.setEnabled(false);
                betInput.setEnabled(false);
                statusLabel.setText("Spinning...");

                // 1. Calculate Result
                rouletteLogic.startRound();
                int winNumInt = rouletteLogic.getWinningNumberInt();

                // 2. Animate
                wheelDisplay.spin(winNumInt, () -> {
                    // Delay before showing text result
                    Timer delayTimer = new Timer(1500, evt -> finishRound());
                    delayTimer.setRepeats(false);
                    delayTimer.start();
                });

            } else {
                statusLabel.setText("Insufficient Funds!");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Enter valid bet!");
        }
    }

    private void finishRound() {
        String winNum = rouletteLogic.getWinningNumberString();
        String winColor = rouletteLogic.getWinningColor();

        wheelDisplay.showResultText(winNum, winColor);

        double won = rouletteLogic.distributeWinnings(false, 0);

        if (won > 0) {
            statusLabel.setText(String.format("Result: %s (%s). WON $%.2f!", winNum, winColor, won));
            statusLabel.setForeground(Color.GREEN);
        } else {
            statusLabel.setText(String.format("Result: %s (%s). You lost.", winNum, winColor));
            statusLabel.setForeground(Color.RED);
        }

        updateBalance();
        btnSpin.setEnabled(true);
        betInput.setEnabled(true);
    }

    private String getRouletteColor(int num) {
        if (num == 0 || num == 37) return "Green";
        if ((num >= 1 && num <= 10) || (num >= 19 && num <= 28)) {
            return (num % 2 != 0) ? "Red" : "Black";
        } else {
            return (num % 2 != 0) ? "Black" : "Red";
        }
    }

    // --- WHEEL COMPONENT ---
    private class WheelComponent extends JPanel {
        private String centerText = "SPIN";
        private Color centerColor = new Color(50, 50, 50);

        private Timer animationTimer;
        private double ballAngle = 0;
        private double ballSpeed = 0;
        private boolean isSpinning = false;
        private boolean isBallVisible = false;
        private Runnable onFinishCallback;
        private double targetAngle = 0;

        // American Sequence (Standard)
        private final int[] wheelNumbers = {
                0, 28, 9, 26, 30, 11, 7, 20, 32, 17, 5, 22, 34, 15, 3, 24, 36, 13, 1,
                37, 27, 10, 25, 29, 12, 8, 19, 31, 18, 6, 21, 33, 16, 4, 23, 35, 14, 2
        };

        public WheelComponent() {
            animationTimer = new Timer(20, e -> updateAnim());
        }

        public void spin(int winningNumberInt, Runnable onFinish) {
            this.onFinishCallback = onFinish;
            this.isSpinning = true;
            this.isBallVisible = true;
            this.centerText = "";
            this.centerColor = new Color(50, 50, 50);

            // Calculate precise target angle
            this.targetAngle = getTargetAngleForNumber(winningNumberInt);

            // Random start
            ballAngle = Math.random() * Math.PI * 2;

            ballSpeed = 0.35;
            animationTimer.start();
        }

        private double getTargetAngleForNumber(int num) {
            int index = -1;
            for (int i=0; i<wheelNumbers.length; i++) {
                if (wheelNumbers[i] == num) {
                    index = i;
                    break;
                }
            }
            // FIXED CALCULATION:
            // Arc 90 deg (Top) is where Index 0 lives.
            // Clockwise = Subtracting from Arc Angle.
            // Ball Drawing (Math.cos/sin) uses Inverted Y, so Clockwise = Adding to Math Angle.
            // Index 0 Center: Arc 90. Math -90.
            // Index 1 Center: Arc 90 - Step. Math -90 + Step.

            double step = 360.0 / 38.0;
            // Target Math Angle = -90 (Top) + (Index * Step)
            double deg = -90.0 + (index * step);

            return Math.toRadians(deg);
        }

        private void updateAnim() {
            if (!isSpinning) return;

            // Move ball Clockwise
            ballAngle += ballSpeed;

            // Friction
            ballSpeed *= 0.99;

            // Crawl Logic
            if (ballSpeed < 0.04) {
                ballSpeed = 0.04;

                double normBall = normalizeAngle(ballAngle);
                double normTarget = normalizeAngle(targetAngle);
                double diff = Math.abs(normBall - normTarget);
                if (diff > Math.PI) diff = (2*Math.PI) - diff;

                if (diff < 0.05) {
                    ballAngle = targetAngle; // Snap
                    animationTimer.stop();
                    isSpinning = false;
                    // isBallVisible remains true
                    if (onFinishCallback != null) onFinishCallback.run();
                }
            }
            repaint();
        }

        private double normalizeAngle(double angle) {
            double a = angle % (2 * Math.PI);
            if (a < 0) a += 2 * Math.PI;
            return a;
        }

        public void showResultText(String numStr, String colName) {
            this.centerText = numStr;
            if (colName.equals("Red")) this.centerColor = new Color(200, 0, 0);
            else if (colName.equals("Black")) this.centerColor = Color.BLACK;
            else this.centerColor = new Color(0, 100, 0);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int diameter = Math.min(w, h) - 20;
            int radius = diameter / 2;
            int cx = w / 2;
            int cy = h / 2;

            // Gold Outer Ring
            g2.setColor(new Color(218, 165, 32));
            g2.fillOval(cx - radius - 5, cy - radius - 5, diameter + 10, diameter + 10);

            // Draw Wedges
            double arcAngle = 360.0 / 38;
            // We draw Index 0 centered at Top (90 deg).
            // So start angle is 90 + halfWedge.
            double currentAngle = 90 + (arcAngle / 2);

            for (int num : wheelNumbers) {
                String colorName = getRouletteColor(num);
                if (colorName.equals("Red")) g2.setColor(new Color(200, 0, 0));
                else if (colorName.equals("Black")) g2.setColor(Color.BLACK);
                else g2.setColor(new Color(0, 100, 0));

                // Draw wedge Clockwise (Negative angle)
                g2.fillArc(cx - radius, cy - radius, diameter, diameter, (int)Math.round(currentAngle), (int)Math.ceil(-arcAngle) - 1);

                // Draw Number
                double midAngleArc = currentAngle - (arcAngle/2.0);
                // Convert Arc Angle (CCW) to Math Angle (Inverted Y)
                // Math = -Arc.
                double midAngleMath = Math.toRadians(-midAngleArc);

                int tx = cx + (int)((radius * 0.85) * Math.cos(midAngleMath));
                int ty = cy + (int)((radius * 0.85) * Math.sin(midAngleMath));

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                String s = (num == 37) ? "00" : String.valueOf(num);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(s, tx - fm.stringWidth(s)/2, ty + fm.getAscent()/2 - 2);

                currentAngle -= arcAngle;
            }

            int innerDia = diameter / 2;
            g2.setColor(centerColor);
            g2.fillOval(cx - innerDia/2, cy - innerDia/2, innerDia, innerDia);
            g2.setColor(new Color(218, 165, 32));
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(cx - innerDia/2, cy - innerDia/2, innerDia, innerDia);

            if (!centerText.isEmpty()) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Serif", Font.BOLD, innerDia / 3));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(centerText, cx - fm.stringWidth(centerText)/2, cy + fm.getAscent()/3);
            }

            // Draw Ball
            if (isBallVisible) {
                int ballSize = 12;
                double trackRadius = radius * 0.7;
                int bx = cx + (int)(trackRadius * Math.cos(ballAngle));
                int by = cy + (int)(trackRadius * Math.sin(ballAngle));

                g2.setColor(Color.WHITE);
                g2.fillOval(bx - ballSize/2, by - ballSize/2, ballSize, ballSize);
                g2.setColor(Color.GRAY);
                g2.drawOval(bx - ballSize/2, by - ballSize/2, ballSize, ballSize);
            }
        }
    }
}