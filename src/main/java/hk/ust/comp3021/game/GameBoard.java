package hk.ust.comp3021.game;

import hk.ust.comp3021.entities.Empty;
import hk.ust.comp3021.entities.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * A Sokoban game board.
 */
public class GameBoard {

    private final int width;

    private final int height;

    private final Map<Position, Entity> entities = new HashMap<>();

    /**
     * Initializes a new instance of GameBoard.
     *
     * @param width  The width of the board.
     * @param height The height of the board.
     */
    public GameBoard(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the width of the game board.
     *
     * @return The width of the game board.
     */
    int getWidth() {
        return width;
    }

    /**
     * Gets the height of the game board.
     *
     * @return The height of the game board.
     */
    int getHeight() {
        return height;
    }

    /**
     * Gets the entity on the board at the given position.
     *
     * @param position The position.
     * @return The {@link Entity} at the specified position.
     */
    Entity getCell(Position position) {
        return entities.getOrDefault(position, new Empty());
    }
}
