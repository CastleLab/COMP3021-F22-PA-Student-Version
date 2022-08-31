package hk.ust.comp3021.game;

import hk.ust.comp3021.entities.*;
import hk.ust.comp3021.utils.ShouldNotReachException;
import hk.ust.comp3021.utils.TestHelper;
import hk.ust.comp3021.utils.TestKind;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Tag(TestKind.HIDDEN)
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
        assertEquals(233, gameState.getUndoQuota().orElse(null));
        assertEquals(2333, gameState.getMapMaxHeight());
        assertEquals(2333, gameState.getMapMaxWidth());
        assertEquals(1, gameState.getDestinations().size());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testAllPlayerIds() {
        final var testMap = TestHelper.parseGameMap("""
                233
                ######
                #APp@#
                #xXa@@#
                ######
                """);
        final var gameState = new GameState(testMap);

        assertEquals(new HashSet<>(Arrays.asList(Position.of(1, 1), Position.of(2, 1), Position.of(2, 2))), gameState.getAllPlayerPositions());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testWin() {
        final var testMap = TestHelper.parseGameMap("""
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

    @Tag(TestKind.PUBLIC)
    @Test
    void testMove() {
        final var gameState = new GameState(TestHelper.parseGameMap("""
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

    @Tag(TestKind.PUBLIC)
    @Test
    void testPushBox() {
        final var gameState = new GameState(TestHelper.parseGameMap("""
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

    @Tag(TestKind.PUBLIC)
    @Test
    void testGetUndoLimit() {
        final var gameState = new GameState(TestHelper.parseGameMap("""
                233
                ######
                #.Aa@#
                #..a@#
                ######
                """
        ));
        assertEquals(233, gameState.getUndoQuota().orElse(null));
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testGetUndoUnlimited() {
        final var gameState = new GameState(TestHelper.parseGameMap("""
                -1
                ######
                #.Aa@#
                #..a@#
                ######
                """
        ));
        assertTrue(gameState.getUndoQuota().isEmpty());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testUndoWhenThereIsCheckpoint() {
        final var gameState = new GameState(TestHelper.parseGameMap("""
                233
                ######
                #.Aa@#
                #..a@#
                ######
                """
        ));
        gameState.move(Position.of(3, 1), Position.of(4, 1));
        gameState.move(Position.of(2, 1), Position.of(3, 1));
        gameState.checkpoint();

        gameState.undo();
        assertEquals(Position.of(2, 1), gameState.getPlayerPositionById(0));
        assertInstanceOf(Box.class, gameState.getEntity(Position.of(3, 1)));
        assertInstanceOf(Empty.class, gameState.getEntity(Position.of(4, 1)));

        assertEquals(232, gameState.getUndoQuota().orElse(null));
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testUndoWhenThereIsMoveButNoCheckpoint() {
        final var gameState = new GameState(TestHelper.parseGameMap("""
                233
                ######
                #A.a@#
                #..a@#
                ######
                """
        ));
        gameState.move(Position.of(1, 1), Position.of(2, 1));

        gameState.undo();

        assertEquals(233, gameState.getUndoQuota().orElse(null));
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testUndoWhenThereIsNoMove() {
        final var gameState = new GameState(TestHelper.parseGameMap("""
                233
                ######
                #A.a@#
                #..a@#
                ######
                """
        ));
        gameState.undo();

        assertEquals(233, gameState.getUndoQuota().orElse(null));
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
