package GameLand;

import java.awt.*;

/**
 * Player object. Keeps track of current position and moves taken.
 *
 * @author Richard Li
 * @version 2014-31-1
 */
public class Player {
    private int pos;
    private int moves;
    private final Board board;
    public String name;
    public Color color;
    // Board and hex value is lighter() than actual color
    public String hex;

    public Player(String name, Board board, Color color) {
        // initialise instance variables
        pos = 0;
        this.name = name;
        this.board = board;
        this.color = color;
        // http://stackoverflow.com/questions/6539879/
        this.hex = String.format("#%06X", (0xFFFFFF & color.brighter().getRGB()));
    }

    public Player(String name, Board board) {
        this(name, board, 
                new Color((float) Math.random(), (float) Math.random(), (float) Math.random()).darker());
    }

    public int getPos() {
        return pos;
    }

    private void setPos(int pos)
    {
        this.pos = pos;
    }

    public int getMoves()
    {
        return moves;
    }

    public void move(int spaces) {
        if (!board.gameOver) {
            int pos1 = pos;
            int next = pos1 + spaces;
            next = board.logic(next);
            //pos = board.logic(next);
            setPos(next);
            moves++;
            //board.updateButtons();
            board.updateButton(pos1);
            board.updateButton(next);

            if(pos == board.size+1)
            {
                // doublechecky-check
                board.gameOver = true;
            }
        }
    }

    public void setName(String s) {
        name = s;
    }

    public String toString()
    {
        return name;
    }
}
