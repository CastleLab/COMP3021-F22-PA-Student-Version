package hk.ust.comp3021.game;

import hk.ust.comp3021.entities.Box;
import hk.ust.comp3021.entities.Entity;
import hk.ust.comp3021.entities.Player;
import hk.ust.comp3021.entities.Wall;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
public class GameBoard {

    @Unmodifiable
    private final Map<Position, Entity> map;

    private final Set<Position> destinations;

    private final int undoLimit;

    /**
     * Initializes a new instance of GameBoard.
     *
     * @param map
     * @param undoLimit
     */
    public GameBoard(HashMap<Position, Entity> map, Set<Position> destinations, int undoLimit) {
        this.map = Collections.unmodifiableMap(map);
        this.destinations = Collections.unmodifiableSet(destinations);
        this.undoLimit = undoLimit;
    }

    public static GameBoard loadGameMap(Path mapFile, int undoLimit) throws IOException {
        var fileContent = Files.readString(mapFile);
        return GameBoard.parse(fileContent, undoLimit);
    }

    public static GameBoard parse(String gameBoardText, int undoLimit) {
        var players = new HashSet<Integer>();
        var map = new HashMap<Position, Entity>();
        var destinations = new HashSet<Position>();
        AtomicInteger lineNumber = new AtomicInteger();
        gameBoardText.lines().forEachOrdered(line -> {
            int x = 0;
            int y = lineNumber.getAndIncrement();
            for (char c : line.toCharArray()) {
                if (c == '#') { // walls
                    map.put(new Position(x, y), new Wall());
                } else if (c == '@') {  // destinations
                    destinations.add(new Position(x, y));
                } else if (c >= 'a' && c <= 'z') { // lower case letters are boxes for each player (corresponding upper case letter)
                    var playerId = c - ('a' - 'A');
                    map.put(new Position(x, y), new Box(playerId));
                } else if (Character.isUpperCase(c)) {
                    var playerId = (int) c;
                    if (players.contains(playerId)) {
                        throw new IllegalArgumentException("duplicate players detected in the map");
                    }
                    players.add(playerId);
                    map.put(new Position(x, y), new Player(playerId));
                }
                x++;
            }
        });
        return new GameBoard(map, destinations, undoLimit);
    }

    public @Unmodifiable Map<Position, Entity> getMap() {
        return map;
    }

    public @Unmodifiable Set<Position> getDestinations() {
        return destinations;
    }

    public int getUndoLimit() {
        return undoLimit;
    }

    public @Unmodifiable int[] getPlayerIds() {
        var idList = this.map.values().stream().filter(e -> e instanceof Player)
                .map(p -> ((Player) p).getId())
                .sorted()
                .toList();
        var ids = new int[idList.size()];
        for (int i = 0; i < idList.size(); i++) {
            ids[i] = idList.get(i);
        }
        return ids;
    }

    // TODO validate map

    /**
     * Create a new game session.
     *
     * @return GameState
     */
    public GameState createGameSession() {
        return new GameState(this);
    }
}
