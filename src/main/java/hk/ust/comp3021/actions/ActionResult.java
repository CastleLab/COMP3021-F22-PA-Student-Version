package hk.ust.comp3021.actions;

import org.jetbrains.annotations.NotNull;

/**
 * The result of an action.
 */
public abstract sealed class ActionResult {

    protected final Action action;

    /**
     * @param action The action.
     */
    protected ActionResult(@NotNull Action action) {
        this.action = action;
    }

    /**
     * @return The action.
     */
    public @NotNull Action getAction() {
        return action;
    }

    /**
     * Denotes a successful result.
     */
    public static final class Success extends ActionResult {

        /**
         * @param action The action.
         */
        public Success(@NotNull Action action) {
            super(action);
        }
    }

    /**
     * Denotes a failing result.
     */
    public static final class Failed extends ActionResult {

        private final String reason;

        /**
         * @return The reason for the failure.
         */
        public @NotNull String getReason() {
            return reason;
        }

        /**
         * @param action The action.
         * @param reason The reason for the failure.
         */
        public Failed(@NotNull Action action, @NotNull String reason) {
            super(action);
            this.reason = reason;
        }
    }
}
