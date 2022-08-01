package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.Action;

/**
 * A base implementation of Sokoban Game.
 */
public abstract class AbstractSokobanGame implements SokobanGame {

    protected GameState state;

    protected boolean shouldStop() {
        return false;
    }

    protected void processAction(Action action) {
    }

}
