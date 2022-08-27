package hk.ust.comp3021.entities;

/**
 * Denotes a player.
 */
public final class Player extends Entity {
    private final int id;

    /**
     * Initiate a player object with an id.
     *
     * @param id id of the player.
     */
    public Player(int id) {
        this.id = id;
    }

    /**
     * Get the player id.
     *
     * @return player id.
     */
    public int getId() {
        return id;
    }
}
