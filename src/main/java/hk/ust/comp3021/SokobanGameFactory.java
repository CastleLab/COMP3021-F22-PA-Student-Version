package hk.ust.comp3021;

import hk.ust.comp3021.game.GameBoard;
import hk.ust.comp3021.game.SokobanGame;
import hk.ust.comp3021.tui.TerminalSokobanGame;

import java.io.File;

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
        var resource = SokobanGameFactory.class.getClassLoader().getResource("map01.txt");
        try {
            var gameBoard = GameBoard.loadGameMap(new File(resource.getFile()));
            return new TerminalSokobanGame(gameBoard);
        } catch (Exception ignored) {
            throw new RuntimeException();
        }
    }
}
