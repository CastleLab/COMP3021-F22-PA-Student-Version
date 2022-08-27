package hk.ust.comp3021.actions;

/**
 * Exit action instructs the game to exit.
 */
public final class Exit extends Action {
    /**
     * @param initiator The id of the player who performed the action.
     */
    public Exit(int initiator) {
        super(initiator);
    }
}
