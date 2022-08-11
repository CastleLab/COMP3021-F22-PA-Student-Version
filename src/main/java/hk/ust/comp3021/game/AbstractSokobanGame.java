package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.Action;
import hk.ust.comp3021.actions.ActionResult;
import hk.ust.comp3021.actions.Move;
import hk.ust.comp3021.actions.Undo;
import hk.ust.comp3021.entities.Box;
import hk.ust.comp3021.entities.Player;
import hk.ust.comp3021.entities.Wall;

/**
 * A base implementation of Sokoban Game.
 */
public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;

    protected boolean shouldStop() {
        return this.state.isWin() || this.state.isDeadlock();
    }

    protected ActionResult processAction(int playerId, Action action) {
        var currentPlayerPos = this.state.getPlayerPositionById(playerId);
        if (currentPlayerPos == null) {
            throw new IllegalArgumentException("player does not exist");
        }
        if (action instanceof Move move) {
            this.processOneStepMove(currentPlayerPos, move);
            return new ActionResult.Success(action);
        } else if (action instanceof Undo) {
            // ensure there is still undo quota
            if (this.state.getUndoQuota() != 0) {
                this.state.undo();
                return new ActionResult.Success(action);
            } else {
                return new ActionResult.Failed(action, "no more undo quota");
            }
        } else {
            return new ActionResult.Failed(action, "invalid action");
        }
    }

    /**
     * @param playerPosition
     * @param move           One-step move. We assume every move has only one step.
     */
    private void processOneStepMove(Position playerPosition, Move move) {
        var nextPlayerPos = playerPosition.shift(move);
        var nextEntity = this.state.getEntity(nextPlayerPos);
        if (nextEntity == null) {
            this.state.move(playerPosition, nextPlayerPos); // move if next place is empty
        } else if (nextEntity instanceof Wall || nextEntity instanceof Player) {
            return; // do nothing if next place is occupied by wall or another player.
        } else if (nextEntity instanceof Box) {
            var nextBoxPos = nextPlayerPos.shift(move);
            if (this.state.getEntity(nextBoxPos) != null) return; // do nothing if the box cannot move forward.
            this.state.move(nextPlayerPos, nextBoxPos);
            this.state.move(playerPosition, nextPlayerPos);
            // Game history checkpoint reached if any box is moved.
            this.state.checkpoint();
        }
    }
}
