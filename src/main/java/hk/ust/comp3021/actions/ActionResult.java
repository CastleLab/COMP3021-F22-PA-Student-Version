package hk.ust.comp3021.actions;

/**
 * The result of an action.
 */
public sealed class ActionResult {

    protected final Action action;

    /**
     * @param action The action.
     */
    protected ActionResult(Action action) {
        this.action = action;
    }

    /**
     * @return The action.
     */
    public Action getAction() {
        return action;
    }

    /**
     * Denotes a successful result.
     */
    public static final class Success extends ActionResult {

        /**
         * @param action The action.
         */
        public Success(Action action) {
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
        public String getReason() {
            return reason;
        }

        /**
         * @param action The action.
         * @param reason The reason for the failure.
         */
        public Failed(Action action, String reason) {
            super(action);
            this.reason = reason;
        }
    }
}
