package hk.ust.comp3021.actions;

import org.jetbrains.annotations.NotNull;

/**
 * Denote an invalid input.
 */
public final class InvalidInput extends Action {

    private final String message;

    /**
     * @param initiator The id of the player who give the invalid input
     * @param message   The error message.
     */
    public InvalidInput(int initiator, @NotNull String message) {
        super(initiator);
        this.message = message;
    }

    /**
     * @return The error message.
     */
    public String getMessage() {
        return message;
    }
}
