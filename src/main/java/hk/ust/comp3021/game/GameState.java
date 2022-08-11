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

    private final Set<Position> destinations;

    private int undoQuota;

    private Transition currentTransition = new Transition();

    GameState(@NotNull GameBoard board) {
        this.entities = new HashMap<>(board.getMap());
        this.destinations = new HashSet<>(board.getDestinations());
        undoQuota = board.getUndoLimit();
    }

    public @Nullable Position getPlayerPositionById(int id) {
        return this.entities.entrySet().stream()
            .filter(e -> e.getValue() instanceof Player p && p.getId() == id)
            .map(Map.Entry::getKey)
            .findFirst().orElse(null);
    }

    public @Nullable Entity getEntity(Position position) {
        return this.entities.get(position);
    }

    public @NotNull @Unmodifiable Map<Position, Entity> getEntities() {
        return entities;
    }

    public @NotNull @Unmodifiable Set<Position> getDestinations() {
        return destinations;
    }

    public int getUndoQuota() {
        return undoQuota;
    }

    public Stack<Transition> getHistory() {
        return history;
    }

    public boolean isWin() {
        return this.destinations.stream().allMatch(p -> this.entities.get(p) instanceof Box);
    }

    /**
     * Returns true if none of the accessible boxes is movable.
     *
     * @return
     */
    public boolean isDeadlock() {
        var expandQueue = new ArrayDeque<Position>();
        var visited = new HashSet<Position>();
        var moves = new Move[]{
            new Move.Down(), new Move.Right(), new Move.Left(), new Move.Up()
        };
        this.entities.entrySet().stream()
            .filter(e -> e.getValue() instanceof Player)
            .forEach(e -> expandQueue.add(e.getKey()));
        while (!expandQueue.isEmpty()) {
            var p = expandQueue.pop();
            if (visited.contains(p)) continue;
            visited.add(p);
            for (var m :
                moves) {
                var adj = p.shift(m);
                var entity = this.entities.get(adj);
                if (entity == null || entity instanceof Player) {
                    expandQueue.add(adj);
                } else if (entity instanceof Box) {
                    var e = this.entities.get(adj.shift(m));
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
     * @param from
     * @param to
     */
    void move(Position from, Position to) {
        // move entity
        var entity = this.entities.remove(from);
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
                var entity = this.entities.remove(e.getKey());
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
        var undoTransition = this.currentTransition.reverse();
        this.applyTransition(undoTransition);
        if (this.history.empty()) return;
        undoTransition = this.history.pop().reverse();
        this.applyTransition(undoTransition);
        this.undoQuota--;
    }

    static class Transition {
        private final Map<Position, Position> moves;

        public void add(Position from, Position to) {
            var key = this.moves.entrySet().stream()
                .filter(e -> e.getValue().equals(from))
                .map(Map.Entry::getKey)
                .findFirst().orElse(from);
            this.moves.put(key, to);
        }

        public Transition(Map<Position, Position> moves) {
            this.moves = moves;
        }

        public Transition() {
            this.moves = new HashMap<>();
        }

        public Transition reverse() {
            var moves = this.moves.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            return new Transition(moves);
        }

        public boolean empty() {
            return this.moves.size() == 0;
        }

        @Override
        public String toString() {
            var moves = this.moves.entrySet().stream()
                .map(e -> String.format("(%d,%d)->(%d,%d)", e.getKey().x(), e.getKey().y(), e.getValue().x(), e.getValue().y()))
                .toList();
            return String.join(",", moves);
        }
    }
}
