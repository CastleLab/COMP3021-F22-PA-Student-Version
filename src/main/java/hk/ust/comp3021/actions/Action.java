package hk.ust.comp3021.actions;

/**
 * An action performed by a player.
 */
public abstract sealed class Action permits InvalidInput, Move, Undo, Exit {

    protected final int initiator;

    /**
     * @return The id of the player who performed the action.
     */
    public int getInitiator() {
        return initiator;
    }

    protected Action(int initiator) {
        this.initiator = initiator;
    }
}
