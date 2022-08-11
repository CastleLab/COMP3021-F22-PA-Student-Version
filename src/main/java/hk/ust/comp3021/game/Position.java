package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.Move;
import hk.ust.comp3021.utils.ShouldNotReachException;
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
        if (move instanceof Move.Down) {
            return new Position(this.x, this.y + 1);
        } else if (move instanceof Move.Left) {
            return new Position(this.x - 1, this.y);
        } else if (move instanceof Move.Right) {
            return new Position(this.x + 1, this.y);
        } else if (move instanceof Move.Up) {
            return new Position(this.x, this.y - 1);
        } else {
            throw new ShouldNotReachException();
        }
    }
}
