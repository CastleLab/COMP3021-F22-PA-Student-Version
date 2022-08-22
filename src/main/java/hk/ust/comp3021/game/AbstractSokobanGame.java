package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.*;
import hk.ust.comp3021.entities.Box;
import hk.ust.comp3021.entities.Empty;
import hk.ust.comp3021.entities.Player;
import hk.ust.comp3021.entities.Wall;
import org.jetbrains.annotations.NotNull;

/**
 * A base implementation of Sokoban Game.
 */
public abstract class AbstractSokobanGame implements SokobanGame {
    protected final GameState state;

    private boolean isExitSpecified = false;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    protected boolean shouldStop() {
        return isExitSpecified || this.state.isWin() || this.state.isStuck();
    }

    protected ActionResult processAction(@NotNull Action action) {
        return switch (action) {
            case InvalidInput i -> new ActionResult.Failed(action, i.getMessage());
            case Undo ignored -> {
                if (this.state.getUndoQuota() != 0) {
                    this.state.undo();
                    yield new ActionResult.Success(action);
                } else {
                    yield new ActionResult.Failed(action, "no more undo quota");
                }
            }
            case Exit ignored -> {
                this.isExitSpecified = true;
                yield new ActionResult.Success(action);
            }
            case Move move -> {
                var currentPlayerPos = this.state.getPlayerPositionById(action.getInitiator());
                if (currentPlayerPos == null) {
                    throw new IllegalArgumentException("player does not exist");
                }
                yield this.processOneStepMove(currentPlayerPos, move);
            }
        };
    }

    /**
     * @param playerPosition The position of the player.
     * @param move           One-step move. We assume every move has only one step.
     * @return The action result for the move.
     */
    private ActionResult processOneStepMove(Position playerPosition, Move move) {
        var nextPlayerPos = move.nextPosition(playerPosition);
        var nextEntity = this.state.getEntity(nextPlayerPos);
        return switch (nextEntity) {
            case Empty ignored -> {
                this.state.move(playerPosition, nextPlayerPos); // move if next place is empty
                yield new ActionResult.Success(move);
            }
            case Wall ignored -> new ActionResult.Failed(move, "You hit a wall.");
            case Player ignored -> new ActionResult.Failed(move, "You hit another player.");
            case Box ignored -> {
                var nextBoxPos = move.nextPosition(nextPlayerPos);
                if (!(this.state.getEntity(nextBoxPos) instanceof Empty))
                    yield new ActionResult.Failed(move, "Failed to push the box.");
                this.state.move(nextPlayerPos, nextBoxPos);
                this.state.move(playerPosition, nextPlayerPos);
                // Game history checkpoint reached if any box is moved.
                this.state.checkpoint();
                yield new ActionResult.Success(move);
            }
        };
    }
}
