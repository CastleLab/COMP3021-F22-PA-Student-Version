package hk.ust.comp3021.tui;

import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.game.RenderingEngine;

import java.io.PrintStream;

/**
 * A rendering engine that prints to the terminal.
 */
public class TerminalRenderingEngine implements RenderingEngine {

    private final PrintStream outputSteam;

    /**
     * @param outputSteam The {@link PrintStream} to write the output to.
     */
    public TerminalRenderingEngine(PrintStream outputSteam) {
        this.outputSteam = outputSteam;
    }

    @Override
    public void render(GameState state) {
        outputSteam.println(233);
    }
}
