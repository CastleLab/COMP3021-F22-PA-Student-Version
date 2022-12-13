package hk.ust.comp3021.gui.scene.start;

import hk.ust.comp3021.gui.component.maplist.MapEvent;
import hk.ust.comp3021.gui.component.maplist.MapList;
import hk.ust.comp3021.gui.component.maplist.MapModel;
import hk.ust.comp3021.gui.utils.Message;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * Control logic for {@link  StartScene}.
 */
public class StartController implements Initializable {
    @FXML
    private MapList mapList;

    @FXML
    private Button deleteButton;

    @FXML
    private Button openButton;

    private final ObservableList<MapModel> mapModelList = FXCollections.observableList(new ArrayList<>());

    private void addMapModel(MapModel mapModel) {
        if (mapModel.gameMap().getPlayerIds().size() > 4) {
            Message.error("Unsupported game map", "The game map has more than 4 players.");
            return;
        }
        this.mapModelList.remove(mapModel);
        this.mapModelList.add(mapModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.loadBuiltinMaps();
        this.mapList.itemsProperty().bind(
            new SimpleListProperty<>(new SortedList<>(
                this.mapModelList,
                Comparator.comparing(MapModel::loadAt).reversed()
            ))
        );
        var sel = this.mapList.getSelectionModel().getSelectedItem();
        this.openButton.setDisable(sel == null);
        this.deleteButton.setDisable(sel == null);
        this.mapList.getSelectionModel().selectedItemProperty().addListener(ev -> {
            var selected = this.mapList.getSelectionModel().getSelectedItem();
            this.openButton.setDisable(selected == null);
            this.deleteButton.setDisable(selected == null);
        });
    }

    private void loadBuiltinMaps() {
        final var classLoader = getClass().getClassLoader();
        Stream.of("map00.map", "map01.map")
            .map(classLoader::getResource)
            .filter(Objects::nonNull)
            .map(url -> {
                try {
                    return MapModel.load(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .forEach(this::addMapModel);
    }

    @FXML
    private void onLoadMapBtnClicked(ActionEvent event) {
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Select Game Map File");
        var node = (Node) event.getSource();
        var selectedFile = fileChooser.showOpenDialog(node.getScene().getWindow());
        if (selectedFile == null) return;
        try {
            var selectedPath = selectedFile.toURI().toURL();
            var gameMapModel = MapModel.load(selectedPath);
            this.addMapModel(gameMapModel);
        } catch (Exception e) {
            Message.error("Failed to load game map file", e.getMessage());
        }
    }

    /**
     * Handle the event when the delete button is clicked.
     */
    @FXML
    public void onDeleteMapBtnClicked() {
        var selected = this.mapList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            this.mapModelList.remove(selected);
        }
    }

    /**
     * Handle the event when the map open button is clicked.
     */
    @FXML
    public void onOpenMapBtnClicked() {
        var selected = this.mapList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            this.openButton.fireEvent(new MapEvent(MapEvent.OPEN_MAP_EVENT_TYPE, selected));
        }
    }

    /**
     * Handle the event when a file is dragged over.
     *
     * @param event The drag event.
     */
    @FXML
    public void onDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    /**
     * Handle the event when the map file is dragged to this app.
     *
     * @param dragEvent The drag event.
     */
    @FXML
    public void onDragDropped(DragEvent dragEvent) {
        var db = dragEvent.getDragboard();
        for (var file : db.getFiles()) {
            try {
                var selectedPath = file.toURI().toURL();
                var gameMapModel = MapModel.load(selectedPath);
                this.addMapModel(gameMapModel);
            } catch (Exception e) {
                Message.error("Failed to load game map file", e.getMessage());
            }
        }
        dragEvent.setDropCompleted(true);
        dragEvent.consume();
    }

}
