package hk.ust.comp3021.game;

import hk.ust.comp3021.entities.Box;
import hk.ust.comp3021.entities.Empty;
import hk.ust.comp3021.entities.Player;
import hk.ust.comp3021.entities.Wall;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class GameMapTest {

    private static final String rectangularMap = """
            233
            ######
            #A..@#
            #...@#
            #....#
            #.a..#
            #..a.#
            ######
            """;

    @Test
    void testWidthForRectangularMap() {
        var gameMap = GameMap.parse(rectangularMap);
        assertEquals(6, gameMap.getMaxWidth());
    }

    @Test
    void testHeightForRectangularMap() {
        var gameMap = GameMap.parse(rectangularMap);
        assertEquals(7, gameMap.getMaxHeight());
    }

    @Test
    void testGetDestinations() {
        var gameMap = GameMap.parse(rectangularMap);
        assertEquals(2, gameMap.getDestinations().size());
        assertInstanceOf(Empty.class, gameMap.getEntity(Position.of(4, 1)));
    }

    @Test
    void testWallParsing() {
        var gameMap = GameMap.parse(rectangularMap);
        assertInstanceOf(Wall.class, gameMap.getEntity(Position.of(0, 0)));
    }

    @Test
    void testPlayerParsing() {
        var gameMap = GameMap.parse(rectangularMap);
        var player = assertInstanceOf(Player.class, gameMap.getEntity(Position.of(1, 1)));
        assertNotNull(player);
        assertEquals(0, player.getId());
    }

    @Test
    void testBoxParsing() {
        var gameMap = GameMap.parse(rectangularMap);
        var box = assertInstanceOf(Box.class, gameMap.getEntity(Position.of(2, 4)));
        assertNotNull(box);
        assertEquals(0, box.getPlayerId());
    }

    @Test
    void testEmptyCellParsing() {
        var gameMap = GameMap.parse(rectangularMap);
        assertInstanceOf(Empty.class, gameMap.getEntity(Position.of(2, 1)));
    }

    @Test
    void testGetEntity() {
        var gameMap = GameMap.parse(rectangularMap);
        var entity = gameMap.getEntity(Position.of(0, 0));
        assertTrue(entity instanceof Wall);
    }

    private static final String nonRectangularMap = """
            233
            ######
            #A..@#
            #...@###
            #a....@##
            #.a.....#
            #..a.####
            ######
            """;

    @Test
    void testWidthForNonRectangularMap() {
        var gameMap = GameMap.parse(nonRectangularMap);
        assertEquals(9, gameMap.getMaxWidth());
    }

    @Test
    void testHeightForNonRectangularMap() {
        var gameMap = GameMap.parse(nonRectangularMap);
        assertEquals(7, gameMap.getMaxHeight());
    }

    @Test
    void testUndoLimitParsing() {
        var gameMap = GameMap.parse(rectangularMap);
        assertEquals(233, gameMap.getUndoLimit());
    }

    @Test
    void testInsufficientDestinations() {
        var invalidMap = """
                233
                ######
                #A..@#
                #...@#
                #a...#
                #.a..#
                #..a.#
                ######
                """;
        assertThrowsExactly(IllegalArgumentException.class, () -> GameMap.parse(invalidMap));
    }

    @Test
    void testEmptyMap() {
        var invalidMap = "";
        assertThrowsExactly(IllegalArgumentException.class, () -> GameMap.parse(invalidMap));
    }

    @Test
    void testMapWithoutPlayer() {
        var invalidMap = """
                233
                ###
                #.#
                ###
                """;
        assertThrowsExactly(IllegalArgumentException.class, () -> GameMap.parse(invalidMap));
    }

    @Test
    void testUnmatchedPlayersAndBoxes() {
        var invalidMap = """
                233
                ######
                #A..@#
                #...@#
                #a.b.#
                #.a..#
                #..a.#
                ######
                """;
        assertThrowsExactly(IllegalArgumentException.class, () -> GameMap.parse(invalidMap));
    }

    @Test
    void testNegativeUndoLimit() {
        assertThrowsExactly(IllegalArgumentException.class, () -> GameMap.parse(rectangularMap));
    }

    @Test
    void testTwoPlayersMap() {
        var twoPlayersMap = """
                233
                ######
                #A..@#
                #..B@#
                #....#
                #.a..#
                #..b.#
                ######
                """;
        var gameMap = GameMap.parse(twoPlayersMap);
        var playerIds = gameMap.getPlayerIds();

        var expectedIds = new HashSet<Integer>();
        expectedIds.add(0);
        expectedIds.add(1);
        assertEquals(expectedIds, playerIds);
    }

    @Test
    void testDuplicatedPlayers() {
        var invalidMap = """
                233
                ######
                #A..@#
                #..A@#
                #....#
                #.a..#
                #..b.#
                ######
                """;
        assertThrowsExactly(IllegalArgumentException.class, () -> GameMap.parse(invalidMap));
    }

    @Test
    void testParseMapWithoutUndoLimit() {
        var invalidMap = String.join("\n", rectangularMap.lines().skip(1).toList());
        assertThrowsExactly(IllegalArgumentException.class, () -> GameMap.parse(invalidMap));
    }

    @Test
    void testSuperLargeMap() {
        assertDoesNotThrow(() -> {
            new GameMap(Integer.MAX_VALUE, Integer.MAX_VALUE, Collections.emptySet(), 0);
        });
    }
}
