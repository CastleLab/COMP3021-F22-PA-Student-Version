package hk.ust.comp3021.tui;

import hk.ust.comp3021.actions.Exit;
import hk.ust.comp3021.actions.InvalidInput;
import hk.ust.comp3021.actions.Move;
import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.game.Position;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
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
        verify(renderingEngine, atLeastOnce()).message(any());
    }

    @Test
    void testGameExit() {
        final var gameState = mock(GameState.class);
        final var inputEngine = mock(TerminalInputEngine.class);
        final var renderingEngine = mock(TerminalRenderingEngine.class);
        when(gameState.isWin()).thenReturn(false, false, false);
        when(inputEngine.fetchAction())
            .thenReturn(new InvalidInput(0, ""),
                new InvalidInput(0, ""),
                new Exit(0));

        final var game = new TerminalSokobanGame(gameState, inputEngine, renderingEngine);
        game.run();

        verify(renderingEngine, atLeastOnce()).message(any());
    }

    @Test
    void testGameWin() {
        final var gameState = mock(GameState.class);
        final var inputEngine = mock(TerminalInputEngine.class);
        final var renderingEngine = mock(TerminalRenderingEngine.class);
        when(gameState.isWin()).thenReturn(false, false, false, true);
        when(inputEngine.fetchAction())
            .thenReturn(new InvalidInput(0, ""));

        final var game = new TerminalSokobanGame(gameState, inputEngine, renderingEngine);
        game.run();

        verify(gameState, atLeastOnce()).isWin();
        verify(renderingEngine, atLeastOnce()).message(any());
    }

    @Test
    void testMoreThanTwoPlayers() {
        final var gameState = mock(GameState.class);
        final var inputEngine = mock(TerminalInputEngine.class);
        final var renderingEngine = mock(TerminalRenderingEngine.class);
        when(gameState.getAllPlayerPositions()).thenReturn(new HashSet<>(Arrays.asList(Position.of(1, 1), Position.of(1, 2), Position.of(1, 3))));

        assertThrowsExactly(IllegalArgumentException.class, () -> new TerminalSokobanGame(gameState, inputEngine, renderingEngine));
    }

    @Test
    void testTwoPlayers() {
        final var gameState = mock(GameState.class);
        final var inputEngine = mock(TerminalInputEngine.class);
        final var renderingEngine = mock(TerminalRenderingEngine.class);
        when(gameState.getAllPlayerPositions()).thenReturn(new HashSet<>(Arrays.asList(Position.of(1, 1), Position.of(1, 2))));

        assertDoesNotThrow(() -> new TerminalSokobanGame(gameState, inputEngine, renderingEngine));
    }
}
