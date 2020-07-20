package editor.controllers;

import editor.database.User;
import editor.database.UserDataAccessor;
import editor.events.EventManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    private PasswordField passwordField;

    @FXML
    private GridPane gridPane;

    @FXML
    private Text actionTarget;

    private UserDataAccessor userDataAccessor;

    @FXML
    public void initialize() throws SQLException {
        userDataAccessor = new UserDataAccessor("jdbc:mysql://127.0.0.1:3306/editor", "root", "root");
        List<User> list = userDataAccessor.getPersonList();
        list.forEach(user -> System.out.println(user.getUsername()));
    }

    @FXML
    private void handleSubmitButtonAction() throws IOException, SQLException {
        if (userDataAccessor.checkUser(textField.getText(), passwordField.getText()))
            changeScene("../../resources/main.fxml");
        else
            actionTarget.setText("Wrong username or password");
    }

    private void changeScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        gridPane.getScene().setRoot(pane);
    }


}
