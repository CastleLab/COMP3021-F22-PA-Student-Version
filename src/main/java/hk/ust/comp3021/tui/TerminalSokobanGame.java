package hk.ust.comp3021.tui;


import hk.ust.comp3021.game.*;

import java.io.File;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

    private final InputEngine inputEngine;

    private final RenderingEngine renderingEngine;


    /**
     * Create a new instance of TerminalSokobanGame.
     */
    public TerminalSokobanGame(GameBoard gameBoard) {
        this.state = gameBoard.createGameSession();
        this.inputEngine = new TerminalInputEngine(System.in, gameBoard.getPlayerIds());
        this.renderingEngine = new TerminalRenderingEngine(System.out);
    }

    @Override
    public void run() {
        System.out.println("Sokoban game is ready.");
        renderingEngine.render(state);
        while (!shouldStop()) {
            var action = inputEngine.fetchAction();
            if (action.isPresent()) {
                processAction(action.get().playerId(), action.get().action());
                renderingEngine.render(state);
            }
        }
    }
}
