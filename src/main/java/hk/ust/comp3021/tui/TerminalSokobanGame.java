package hk.ust.comp3021.tui;


import hk.ust.comp3021.actions.ActionResult;
import hk.ust.comp3021.game.*;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

    private final InputEngine inputEngine;

    private final RenderingEngine renderingEngine;

    /**
     * Create a new instance of TerminalSokobanGame.
     *
     * @param gameMap The game map.
     */
    public TerminalSokobanGame(GameMap gameMap) {
        super(new GameState(gameMap));
        this.inputEngine = new TerminalInputEngine(System.in);
        this.renderingEngine = new TerminalRenderingEngine(System.out);
    }

    @Override
    public void run() {
        renderingEngine.message("Sokoban game is ready.");
        renderingEngine.render(state);
        while (!shouldStop()) {
            renderingEngine.message(">>> ");
            var action = inputEngine.fetchAction();
            var result = processAction(action);
            if (result instanceof ActionResult.Failed r) {
                renderingEngine.message(r.getReason());
                break;
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
