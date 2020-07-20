package editor;

import editor.controllers.SceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/main.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/login.fxml"));
        Scene rootScene = new Scene(loader.load());
        //SceneController sceneController = new SceneController(rootScene);


        primaryStage.setTitle("Text Editor");
        primaryStage.setScene(rootScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
