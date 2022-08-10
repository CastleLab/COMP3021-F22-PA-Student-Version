package hk.ust.comp3021.actions;

import org.jetbrains.annotations.Nullable;

public record PlayerAction(@Nullable Integer playerId, Action[] actions) {
    public PlayerAction(@Nullable Integer playerId, Action action) {
        this(playerId, new Action[]{action});
    }
}
