package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.Action;

import java.util.Optional;

/**
 * An engine for getting inputs from players.
 */
public interface InputEngine {

    /**
     * Fetches an unprocessed action performed by the players.
     *
     * @return Returns an {@link Optional} with an action. Returns an empty {@link Optional} there is no unprocessed action.
     */
    Optional<? extends Action> fetchAction();

}
