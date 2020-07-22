package editor.controllers;

import editor.LoginManager;
import editor.database.User;
import editor.database.UserDataAccessor;
import editor.events.EventManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LoginController {


    public TextField textField;

    @FXML
    public Button submitBtn;

    @FXML
    private PasswordField passwordField;

    @FXML
    private GridPane gridPane;

    @FXML
    private Text actionTarget;

    private UserDataAccessor userDataAccessor = UserDataAccessor.getInstance();

    public void initManager(LoginManager loginManager) {
        submitBtn.setOnAction(e -> {
            User user = null;

            user = userDataAccessor.checkUser(textField.getText(), passwordField.getText());

            if (user != null) {
                try {
                    loginManager.showMainView(user);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            else
                actionTarget.setText("Wrong username or password");
        });
    }

    @FXML
    public void initialize() throws SQLException {
        List<User> list = userDataAccessor.getPersonList();
        list.forEach(user -> System.out.println(user.getUsername()));
    }

    private void changeScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        gridPane.getScene().setRoot(pane);
    }


}
