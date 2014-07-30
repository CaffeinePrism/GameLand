package GameLand;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;

/**
 * Prettypretty grid. Draws UI and holds reference to player list and interfaces with main game methods
 *
 * @author Richard Li
 * @version 1
 */
public class Board implements MouseListener {
    public final int size;
    private final JButton[] buttons;
    private final Game g;
    public Player[] players;
    private final JButton turnButton;
    private final JButton roundButton;
    private final JLabel gameInfo;

    //HTML in log out() function. blegh
    private final JTextPane textArea;
    private final HTMLEditorKit kit = new HTMLEditorKit();
    private final HTMLDocument doc = new HTMLDocument();

    private final JLabel info;
    private final int gridXSize = 10;
    // unsafe, but who cares?
    public boolean gameOver = false;

    /*
     * Possible solutions for pos -> player mapping
     * - brute force (... like 3 different ways to do)
     * - extend logic() to take Player object/playerID  <-- that one?
     * - dont care, just redraw every time
     */
    public Board(Game game, int size, int players) {
        // +2 b/c start & finish buttons
        int buttonCount = size + 2;
        g = game;
        this.size = size;
        this.players = new Player[players + 1];
        for (int i = 1; i <= players; i++) {
            this.players[i] = new Player("Player " + Integer.toString(i), this);
        }

        // Swing
        // http://docs.oracle.com/javase/tutorial/uiswing/components/index.html

        // try system ui -> nimbus ui(jdk7) -> ugly default java
        try {
            // sys ui default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());    
        } catch (Exception e) {
            try {
                // else try numbus
                for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception f) {
                // no nimbus = javametal
            }
        }

        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());

