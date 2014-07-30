package GameLand;

import java.util.Random;
/**
 * Dice object. Handles randomness, rolls, and probability calculations
 */
public class Dice {
    public final int dice;
    public final int sides;
    public final Random rng;
    public Dice(int dice, int sides, long seed)
    {
        this.rng = new Random(seed);
        this.dice = dice;
        this.sides = sides;
    }

    public int diceRoll() {
        int dicemin = dice;
        int dicemax = dice * sides;
        return ((rng.nextInt(dicemax - dicemin) + 1) + dicemin);
    }

    /**
     * get most likely sum of "n" dice with "s" sides
     */
    public int probability()
    {
        /*
         * get most likely sum of "n" dice with "s" sides
         * http://mathworld.wolfram.com/Dice.html
         * P(n,s) = { (1/2)n(s+1)       for n even
         *          { (1/2)[n(s+1)-1]   for n odd, s even
         *          { (1/2)n(s+1)       for n odd, s odd
         */

        int n = dice;
        int s = sides;
        if(n%2 != 0) // n odd
        {
            if(s%2 == 0) // s even
            {
                return (int) ((1/2.0) * (n * (s+1) - 1));
            }
        }
        // otherwise n even or n odd & s odd
        return (int) ((1/2.0) * n * (s+1));
    }
}
