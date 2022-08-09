package hk.ust.comp3021.tui;

import hk.ust.comp3021.entities.Box;
import hk.ust.comp3021.entities.Player;
import hk.ust.comp3021.entities.Wall;
import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.game.Position;
import hk.ust.comp3021.game.RenderingEngine;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

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
        var builder = new StringBuilder();
        var lines = state.getEntities().entrySet().stream()
            .collect(Collectors.groupingBy(e -> e.getKey().y()));
        int maxLineNumber = lines.keySet().stream().max(Integer::compare).orElse(-1);
        for (int y = 0; y <= maxLineNumber; y++) {
            var line = lines.get(y).stream()
                .collect(Collectors.toMap(e -> e.getKey().x(), Map.Entry::getValue));
            int maxLineWidth = line.keySet().stream().max(Integer::compare).orElse(-1);
            for (int x = 0; x <= maxLineWidth; x++) {
                var entity = line.get(x);
                if (entity == null && state.getDestinations().contains(new Position(x, y))) {
                    builder.append('@');
                } else if (entity instanceof Wall) {
                    builder.append('#');
                } else if (entity instanceof Box box) {
                    builder.append((char) (box.getPlayerId() + 'a' - 'A'));
                } else if (entity instanceof Player player) {
                    builder.append((char) (player.getId()));
                } else {
                    builder.append('.');
                }
            }
            builder.append('\n');
        }
        System.out.println(builder.toString());
    }
}
