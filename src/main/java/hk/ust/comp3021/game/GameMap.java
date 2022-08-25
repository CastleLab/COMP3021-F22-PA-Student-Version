package hk.ust.comp3021.game;

import hk.ust.comp3021.entities.*;
import hk.ust.comp3021.utils.NotImplementedException;
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


    public GameMap(int maxWidth, int maxHeight, Set<Position> destinations, int undoLimit) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = Collections.unmodifiableSet(destinations);
        this.undoLimit = undoLimit;
        this.map = new HashMap<>();
    }

    /**
     * Initializes a new instance of GameBoard.
     *
     * @param map
     * @param undoLimit
     */
    private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
        this.map = Collections.unmodifiableMap(map);
        this.destinations = Collections.unmodifiableSet(destinations);
        this.undoLimit = undoLimit;
        this.maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(0) + 1;
        this.maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
    }

    /**
     * Parses the map from a string representation.
     *
     * @param mapText The string representation.
     * @return The parsed GameMap object.
     */
    public static GameMap parse(String mapText) {
        var players = new HashSet<Integer>();
        var map = new HashMap<Position, Entity>();
        var destinations = new HashSet<Position>();
        AtomicInteger lineNumber = new AtomicInteger();
        var firstLine = mapText.lines().findFirst();
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
                    var playerId = Character.toUpperCase(c) - 'A';
                    map.put(Position.of(x, y), new Box(playerId));
                } else if (Character.isUpperCase(c)) {
                    var playerId = c - 'A';
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
        return new GameMap(map, destinations, undoLimit);
    }

    @Nullable
    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        this.map.put(position, entity);
    }

    public boolean isValid() {
       throw new NotImplementedException();
    }

    public @Unmodifiable Set<Position> getDestinations() {
        return destinations;
    }

    public int getUndoLimit() {
        return undoLimit;
    }

    public Set<Integer> getPlayerIds() {
        return this.map.values().stream()
                .filter(it -> it instanceof Player)
                .map(it -> ((Player) it).getId())
                .collect(Collectors.toSet());
    }

    // TODO validate map

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }
}
