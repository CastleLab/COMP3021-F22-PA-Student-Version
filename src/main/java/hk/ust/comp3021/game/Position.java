package hk.ust.comp3021.game;

import org.jetbrains.annotations.NotNull;

/**
 * Denotes a position on a game map.
 * The original point is at top-left corner.
 * The coordinates start from 0;
 *
 * @param x The horizontal displacement.
 * @param y The vertical displacement.
 */
public record Position(int x, int y) {

    /**
     * Creates a new position.
     *
     * @param x The horizontal displacement.
     * @param y The vertical displacement.
     * @return The newly created position.
     */
    public static @NotNull Position of(int x, int y) {
        return new Position(x, y);
    }

}
