package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controllers.TreeViewController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui.fxml"));

        primaryStage.setTitle("Editor");
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
