package hk.ust.comp3021.game;

/**
 * An engine for rendering the game state to the players.
 */
public interface RenderingEngine {

    /**
     * Renders the game based on the current state.
     *
     * @param state The current game state.
     */
    void render(GameState state);

    void message(String string);
}
