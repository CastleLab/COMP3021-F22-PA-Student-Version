package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.Action;
import org.jetbrains.annotations.NotNull;

/**
 * An engine for getting inputs from players.
 */
public interface InputEngine {

    /**
     * Fetches an unprocessed action performed by the players.
     *
     * @return the action to process.
     */
    @NotNull
    Action fetchAction();
}
