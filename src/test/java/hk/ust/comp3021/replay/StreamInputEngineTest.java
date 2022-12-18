package hk.ust.comp3021.replay;

import hk.ust.comp3021.actions.Exit;
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
class StreamInputEngineTest {

    @Tag(TestKind.REGRESSION)
    @Test
    void testExit() {
        final var inputStream = fixValueStream("0\nexit");

        final var inputEngine = new StreamInputEngine(inputStream);
        final var action = inputEngine.fetchAction();

        assertTrue(action instanceof Exit);
    }

    @Tag(TestKind.REGRESSION)
    @ParameterizedTest
    @CsvSource({"U,1"})
    void testUndo(String input, int playerId) {
        final var inputStream = fixValueStream(playerId + "\n" + input);

        final var inputEngine = new StreamInputEngine(inputStream);
        final var action = inputEngine.fetchAction();

        assertTrue(action instanceof Undo);
        assertEquals(playerId, action.getInitiator());
    }

    @Tag(TestKind.REGRESSION)
    @ParameterizedTest
    @CsvSource({
        "H,Left,1",
        "J,Down,1",
        "K,Up,1",
        "L,Right,1",
    })
    void testMove(String input, String name, int playerId) {
        final var inputStream = fixValueStream(playerId + "\n" + input);

        final var inputEngine = new StreamInputEngine(inputStream);
        final var action = inputEngine.fetchAction();

        assertEquals(name, action.getClass().getSimpleName());
        assertEquals(playerId, action.getInitiator());
    }

    private InputStream fixValueStream(String content) {
        final var bytes = content.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(bytes);
    }
}
