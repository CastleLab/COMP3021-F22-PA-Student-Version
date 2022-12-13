package hk.ust.comp3021.gui.component.maplist;

import hk.ust.comp3021.game.GameMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Data model for storing the metadata of a  game map.
 *
 * @param name    The name of the map.
 * @param file    The location of the map.
 * @param loadAt  The date and time when the map wad loaded.
 * @param gameMap The game map itself.
 */
public record MapModel(String name, Path file, Date loadAt, GameMap gameMap) {

    /**
     * @param gameMapURL The path to the game map files.
     * @return The map model.
     * @throws IOException When fails to access the file.
     */
    public static MapModel load(URL gameMapURL) throws IOException {
        var gameMapFile = Path.of(gameMapURL.getPath());
        var reader = new BufferedReader(new InputStreamReader(gameMapURL.openStream()));
        var content = reader.lines().collect(Collectors.joining("\n"));
        var gameMap = GameMap.parse(content);
        return new MapModel(gameMapFile.getFileName().toString(), gameMapFile, new Date(), gameMap);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapModel mapModel = (MapModel) o;

        return file.equals(mapModel.file);
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }
}
