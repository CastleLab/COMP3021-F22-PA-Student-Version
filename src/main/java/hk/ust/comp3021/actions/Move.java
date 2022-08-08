package hk.ust.comp3021.actions;

/**
 * An actions of moving a player.
 */
public sealed class Move extends Action {

    private final int steps;

    /**
     * @param steps The number of steps.
     */
    public Move(int steps) {
        this.steps = steps;
    }

    /**
     * @return The number of steps in this move.
     */
    public int getSteps() {
        return steps;
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

