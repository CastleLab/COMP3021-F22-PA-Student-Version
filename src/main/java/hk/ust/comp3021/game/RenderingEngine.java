package hk.ust.comp3021.game;

import org.jetbrains.annotations.NotNull;

/**
 * An engine for rendering the game state to the players.
 */
public interface RenderingEngine {

    /**
     * Renders the game based on the current state.
     *
     * @param state The current game state.
     */
    void render(@NotNull GameState state);

    /**
     * Display a message to the player.
     *
     * @param content The message
     */
    void message(@NotNull String content);
}
