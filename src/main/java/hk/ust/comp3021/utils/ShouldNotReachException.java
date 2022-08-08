package hk.ust.comp3021.utils;

/**
 * Thrown when a branch should not be reached. Used to avoid compilation error.
 */
public class ShouldNotReachException extends RuntimeException {

    /**
     * Create a new should not reach exception.
     */
    public ShouldNotReachException() {
        super("This branch should not be reached.");
    }
}
