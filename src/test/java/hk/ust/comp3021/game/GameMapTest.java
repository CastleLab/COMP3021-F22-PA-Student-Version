package hk.ust.comp3021.game;

import hk.ust.comp3021.entities.Wall;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class GameMapTest {

    private final String rectangularMap = """
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
    }

    @Test
    void testGetEntity() {
        var gameMap = GameMap.parse(rectangularMap);
        var entity = gameMap.getEntity(Position.of(0, 0));
        assertTrue(entity instanceof Wall);
    }

    private final String nonRectangularMap = """
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
        assertThrows(IllegalArgumentException.class, () -> GameMap.parse(invalidMap));
    }

    @Test
    void testEmptyMap() {
        var invalidMap = "";
        assertThrows(IllegalArgumentException.class, () -> GameMap.parse(invalidMap));
    }

    @Test
    void testMapWithoutPlayer() {
        var invalidMap = """
                233
                ###
                #.#
                ###
                """;
        assertThrows(IllegalArgumentException.class, () -> GameMap.parse(invalidMap));
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
        assertThrows(IllegalArgumentException.class, () -> GameMap.parse(invalidMap));
    }

    @Test
    void testNegativeUndoLimit() {
        assertThrows(IllegalArgumentException.class, () -> GameMap.parse(rectangularMap));
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
        assertArrayEquals(new int[]{'A', 'B'}, playerIds);
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
        assertThrows(IllegalArgumentException.class, () -> GameMap.parse(invalidMap));
    }

    @Test
    void testSuperLargeMap() {
        assertDoesNotThrow(() -> {
            new GameMap(Integer.MAX_VALUE, Integer.MAX_VALUE, Collections.emptySet(), 0);
        });
    }
}
