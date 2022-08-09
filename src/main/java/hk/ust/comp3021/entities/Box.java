package hk.ust.comp3021.entities;

/**
 * Denotes a box.
 */
public final class Box extends Entity {
    private final int playerId;

    public Box(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }
}
