package hk.ust.comp3021.gui.component.board;

import hk.ust.comp3021.entities.*;
import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.game.Position;
import hk.ust.comp3021.game.RenderingEngine;
import hk.ust.comp3021.gui.utils.Resource;
import hk.ust.comp3021.gui.utils.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Control logic for a {@link GameBoard}.
 */
public class GameBoardController implements RenderingEngine, Initializable {
    @FXML
    private GridPane map;

    @FXML
    private Label undoQuota;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void render(@NotNull GameState state) {
        Platform.runLater(() -> {
            for (int y = 0; y < state.getMapMaxHeight(); y++) {
                for (int x = 0; x < state.getMapMaxWidth(); x++) {
                    final var pos = Position.of(x, y);
                    final var entity = state.getEntity(pos);
                    if (entity == null) {
                        continue;
                    }
                    final var node = this.loadGridCell(entity, state.getDestinations().contains(pos));
                    this.map.add(node, x, y);
                }
            }
            final var quota = state.getUndoQuota().map(Object::toString).orElse("∞");
            this.undoQuota.setText(String.format("Undo Quota: %s", quota));
        });
    }

    @Override
    public void message(@NotNull String content) {
        Platform.runLater(() -> Message.info("Sokoban", content));
    }

    private @NotNull Node loadGridCell(@NotNull Entity entity, boolean isDestination) {
        final var imageUrl = switch (entity) {
            case Empty ignore -> isDestination ? Resource.getDestinationImageURL() : Resource.getEmptyImageURL();
            case Wall ignore -> Resource.getWallImageURL();
            case Player player -> Resource.getPlayerImageURL(player.getId());
            case Box box -> Resource.getBoxImageURL(box.getPlayerId());
        };
        if (imageUrl == null) {
            throw new RuntimeException("Failed to load image for entity " + entity);
        }
        try {
            final var cell = new Cell();
            cell.getController().setImage(imageUrl);
            if (isDestination && entity instanceof Box) {
                cell.getController().markAtDestination();
            }
            return cell;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load cell FXML", e);
        }
    }
}
