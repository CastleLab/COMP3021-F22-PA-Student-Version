package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.Move;
import org.jetbrains.annotations.NotNull;

/**
 * Denotes a position on a game board.
 * The original point is at top-left corner.
 * The coordinates start from 0;
 *
 * @param x The horizontal displacement.
 * @param y The vertical displacement.
 */
public record Position(int x, int y) {
    @NotNull Position shift(@NotNull Move move) {
        return switch (move) {
            case Move.Down down -> new Position(this.x, this.y + 1);
            case Move.Left left -> new Position(this.x - 1, this.y);
            case Move.Right right -> new Position(this.x + 1, this.y);
            case Move.Up up -> new Position(this.x, this.y - 1);
        };
    }

    /**
     * Creates a new position.
     * @param x The horizontal displacement.
     * @param y The vertical displacement.
     * @return The newly created position.
     */
    public static Position of(int x, int y) {
        return new Position(x, y);
    }
}
