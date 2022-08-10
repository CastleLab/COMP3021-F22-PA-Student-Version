package hk.ust.comp3021.actions;

/**
 * An action performed by a player.
 */
public abstract sealed class Action permits InvalidInput, Move, Undo, Exit {
}
