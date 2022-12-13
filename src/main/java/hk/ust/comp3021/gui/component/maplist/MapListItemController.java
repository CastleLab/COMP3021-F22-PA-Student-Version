package hk.ust.comp3021.gui.component.maplist;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Control logic for {@link MapListItem}.
 */
public class MapListItemController implements Initializable {
    @FXML
    private GridPane item;

    @FXML
    private Label mapName;

    @FXML
    private Label loadAt;

    @FXML
    private Label mapFilePath;

    private final Property<MapModel> mapModelProperty = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.mapModelProperty.addListener(ev -> render());
    }

    private void render() {
        final var model = this.mapModelProperty.getValue();
        this.mapName.setText(model.name());
        this.loadAt.setText(model.loadAt().toString());
        this.mapFilePath.setText(model.file().toString());
    }

    /**
     * @return The property for the map model.
     */
    public Property<MapModel> getMapModelProperty() {
        return mapModelProperty;
    }
}