        // log output thing. forgot where jscrollpane was from
        Dimension textAreaSize = new Dimension(400, 250);
        textArea = new JTextPane();
        textArea.setEditorKit(kit);
        textArea.setDocument(doc);
        // need pref-size so its not a mini pixel
        textArea.setPreferredSize(textAreaSize);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        // force textArea to use HTML properties
        textArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        // text area container = console
        JScrollPane console = new JScrollPane(textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        console.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Log"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        textArea.setEditable(false);
        // so no weird overflow stuff
        console.setPreferredSize(textAreaSize);

        // ...infopanel...
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setPreferredSize(new Dimension(50, 200));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Info"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        // Button hover thingy
        info = new JLabel();
        info.setVerticalAlignment(SwingConstants.TOP);

        // game info panel section
        gameInfo = new JLabel();
        // update gameInfo thing
        genPlayerInfo();

        // align top
        gameInfo.setVerticalTextPosition(SwingConstants.TOP);

        // add panels
        infoPanel.add(info, BorderLayout.LINE_START);
        infoPanel.add(gameInfo, BorderLayout.LINE_END);

        sidebar.add(console, BorderLayout.PAGE_START);
        sidebar.add(infoPanel, BorderLayout.PAGE_END);

        // Buttons/Button Grid
        int gridsize = gridsize();
        buttons = new JButton[buttonCount];
        JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(gridXSize, gridsize));
        grid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("The game!"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        for (int i = 0; i < buttonCount; i++) {
            JButton b = new JButton(Integer.toString(i));
            b.setRolloverEnabled(true);
            b.addMouseListener(this);
            // so grey and dont have to style rectangrols
            b.setEnabled(false);
            grid.add(b);
            // add to array so can manipulate them later
            buttons[i] = b;
        }
        buttons[0].setText("Start");
        buttons[buttons.length - 1].setText("Finish");

        // Game Controls
        JPanel controls = new JPanel();
        turnButton = new JButton("1 Turn");
        roundButton = new JButton(this.players.length - 1 + " Turns");
        turnButton.addMouseListener(
            new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    g.doTurn();
                }
            });
        roundButton.addMouseListener(
            new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    g.doRound();
                }
            });

        controls.add(turnButton, BorderLayout.LINE_START);
        controls.add(roundButton, BorderLayout.LINE_START);

        //size and stuff
        frame.add(controls, BorderLayout.PAGE_END);
        frame.add(grid, BorderLayout.CENTER);
        frame.add(sidebar, BorderLayout.LINE_END);
        // AUTO-SIZING :D
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        // update the first button properties
        updateButton(0);
    }

    /**
     * prevent NullPointerException by routing everything though this to get valid positions
     */
    public int logic(int next) {
        int pos = next;
        if (pos > size) {
            return size + 1;
        } else if (pos < 0) {
            return 0;
        } else {
            return pos;
        }
    }

    /**
     * Update ALL THE BUTTONS
     */
    public void updateButtons() {
        // icon repainting
        for (int i = 0; i < buttons.length; i++) {
            updateButton(i);
        }
    }

    /**
     * Print stuff to the textArea with the game-log
     */
    public void out(String s)
    {
        try {
            kit.insertHTML(doc, doc.getLength(), s, 0, 0, null);
            //textArea.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //textArea.append(s + "\n");
        //System.out.println(s);
        //System.out.println(textArea.getText());
        //textArea.setText(textArea.getText() + s);
    }

    public void updateButton(int pos) {
        JButton button = buttons[pos];
        // reset to prevent weird stuff
        // important to run this function on the last square player was on too
        button.setBackground(null);
        for (Player player : players) {
            if (player != null) {
                if (player.getPos() == pos) {
                    button.setBackground(player.color.brighter());
                }
            }
        }
        button.revalidate();
    }

    private void updateInfo(int button) {
        String pos = "Position # ";
        if (button == 0) {
            pos = "Start";
        } else if (button == buttons.length - 1) {
            pos = "Finish!";
        } else {
            pos += button;
        }
        String text = "<html><strong>" + pos + "</strong><br />"
            + "Players in this location: <br />";

        for (Player player : players) {
            if (player != null) {
                if (player.getPos() == button) {
                    text +=
                    "<font color='" + player.hex + "'>"
                    + player.name + "</font><br />";
                }
            }
        }

        info.setText(text + "</html>");
    }

    /**
     * run when game over
     */
    public void gameOver(Player player) {
        if(gameOver) // double-y sure!
        {
            turnButton.setEnabled(false);
            roundButton.setEnabled(false);
            out("================= GAME OVER! =================");
            out("Winner is: " + "<font color='" + player.hex + "'>"
                + player.name + "</font><br />"
            );
            out("Turn: " + g.turn + " (" + player.getMoves() + " moves)");
        }
    }

    /**
     * Width of grid
     */
    public int gridsize() {
        for (int i = 1; i < size; i++) {
            if (gridXSize * i >= size) {
                return i;
            }
        }
        return -1;
    }

    /**
     * (Force) update the player info in the gameInfo panel
     */
    public void genPlayerInfo()
    {
        // list of players -> appended to gameInfo
        String playerText = "";
        for (Player player : players) {
            if (player != null) {
                playerText +=
                "<font color='" + player.hex + "'>"
                + player.name + "</font><br />";
            }
        }
        gameInfo.setText("<html><strong>THE GAME....</strong><br />"
            + "RNG Seed: " + g.RNGSEED + "<br />"
            + "Playing with " + g.DICENUM + " dice (" + g.DICESIDES + " sides) <br />"
            + g.DICEMOSTLIKELY + " is the most likely sum <br />"
            + "There are " + ((players.length) - 1) + " players: <br />"
            + playerText
            + "</html>"
        );
    }

    /**
     * get button index for given button
     */
    private int whichbutton(JButton button) {
        String text = button.getText();
        if (text.matches("\\p{Alpha}++")) {
            if (text.equals("Start")) {
                return 0;
            } else if (text.equals("Finish")) {
                return buttons.length - 1;
            } else {
                return -1;
            }
        } else if (text.matches("[0-9]++")) {
            return Integer.parseInt(text);
        } else {
            return -1;
        }
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        JButton b = (JButton) event.getSource();
        int i = whichbutton(b);
        //System.out.println("Mouseover button: " + i);
        updateInfo(i);
    }

    // so thingy compiles
    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }
}
