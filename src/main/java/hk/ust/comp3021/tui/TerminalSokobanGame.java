package hk.ust.comp3021.tui;


import hk.ust.comp3021.actions.ActionResult;
import hk.ust.comp3021.game.AbstractSokobanGame;
import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.game.InputEngine;
import hk.ust.comp3021.game.RenderingEngine;

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
        renderingEngine.message("Sokoban game is ready.");
        renderingEngine.render(state);
        while (!shouldStop()) {
            var undoQuota = state.getUndoQuota();
            var undoQuotaMessage = String.format("Undo Quota: %s\n", undoQuota < 0 ? "unlimited" : String.valueOf(undoQuota));
            renderingEngine.message(undoQuotaMessage);
            renderingEngine.message(">>> ");
            var action = inputEngine.fetchAction();
            var result = processAction(action);
            if (result instanceof ActionResult.Failed r) {
                renderingEngine.message(r.getReason());
            }
            renderingEngine.render(state);
        }
        renderingEngine.message("Game exits.");
        if (this.state.isWin()) {
            renderingEngine.message("You win.");
        } else if (this.state.isStuck()) {
            renderingEngine.message("You lose.");
        }
    }
}
