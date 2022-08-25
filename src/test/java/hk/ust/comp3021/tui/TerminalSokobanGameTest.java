package hk.ust.comp3021.tui;

import hk.ust.comp3021.actions.Exit;
import hk.ust.comp3021.actions.InvalidInput;
import hk.ust.comp3021.game.GameState;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class TerminalSokobanGameTest {

    @Test
    void testGameLoop() {
        final var gameState = mock(GameState.class);
        final var inputEngine = mock(TerminalInputEngine.class);
        final var renderingEngine = mock(TerminalRenderingEngine.class);
        when(gameState.isWin()).thenReturn(false);
        when(inputEngine.fetchAction())
                .thenReturn(new InvalidInput(0, ""))
                .thenReturn(new Exit(0));

        final var game = new TerminalSokobanGame(gameState, inputEngine, renderingEngine);
        game.run();

        final var inOrder = inOrder(inputEngine, renderingEngine);

        // Before loop
        inOrder.verify(renderingEngine).render(eq(gameState));

        // First round
        inOrder.verify(inputEngine).fetchAction();
        inOrder.verify(renderingEngine).render(eq(gameState));

        // Second round
        inOrder.verify(inputEngine).fetchAction();
        inOrder.verify(renderingEngine).render(eq(gameState));

        verify(gameState, atLeastOnce()).getUndoQuota();
        verify(gameState, atLeast(0)).isWin();
        verify(gameState, atLeast(0)).isStuck();
        verify(renderingEngine, atLeastOnce()).message(any());

    }

}
