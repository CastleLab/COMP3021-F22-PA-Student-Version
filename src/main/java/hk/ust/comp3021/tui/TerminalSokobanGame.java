package hk.ust.comp3021.tui;


import hk.ust.comp3021.actions.ActionResult;
import hk.ust.comp3021.actions.Exit;
import hk.ust.comp3021.game.*;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

    private final InputEngine inputEngine;

    private final RenderingEngine renderingEngine;

    /**
     * Create a new instance of TerminalSokobanGame.
     */
    public TerminalSokobanGame(GameMap gameMap) {
        super(new GameState(gameMap));
        this.inputEngine = new TerminalInputEngine(System.in);
        this.renderingEngine = new TerminalRenderingEngine(System.out);
    }

    @Override
    public void run() {
        System.out.println("Sokoban game is ready.");
        renderingEngine.render(state);
        while (!shouldStop()) {
            System.out.println(">>> ");
            var action = inputEngine.fetchAction();
            if (action instanceof Exit) {
                System.out.println("Game exits.");
                return;
            }
            var result = processAction(action);
            if (result instanceof ActionResult.Failed r) {
                renderingEngine.message(r.getReason());
                break;
            }
            renderingEngine.render(state);
        }
        if (this.state.isWin()) {
            renderingEngine.message("You win.");
        } else if (this.state.isDeadlock()) {
            renderingEngine.message("You lose.");
        } else {
            renderingEngine.message("Exit unexpectedly.");
        }
    }
}
