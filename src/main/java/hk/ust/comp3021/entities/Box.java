package hk.ust.comp3021.entities;

/**
 * Denotes a box.
 */
public final class Box extends Entity {
    private final int playerId;

    /**
     * Create a Box entity.
     *
     * @param playerId the id of the player that can move this box.
     */
    public Box(int playerId) {
        this.playerId = playerId;
    }

    /**
     * Get the id of the player that is allowed to move this box.
     *
     * @return player id.
     */
    public int getPlayerId() {
        return playerId;
    }
}
