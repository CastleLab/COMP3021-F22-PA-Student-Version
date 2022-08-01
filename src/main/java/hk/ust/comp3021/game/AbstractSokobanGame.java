package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.Action;
import hk.ust.comp3021.actions.ActionResult;
import hk.ust.comp3021.utils.NotImplementedException;

/**
 * A base implementation of Sokoban Game.
 */
public abstract class AbstractSokobanGame implements SokobanGame {

    protected GameState state;

    protected boolean shouldStop() {
        return false;
    }

    protected void processAction(Action action) {
        throw new NotImplementedException();
    }

}
