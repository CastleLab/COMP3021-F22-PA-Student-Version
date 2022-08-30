package hk.ust.comp3021.game;

import hk.ust.comp3021.entities.Box;
import hk.ust.comp3021.entities.Empty;
import hk.ust.comp3021.entities.Player;
import hk.ust.comp3021.entities.Wall;
import hk.ust.comp3021.utils.Helper;
import hk.ust.comp3021.utils.TestTag;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
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

    @Tag(TestTag.PUBLIC)
    @Test
    void testWidthForRectangularMap() {
        final var gameMap = Helper.parseGameMap(rectangularMap);
        assertEquals(6, gameMap.getMaxWidth());
    }

    @Tag(TestTag.PUBLIC)
    @Test
    void testHeightForRectangularMap() {
        final var gameMap = Helper.parseGameMap(rectangularMap);
        assertEquals(7, gameMap.getMaxHeight());
    }

    @Tag(TestTag.PUBLIC)
    @Test
    void testGetDestinations() {
        final var gameMap = Helper.parseGameMap(rectangularMap);
        assertEquals(2, gameMap.getDestinations().size());
        assertInstanceOf(Empty.class, gameMap.getEntity(Position.of(4, 1)));
    }

    @Tag(TestTag.PUBLIC)
    @Test
    void testWallParsing() {
        final var gameMap = Helper.parseGameMap(rectangularMap);
        assertInstanceOf(Wall.class, gameMap.getEntity(Position.of(0, 0)));
    }

    @Tag(TestTag.PUBLIC)
    @Test
    void testPlayerParsing() {
        final var gameMap = Helper.parseGameMap(rectangularMap);
        final var player = assertInstanceOf(Player.class, gameMap.getEntity(Position.of(1, 1)));
        assertNotNull(player);
        assertEquals(0, player.getId());
    }

    @Tag(TestTag.PUBLIC)
    @Test
    void testBoxParsing() {
        final var gameMap = Helper.parseGameMap(rectangularMap);
        final var box = assertInstanceOf(Box.class, gameMap.getEntity(Position.of(2, 4)));
        assertNotNull(box);
        assertEquals(0, box.getPlayerId());
    }

    @Tag(TestTag.PUBLIC)
    @Test
    void testEmptyCellParsing() {
        final var gameMap = Helper.parseGameMap(rectangularMap);
        assertInstanceOf(Empty.class, gameMap.getEntity(Position.of(2, 1)));
    }

    @Tag(TestTag.PUBLIC)
    @Test
    void testGetEntity() {
        final var gameMap = Helper.parseGameMap(rectangularMap);
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

    @Tag(TestTag.HIDDEN)
    @Test
    void testWidthForNonRectangularMap() {
        final var gameMap = Helper.parseGameMap(nonRectangularMap);
        assertEquals(9, gameMap.getMaxWidth());
    }

    @Tag(TestTag.HIDDEN)
    @Test
    void testHeightForNonRectangularMap() {
        final var gameMap = Helper.parseGameMap(nonRectangularMap);
        assertEquals(7, gameMap.getMaxHeight());
    }

    @Tag(TestTag.HIDDEN)
    @Test
    void testUndoLimitParsing() {
        final var gameMap = Helper.parseGameMap(rectangularMap);
        assertEquals(233, gameMap.getUndoLimit().orElse(null));
    }

    @Tag(TestTag.HIDDEN)
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
        assertThrowsExactly(IllegalArgumentException.class, () -> Helper.parseGameMap(invalidMap));
    }

    @Tag(TestTag.HIDDEN)
    @Test
    void testEmptyMap() {
        final var invalidMap = "";
        assertThrowsExactly(IllegalArgumentException.class, () -> Helper.parseGameMap(invalidMap));
    }

    @Tag(TestTag.HIDDEN)
    @Test
    void testMapWithoutPlayer() {
        final var invalidMap = """
            233
            ###
            #.#
            ###
            """;
        assertThrowsExactly(IllegalArgumentException.class, () -> Helper.parseGameMap(invalidMap));
    }

    @Tag(TestTag.HIDDEN)
    @Test
    void testUnmatchedPlayersAndBoxes() {
        final var invalidMap = """
            233
            ######
            #A..@#
            #...@#
            #a.b@#
            #.a.@#
            #..a.#
            ######
            """;
        assertThrowsExactly(IllegalArgumentException.class, () -> Helper.parseGameMap(invalidMap));
    }

    @Tag(TestTag.HIDDEN)
    @Test
    void testInvalidUndoLimit() {
        final var invalidMap = """
            -233
            ######
            #A..@#
            #...@#
            #a.b@#
            #.a.@#
            #..a.#
            ######
            """;
        assertThrowsExactly(IllegalArgumentException.class, () -> Helper.parseGameMap(invalidMap));
    }

    @Tag(TestTag.HIDDEN)
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
        final var gameMap = Helper.parseGameMap(twoPlayersMap);
        final var playerIds = gameMap.getPlayerIds();

        final var expectedIds = new HashSet<Integer>();
        expectedIds.add(0);
        expectedIds.add(1);
        assertEquals(expectedIds, playerIds);
    }

    @Tag(TestTag.HIDDEN)
    @Test
    void testManyPlayersMap() {
        final var manyPlayersMap = """
            233
            ######
            #A.P@@#
            #vCB@@#
            #.cpD@#
            #Ga.d@#
            #g.bV@#
            ######
            """;
        final var gameMap = Helper.parseGameMap(manyPlayersMap);
        final var playerIds = gameMap.getPlayerIds();

        final var expectedIds = new HashSet<>(Arrays.asList(
            0,
            'P' - 'A',
            'C' - 'A',
            'B' - 'A',
            'D' - 'A',
            'G' - 'A',
            'V' - 'A'
        ));
        assertEquals(expectedIds, playerIds);
    }

    @Tag(TestTag.HIDDEN)
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
        assertThrowsExactly(IllegalArgumentException.class, () -> Helper.parseGameMap(invalidMap));
    }

    @Tag(TestTag.HIDDEN)
    @Test
    void testParseMapWithoutUndoLimit() {
        final var invalidMap = String.join("\n", rectangularMap.lines().skip(1).toList());
        assertThrowsExactly(IllegalArgumentException.class, () -> Helper.parseGameMap(invalidMap));
    }

    @Tag(TestTag.HIDDEN)
    @Test
    void testSuperLargeMap() {
        assertDoesNotThrow(() -> {
            new GameMap(Integer.MAX_VALUE, Integer.MAX_VALUE, Collections.emptySet(), 0);
        });
    }
}
