package hk.ust.comp3021.game;

import hk.ust.comp3021.entities.Wall;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class GameMapTest {

    private final String rectangularMap = """
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
        var gameMap = GameMap.parse(rectangularMap, 0);
        assertEquals(6, gameMap.getMaxWidth());
    }

    @Test
    void testHeightForRectangularMap() {
        var gameMap = GameMap.parse(rectangularMap, 0);
        assertEquals(7, gameMap.getMaxHeight());
    }

    @Test
    void testGetDestinations() {
        var gameMap = GameMap.parse(rectangularMap, 0);
        assertEquals(2, gameMap.getDestinations().size());
    }

    @Test
    void testGetEntity() {
        var gameMap = GameMap.parse(rectangularMap, 0);
        var entity = gameMap.getEntity(Position.of(0, 0));
        assertTrue(entity instanceof Wall);
    }

    private final String nonRectangularMap = """
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
        var gameMap = GameMap.parse(nonRectangularMap, 0);
        assertEquals(9, gameMap.getMaxWidth());
    }

    @Test
    void testHeightForNonRectangularMap() {
        var gameMap = GameMap.parse(nonRectangularMap, 0);
        assertEquals(7, gameMap.getMaxHeight());
    }

    @Test
    void testUndoLimitPassing() {
        var gameMap = GameMap.parse(rectangularMap, 233);
        assertEquals(233, gameMap.getUndoLimit());
    }

    @Test
    void testInsufficientDestinations() {
        var invalidMap = """
                ######
                #A..@#
                #...@#
                #a...#
                #.a..#
                #..a.#
                ######
                """;
        assertThrows(IllegalArgumentException.class, () -> GameMap.parse(invalidMap, 233));
    }

    @Test
    void testEmptyMap() {
        var invalidMap = "";
        assertThrows(IllegalArgumentException.class, () -> GameMap.parse(invalidMap, 233));
    }

    @Test
    void testMapWithoutPlayer() {
        var invalidMap = """
                ###
                #.#
                ###
                """;
        assertThrows(IllegalArgumentException.class, () -> GameMap.parse(invalidMap, 233));
    }

    @Test
    void testUnmatchedPlayersAndBoxes() {
        var invalidMap = """
                ######
                #A..@#
                #...@#
                #a.b.#
                #.a..#
                #..a.#
                ######
                """;
        assertThrows(IllegalArgumentException.class, () -> GameMap.parse(invalidMap, 233));
    }

    @Test
    void testNegativeUndoLimit() {
        assertThrows(IllegalArgumentException.class, () -> GameMap.parse(rectangularMap, -1));
    }

    @Test
    void testTwoPlayersMap() {
        var twoPlayersMap = """
                ######
                #A..@#
                #..B@#
                #....#
                #.a..#
                #..b.#
                ######
                """;
        var gameMap = GameMap.parse(twoPlayersMap, 0);
        var playerIds = gameMap.getPlayerIds();
        assertArrayEquals(new int[]{'A', 'B'}, playerIds);
    }

    @Test
    void testDuplicatedPlayers() {
        var invalidMap = """
                ######
                #A..@#
                #..A@#
                #....#
                #.a..#
                #..b.#
                ######
                """;
        assertThrows(IllegalArgumentException.class, () -> GameMap.parse(invalidMap, 233));
    }

    @TempDir
    private Path tempDir;

    @Test
    void testLoadFromFile() throws IOException {
        var mapFile = tempDir.resolve("tempMap.map");
        Files.writeString(mapFile, rectangularMap);
        var gameMap = GameMap.loadFromFile(mapFile, 0);
        assertEquals(6, gameMap.getMaxWidth());
        assertEquals(7, gameMap.getMaxHeight());
    }

    @Test
    void testSuperLargeMap() {
        assertDoesNotThrow(() -> {
            new GameMap(Integer.MAX_VALUE, Integer.MAX_VALUE, Collections.emptySet(), 0);
        });
    }
}
