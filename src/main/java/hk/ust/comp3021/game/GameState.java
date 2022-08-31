package hk.ust.comp3021.game;

import hk.ust.comp3021.entities.Box;
import hk.ust.comp3021.entities.Empty;
import hk.ust.comp3021.entities.Entity;
import hk.ust.comp3021.entities.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The state of the Sokoban Game.
 * Each game state represents an ongoing game.
 * As the game goes, the game state changes while players are moving while the original game map stays the unmodified.
 * <b>The game state should not modify the original game map.</b>
 * <p>
 * GameState consists of things changing as the game goes, such as:
 * <li>Current locations of all crates.</li>
 * <li>A move history.</li>
 * <li>Current location of player.</li>
 * <li>Undo quota left.</li>
 */
public class GameState {

    private final Stack<Transition> history = new Stack<>();

    private final Map<Position, Entity> entities;

    private final int boardWidth;

    private final int boardHeight;

    private final Set<Position> destinations;

    private int undoQuota;

    private Transition currentTransition = new Transition();

    /**
     * Create a running game state from a game map.
     *
     * @param map the game map from which to create this game state.
     */
    public GameState(@NotNull GameMap map) {
        this.entities = new HashMap<>();
        this.boardWidth = map.getMaxWidth();
        this.boardHeight = map.getMaxHeight();

        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                final var pos = Position.of(x, y);
                final var entity = map.getEntity(pos);
                if (entity != null)
                    this.entities.put(pos, entity);
            }
        }
        this.destinations = map.getDestinations();
        undoQuota = map.getUndoLimit().orElse(-1);
    }

    /**
     * Get the current position of the player with the given id.
     *
     * @param id player id.
     * @return the current position of the player.
     */
    public @Nullable Position getPlayerPositionById(int id) {
        return this.entities.entrySet().stream()
                .filter(e -> e.getValue() instanceof Player p && p.getId() == id)
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }

    /**
     * Get current positions of all players in the game map.
     *
     * @return a set of positions of all players.
     */
    public @NotNull Set<Position> getAllPlayerPositions() {
        return this.entities.entrySet().stream()
                .filter(e -> e.getValue() instanceof Player)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * Get the entity that is currently at the given position.
     *
     * @param position the position of the entity.
     * @return the entity object.
     */
    public @Nullable Entity getEntity(@NotNull Position position) {
        return this.entities.get(position);
    }

    /**
     * Get all box destination positions as a set in the game map.
     * This should be the same as that in {@link GameMap} class.
     *
     * @return a set of positions.
     */
    public @NotNull @Unmodifiable Set<Position> getDestinations() {
        return destinations;
    }

    /**
     * Get the undo quota currently left, i.e., the maximum number of undo actions that can be performed from now on.
     * If undo is unlimited,
     *
     * @return the undo quota left (using {@link Optional#of(Object)}) if the game has an undo limit;
     * {@link Optional#empty()} if the game has unlimited undo.
     */
    public Optional<Integer> getUndoQuota() {
        if (this.undoQuota < 0) {
            return Optional.empty();
        } else {
            return Optional.of(this.undoQuota);
        }
    }

    /**
     * Check whether the game wins or not.
     * The game wins only when all box destinations have been occupied by boxes.
     *
     * @return true is the game wins.
     */
    public boolean isWin() {
        return this.destinations.stream().allMatch(p -> this.entities.get(p) instanceof Box);
    }

    /**
     * Move the entity from one position to another.
     * This method assumes the validity of this move is ensured.
     * <b>The validity of the move of the entity in one position to another need not to check.</b>
     *
     * @param from The current position of the entity to move.
     * @param to   The position to move the entity to.
     */
    public void move(Position from, Position to) {
        // move entity
        final var entity = this.entities.remove(from);
        this.entities.put(from, new Empty());
        this.entities.put(to, entity);

        // append to history
        this.currentTransition.add(from, to);
    }

    /**
     * Record a checkpoint of the game state, including:
     * <li>All current positions of entities in the game map.</li>
     * <li>Current undo quota</li>
     * <p>
     * Checkpoint is used in {@link GameState#undo()}.
     * Every undo actions reverts the game state to the last checkpoint.
     */
    public void checkpoint() {
        this.history.push(this.currentTransition);
        this.currentTransition = new Transition();
    }

    /**
     * Apply transition on current entity map.
     * History is not touched in this method.
     * Callers should maintain history themselves.
     *
     * @param transition the transition to apply.
     */
    private void applyTransition(Transition transition) {
        transition.moves.entrySet().stream()
                .map(e -> {
                    final var entity = this.entities.remove(e.getKey());
                    this.entities.put(e.getKey(), new Empty());
                    return Map.entry(e.getValue(), entity);
                })
                .toList()
                .forEach(e -> this.entities.put(e.getKey(), e.getValue()));
    }

    /**
     * Revert the game state to the last checkpoint in history.
     * This method assumes there is still undo quota left, and decreases the undo quota by one.
     * <p>
     * If there is no checkpoint recorded, i.e., before moving any box when the game starts,
     * revert to the initial game state.
     */
    public void undo() {
        final var undoTransition = this.currentTransition.reverse();
        this.applyTransition(undoTransition);
        if (!this.history.empty()) {
            final var historyTransaction = this.history.pop().reverse();
            this.applyTransition(historyTransaction);
            this.undoQuota--;
        }
    }

    /**
     * Get the maximum width of the game map.
     * This should be the same as that in {@link GameMap} class.
     *
     * @return maximum width.
     */
    public int getMapMaxWidth() {
        return boardWidth;
    }

    /**
     * Get the maximum height of the game map.
     * This should be the same as that in {@link GameMap} class.
     *
     * @return maximum height.
     */
    public int getMapMaxHeight() {
        return boardHeight;
    }

    private static class Transition {
        private final Map<Position, Position> moves;

        private void add(Position from, Position to) {
            final var key = this.moves.entrySet().stream()
                    .filter(e -> e.getValue().equals(from))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(from);
            this.moves.put(key, to);
        }

        private Transition(Map<Position, Position> moves) {
            this.moves = moves;
        }

        private Transition() {
            this.moves = new HashMap<>();
        }

        private Transition reverse() {
            final var moves = this.moves.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            return new Transition(moves);
        }

        @Override
        public String toString() {
            final var moves = this.moves.entrySet().stream()
                    .map(e -> String.format("(%d,%d)->(%d,%d)", e.getKey().x(), e.getKey().y(), e.getValue().x(), e.getValue().y()))
                    .toList();
            return String.join(",", moves);
        }
    }
}
