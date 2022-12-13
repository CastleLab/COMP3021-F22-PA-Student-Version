package hk.ust.comp3021.gui.component.maplist;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller logic for {@link  MapList}.
 */
public class MapListController implements Initializable {
    @FXML
    private ListView<MapModel> list;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        list.setCellFactory(listView -> new MapListCell());
    }
}
