package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.*;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbstractSokobanGameTest {

    @Test
    void testInvalidInput() {
        final var game = new SokobanGameForTesting(mock(GameState.class));
        final var message = String.valueOf(new Random().nextLong());

        final var action = new InvalidInput(-1, message);
        final var result = game.feedActionForProcessing(action);

        assertTrue(result instanceof ActionResult.Failed);
        assertEquals(message, ((ActionResult.Failed) result).getReason());
    }

    @Test
    void testExceedingUndoQuota() {
        final var gameState = mock(GameState.class);
        when(gameState.getPlayerPositionById(anyInt())).thenReturn(Position.of(0, 0));
        when(gameState.getUndoQuota()).thenReturn(0);

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Undo(0));

        assertTrue(result instanceof ActionResult.Failed);
    }

    @Test
    void testUndoWithinQuota() {
        final var gameState = mock(GameState.class);
        when(gameState.getPlayerPositionById(anyInt())).thenReturn(Position.of(0, 0));
        when(gameState.getUndoQuota()).thenReturn(1);

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Undo(0));

        verify(gameState, times(1)).undo();
        assertTrue(result instanceof ActionResult.Success);
    }

    @Test
    void testExit() {
        final var gameState = mock(GameState.class);

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Exit(0));

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
        final var testMap = GameMap.parse(mapText);
        final var gameState = spy(new GameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Move.Down(0));

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
        final var testMap = GameMap.parse(mapText);
        final var gameState = spy(new GameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Move.Down(0));

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
        final var testMap = GameMap.parse(mapText);
        final var gameState = spy(new GameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Move.Down(0));

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
        final var testMap = GameMap.parse(mapText);
        final var gameState = spy(new GameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Move.Down(0));

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
        final var testMap = GameMap.parse(mapText);
        final var gameState = spy(new GameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(new Move.Down(0));

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
        final var testMap = GameMap.parse(mapText);
        final var gameState = spy(new GameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        assertThrowsExactly(IllegalArgumentException.class, () -> game.feedActionForProcessing(new Move.Down(1)));
    }

    @Test
    void testShouldStopWhenWin() {
        final var gameState = mock(GameState.class);
        when(gameState.isWin()).thenReturn(true);

        final var game = new SokobanGameForTesting(gameState);
        assertTrue(game.shouldStop());
    }

    @Test
    void testShouldStopWhenStuck() {
        final var gameState = mock(GameState.class);
        when(gameState.isStuck()).thenReturn(true);

        final var game = new SokobanGameForTesting(gameState);
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
