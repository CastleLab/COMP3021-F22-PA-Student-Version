package hk.ust.comp3021.tui;


import hk.ust.comp3021.actions.ActionResult;
import hk.ust.comp3021.game.AbstractSokobanGame;
import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.game.InputEngine;
import hk.ust.comp3021.game.RenderingEngine;

import static hk.ust.comp3021.utils.StringResources.*;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

    private final InputEngine inputEngine;

    private final RenderingEngine renderingEngine;

    /**
     * Create a new instance of TerminalSokobanGame.
     *
     * @param gameState The game state.
     */
    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    @Override
    public void run() {
        renderingEngine.message(GAME_READY_MESSAGE);
        renderingEngine.render(state);
        while (!shouldStop()) {
            final var undoQuota = state.getUndoQuota();
            final var undoQuotaMessage = String.format(UNDO_QUOTA_TEMPLATE, undoQuota < 0 ? UNDO_QUOTA_UNLIMITED : String.valueOf(undoQuota));
            renderingEngine.message(undoQuotaMessage);
            renderingEngine.message(">>> ");
            final var action = inputEngine.fetchAction();
            final var result = processAction(action);
            if (result instanceof ActionResult.Failed r) {
                renderingEngine.message(r.getReason());
            }
            renderingEngine.render(state);
        }
        renderingEngine.message(GAME_EXIT_MESSAGE);
        if (this.state.isWin()) {
            renderingEngine.message(WIN_MESSAGE);
        } else if (this.state.isStuck()) {
            renderingEngine.message(LOSE_MESSAGE);
        }
    }
}
