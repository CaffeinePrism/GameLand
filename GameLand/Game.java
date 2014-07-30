package GameLand;

import java.util.Scanner;

/**
 * Game Runner class. Initialize game and holds game logic
 *
 * @author Richard Li
 * @version 2014-31-1
 */
public class Game {
    public final Board board;
    public final Dice dice;
    public final long RNGSEED;
    public final int DICENUM;
    public final int DICESIDES;
    public final int DICEMOSTLIKELY;
    public int turn = 1;
    /**
     * Array of players. For convenience, [0] is null, P1 is [1] und so weiter;
     */
    public Player[] players;

    /**
     * Constructor for objects of class Main
     */

    public Game(int players, int dicenum, int dicesides) {
        // pass -1 because cannot run genSeed() in constructor :(
        this(players, dicenum, dicesides, -1);
    }

    public Game(int players) {
        // sensible default values for a quick-and-dirty demo
        this(players, 2, 6);
    }

    public Game(int players, int dicenum, int dicesides, long seed) {
        if(seed == -1)
        {
            seed = genSeed();
        }
        RNGSEED = seed;

        dice = new Dice(dicenum, dicesides, seed);
        DICENUM = dicenum;
        DICESIDES = dicesides;
        DICEMOSTLIKELY = dice.probability();

        board = new Board(this, 150, players);
        this.players = board.players;
    }

    public Game(int players, String[] names, int dicenum, int dicesides, long seed)
    {
        this(players, dicenum, dicesides, seed);
        for(int i = 1; i < players; i++)
        {
            if(names[i].length() != 0)
            {
                this.players[i].setName(names[i]);
            }
        }
        // update game info because player names have changed
        board.genPlayerInfo();
    }

    /**
     * Generate a seed for the RNG
     */
    private static long genSeed()
    {
        /*
         * http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/java/util/Random.java
         *  public Random() { this(++seedUniquifier + System.nanoTime()); }
         *  private static volatile long seedUniquifier = 8682522807148012L;
         */
        long seedUniquifier = 8682522807148012L;
        return ++seedUniquifier + System.nanoTime();
    }

    public int diceRoll() {
        return dice.diceRoll();
    }

    /**
     * Should player go backwards or forwards? Go backwards if either: lowest, highest, or most likely sum
     */
    private int calcSpaces(int roll) {
        if (roll == DICENUM || roll == DICEMOSTLIKELY || roll == (DICESIDES * DICENUM)) {
            return -roll;
        } else {
            return roll;
        }
    }

    public void doTurn() {
        if (!board.gameOver) {
            int roll = diceRoll();
            int spaces = calcSpaces(roll);

            // who's next?
            Player player = players[next(turn)];
            player.move(spaces);

            int pos = player.getPos();
            // position string -- so can change "0" to start and stuff
            String posS = Integer.toString(pos);

            // so we can keep track of who goes next
            turn++;

            if (pos == 0)
                posS = "Start";
            if (pos == board.size + 1)
                posS = "Finish!";

            // TODO: string formatting to have aligned output? nah.
            board.out(
                "<html>"
                + "<font color='" + player.hex + "'>"
                + player.name + "</font> rolls " + roll + "; moves: " + spaces + " new pos: " + posS
                + "</html>"
            );
            //board.out(player.name + " rolls " + roll + "; moves: " + spaces + " new pos: " + posS);

            if (board.gameOver) {
                board.gameOver(player);
            }
        }
    }

    /**
     * Run doTurn() as many times as there are players
     */
    public void doRound() {
        for (Player player : players) // foreach player in players
        {
            if (player != null) {
                if (!board.gameOver) {
                    doTurn();
                }
            }
        }
    }

    /**
     * Returns player index of next person to go
     */
    private int next(int turn) {
        //         i -= 1;
        //         i %= players.length;
        //         i += 1;
        //         return i;
        return ((turn - 1) % (players.length - 1)) + 1;
    }

    public static void TestMe()
    {
        main(new String[0]);
    }

    public static void main(String[] args) {
        // DEFAULTS when running through main()
        int playerNum = 5;
        int diceCnt = 2;
        int diceSides = 12;
        long seed = genSeed();
        String[] names;

        Scanner in = new Scanner(System.in);

        System.out.println("THE GAME...\nLeave blank for defaults");
        System.out.println("How many players? (5)");

        // SOLUTION: docs says that nextLine() consumes the \n everytime :D
        //          use try-catch blocks as necessary to convert to ints/doubles/longs
        if(in.hasNextLine())
        {
            try
            {
                playerNum = Integer.parseInt(in.nextLine());
            } catch (Exception e) {}
        }

        System.out.print("Player names? Leave bank for defaults\n");
        names = new String[playerNum + 1];
        for(int i = 1; i <= playerNum; i++)
        {
            System.out.print("\tPlayer " + i + "(Player " + i + "): ");
            if(in.hasNextLine())
            {
                names[i] = in.nextLine();
            }
        }

        System.out.print("Dice Count? (2): ");
        if(in.hasNextLine())
        {
            try
            {
                diceCnt = Integer.parseInt(in.nextLine());
            }
            catch (Exception e) {}
        }

        System.out.print("Dice Sides? (12): ");
        if(in.hasNextLine())
        {
            try
            {
                diceSides = Integer.parseInt(in.nextLine());
            }
            catch (Exception e) {}
        }

        System.out.print("Random number generator seed? (" + seed + "): ");
        if(in.hasNextLine())
        {
            try
            {
                seed = Long.parseLong(in.nextLine());
            } catch (Exception e) {}
        }
        in.close();
        
        System.out.print("\n\n\nStarting Game :D");
        new Game(playerNum, names, diceCnt, diceSides, seed);
    }
}
