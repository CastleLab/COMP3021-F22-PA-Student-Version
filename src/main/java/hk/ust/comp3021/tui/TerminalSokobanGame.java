package hk.ust.comp3021.tui;


import hk.ust.comp3021.actions.ActionResult;
import hk.ust.comp3021.actions.Exit;
import hk.ust.comp3021.game.AbstractSokobanGame;
import hk.ust.comp3021.game.GameBoard;
import hk.ust.comp3021.game.InputEngine;
import hk.ust.comp3021.game.RenderingEngine;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

    private final InputEngine inputEngine;

    private final RenderingEngine renderingEngine;

    private final int[] playerIds;

    /**
     * Create a new instance of TerminalSokobanGame.
     */
    public TerminalSokobanGame(GameBoard gameBoard) {
        this.state = gameBoard.createGameSession();
        this.playerIds = gameBoard.getPlayerIds();
        this.inputEngine = new TerminalInputEngine(System.in);
        this.renderingEngine = new TerminalRenderingEngine(System.out);
    }

    @Override
    public void run() {
        System.out.println("Sokoban game is ready.");
        renderingEngine.render(state);
        while (!shouldStop()) {
            var action = inputEngine.fetchAction(this.playerIds);
            for (var act :
                action.actions()) {
                if (act instanceof Exit) {
                    System.out.println("Game exits.");
                    return;
                }
                if (action.playerId() == null) throw new IllegalArgumentException();
                var result = processAction(action.playerId(), act);
                if (result instanceof ActionResult.Failed r) {
                    renderingEngine.message(r.getReason());
                    break;
                }
            }
            renderingEngine.render(state);
        }
        if (this.state.isWin()) {
            System.out.println("You win.");
        } else if (this.state.isDeadlock()) {
            System.out.println("You lose.");
        } else {
            System.out.println("Exit unexpectedly.");
        }
    }
}
