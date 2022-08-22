package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.*;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class AbstractSokobanGameTest {

    @Test
    void testInvalidInput() {
        var game = new SokobanGameForTesting(mock(GameState.class));
        var message = String.valueOf(new Random().nextLong());

        var action = new InvalidInput(-1, message);
        var result = game.feedActionForProcessing(action);

        assertTrue(result instanceof ActionResult.Failed);
        assertEquals(message, ((ActionResult.Failed) result).getReason());
    }

    @Test
    void testExceedingUndoQuota() {
        var gameState = mock(GameState.class);
        when(gameState.getPlayerPositionById(anyInt())).thenReturn(Position.of(0, 0));
        when(gameState.getUndoQuota()).thenReturn(0);

        var game = new SokobanGameForTesting(gameState);
        var result = game.feedActionForProcessing(new Undo(0));

        assertTrue(result instanceof ActionResult.Failed);
    }

    @Test
    void testUndoWithinQuota() {
        var gameState = mock(GameState.class);
        when(gameState.getPlayerPositionById(anyInt())).thenReturn(Position.of(0, 0));
        when(gameState.getUndoQuota()).thenReturn(1);

        var game = new SokobanGameForTesting(gameState);
        var result = game.feedActionForProcessing(new Undo(0));

        verify(gameState, times(1)).undo();
        assertTrue(result instanceof ActionResult.Success);
    }

    @Test
    void testExit() {
        var gameState = mock(GameState.class);

        var game = new SokobanGameForTesting(gameState);
        var result = game.feedActionForProcessing(new Exit(0));

        assertTrue(result instanceof ActionResult.Success);
        assertTrue(game.shouldStop());
    }

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
        var testMap = GameMap.parse(mapText);
        var gameState = spy(new GameState(testMap));

        var game = new SokobanGameForTesting(gameState);
        var result = game.feedActionForProcessing(new Move.Down('A'));

        assertTrue(result instanceof ActionResult.Success);
        verify(gameState, times(1)).move(any(), any());
    }

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
        var testMap = GameMap.parse(mapText);
        var gameState = spy(new GameState(testMap));

        var game = new SokobanGameForTesting(gameState);
        var result = game.feedActionForProcessing(new Move.Down('A'));

        assertTrue(result instanceof ActionResult.Failed);
        verify(gameState, never()).move(any(), any());
    }

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
        var testMap = GameMap.parse(mapText);
        var gameState = spy(new GameState(testMap));

        var game = new SokobanGameForTesting(gameState);
        var result = game.feedActionForProcessing(new Move.Down('A'));

        assertTrue(result instanceof ActionResult.Failed);
        verify(gameState, never()).move(any(), any());
    }

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
        var testMap = GameMap.parse(mapText);
        var gameState = spy(new GameState(testMap));

        var game = new SokobanGameForTesting(gameState);
        var result = game.feedActionForProcessing(new Move.Down('A'));

        assertTrue(result instanceof ActionResult.Success);
        verify(gameState, times(2)).move(any(), any());
        verify(gameState, times(1)).checkpoint();
    }

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
        var testMap = GameMap.parse(mapText);
        var gameState = spy(new GameState(testMap));

        var game = new SokobanGameForTesting(gameState);
        var result = game.feedActionForProcessing(new Move.Down('A'));

        assertTrue(result instanceof ActionResult.Failed);
        verify(gameState, never()).move(any(), any());
    }


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
        var testMap = GameMap.parse(mapText);
        var gameState = spy(new GameState(testMap));

        var game = new SokobanGameForTesting(gameState);
        assertThrowsExactly(IllegalArgumentException.class, () -> game.feedActionForProcessing(new Move.Down('B')));
    }

    @Test
    void testShouldStopWhenWin() {
        var gameState = mock(GameState.class);
        when(gameState.isWin()).thenReturn(true);

        var game = new SokobanGameForTesting(gameState);
        assertTrue(game.shouldStop());
    }

    @Test
    void testShouldStopWhenStuck() {
        var gameState = mock(GameState.class);
        when(gameState.isStuck()).thenReturn(true);

        var game = new SokobanGameForTesting(gameState);
        assertTrue(game.shouldStop());
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
