package hk.ust.comp3021.tui;

import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.utils.TestHelper;
import hk.ust.comp3021.utils.TestKind;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerminalRenderingEngineTest {

    @Tag(TestKind.PUBLIC)
    @Test
    void testMessage() {
        final var stream = new CapturingStream();
        final var randomString = String.valueOf(this.hashCode());

        final var renderingEngine = new TerminalRenderingEngine(stream);
        renderingEngine.message(randomString);

        assertEquals(randomString + System.lineSeparator(), stream.getContent());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testRender() {
        String testMap = """
                233
                ######
                #A..@#
                #...@###
                #a....@##
                #.a.....#
                #..a.####
                ######
                """;
        final var gameState = new GameState(TestHelper.parseGameMap(testMap));
        final var stream = new CapturingStream();

        final var renderingEngine = new TerminalRenderingEngine(stream);
        renderingEngine.render(gameState);

        final var renderedContent = stream.getContent();
        assertEquals(8, renderedContent.lines().count());
        assertTrue(renderedContent.lines().allMatch(it -> it.length() >= 10 && it.length() <= 11)); // On Windows there may be \n\r
        final var lines = renderedContent.lines().toList();
        assertEquals('#', lines.get(0).charAt(0));
        assertEquals(' ', lines.get(0).charAt(8));
        assertEquals('a', lines.get(3).charAt(1));
    }

    static class CapturingStream extends PrintStream {
        public CapturingStream() {
            super(new ByteArrayOutputStream());
        }

        public String getContent() {
            return ((ByteArrayOutputStream) this.out).toString(StandardCharsets.UTF_8);
        }
    }

}
