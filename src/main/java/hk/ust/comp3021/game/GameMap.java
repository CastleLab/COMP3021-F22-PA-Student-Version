package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.Move;
import hk.ust.comp3021.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A Sokoban game board.
 * GameBoard consists of information loaded from map data, such as
 * <li>Width and height of the game map</li>
 * <li>Walls in the map</li>
 * <li>Box destinations</li>
 * <li>Initial locations of boxes and player</li>
 * <p/>
 * GameBoard is capable to create many GameState instances, each representing an ongoing game.
 */
public class GameMap {

    private final Map<Position, Entity> map;

    private final int maxWidth;

    private final int maxHeight;

    private final Set<Position> destinations;

    private final int undoLimit;


    /**
     * Create a new GameMap with width, height, set of box destinations and undo limit.
     *
     * @param maxWidth     Width of the game map.
     * @param maxHeight    Height of the game map.
     * @param destinations Set of box destination positions.
     * @param undoLimit    Undo limit.
     *                     Positive numbers specify the maximum number of undo actions.
     *                     0 means undo is not allowed.
     *                     -1 means unlimited. Other negative numbers are not allowed.
     */
    public GameMap(int maxWidth, int maxHeight, Set<Position> destinations, int undoLimit) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = Collections.unmodifiableSet(destinations);
        this.undoLimit = undoLimit;
        this.map = new HashMap<>();
    }

    private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
        this.map = Collections.unmodifiableMap(map);
        this.destinations = Collections.unmodifiableSet(destinations);
        this.undoLimit = undoLimit;
        this.maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(0) + 1;
        this.maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
    }

    /**
     * Parses the map from a string representation.
     * The first line is undo limit.
     * Starting from the second line, the game map is represented as follows,
     * <li># represents a {@link Wall}</li>
     * <li>@ represents a box destination.</li>
     * <li>Any upper-case letter represents a {@link Player}.</li>
     * <li>
     * Any lower-case letter represents a {@link Box} that is only movable by the player with the corresponding upper-case letter.
     * For instance, box "a" can only be moved by player "A" and not movable by player "B".
     * </li>
     * <li>. represents an {@link Empty} position in the map, meaning there is no player or box currently at this position.</li>
     * <p>
     * Notes:
     * <li>
     * There can be at most 26 players.
     * All implementations of classes in the hk.ust.comp3021.game package should support up to 26 players.
     * </li>
     * <li>
     * For simplicity, we assume the given map is bounded with a closed boundary.
     * There is no need to check this point.
     * </li>
     * <li>
     * Example maps can be found in "src/main/resources".
     * </li>
     *
     * @param mapText The string representation.
     * @return The parsed GameMap object.
     * @throws IllegalArgumentException if undo limit is negative but not -1.
     * @throws IllegalArgumentException if there are multiple same upper-case letters, i.e., one player can only exist at one position.
     * @throws IllegalArgumentException if there are no players in the map.
     * @throws IllegalArgumentException if the number of boxes is not equal to the number of box destinations.
     * @throws IllegalArgumentException if there are boxes whose {@link Box#getPlayerId()} do not match any player on the game board,
     *                                  or if there are players that have no corresponding boxes.
     */
    public static GameMap parse(String mapText) {
        final var players = new HashSet<Integer>();
        final var map = new HashMap<Position, Entity>();
        final var destinations = new HashSet<Position>();
        AtomicInteger lineNumber = new AtomicInteger();
        final var firstLine = mapText.lines().findFirst();
        if (firstLine.isEmpty())
            throw new IllegalArgumentException("Invalid map file.");
        var undoLimit = 0;
        try {
            undoLimit = Integer.parseInt(firstLine.get());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to parse undo limit.", e);
        }
        mapText.lines().skip(1).forEachOrdered(line -> {
            int x = 0;
            int y = lineNumber.getAndIncrement();
            for (char c : line.toCharArray()) {
                if (c == '#') { // walls
                    map.put(Position.of(x, y), new Wall());
                } else if (c == '@') {  // destinations
                    destinations.add(new Position(x, y));
                    map.put(Position.of(x, y), new Empty());
                } else if (Character.isLowerCase(c)) { // lower case letters are boxes for each player (corresponding upper case letter)
                    final var playerId = Character.toUpperCase(c) - 'A';
                    map.put(Position.of(x, y), new Box(playerId));
                } else if (Character.isUpperCase(c)) {
                    final var playerId = c - 'A';
                    if (players.contains(playerId)) {
                        throw new IllegalArgumentException("duplicate players detected in the map");
                    }
                    players.add(playerId);
                    map.put(new Position(x, y), new Player(playerId));
                } else if (c == '.') {
                    map.put(Position.of(x, y), new Empty());
                }
                x++;
            }
        });

        // validate closed boundary map
        final var moves = new Move[]{new Move.Down(-1), new Move.Up(-1), new Move.Left(-1), new Move.Right(-1)};
        final var closedBoundary = map.entrySet().parallelStream()
            .filter(entry -> !(entry.getValue() instanceof Wall))
            .allMatch(entry -> Arrays.stream(moves)
                .allMatch(m -> map.get(m.nextPosition(entry.getKey())) != null));
        if (!closedBoundary)
            throw new IllegalArgumentException("not a closed boundary map");

        final var allBoxes = map.values().stream()
            .filter(Box.class::isInstance)
            .map(Box.class::cast)
            .collect(Collectors.toSet());
        final var allReferencedPlayers = allBoxes.stream()
            .map(Box::getPlayerId)
            .collect(Collectors.toSet());
        if (undoLimit < -1)
            throw new IllegalArgumentException("invalid undo limit");
        if (players.size() == 0)
            throw new IllegalArgumentException("no player");
        if (destinations.size() != allBoxes.size())
            throw new IllegalArgumentException("mismatch destinations");
        if (!allReferencedPlayers.equals(players))
            throw new IllegalArgumentException("unmatched players");
        return new GameMap(map, destinations, undoLimit);
    }

    /**
     * Get the entity object at the given position.
     *
     * @param position the position of the entity in the game map.
     * @return Entity object.
     */
    @Nullable
    public Entity getEntity(Position position) {
        return map.get(position);
    }

    /**
     * Put one entity at the given position in the game map.
     *
     * @param position the position in the game map to put the entity.
     * @param entity   the entity to put into game map.
     */
    public void putEntity(Position position, Entity entity) {
        this.map.put(position, entity);
    }

    /**
     * Get all box destination positions as a set in the game map.
     *
     * @return a set of positions.
     */
    public @NotNull @Unmodifiable Set<Position> getDestinations() {
        return destinations;
    }

    /**
     * Get the undo limit of the game map.
     *
     * @return undo limit.
     */
    public Optional<Integer> getUndoLimit() {
        return undoLimit < 0 ? Optional.empty() : Optional.of(undoLimit);
    }

    /**
     * Get all players' id as a set.
     *
     * @return a set of player id.
     */
    public Set<Integer> getPlayerIds() {
        return this.map.values().stream()
            .filter(it -> it instanceof Player)
            .map(it -> ((Player) it).getId())
            .collect(Collectors.toSet());
    }

    /**
     * Get the maximum width of the game map.
     *
     * @return maximum width.
     */
    public int getMaxWidth() {
        return maxWidth;
    }

    /**
     * Get the maximum height of the game map.
     *
     * @return maximum height.
     */
    public int getMaxHeight() {
        return maxHeight;
    }
}
