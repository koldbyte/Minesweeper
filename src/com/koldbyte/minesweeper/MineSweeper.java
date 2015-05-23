package com.koldbyte.minesweeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.Random;
import javax.swing.*;

/**
 *
 * @author koldbyte
 */
public class MineSweeper extends javax.swing.JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7085306328354698616L;
	private int columns = 10;
    private int rows = 10;
    private int time = 0;
    private int delay = 1000;
    Timer timer;
    boolean jBombs[][];             // Stores which cells are bombs and which are not
    boolean jShown[][];             // Stores information on which cell has been displayed
    int jCells[][];                 // Stores information on surrounding cells
    private int currX, currY = 0;
    int countBombs = 0;
    int countBombsMarkedCorrect = 0;
    int countBombsUnMarked = 0;
    JToggleButton jButtons[];
    private JPanel jPanel = null;
    private JPanel jContentPane = null;
    private JProgressBar jProgressBar = null;
    private JPanel buttonPanel = null;
    JButton smileButton = new javax.swing.JButton();
    JLabel TotalBombsLabel = new javax.swing.JLabel();
    JLabel TimerLabel = new javax.swing.JLabel();
    JPanel jPanel1 = new javax.swing.JPanel();
    JMenuBar menuBar = new javax.swing.JMenuBar();
    JMenu gamesMenu = new javax.swing.JMenu();
    JMenuItem newMenuItem = new javax.swing.JMenuItem();
    JPopupMenu.Separator jSeparator1 = new javax.swing.JPopupMenu.Separator();
    JMenuItem exitMenuItem = new javax.swing.JMenuItem();
    JMenu helpMenu = new javax.swing.JMenu();
    JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
    JMenuItem setSkinMenuItem = new javax.swing.JMenuItem();

    /**
     * This is the default constructor
     */
    public MineSweeper() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setSize(640, 480);
        updateLAF(4);
        updatePane();
        this.setTitle("Minesweeper");
        this.setVisible(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int choice = javax.swing.JOptionPane.showConfirmDialog(null, "Are you sure to end the game?", "Really Exiting?", javax.swing.JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });
        getMenus();
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());
            jPanel.add(getButtonPanel(), BorderLayout.NORTH);
            jPanel.add(getJContentPane(), BorderLayout.CENTER);
            jPanel.add(getJProgressBar(), BorderLayout.SOUTH);
        }
        return jPanel;
    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(rows);
            gridLayout.setColumns(columns);
            jContentPane = new JPanel();
            jContentPane.setLayout(gridLayout);
            BuildBoard();
        }
        return jContentPane;
    }

    /**
     * This method builds the board.It is called with every new game.
     *
     * @return void
     */
    private void BuildBoard() {
        this.jBombs = new boolean[rows][columns];
        this.jCells = new int[rows][columns];
        this.jShown = new boolean[rows][columns];
        jButtons = new JToggleButton[columns * rows];
        if (jProgressBar != null) {
            jProgressBar.setValue(0);
        }
        jContentPane.removeAll();
        int i = 0;
        countBombs = 0;
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < columns; y++) {
                currX = x;
                currY = y;
                Random randBomb = new Random();
                jBombs[x][y] = randBomb.nextBoolean() && randBomb.nextBoolean() && randBomb.nextBoolean(); // 13% chances of a bomb
                if (jBombs[x][y]) {
                    countBombs++;
                }
                jButtons[i] = new JToggleButton("");
                jButtons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent e) {
                        if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
                            markCell(e);
                        } else if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
                            showCell(e);
                        }
                    }
                });
                jContentPane.add(jButtons[i]);
                i++;
            }
        }

        // Build the board
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < columns; y++) {
                jCells[x][y] = bombCount(x, y);
                jShown[x][y] = false; // Reset previous values
            }
        }
        jContentPane.setEnabled(true);
        updateTotalBombsLabel(countBombs);
        countBombsUnMarked = countBombs;
        this.repaint();
        this.validate();
    }

    /**
     * This method initializes jProgressBar
     *
     * @return javax.swing.JProgressBar
     */
    private JProgressBar getJProgressBar() {
        if (jProgressBar == null) {
            jProgressBar = new JProgressBar();
            jProgressBar.setMaximum(columns * rows);
        }
        return jProgressBar;
    }

    /**
     * The Main function for the Game Class.
     *
     * @param args
     */
    public static void main(String args[]) {
        MineSweeper m = new MineSweeper();
        m.BuildBoard();

    }

    // Displays all cells marked as bombs
    private void showAllBombs() {
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < columns; y++) {
                if (jBombs[x][y] == true) {
                    JToggleButton jButton = findButton(x, y);
                    if (jButton.isEnabled()) // Don't go over the ones that were already counted
                    {
                        jProgressBar.setValue(jProgressBar.getValue() + 1);
                    }
                    jButton.setText("X");   //show as bomb 
                    jButton.setSelected(true);
                    jButton.setEnabled(false);
                }
            }
        }
    }

    private void clearCells(int x, int y) {
        // If the cell is in bounds
        if (inBounds(x, y)) {
            if (!jShown[x][y] && jBombs[x][y] == false) {
                jShown[x][y] = true;
                JToggleButton jButton = findButton(x, y);
                if (jCells[x][y] > 0) {
                    jButton.setText(Integer.toString(jCells[x][y]));
                } else {
                    jButton.setText("");
                }
                if (jButton.isEnabled()) // Don't count if button is enabled
                {
                    jProgressBar.setValue(jProgressBar.getValue() + 1);
                }
                jButton.setSelected(true);
                jButton.setEnabled(false);

                // Check surrounding cells
                if (jCells[x][y] == 0) {
                    for (int r = -1; r <= 1; r++) {
                        for (int c = -1; c <= 1; c++) {
                            clearCells(x + r, y + c);
                        }
                    }
                }
            }
        }
    }

    private boolean inBounds(int x, int y) {
        // Check if position is within range
        return 0 <= x && x < jCells.length && 0 <= y && y < jCells[x].length;
    }

    private boolean isBomb(JToggleButton jButton) {
        int i = 0;
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < columns; y++) {
                if (jButton == jButtons[i]) {
                    currX = x;
                    currY = y;
                    return jBombs[x][y];
                }
                i++;
            }
        }
        return false;
    }

    private void disableBoard() {
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < columns; y++) {
                JToggleButton jButton = findButton(x, y);
                jButton.setEnabled(false);
            }
        }
    }

    private JToggleButton findButton(int x, int y) {
        return jButtons[(x * rows + y)];
    }

    private void showCell(java.awt.event.MouseEvent e) {
        JToggleButton jButton = (JToggleButton) e.getSource();
        if (jButton.isEnabled()) {
            jProgressBar.setValue(jProgressBar.getValue() + 1);
            jButton.setEnabled(false);

            if (isBomb(jButton)) {
                showAllBombs();
                stopTimer();
                jButton.setEnabled(false);
                JOptionPane.showMessageDialog(null, "You lost " + Math.round((jProgressBar.getPercentComplete() * 100)) + "% through.", "You Lost!", JOptionPane.INFORMATION_MESSAGE);
                smileButton.setText(":(");
                disableBoard();
            } else {
                if (jCells[currX][currY] > 0) {
                    jButton.setText(Integer.toString(jCells[currX][currY]));
                } else if (jCells[currX][currY] == 0) {
                    clearCells(currX, currY);
                }
            }
        }
    }

    private int bombCount(int x, int y) {
        int bombCount = 0;

        // Count bombs in surrounding cells
        for (int r = -1; r <= 1; r++) {
            for (int c = -1; c <= 1; c++) {
                int newx = x + r;
                int newy = y + c;
                if (inBounds(newx, newy)) {
                    if (jBombs[newx][newy] == true) {
                        bombCount++;
                    }
                }
            }
        }
        return bombCount;
    }

    private void markCell(java.awt.event.MouseEvent e) // To set and remove the "!" flag
    {
        JToggleButton jButton = (JToggleButton) e.getSource();
        if (jButton.isEnabled()) {
            if (!"!".equals(jButton.getText()) && countBombsUnMarked > 0) {
                jButton.setText("!");
                countBombsUnMarked--;
                updateTotalBombsLabel(countBombsUnMarked);
            } else if ("!".equals(jButton.getText())) {
                jButton.setText("");
                countBombsUnMarked++;
                updateTotalBombsLabel(countBombsUnMarked);
            }
        }
        checkWin();
    }

    private Component getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
        }
        //BorderLayout bl = new BorderLayout();
        FlowLayout fl = new FlowLayout(FlowLayout.CENTER, this.getWidth() / 5, 5);
        buttonPanel.setLayout(fl);
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        buttonPanel.add(getTotalBombsLabel(), FlowLayout.LEFT);
        buttonPanel.add(getSmileButton(), FlowLayout.CENTER);
        buttonPanel.add(getTimerLabel(), FlowLayout.RIGHT);
        return buttonPanel;
    }

    /**
     * This initializes the Smile Button.
     *
     * @return
     */
    public JButton getSmileButton() {
        if (smileButton == null) {
            smileButton = new javax.swing.JButton();
        }
        smileButton.setFont(new java.awt.Font("Tahoma", 3, 18)); // NOI18N
        smileButton.setText(":)");

        smileButton.setSize(500, 500);
        smileButton.setFocusable(false);
        smileButton.setPreferredSize(new java.awt.Dimension(60, 30));

        smileButton.setMaximumSize(new java.awt.Dimension(120, 60));
        smileButton.setMinimumSize(new java.awt.Dimension(20, 10));


        smileButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        smileButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                newGame();
            }
        });
        return smileButton;
    }

    /**
     * Starts a new game by prompting user.
     */
    public void newGame() {
        int newGame = JOptionPane.showConfirmDialog(null, "Do you want to start a new Game?", "Start a new Game", JOptionPane.YES_NO_OPTION);
        if (newGame == JOptionPane.YES_OPTION) {
            time = 0;
            TimerLabel.setText("000");
            smileButton.setText(":)");
            BuildBoard();
            startTimer();
            //updatePane();
            //updateTotalBombsLabel(countBombs);
        }
    }

    /**
     * This initializes the Label that displays Total Bombs.
     *
     * @return
     */
    public JLabel getTotalBombsLabel() {

        TotalBombsLabel.setBackground(new java.awt.Color(51, 153, 255));
        TotalBombsLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        TotalBombsLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        TotalBombsLabel.setText("00");
        TotalBombsLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
        return TotalBombsLabel;
    }

    /**
     * This initializes the timer label.
     *
     * @return
     */
    public JLabel getTimerLabel() {
        TimerLabel.setBackground(new java.awt.Color(51, 153, 255));
        TimerLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        TimerLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        TimerLabel.setText("000");
        TimerLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
        ActionListener taskPerformer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                time = Integer.parseInt(TimerLabel.getText());
                time++;
                TimerLabel.setText(String.format("%03d", time));
                if (time == 999) {
                    JOptionPane.showMessageDialog(null, "Your time is over.", "You Lost!", JOptionPane.INFORMATION_MESSAGE);
                    smileButton.setText(":(");
                    stopTimer();
                    newGame();
                }
            }
        };
        timer = new Timer(delay, taskPerformer);
        timer.start();
        return TimerLabel;
    }

    private void getMenus() {
        gamesMenu.setMnemonic('g');
        gamesMenu.setText("Games");
        newMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        newMenuItem.setMnemonic('n');
        newMenuItem.setText("New");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGame();
            }
        });

        setSkinMenuItem.setMnemonic('s');
        setSkinMenuItem.setText("Change Skin");
        setSkinMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        setSkinMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String choice = JOptionPane.showInputDialog(null, "Enter number to change skin:\n 1: Metal\n 2: Motif \n 3: GTK\n 4: System Default", "Change Skin", JOptionPane.OK_CANCEL_OPTION);
                updateLAF(Integer.parseInt(choice));
            }
        });


        gamesMenu.add(newMenuItem);
        gamesMenu.add(jSeparator1);
        gamesMenu.add(setSkinMenuItem);
        gamesMenu.add(jSeparator1);
        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
            }
        });
        gamesMenu.add(exitMenuItem);
        menuBar.add(gamesMenu);
        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");
        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JOptionPane.showMessageDialog(null, "Developed by:\n Koldbyte( https://github.com/koldbyte/Minesweeper )",
                        "About MineSweeper", JOptionPane.PLAIN_MESSAGE);
            }
        });
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    /**
     * Update the Mines Pane.
     */
    public void updatePane() {
        this.setContentPane(getJPanel());
    }

    /**
     * Update the total Bombs Label to reflect change.
     *
     * @param count
     */
    public void updateTotalBombsLabel(int count) {
        TotalBombsLabel.setText(Integer.toString(count));
    }

    private void checkWin() {
        countBombsMarkedCorrect = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                JToggleButton k = findButton(i, j);
                String label = k.getText();
                if (label.equalsIgnoreCase("!") && jBombs[i][j]) {
                    countBombsMarkedCorrect++;
                }
            }
        }

        if (countBombs == countBombsMarkedCorrect) {
            stopTimer();
            JOptionPane.showConfirmDialog(null, "You win!!!\n Your time: " + TimerLabel.getText(), "Congratulations!!", JOptionPane.OK_OPTION);
            newGame();
        }
    }

    /**
     * Stops the Game Timer.s
     */
    public void stopTimer() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }
    
     public void startTimer() {
        if (!(timer.isRunning())) {
            timer.start();
        }
    }
    

    private void updateLAF(int lAF) {
        try {
            String plaf = "";
            if (lAF == 1) {
                plaf = "javax.swing.plaf.metal.MetalLookAndFeel";
            } else if (lAF == 2) {
                plaf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            } else if (lAF == 3) {
                plaf = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            } else if (lAF == 4) {
                plaf = UIManager.getSystemLookAndFeelClassName();
            }

            UIManager.setLookAndFeel(plaf);
            SwingUtilities.updateComponentTreeUI(this);

        } catch (Exception ue) {
            System.err.println(ue.toString());
        }
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
