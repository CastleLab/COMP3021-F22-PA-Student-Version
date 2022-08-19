package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.Action;

/**
 * An engine for getting inputs from players.
 */
public interface InputEngine {

    /**
     * Fetches an unprocessed action performed by the players.
     */
    Action fetchAction();
}
