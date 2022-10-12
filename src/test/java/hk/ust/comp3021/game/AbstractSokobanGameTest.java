package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.*;
import hk.ust.comp3021.utils.TestHelper;
import hk.ust.comp3021.utils.TestKind;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbstractSokobanGameTest {

    @Tag(TestKind.HIDDEN)
    @Test
    void testInvalidInput() {
        final var game = new SokobanGameForTesting(mock(GameState.class));
        final var message = String.valueOf(new Random().nextLong());

        final var action = new InvalidInput(-1, message);
        final var result = game.feedActionForProcessing(action);

        assertTrue(result instanceof ActionResult.Failed);
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testExceedingUndoQuota() {
        final var gameState = mock(GameState.class);
        when(gameState.getPlayerPositionById(anyInt())).thenReturn(Position.of(0, 0));
        when(gameState.getUndoQuota()).thenReturn(Optional.of(0));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Undo(0));

        assertTrue(result instanceof ActionResult.Failed);
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testUndoWithinQuota() {
        final var gameState = mock(GameState.class);
        when(gameState.getPlayerPositionById(anyInt())).thenReturn(Position.of(0, 0));
        when(gameState.getUndoQuota()).thenReturn(Optional.of(1));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Undo(0));

        verify(gameState, times(1)).undo();
        assertTrue(result instanceof ActionResult.Success);
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testUndoUnlimited() {
        final var gameState = mock(GameState.class);
        when(gameState.getPlayerPositionById(anyInt())).thenReturn(Position.of(0, 0));
        when(gameState.getUndoQuota()).thenReturn(Optional.empty());

        final var game = new SokobanGameForTesting(gameState);
        for (int i = 0; i < 10000; i++) {
            final var result = game.feedActionForProcessing(new Undo(0));
            assertTrue(result instanceof ActionResult.Success);
        }
        verify(gameState, times(10000)).undo();
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testExit() {
        final var gameState = mock(GameState.class);

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Exit(0));

        assertTrue(result instanceof ActionResult.Success);
        assertTrue(game.shouldStop());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testMove() {
        String mapText = """
                233
                ######
                #A..@#
                #....#
                #a...#
                ######
                """;
        final var testMap = TestHelper.parseGameMap(mapText);
        final var gameState = spy(new GameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Move.Down(0));

        assertTrue(result instanceof ActionResult.Success);
        verify(gameState, times(1)).move(any(), any());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testHitWall() {
        String mapText = """
                233
                ######
                #A..@#
                ##...#
                #a...#
                ######
                """;
        final var testMap = TestHelper.parseGameMap(mapText);
        final var gameState = spy(new GameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Move.Down(0));

        assertTrue(result instanceof ActionResult.Failed);
        verify(gameState, never()).move(any(), any());
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testHitAnotherPlayer() {
        String mapText = """
                233
                ######
                #A..@#
                #B...#
                #ab.@#
                ######
                """;
        final var testMap = TestHelper.parseGameMap(mapText);
        final var gameState = spy(new GameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Move.Down(0));

        assertTrue(result instanceof ActionResult.Failed);
        verify(gameState, never()).move(any(), any());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testPushBox() {
        String mapText = """
                233
                ######
                #A..@#
                #a...#
                #....#
                ######
                """;
        final var testMap = TestHelper.parseGameMap(mapText);
        final var gameState = spy(new GameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Move.Down(0));

        assertTrue(result instanceof ActionResult.Success);
        verify(gameState, times(2)).move(any(), any());
        verify(gameState, times(1)).checkpoint();
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testPushBoxAgainstWall() {
        String mapText = """
                233
                ######
                #A..@#
                #a...#
                ##...#
                ######
                """;
        final var testMap = TestHelper.parseGameMap(mapText);
        final var gameState = spy(new GameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Move.Down(0));

        assertTrue(result instanceof ActionResult.Failed);
        verify(gameState, never()).move(any(), any());
    }


    @Tag(TestKind.HIDDEN)
    @Test
    void testPushOtherPlayerBox() {
        String mapText = """
                233
                ######
                #AB.@#
                #b.a.#
                #@...#
                ######
                """;
        final var testMap = TestHelper.parseGameMap(mapText);
        final var gameState = spy(new GameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Move.Down(0));

        assertTrue(result instanceof ActionResult.Failed);
        verify(gameState, never()).move(any(), any());
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testMoveNonExistingPlayer() {
        String mapText = """
                233
                ######
                #A..@#
                #a...#
                ##...#
                ######
                """;
        final var testMap = TestHelper.parseGameMap(mapText);
        final var gameState = spy(new GameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Move.Down(23));

        assertTrue(result instanceof ActionResult.Failed);
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testShouldStopWhenWin() {
        final var gameState = mock(GameState.class);
        when(gameState.isWin()).thenReturn(true);

        final var game = new SokobanGameForTesting(gameState);
        assertTrue(game.shouldStop());
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testCheckpointWhenNeed() {
        final var gameState = spy(new GameState(TestHelper.parseGameMap("""
                233
                ######
                #.Aa@#
                #..a@#
                ######
                """
        )));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Move.Right(0));

        verify(gameState, times(1)).checkpoint();
        assertInstanceOf(ActionResult.Success.class, result);
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testCheckpointWhenNotNeed() {
        final var gameState = spy(new GameState(TestHelper.parseGameMap("""
                233
                ######
                #A.a@#
                #..a@#
                ######
                """
        )));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Move.Right(0));

        verify(gameState, never()).checkpoint();
        assertInstanceOf(ActionResult.Success.class, result);
    }

    private static class SokobanGameForTesting extends AbstractSokobanGame {

        protected SokobanGameForTesting(GameState gameState) {
            super(gameState);
        }

        @Override
        public void run() {
        }

        public ActionResult feedActionForProcessing(Action action) {
            return processAction(action);
        }
    }
}
