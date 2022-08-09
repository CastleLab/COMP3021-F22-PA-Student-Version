package hk.ust.comp3021.entities;

/**
 * Denotes a player.
 */
public final class Player extends Entity {
    private final int id;

    public Player(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
