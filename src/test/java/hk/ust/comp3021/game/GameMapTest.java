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
        final var gameMap = GameMap.parse(rectangularMap);
        assertEquals(6, gameMap.getMaxWidth());
    }

    @Test
    void testHeightForRectangularMap() {
        final var gameMap = GameMap.parse(rectangularMap);
        assertEquals(7, gameMap.getMaxHeight());
    }

    @Test
    void testGetDestinations() {
        final var gameMap = GameMap.parse(rectangularMap);
        assertEquals(2, gameMap.getDestinations().size());
        assertInstanceOf(Empty.class, gameMap.getEntity(Position.of(4, 1)));
    }

    @Test
    void testWallParsing() {
        final var gameMap = GameMap.parse(rectangularMap);
        assertInstanceOf(Wall.class, gameMap.getEntity(Position.of(0, 0)));
    }

    @Test
    void testPlayerParsing() {
        final var gameMap = GameMap.parse(rectangularMap);
        final var player = assertInstanceOf(Player.class, gameMap.getEntity(Position.of(1, 1)));
        assertNotNull(player);
        assertEquals(0, player.getId());
    }

    @Test
    void testBoxParsing() {
        final var gameMap = GameMap.parse(rectangularMap);
        final var box = assertInstanceOf(Box.class, gameMap.getEntity(Position.of(2, 4)));
        assertNotNull(box);
        assertEquals(0, box.getPlayerId());
    }

    @Test
    void testEmptyCellParsing() {
        final var gameMap = GameMap.parse(rectangularMap);
        assertInstanceOf(Empty.class, gameMap.getEntity(Position.of(2, 1)));
    }

    @Test
    void testGetEntity() {
        final var gameMap = GameMap.parse(rectangularMap);
        final var entity = gameMap.getEntity(Position.of(0, 0));
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
        final var gameMap = GameMap.parse(nonRectangularMap);
        assertEquals(9, gameMap.getMaxWidth());
    }

    @Test
    void testHeightForNonRectangularMap() {
        final var gameMap = GameMap.parse(nonRectangularMap);
        assertEquals(7, gameMap.getMaxHeight());
    }

    @Test
    void testUndoLimitParsing() {
        final var gameMap = GameMap.parse(rectangularMap);
        assertEquals(233, gameMap.getUndoLimit());
    }

    @Test
    void testInsufficientDestinations() {
        final var invalidMap = """
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
        final var invalidMap = "";
        assertThrowsExactly(IllegalArgumentException.class, () -> GameMap.parse(invalidMap));
    }

    @Test
    void testMapWithoutPlayer() {
        final var invalidMap = """
                233
                ###
                #.#
                ###
                """;
        assertThrowsExactly(IllegalArgumentException.class, () -> GameMap.parse(invalidMap));
    }

    @Test
    void testUnmatchedPlayersAndBoxes() {
        final var invalidMap = """
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
        final var twoPlayersMap = """
                233
                ######
                #A..@#
                #..B@#
                #....#
                #.a..#
                #..b.#
                ######
                """;
        final var gameMap = GameMap.parse(twoPlayersMap);
        final var playerIds = gameMap.getPlayerIds();

        final var expectedIds = new HashSet<Integer>();
        expectedIds.add(0);
        expectedIds.add(1);
        assertEquals(expectedIds, playerIds);
    }

    @Test
    void testDuplicatedPlayers() {
        final var invalidMap = """
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
        final var invalidMap = String.join("\n", rectangularMap.lines().skip(1).toList());
        assertThrowsExactly(IllegalArgumentException.class, () -> GameMap.parse(invalidMap));
    }

    @Test
    void testSuperLargeMap() {
        assertDoesNotThrow(() -> {
            new GameMap(Integer.MAX_VALUE, Integer.MAX_VALUE, Collections.emptySet(), 0);
        });
    }
}
