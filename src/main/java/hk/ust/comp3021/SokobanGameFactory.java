package hk.ust.comp3021;

import hk.ust.comp3021.game.SokobanGame;
import hk.ust.comp3021.tui.TerminalInputEngine;
import hk.ust.comp3021.tui.TerminalRenderingEngine;
import hk.ust.comp3021.tui.TerminalSokobanGame;

/**
 * Factory for creating Sokoban games
 */
public class SokobanGameFactory {

    /**
     * Create a TUI version of the Sokoban game.
     *
     * @return The Sokoban game.
     */
    public static SokobanGame createTUIGame() {
        return new TerminalSokobanGame(
                new TerminalInputEngine(System.in),
                new TerminalRenderingEngine(System.out)
        );
    }
}
