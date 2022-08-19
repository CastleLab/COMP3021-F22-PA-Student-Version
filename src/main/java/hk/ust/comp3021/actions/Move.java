package hk.ust.comp3021.actions;

import hk.ust.comp3021.game.Position;
import hk.ust.comp3021.utils.NotImplementedException;
import org.jetbrains.annotations.NotNull;

/**
 * An actions of moving a player.
 */
public abstract sealed class Move extends Action permits Move.Down, Move.Left, Move.Right, Move.Up {
    @NotNull
    public abstract Position nextPosition(@NotNull Position currentPosition);

    /**
     * The action of moving down.
     */
    public static final class Down extends Move {
        @Override
        @NotNull
        public Position nextPosition(@NotNull Position currentPosition) {
            throw new NotImplementedException();
        }
    }

    /**
     * The action of moving left.
     */
    public static final class Left extends Move {
        @Override
        @NotNull
        public Position nextPosition(@NotNull Position currentPosition) {
            throw new NotImplementedException();
        }
    }

    /**
     * The action of mocking right.
     */
    public static final class Right extends Move {
        @Override
        @NotNull
        public Position nextPosition(@NotNull Position currentPosition) {
            throw new NotImplementedException();
        }
    }

    /**
     * The action of moving up.
     */
    public static final class Up extends Move {
        @Override
        @NotNull
        public Position nextPosition(@NotNull Position currentPosition) {
            throw new NotImplementedException();

        }
    }
}

