package hk.ust.comp3021;

import hk.ust.comp3021.game.GameBoard;
import hk.ust.comp3021.game.SokobanGame;
import hk.ust.comp3021.tui.TerminalSokobanGame;

import java.io.File;
import java.io.IOException;

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
        File file;
        if (!mapFile.endsWith(".map")) {
            // treat as built-in maps
            var resource = SokobanGameFactory.class.getClassLoader().getResource(mapFile + ".map");
            if (resource == null) throw new RuntimeException("No such built-in map: " + mapFile);
            file = new File(resource.getFile());
        } else {
            file = new File(mapFile);
        }
        var gameBoard = GameBoard.loadGameMap(file, undoLimit);
        return new TerminalSokobanGame(gameBoard);
    }
}
