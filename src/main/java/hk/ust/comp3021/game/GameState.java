package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.Move;
import hk.ust.comp3021.entities.Box;
import hk.ust.comp3021.entities.Entity;
import hk.ust.comp3021.entities.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

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

    GameState(@NotNull GameBoard board) {
        this.entities = new HashMap<>(board.getMap());
        this.destinations = new HashSet<>(board.getDestinations());
        this.history.push(new Transition());
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

    public boolean isWin() {
        return this.destinations.stream().allMatch(this.entities::containsKey);
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
            new Move.Down(1), new Move.Right(1), new Move.Left(1), new Move.Up(1)
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
    public void move(Position from, Position to) {
        // Game history checkpoint reached if any box is moved.
        var checkpointReached = this.history.peek().moves.keySet().stream()
            .map(this.entities::get)
            .anyMatch(e -> e instanceof Box);
        if (checkpointReached) {
            this.history.push(new Transition());
        }

        // move entity
        var entity = this.entities.remove(from);
        if (entity == null) return;
        this.entities.put(to, entity);

        // append to history
        this.history.peek().add(from, to);
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
            .forEach(e -> this.entities.put(e.getKey(), e.getValue()));
    }

    /**
     * Revert to the last checkpoint in history.
     */
    public void undo() {
        if (this.history.empty()) return;
        var undoTransition = this.history.pop().reverse();
        this.applyTransition(undoTransition);
    }

    static class Transition {
        private final Map<Position, Position> moves = new HashMap<>();

        public void add(Position from, Position to) {
            var key = this.moves.entrySet().stream()
                .filter(e -> e.getValue().equals(from))
                .map(Map.Entry::getKey)
                .findFirst().orElse(from);
            this.moves.put(key, to);
        }

        public Transition reverse() {
            var tr = new Transition();
            this.moves.forEach((k, v) -> tr.add(v, k));
            return tr;
        }
    }
}
