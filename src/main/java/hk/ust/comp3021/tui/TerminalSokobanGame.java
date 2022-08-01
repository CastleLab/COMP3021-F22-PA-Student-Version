package hk.ust.comp3021.tui;


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
     * @param inputEngine     The input engine.
     * @param renderingEngine The rendering engine.
     */
    public TerminalSokobanGame(InputEngine inputEngine, RenderingEngine renderingEngine) {
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    @Override
    public void run() {
        System.out.println("Sokoban game is ready.");
        while (!shouldStop()) {
            var action = inputEngine.fetchAction();
            if (action.isPresent()) {
                processAction(action.get());
                renderingEngine.render(state);
            }
        }
    }
}
