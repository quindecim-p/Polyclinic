package client;

import client.utils.SceneSwitcher;
import common.enums.SceneRoute;
import javafx.application.Application;
import javafx.stage.Stage;
import client.utils.ClientSocket;

public class ClientMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        ClientSocket.getInstance().getSocket();
        SceneSwitcher.switchScene(primaryStage, SceneRoute.LOGIN);
    }

    public static void main(String[] args) {
        launch(args);
    }
}