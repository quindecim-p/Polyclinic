package client.utils;

import common.enums.SceneRoute;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneSwitcher {
    public static void switchScene(Stage stage, SceneRoute sceneType) throws IOException {
        String fxmlPath = sceneType.getFxmlPath();
        String title = sceneType.getTitle();

        Parent root = FXMLLoader.load(Objects.requireNonNull(SceneSwitcher.class.getResource(fxmlPath)));

        Scene scene = new Scene(root);

        stage.setTitle(title);
        stage.setScene(scene);

        stage.centerOnScreen();

        stage.show();
    }
}