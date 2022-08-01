package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.Action;
import hk.ust.comp3021.utils.NotImplementedException;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * The state of the Sokoban Game.
 */
public class GameState {

    private GameBoard board;

    private final Stack<Action> actions = new Stack<>();

    private final Set<Position> destinations = new HashSet<>();

    public boolean isWin() {
        throw new NotImplementedException();
    }
}
