package hk.ust.comp3021.gui.component.control;

import hk.ust.comp3021.actions.Action;
import hk.ust.comp3021.actions.Undo;
import hk.ust.comp3021.entities.Player;
import hk.ust.comp3021.game.InputEngine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.ResourceBundle;

/**
 * Control logic for a {@link ControlPanel}.
 */
public class ControlPanelController implements Initializable, InputEngine {
    @FXML
    private FlowPane playerControls;

    private final Queue<Action> unprocessedActions = new ArrayDeque<>();

    @Override
    public @NotNull Action fetchAction() {
        Action result;
        do {
            result = unprocessedActions.poll();
            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException ignored) {
            }
        } while (result == null);
        return result;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Event handler for the undo button.
     *
     * @param event Event data related to clicking the button.
     */
    public void onUndo(ActionEvent event) {
        final var action = new Undo(-1);
        unprocessedActions.add(action);
    }

    /**
     * Adds a player to the control player.
     * Should add a new movement button group for the player.
     *
     * @param player         The player.
     * @param playerImageUrl The URL to the profile image of the player
     */
    public void addPlayer(Player player, URL playerImageUrl) {
        final MovementButtonGroup movementButtonGroup;
        try {
            movementButtonGroup = new MovementButtonGroup();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load player control", e);
        }
        movementButtonGroup.getController().setPlayer(player);
        movementButtonGroup.getController().setPlayerImage(playerImageUrl);
        movementButtonGroup.getController().setActionQueue(unprocessedActions);
        this.playerControls.getChildren().add(movementButtonGroup);
    }

}
