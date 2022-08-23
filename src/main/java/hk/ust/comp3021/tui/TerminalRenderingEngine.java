package hk.ust.comp3021.tui;

import hk.ust.comp3021.entities.Box;
import hk.ust.comp3021.entities.Empty;
import hk.ust.comp3021.entities.Player;
import hk.ust.comp3021.entities.Wall;
import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.game.Position;
import hk.ust.comp3021.game.RenderingEngine;
import org.jetbrains.annotations.NotNull;

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
    public void render(@NotNull GameState state) {
        var builder = new StringBuilder();
        var undo = state.getUndoQuota();
        var undoQuotaText = String.format("Undo Quota: %s\n", undo < 0 ? "unlimited" : String.valueOf(undo));
        builder.append(undoQuotaText);
        for (int y = 0; y <= state.getBoardHeight(); y++) {
            for (int x = 0; x <= state.getBoardWidth(); x++) {
                var entity = state.getEntity(Position.of(x, y));
                var charToPrint = switch (entity) {
                    case Wall ignored -> '#';
                    case Box b -> (char) (b.getPlayerId() + 'a');
                    case Player p -> (char) (p.getId() + 'A');
                    case Empty ignored -> state.getDestinations().contains(new Position(x, y)) ? '@' : '.';
                    case null -> ' ';
                };
                builder.append(charToPrint);
            }
            builder.append('\n');
        }
        outputSteam.println(builder.toString());
    }

    @Override
    public void message(@NotNull String content) {
        outputSteam.println(content);
    }
}
