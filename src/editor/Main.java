package editor;

import editor.controllers.SceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/main.fxml"));

        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/login.fxml"));
        Scene scene = new Scene(new StackPane());
        //Scene rootScene = new Scene(loader.load());
        LoginManager loginManager = new LoginManager(scene);
        loginManager.showLoginView();
        //SceneController sceneController = new SceneController(rootScene);


        primaryStage.setTitle("Text Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
