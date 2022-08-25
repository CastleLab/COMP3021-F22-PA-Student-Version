package hk.ust.comp3021;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SokobanGameFactoryTest {

    @TempDir
    private Path tempDir;

    @Test
    void testLoadFromFile() throws IOException {
        var mapFile = tempDir.resolve("tempMap.map");
        String rectangularMap = """
                233
                ######
                #A..@#
                #...@#
                #....#
                #.a..#
                #..a.#
                ######
                """;
        Files.writeString(mapFile, rectangularMap);
        var gameMap = SokobanGameFactory.loadGameMap(mapFile);
        assertEquals(6, gameMap.getMaxWidth());
        assertEquals(7, gameMap.getMaxHeight());
        assertEquals(233, gameMap.getUndoLimit());
    }


}
