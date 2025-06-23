package TTTGraphicsSoundImage;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
    private int roundCounter = 0;

    public GameMain(String player1Name, String player2Name) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;

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
                        updateScoreLabel();
                    }
                } else {
                    newGame();
                }
                repaint();
            }
        });

        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        timerLabel = new JLabel("Time left: 10 seconds");
        timerLabel.setFont(FONT_STATUS);
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerLabel.setOpaque(true);
        timerLabel.setBackground(COLOR_BG_STATUS);
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setPreferredSize(new Dimension(300, 30));

        scoreLabel = new JLabel();
        scoreLabel.setFont(FONT_STATUS);
        scoreLabel.setBackground(COLOR_BG_STATUS);
        scoreLabel.setOpaque(true);
        scoreLabel.setPreferredSize(new Dimension(300, 140));
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setForeground(Color.WHITE);
        updateScoreLabel();

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(COLOR_BG);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(timerLabel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(scoreLabel);
        rightPanel.setPreferredSize(new Dimension(250, Board.CANVAS_HEIGHT));

        setLayout(new BorderLayout());
        add(statusBar, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);

        setPreferredSize(new Dimension(Board.CANVAS_WIDTH + 250, Board.CANVAS_HEIGHT + 30));
        setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        initGame();
        newGame();
    }

    private void updateScoreLabel() {
        scoreLabel.setText("<html><div style='text-align: center;'>" +
                player1Name + ": " + score1 + "<br><img src='file:src/images/PacmanOKE.png' width='25' height='25'><br><br>" +
                player2Name + ": " + score2 + "<br><img src='file:src/images/GhostOKE.png' width='25' height='25'></div></html>");
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
                    boolean crossWins = (currentPlayer == Seed.NOUGHT);
                    currentState = crossWins ? State.CROSS_WON : State.NOUGHT_WON;
                    if (crossWins) score1++;
                    else score2++;
                    updateScoreLabel();
                    SoundEffect.WIN.play();
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
            SoundEffect.BACKGROUND.playLoop(); // loop the background sound during welcome and input

            UIManager.put("OptionPane.background", Color.BLACK);
            UIManager.put("Panel.background", Color.BLACK);
            UIManager.put("OptionPane.messageForeground", Color.YELLOW);
            UIManager.put("OptionPane.messageFont", new Font("OCR A Extended", Font.BOLD, 18));
            UIManager.put("Button.background", Color.BLACK);
            UIManager.put("Button.foreground", Color.GREEN);

            int dialogWidth = 420;
            int dialogHeight = 240;

            JPanel welcomePanel = new JPanel();
            welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
            welcomePanel.setBackground(Color.BLACK);
            welcomePanel.setPreferredSize(new Dimension(dialogWidth, dialogHeight));

            JLabel title = new JLabel("Welcome to Pacman Tic Tac Toe!", SwingConstants.CENTER);
            title.setFont(new Font("OCR A Extended", Font.BOLD, 22));
            title.setForeground(Color.YELLOW);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            title.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel pacmanImg = new JLabel(new ImageIcon(new ImageIcon("src/images/PacmanOKE.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
            JLabel ghostImg = new JLabel(new ImageIcon(new ImageIcon("src/images/GhostOKE.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

            JPanel iconPanel = new JPanel();
            iconPanel.setBackground(Color.BLACK);
            iconPanel.add(pacmanImg);
            iconPanel.add(Box.createRigidArea(new Dimension(20, 0)));
            iconPanel.add(ghostImg);

            welcomePanel.add(Box.createVerticalStrut(10));
            welcomePanel.add(title);
            welcomePanel.add(Box.createVerticalStrut(15));
            welcomePanel.add(iconPanel);

            JOptionPane.showMessageDialog(null, welcomePanel, "Welcome", JOptionPane.PLAIN_MESSAGE);

            JPanel inputPanel = new JPanel();
            inputPanel.setBackground(Color.BLACK);
            inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
            inputPanel.setPreferredSize(new Dimension(dialogWidth, dialogHeight));

            JLabel label1 = new JLabel("Player X (Pacman):", SwingConstants.RIGHT);
            label1.setForeground(Color.YELLOW);
            label1.setAlignmentX(Component.RIGHT_ALIGNMENT);
            JTextField player1Field = new JTextField("Pacman");

            JLabel label2 = new JLabel("Player O (Ghost):", SwingConstants.RIGHT);
            label2.setForeground(Color.CYAN);
            label2.setAlignmentX(Component.RIGHT_ALIGNMENT);
            JTextField player2Field = new JTextField("Ghost");

            inputPanel.add(label1);
            inputPanel.add(player1Field);
            inputPanel.add(Box.createVerticalStrut(10));
            inputPanel.add(label2);
            inputPanel.add(player2Field);

            JPanel inputIconPanel = new JPanel();
            inputIconPanel.setBackground(Color.BLACK);
            inputIconPanel.add(new JLabel(new ImageIcon(new ImageIcon("src/images/PacmanOKE.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH))));
            inputIconPanel.add(Box.createRigidArea(new Dimension(20, 0)));
            inputIconPanel.add(new JLabel(new ImageIcon(new ImageIcon("src/images/GhostOKE.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH))));
            inputPanel.add(Box.createVerticalStrut(15));
            inputPanel.add(inputIconPanel);

            int result = JOptionPane.showConfirmDialog(null, inputPanel, "Enter Player Names", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) System.exit(0);

            String name1 = player1Field.getText().trim();
            String name2 = player2Field.getText().trim();
            if (name1.isEmpty()) name1 = "Pacman";
            if (name2.isEmpty()) name2 = "Ghost";

            JPanel startPanel = new JPanel();
            startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));
            startPanel.setBackground(Color.BLACK);
            startPanel.setPreferredSize(new Dimension(dialogWidth, dialogHeight));

            JLabel startLabel = new JLabel("Start Game", SwingConstants.CENTER);
            startLabel.setFont(new Font("OCR A Extended", Font.BOLD, 22));
            startLabel.setForeground(Color.GREEN);
            startLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            startLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel startIcons = new JPanel();
            startIcons.setBackground(Color.BLACK);
            startIcons.add(new JLabel(new ImageIcon(new ImageIcon("src/images/PacmanOKE.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH))));
            startIcons.add(Box.createRigidArea(new Dimension(20, 0)));
            startIcons.add(new JLabel(new ImageIcon(new ImageIcon("src/images/GhostOKE.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH))));

            startPanel.add(Box.createVerticalStrut(10));
            startPanel.add(startLabel);
            startPanel.add(Box.createVerticalStrut(15));
            startPanel.add(startIcons);

            JOptionPane.showMessageDialog(null, startPanel, "Start", JOptionPane.PLAIN_MESSAGE);

            SoundEffect.BACKGROUND.stop(); // stop background music when game starts

            JFrame frame = new JFrame(TITLE);
            frame.setContentPane(new GameMain(name1, name2));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
