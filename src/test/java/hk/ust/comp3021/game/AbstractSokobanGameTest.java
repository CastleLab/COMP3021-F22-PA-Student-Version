package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.Action;
import hk.ust.comp3021.actions.ActionResult;
import hk.ust.comp3021.actions.InvalidInput;
import hk.ust.comp3021.actions.Undo;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
