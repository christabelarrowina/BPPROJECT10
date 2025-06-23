package TTTGraphicsSoundImage;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Tic-Tac-Toe: Enhanced Version with Intro, Name Input, Color Picker, and Background Music.
 */
public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    // Constants
    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // Game objects
    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private JLabel statusBar;

    // User input
    private String playerXName;
    private String playerOName;
    private Color boardColor = COLOR_BG;

    /** Constructor */
    public GameMain() {
        // Show welcome message
        JOptionPane.showMessageDialog(null, "Welcome to TicTacToe Game", "Welcome", JOptionPane.INFORMATION_MESSAGE);

        // Get player names
        playerXName = JOptionPane.showInputDialog(this, "Enter name for Player X:");
        if (playerXName == null || playerXName.trim().isEmpty()) playerXName = "Player X";

        playerOName = JOptionPane.showInputDialog(this, "Enter name for Player O:");
        if (playerOName == null || playerOName.trim().isEmpty()) playerOName = "Player O";

        // Choose board color
        boardColor = JColorChooser.showDialog(this, "Choose Board Background Color", COLOR_BG);
        if (boardColor == null) boardColor = COLOR_BG;

        // Confirm start
        int response = JOptionPane.showConfirmDialog(this, "Start the game?", "Start Game", JOptionPane.YES_NO_OPTION);
        if (response != JOptionPane.YES_OPTION) {
            System.exit(0);
        }

        // Mouse interaction for game logic
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
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                    }

                    if (currentState == State.PLAYING) {
                        SoundEffect.DIE.play();
                    } else if (currentState == State.DRAW) {
                        SoundEffect.EAT_FOOD.play();
                    } else {
                        SoundEffect.EXPLODE.play();
                    }
                } else {
                    newGame();
                }

                repaint();
            }
        });

        // Setup status bar
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        super.setLayout(new BorderLayout());
        super.add(statusBar, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        // Start music
        SoundEffect.initGame();
        SoundEffect.BACKGROUND.playLoop();

        // Setup game
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
        SoundEffect.BACKGROUND.playLoop();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(boardColor);
        board.paint(g);

        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS ? playerXName : playerOName) + "'s Turn (" + (currentPlayer == Seed.CROSS ? "X" : "O") + ")");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'" + playerXName + "' Won! Click to play again.");
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'" + playerOName + "' Won! Click to play again.");
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.setContentPane(new GameMain());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
