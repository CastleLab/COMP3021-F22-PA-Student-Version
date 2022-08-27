package hk.ust.comp3021.utils;

import hk.ust.comp3021.game.GameMap;

public class Helper {
    public static GameMap parseGameMap(String mapText) {
        return GameMap.parse(mapText);
    }
}
