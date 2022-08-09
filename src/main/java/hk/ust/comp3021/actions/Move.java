package hk.ust.comp3021.actions;

import hk.ust.comp3021.utils.ShouldNotReachException;
import jdk.dynalink.linker.LinkerServices;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * An actions of moving a player.
 */
public abstract sealed class Move extends Action permits Move.Down, Move.Left, Move.Right, Move.Up {

    private final int steps;

    /**
     * @param steps The number of steps.
     */
    protected Move(int steps) {
        this.steps = steps;
    }

    /**
     * @return The number of steps in this move.
     */
    public int getSteps() {
        return steps;
    }

    /**
     * Break down one move into an array of one-step moves.
     *
     * @return an array of one-step moves.
     */
    public @NotNull Move[] breakdown() {
        var moves = new Move[this.steps];
        if (this instanceof Move.Down) {
            Arrays.fill(moves, new Move.Down(1));
        } else if (this instanceof Move.Left) {
            Arrays.fill(moves, new Move.Left(1));
        } else if (this instanceof Move.Right) {
            Arrays.fill(moves, new Move.Right(1));
        } else if (this instanceof Move.Up) {
            Arrays.fill(moves, new Move.Up(1));
        } else {
            throw new ShouldNotReachException();
        }
        return moves;
    }

    /**
     * The action of moving down.
     */
    public static final class Down extends Move {
        /**
         * @param steps The number of steps.
         */
        public Down(int steps) {
            super(steps);
        }
    }

    /**
     * The action of moving left.
     */
    public static final class Left extends Move {
        /**
         * @param steps The number of steps.
         */
        public Left(int steps) {
            super(steps);
        }
    }

    /**
     * The action of mocking right.
     */
    public static final class Right extends Move {
        /**
         * @param steps The number of steps.
         */
        public Right(int steps) {
            super(steps);
        }
    }

    /**
     * The action of moving up.
     */
    public static final class Up extends Move {
        /**
         * @param steps The number of steps.
         */
        public Up(int steps) {
            super(steps);
        }
    }
}

