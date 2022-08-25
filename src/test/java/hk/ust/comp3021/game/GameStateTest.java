package hk.ust.comp3021.game;

import hk.ust.comp3021.entities.*;
import hk.ust.comp3021.utils.ShouldNotReachException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    void testMapCopying() {
        final var maxWidth = 2333;
        final var maxHeight = 2333;
        final var random = new Random();
        final var randomEntities = Stream.generate(() -> Position.of(random.nextInt(maxWidth), random.nextInt(maxHeight)))
                .distinct()
                .limit(100)
                .collect(Collectors.toMap(Function.identity(), it -> generateEntity(it.x())));

        final var firstPos = randomEntities.keySet().stream().findFirst();
        assertTrue(firstPos.isPresent());
        final var gameMap = new GameMap(maxWidth, maxHeight, Collections.singleton(firstPos.get()), 233);
        randomEntities.forEach(gameMap::putEntity);

        final var gameState = new GameState(gameMap);

        final var randomPosition = randomEntities.keySet().stream().findAny();
        assertTrue(randomPosition.isPresent());
        gameMap.putEntity(randomPosition.get(), new Empty());

        randomEntities.forEach((p, e) -> assertEquals(e, gameState.getEntity(p)));
        assertEquals(233, gameState.getUndoQuota());
        assertEquals(2333, gameState.getBoardHeight());
        assertEquals(2333, gameState.getBoardWidth());
        assertEquals(1, gameState.getDestinations().size());
    }

    @Test
    void testWin() {
        final var testMap = GameMap.parse("""
                233
                ######
                #A.a@#
                #..a@#
                ######
                """);
        final var gameState = new GameState(testMap);
        gameState.move(Position.of(3, 1), Position.of(4, 1));
        gameState.move(Position.of(3, 2), Position.of(4, 2));

        assertTrue(gameState.isWin());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
                    233
                    ######
                    #A.a@#
                    #..a@#
                    ######
                    """,
            """
                    233
                    ######
                    #A.a@#
                    #B.b@#
                    ######
                    """,
            """
                    233
                    ######
                    #A.a@#
                    ######
                    #B.b@#
                    ######
                    """,
            """
                    233
                    ######
                    #B.a@#
                    ##.###
                    #A.b@#
                    ######
                    """,
            """
                    233
                    #####
                    #AaB#
                    #.@.#
                    #####
                    """,
    })
    void testNonStuck(String mapText) {
        final var testMap = GameMap.parse(mapText);
        final var gameState = new GameState(testMap);

        assertFalse(gameState.isStuck());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
                    233
                    #####
                    #A.@#
                    #..a#
                    #####
                    """,
            """
                    233
                    ######
                    #B.a@#
                    ######
                    #A.b@#
                    ######
                    """,
            """
                    233
                    #####
                    #A@@#
                    #.aa#
                    #####
                    """,
            """
                    233
                    #####
                    #AaB#
                    #.@b#
                    #####
                    """,
    })
    void testStuck(String mapText) {
        final var testMap = GameMap.parse(mapText);
        final var gameState = new GameState(testMap);

        assertTrue(gameState.isStuck());
    }

    @Test
    void testMove() {
        final var gameState = new GameState(GameMap.parse("""
                233
                ######
                #A.a@#
                #..a@#
                ######
                """
        ));

        gameState.move(Position.of(1, 1), Position.of(2, 1));
        assertEquals(Position.of(2, 1), gameState.getPlayerPositionById(0));
    }

    @Test
    void testPushBox() {
        final var gameState = new GameState(GameMap.parse("""
                233
                ######
                #.Aa@#
                #..a@#
                ######
                """
        ));

        gameState.move(Position.of(3, 1), Position.of(4, 1));
        gameState.move(Position.of(2, 1), Position.of(3, 1));

        assertEquals(Position.of(3, 1), gameState.getPlayerPositionById(0));
        assertInstanceOf(Box.class, gameState.getEntity(Position.of(4, 1)));
    }

    @Test
    void testMoveMoreThanOneStep() {
        final var gameState = new GameState(GameMap.parse("""
                233
                ######
                #A.a@#
                #..a@#
                ######
                """
        ));

        assertThrowsExactly(IllegalArgumentException.class, () -> gameState.move(Position.of(1, 1), Position.of(2, 2)));
    }

    @Test
    void testUndoWhenNeed() {
        final var gameState = new GameState(GameMap.parse("""
                233
                ######
                #.Aa@#
                #..a@#
                ######
                """
        ));
        gameState.move(Position.of(3, 1), Position.of(4, 1));
        gameState.move(Position.of(2, 1), Position.of(3, 1));

        gameState.undo();
        assertEquals(Position.of(2, 1), gameState.getPlayerPositionById(0));
        assertInstanceOf(Box.class, gameState.getEntity(Position.of(3, 1)));
        assertInstanceOf(Empty.class, gameState.getEntity(Position.of(4, 1)));

        assertEquals(232, gameState.getUndoQuota());
    }

    @Test
    void testUndoWhenNoNeed() {
        final var gameState = new GameState(GameMap.parse("""
                233
                ######
                #A.a@#
                #..a@#
                ######
                """
        ));
        gameState.move(Position.of(1, 1), Position.of(2, 1));

        gameState.undo();

        assertEquals(233, gameState.getUndoQuota());
    }

    private Entity generateEntity(int key) {
        return switch (key % 4) {
            case 0 -> new Box(0);
            case 1 -> new Empty();
            case 2 -> new Player(0);
            case 3 -> new Wall();
            default -> throw new ShouldNotReachException();
        };
    }
}
