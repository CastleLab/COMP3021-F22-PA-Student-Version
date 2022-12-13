package hk.ust.comp3021.gui.component.maplist;

import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import java.io.IOException;

/**
 * The cell of the map list.
 */
public class MapListCell extends ListCell<MapModel> {

    @Override
    protected void updateItem(MapModel item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            final var loader = MapListItem.fxmlLoader();
            try {
                final var node = loader.<GridPane>load();
                setGraphic(node);
                final var controller = loader.<MapListItemController>getController();
                controller.getMapModelProperty().setValue(item);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load game map list item", e);
            }
        }
    }
}
