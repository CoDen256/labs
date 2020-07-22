package editor;

import editor.controllers.LoginController;
import editor.controllers.MainController;
import editor.database.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class LoginManager {

    private Scene scene;

    public LoginManager(Scene scene) {
        this.scene = scene;
    }

    public void showMainView(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/main.fxml"));
        scene.setRoot(loader.load());
        MainController main = loader.getController();
        main.setUser(user);
        main.initManager(this);
    }

    public void showLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/login.fxml"));
        scene.setRoot(loader.load());
        LoginController main = loader.getController();
        main.initManager(this);
    }
}
