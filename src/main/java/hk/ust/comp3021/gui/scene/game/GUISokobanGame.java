package hk.ust.comp3021.gui.scene.game;

import hk.ust.comp3021.actions.ActionResult;
import hk.ust.comp3021.game.AbstractSokobanGame;
import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.game.InputEngine;
import hk.ust.comp3021.game.RenderingEngine;
import org.jetbrains.annotations.NotNull;

import static hk.ust.comp3021.utils.StringResources.WIN_MESSAGE;

/**
 * Java FX implementation of the Sokoban game.
 */
public class GUISokobanGame extends AbstractSokobanGame implements Runnable {
    private final InputEngine inputEngine;
    private final RenderingEngine renderingEngine;

    /**
     * @param state           The game state.
     * @param inputEngine     The input engine.
     * @param renderingEngine The rendering engine.
     */
    public GUISokobanGame(@NotNull GameState state, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(state);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    @Override
    public void run() {
        renderingEngine.render(state);
        while (!shouldStop()) {
            final var action = inputEngine.fetchAction();
            final var result = processAction(action);
            if (result instanceof ActionResult.Failed failure) {
                renderingEngine.message(failure.getReason());
            }
            renderingEngine.render(state);
        }
        if (this.state.isWin()) {
            renderingEngine.message(WIN_MESSAGE);
        }
    }

}
