package hk.ust.comp3021.tui;

import hk.ust.comp3021.actions.Exit;
import hk.ust.comp3021.actions.InvalidInput;
import hk.ust.comp3021.actions.Undo;
import hk.ust.comp3021.utils.TestExtension;
import hk.ust.comp3021.utils.TestKind;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(TestExtension.class)
class TerminalInputEngineTest {

    @Tag(TestKind.HIDDEN)
    @Test
    void testInvalidInput() {
        final var inputStream = fixValueStream("blah blah");

        final var inputEngine = new TerminalInputEngine(inputStream);
        final var action = inputEngine.fetchAction();

        assertTrue(action instanceof InvalidInput);
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testExit() {
        final var inputStream = fixValueStream("exit");

        final var inputEngine = new TerminalInputEngine(inputStream);
        final var action = inputEngine.fetchAction();

        assertTrue(action instanceof Exit);
    }

    @Tag(TestKind.HIDDEN)
    @ParameterizedTest
    @CsvSource({"U,-1"})
    void testUndo(String input, int playerId) {
        final var inputStream = fixValueStream(input);

        final var inputEngine = new TerminalInputEngine(inputStream);
        final var action = inputEngine.fetchAction();

        assertTrue(action instanceof Undo);
    }

    @Tag(TestKind.HIDDEN)
    @ParameterizedTest
    @CsvSource({
            "W,Up,0",
            "A,Left,0",
            "S,Down,0",
            "D,Right,0",
            "H,Left,1",
            "J,Down,1",
            "K,Up,1",
            "L,Right,1",
    })
    void testMove(String input, String name, int playerId) {
        final var inputStream = fixValueStream(input);

        final var inputEngine = new TerminalInputEngine(inputStream);
        final var action = inputEngine.fetchAction();

        assertEquals(name, action.getClass().getSimpleName());
        assertEquals(playerId, action.getInitiator());
    }

    private InputStream fixValueStream(String content) {
        final var bytes = content.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(bytes);
    }
}
