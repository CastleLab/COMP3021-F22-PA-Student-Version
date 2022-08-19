package hk.ust.comp3021.tui;

import hk.ust.comp3021.actions.Exit;
import hk.ust.comp3021.actions.InvalidInput;
import hk.ust.comp3021.actions.Undo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerminalInputEngineTest {


    @Test
    void testInvalidInput() {
        var inputStream = fixValueStream("blah blah");

        var inputEngine = new TerminalInputEngine(inputStream);
        var action = inputEngine.fetchAction();

        assertTrue(action instanceof InvalidInput);
    }

    @Test
    void testExit() {
        var inputStream = fixValueStream("exit");

        var inputEngine = new TerminalInputEngine(inputStream);
        var action = inputEngine.fetchAction();

        assertTrue(action instanceof Exit);
    }

    @ParameterizedTest
    @CsvSource({"R,0", "U,1"})
    void testUndo(String input, int playerId) {
        var inputStream = fixValueStream(input);

        var inputEngine = new TerminalInputEngine(inputStream);
        var action = inputEngine.fetchAction();

        assertTrue(action instanceof Undo);
        assertEquals(playerId, action.getInitiator());
    }

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
        var inputStream = fixValueStream(input);

        var inputEngine = new TerminalInputEngine(inputStream);
        var action = inputEngine.fetchAction();

        assertEquals(name, action.getClass().getSimpleName());
        assertEquals(playerId, action.getInitiator());
    }

    private InputStream fixValueStream(String content) {
        var bytes = content.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(bytes);
    }
}