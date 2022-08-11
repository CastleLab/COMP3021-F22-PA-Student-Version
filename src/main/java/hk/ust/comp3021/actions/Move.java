package hk.ust.comp3021.actions;

/**
 * An actions of moving a player.
 */
public abstract sealed class Move extends Action permits Move.Down, Move.Left, Move.Right, Move.Up {

    /**
     * The action of moving down.
     */
    public static final class Down extends Move {
    }

    /**
     * The action of moving left.
     */
    public static final class Left extends Move {
    }

    /**
     * The action of mocking right.
     */
    public static final class Right extends Move {
    }

    /**
     * The action of moving up.
     */
    public static final class Up extends Move {
    }
}

