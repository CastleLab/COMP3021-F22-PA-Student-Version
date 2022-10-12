package hk.ust.comp3021;

import hk.ust.comp3021.game.GameMap;
import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.game.SokobanGame;
import hk.ust.comp3021.tui.TerminalInputEngine;
import hk.ust.comp3021.tui.TerminalRenderingEngine;
import hk.ust.comp3021.tui.TerminalSokobanGame;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Factory for creating Sokoban games
 */
public class SokobanGameFactory {

    /**
     * Create a TUI version of the Sokoban game.
     *
     * @param mapFile map file.
     * @return The Sokoban game.
     * @throws IOException if mapFile cannot be load
     */
    public static @NotNull SokobanGame createTUIGame(@NotNull String mapFile) throws IOException {
        Path file;
        if (!mapFile.endsWith(".map")) {
            // treat as built-in maps
            final var resource = SokobanGameFactory.class.getClassLoader().getResource(mapFile + ".map");
            if (resource == null) throw new RuntimeException("No such built-in map: " + mapFile);
            try {
                file = Path.of(resource.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException("Error loading map:" + mapFile);
            }
        } else {
            file = Path.of(mapFile);
        }
        final var gameMap = loadGameMap(file);
        return new TerminalSokobanGame(
                new GameState(gameMap),
                new TerminalInputEngine(System.in),
                new TerminalRenderingEngine(System.out)
        );
    }


    /**
     * @param mapFile The file containing the game map.
     * @return The parsed game map.
     * @throws IOException When there is an issue loading the file.
     */
    public static @NotNull GameMap loadGameMap(@NotNull Path mapFile) throws IOException {
        final var fileContent = Files.readString(mapFile);
        return GameMap.parse(fileContent);
    }

}
