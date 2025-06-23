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

    private String player1Name;
    private String player2Name;
    private int score1 = 0;
    private int score2 = 0;
    private int roundCounter = 0; // track round number

    public GameMain(String player1Name, String player2Name) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;

        board = new Board();
        SoundEffect.BACKGROUND.playLoop();

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
                            if (currentPlayer == Seed.CROSS) {
                                SoundEffect.GHOST.play();
                            } else {
                                SoundEffect.PACMAN.play();
                            }
                            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                            startTurnTimer();
                        } else if (currentState == State.DRAW) {
                            stopTurnTimer();
                            SoundEffect.DRAW.play();
                        } else {
                            stopTurnTimer();
                            SoundEffect.WIN.play();
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
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
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
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
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
        // Alternating first turn by round number
        currentPlayer = (roundCounter % 2 == 0) ? Seed.CROSS : Seed.NOUGHT;
        roundCounter++;
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

                    // Tentukan pemenang karena lawan kehabisan waktu
                    boolean crossWins = (currentPlayer == Seed.NOUGHT); // lawan dari NOUGHT adalah CROSS
                    currentState = crossWins ? State.CROSS_WON : State.NOUGHT_WON;

                    // Tambah skor untuk lawan
                    if (crossWins) score1++;
                    else score2++;

                    // Update skor
                    scoreLabel.setText(player1Name + ": " + score1 + "   |   " + player2Name + ": " + score2);

                    // Mainkan suara menang
                    SoundEffect.WIN.play();

                    // Tampilkan pesan
                    String winner = crossWins ? player1Name + " (Pacman)" : player2Name + " (Ghost)";
                    String loser = (currentPlayer == Seed.CROSS) ? player1Name + " (Pacman)" : player2Name + " (Ghost)";
                    JOptionPane.showMessageDialog(null, "Time is up! " + loser + " lose.");

                    repaint();
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
            statusBar.setText((currentPlayer == Seed.CROSS) ? player1Name + "'s Turn (Pacman)" : player2Name + "'s Turn (Ghost)");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText(player1Name + " Won! Click to play again.");
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText(player2Name + " Won! Click to play again.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Welcome to the Tic-Tac-Toe Game!", "Welcome", JOptionPane.INFORMATION_MESSAGE);

            String name1 = JOptionPane.showInputDialog("Enter name for Player X (Pacman):");
            if (name1 == null || name1.trim().isEmpty()) name1 = "Pacman";

            String name2 = JOptionPane.showInputDialog("Enter name for Player O (Ghost):");
            if (name2 == null || name2.trim().isEmpty()) name2 = "Ghost";

            int response = JOptionPane.showConfirmDialog(null, "Start the game?", "Start Game", JOptionPane.YES_NO_OPTION);
            if (response != JOptionPane.YES_OPTION) {
                System.exit(0);
            }

            JFrame frame = new JFrame(TITLE);
            frame.setContentPane(new GameMain(name1, name2));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            SoundEffect.BACKGROUND.play();
        });
    }
}
