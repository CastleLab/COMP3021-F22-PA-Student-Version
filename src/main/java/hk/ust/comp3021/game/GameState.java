package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.Move;
import hk.ust.comp3021.entities.Box;
import hk.ust.comp3021.entities.Entity;
import hk.ust.comp3021.entities.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The state of the Sokoban Game.
 * GameState consists of things changing as the game goes, such as:
 * - Current locations of all crates.
 * - A move history.
 * - Current location of player.
 */
public class GameState {

    private final Stack<Transition> history = new Stack<>();

    private final Map<Position, Entity> entities;

    private final int boardWidth;

    private final int boardHeight;

    private final Set<Position> destinations;

    private int undoQuota;

    private Transition currentTransition = new Transition();

    public GameState(@NotNull GameMap board) {
        this.entities = new HashMap<>();
        this.boardWidth = board.getMaxWidth();
        this.boardHeight = board.getMaxHeight();

        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                final var pos = Position.of(x, y);
                final var entity = board.getEntity(pos);
                if (entity != null)
                    this.entities.put(pos, entity);
            }
        }
        this.destinations = board.getDestinations();
        undoQuota = board.getUndoLimit();
    }

    public @Nullable Position getPlayerPositionById(int id) {
        return this.entities.entrySet().stream()
                .filter(e -> e.getValue() instanceof Player p && p.getId() == id)
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }

    public @Nullable Entity getEntity(@NotNull Position position) {
        return this.entities.get(position);
    }

    public @NotNull @Unmodifiable Set<Position> getDestinations() {
        return destinations;
    }

    public int getUndoQuota() {
        return undoQuota;
    }

    public boolean isWin() {
        return this.destinations.stream().allMatch(p -> this.entities.get(p) instanceof Box);
    }

    /**
     * Returns true if none of the accessible boxes is movable.
     *
     * @return Whether there is no possible moves of the game.
     */
    public boolean isStuck() {
        final var expandQueue = new ArrayDeque<Position>();
        final var visited = new HashSet<Position>();
        final var moves = new Move[]{
                new Move.Down(-1), new Move.Right(-1), new Move.Left(-1), new Move.Up(-1)
        };
        this.entities.entrySet().stream()
                .filter(e -> e.getValue() instanceof Player)
                .forEach(e -> expandQueue.add(e.getKey()));
        while (!expandQueue.isEmpty()) {
            final var p = expandQueue.pop();
            if (visited.contains(p)) continue;
            visited.add(p);
            for (var m : moves) {
                final var adj = m.nextPosition(p);
                final var entity = this.entities.get(adj);
                if (entity == null || entity instanceof Player) {
                    expandQueue.add(adj);
                } else if (entity instanceof Box) {
                    final var e = this.entities.get(m.nextPosition(adj));
                    // FIXME there is a bug in this logic. Some corner deadlock cases of multiple players cannot be recognized.
                    if (e == null || e instanceof Player) {
                        return false; // the box is movable by the player.
                    }
                }
            }
        }
        return true;
    }

    /**
     * Move the entity from one position to another.
     * This method assumes the validity of this move is ensured.
     *
     * @param from The position of the source location.
     * @param to   The position of the destination location.
     */
    void move(Position from, Position to) {
        // move entity
        final var entity = this.entities.remove(from);
        if (entity == null) return;
        this.entities.put(to, entity);

        // append to history
        this.currentTransition.add(from, to);
    }

    void checkpoint() {
        this.history.push(this.currentTransition);
        this.currentTransition = new Transition();
    }

    /**
     * Apply transition on current entity map.
     * History is not touched in this method.
     * Callers should maintain history themselves.
     *
     * @param transition
     */
    private void applyTransition(Transition transition) {
        transition.moves.entrySet().stream()
                .map(e -> {
                    final var entity = this.entities.remove(e.getKey());
                    return Map.entry(e.getValue(), entity);
                })
                .toList()
                .forEach(e -> this.entities.put(e.getKey(), e.getValue()));
    }

    /**
     * Revert to the last checkpoint in history.
     * This method assumes there is still undo quota left.
     * If the history is empty, undo quota is not decreased.
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

    public int getBoardWidth() {
        return boardWidth;
    }

    public int getBoardHeight() {
        return boardHeight;
    }

    static class Transition {
        private final Map<Position, Position> moves;

        void add(Position from, Position to) {
            final var key = this.moves.entrySet().stream()
                    .filter(e -> e.getValue().equals(from))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(from);
            this.moves.put(key, to);
        }

        Transition(Map<Position, Position> moves) {
            this.moves = moves;
        }

        Transition() {
            this.moves = new HashMap<>();
        }

        Transition reverse() {
            final var moves = this.moves.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            return new Transition(moves);
        }

        boolean empty() {
            return this.moves.size() == 0;
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
