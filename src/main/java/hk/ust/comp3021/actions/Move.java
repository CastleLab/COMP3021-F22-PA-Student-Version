package hk.ust.comp3021.actions;

import hk.ust.comp3021.game.Position;
import org.jetbrains.annotations.NotNull;

/**
 * An actions of moving a player.
 */
public abstract sealed class Move extends Action permits Move.Down, Move.Left, Move.Right, Move.Up {
    protected Move(int initiator) {
        super(initiator);
    }

    /**
     * Generates the next position after the move based on the current position.
     *
     * @param currentPosition The current position.
     * @return The next position.
     */
    public abstract @NotNull Position nextPosition(@NotNull Position currentPosition);

    /**
     * The action of moving down.
     */
    public static final class Down extends Move {

        /**
         * @param initiator The id of the player who give the invalid input.
         */
        public Down(int initiator) {
            super(initiator);
        }

        @Override
        public @NotNull Position nextPosition(@NotNull Position currentPosition) {
            return Position.of(currentPosition.x(), currentPosition.y() + 1);
        }
    }

    /**
     * The action of moving left.
     */
    public static final class Left extends Move {
        /**
         * @param initiator The id of the player who give the invalid input.
         */
        public Left(int initiator) {
            super(initiator);
        }

        @Override
        public @NotNull Position nextPosition(@NotNull Position currentPosition) {
            return Position.of(currentPosition.x() - 1, currentPosition.y());
        }
    }

    /**
     * The action of mocking right.
     */
    public static final class Right extends Move {
        /**
         * @param initiator The id of the player who give the invalid input.
         */
        public Right(int initiator) {
            super(initiator);
        }

        @Override
        public @NotNull Position nextPosition(@NotNull Position currentPosition) {
            return Position.of(currentPosition.x() + 1, currentPosition.y());
        }
    }

    /**
     * The action of moving up.
     */
    public static final class Up extends Move {
        /**
         * @param initiator The id of the player who give the invalid input.
         */
        public Up(int initiator) {
            super(initiator);
        }

        @Override
        public @NotNull Position nextPosition(@NotNull Position currentPosition) {
            return Position.of(currentPosition.x(), currentPosition.y() - 1);
        }
    }
}

