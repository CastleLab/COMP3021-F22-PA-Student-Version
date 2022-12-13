package hk.ust.comp3021.gui.component.control;

import hk.ust.comp3021.actions.Action;
import hk.ust.comp3021.actions.Move;
import hk.ust.comp3021.entities.Player;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.Queue;
import java.util.ResourceBundle;

/**
 * Control logic for {@link MovementButtonGroup}.
 */
public class MovementButtonGroupController implements Initializable {
    @FXML
    private GridPane playerControl;

    @FXML
    private ImageView playerImage;

    private Player player = null;

    private Queue<Action> actionQueue;

    /**
     * Sets the player controller by the button group.
     *
     * @param player The player.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * The URL to the profile image of the player.
     *
     * @param url The URL.
     */
    public void setPlayerImage(URL url) {
        this.playerImage.setImage(new Image(url.toExternalForm()));
    }

    @FXML
    private void moveUp() {
        final var action = new Move.Up(this.player.getId());
        actionQueue.add(action);
    }

    @FXML
    private void moveDown() {
        final var action = new Move.Down(this.player.getId());
        actionQueue.add(action);
    }

    @FXML
    private void moveLeft() {
        final var action = new Move.Left(this.player.getId());
        actionQueue.add(action);
    }

    @FXML
    private void moveRight() {
        final var action = new Move.Right(this.player.getId());
        actionQueue.add(action);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Sets the actions queue to store the unprocessed movement actions.
     *
     * @param queue The queue.
     */
    public void setActionQueue(Queue<Action> queue) {
        this.actionQueue = queue;
    }
}
