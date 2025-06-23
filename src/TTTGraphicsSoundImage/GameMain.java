package TTTGraphicsSoundImage;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Tic-Tac-Toe: Two-player Graphic version with better OO design.
 * The Board and Cell classes are separated in their own classes.
 */
public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.BLACK;
    public static final Color COLOR_BG_STATUS = Color.BLACK;
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    private Board board;
    private State currentState;
    private Seed currentPlayer;

    private JLabel statusBar;
    private JLabel timerLabel;
    private JLabel scoreLabel;

    private Timer turnTimer;
    private int timeLeft = 10;

    private String player1Name = "PACMAN";
    private String player2Name = "GHOST";
    private int score1 = 0;
    private int score2 = 0;

    public GameMain() {
        board = new Board();

        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                int row = mouseY / Cell.SIZE;
                int col = mouseX / Cell.SIZE;

                if (currentState == State.PLAYING) {
                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {
                        currentState = board.stepGame(currentPlayer, row, col);

                        if (currentState == State.PLAYING) {
                            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                            startTurnTimer();
                            SoundEffect.DIE.play();
                        } else if (currentState == State.DRAW) {
                            stopTurnTimer();
                            SoundEffect.EAT_FOOD.play();
                        } else {
                            stopTurnTimer();
                            SoundEffect.EXPLODE.play();
                            if (currentState == State.CROSS_WON) score1++;
                            if (currentState == State.NOUGHT_WON) score2++;
                        }
                        scoreLabel.setText(player1Name + ": " + score1 + "   |   " + player2Name + ": " + score2);
                    }
                } else {
                    newGame();
                }
                repaint();
            }
        });

        // Status Bar
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        // Timer Label
        timerLabel = new JLabel("Time left: 10 seconds");
        timerLabel.setFont(FONT_STATUS);
        timerLabel.setHorizontalAlignment(JLabel.LEFT);
        timerLabel.setOpaque(true);
        timerLabel.setBackground(COLOR_BG_STATUS);
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setPreferredSize(new Dimension(300, 30));

        // Score Label
        scoreLabel = new JLabel(player1Name + ": 0   |   " + player2Name + ": 0");
        scoreLabel.setFont(FONT_STATUS);
        scoreLabel.setBackground(COLOR_BG_STATUS);
        scoreLabel.setOpaque(true);
        scoreLabel.setPreferredSize(new Dimension(300, 30));
        scoreLabel.setHorizontalAlignment(JLabel.LEFT);
        scoreLabel.setForeground(Color.WHITE);

        // Right panel: Timer + Score
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(COLOR_BG);
        rightPanel.add(Box.createVerticalStrut(40));
        rightPanel.add(timerLabel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(scoreLabel);
        rightPanel.setPreferredSize(new Dimension(250, Board.CANVAS_HEIGHT));

        // Main layout
        setLayout(new BorderLayout());
        add(statusBar, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);

        setPreferredSize(new Dimension(Board.CANVAS_WIDTH + 250, Board.CANVAS_HEIGHT + 30));
        setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        initGame();
        newGame();
    }

    public void initGame() {
        board = new Board();
    }

    public void newGame() {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED;
            }
        }
        currentPlayer = Seed.CROSS;
        currentState = State.PLAYING;
        startTurnTimer();
    }

    private void startTurnTimer() {
        if (turnTimer != null) turnTimer.stop();
        timeLeft = 10;
        timerLabel.setText("Time left: " + timeLeft + " seconds");

        turnTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("Time left: " + timeLeft + " seconds");
                if (timeLeft <= 0) {
                    turnTimer.stop();
                    currentState = (currentPlayer == Seed.CROSS) ? State.NOUGHT_WON : State.CROSS_WON;
                    repaint();
                    SoundEffect.EXPLODE.play();
                    JOptionPane.showMessageDialog(null,
                            "Waktu habis! Pemain " + (currentPlayer == Seed.CROSS ? "X" : "O") + " kalah.");
                }
            }
        });
        turnTimer.start();
    }

    private void stopTurnTimer() {
        if (turnTimer != null) turnTimer.stop();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG);
        board.paint(g);

        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.WHITE);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("Pacman Won! Click to play again.");
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("Ghost Won! Click to play again.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.setContentPane(new GameMain());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

