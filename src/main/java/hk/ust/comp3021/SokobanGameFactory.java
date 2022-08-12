package hk.ust.comp3021;

import hk.ust.comp3021.game.GameMap;
import hk.ust.comp3021.game.SokobanGame;
import hk.ust.comp3021.tui.TerminalSokobanGame;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

/**
 * Factory for creating Sokoban games
 */
public class SokobanGameFactory {

    /**
     * Create a TUI version of the Sokoban game.
     *
     * @return The Sokoban game.
     */
    public static SokobanGame createTUIGame(String mapFile, int undoLimit) throws IOException {
        Path file;
        if (!mapFile.endsWith(".map")) {
            // treat as built-in maps
            var resource = SokobanGameFactory.class.getClassLoader().getResource(mapFile + ".map");
            if (resource == null) throw new RuntimeException("No such built-in map: " + mapFile);
            try {
                file = Path.of(resource.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException("Error loading map:" + mapFile);
            }
        } else {
            file = Path.of(mapFile);
        }
        var gameBoard = GameMap.loadGameMap(file, undoLimit);
        return new TerminalSokobanGame(gameBoard);
    }
}
