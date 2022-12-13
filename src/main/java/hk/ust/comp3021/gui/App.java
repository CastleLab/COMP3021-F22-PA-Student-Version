package hk.ust.comp3021.gui;

import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.gui.component.maplist.MapEvent;
import hk.ust.comp3021.gui.scene.game.ExitEvent;
import hk.ust.comp3021.gui.scene.game.GameScene;
import hk.ust.comp3021.gui.scene.start.StartScene;
import hk.ust.comp3021.gui.utils.Message;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The JavaFX application that launches the game.
 */
public class App extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Sokoban Game - COMP3021 2022Fall");

        primaryStage.setScene(this.loadStartScene());

        primaryStage.show();
    }

    private Scene _startScene;

    private Scene loadStartScene() throws IOException {
        if (_startScene == null) {
            _startScene = new StartScene();
            _startScene.addEventHandler(MapEvent.OPEN_MAP_EVENT_TYPE, this::onOpenMap);
        }
        return _startScene;
    }

    private Scene loadGameScene(GameState gameState) throws IOException {
        final var gameScene = new GameScene(gameState);
        gameScene.addEventHandler(ExitEvent.EVENT_TYPE, this::onExitGame);
        return gameScene;
    }

    /**
     * Event handler for opening a map.
     *
     * @param event The event data related to the map being opened.
     */
    public void onOpenMap(MapEvent event) {
        try {
            final var gameState = new GameState(event.getModel().gameMap());
            final var gameScene = loadGameScene(gameState);
            this.primaryStage.setScene(gameScene);
        } catch (IOException e) {
            Message.error("Failed to load game view", e.getMessage());
        }
    }

    /**
     * Event handler for exiting the game.
     *
     * @param event The event data related to exiting the game.
     */
    public void onExitGame(ExitEvent event) {
        try {
            final var startScene = loadStartScene();
            this.primaryStage.setScene(startScene);
        } catch (IOException e) {
            Message.error("Failed to load start view", e.getMessage());
        }
    }
}
