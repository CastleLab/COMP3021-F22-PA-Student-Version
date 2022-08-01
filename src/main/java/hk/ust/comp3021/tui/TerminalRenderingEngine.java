package hk.ust.comp3021.tui;

import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.game.RenderingEngine;

/**
 * A rendering engine that prints to the terminal.
 */
public class TerminalRenderingEngine implements RenderingEngine {
    @Override
    public void render(GameState state) {
        System.out.println(233);
    }
}
