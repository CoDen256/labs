package editor.database;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private final StringProperty username = new SimpleStringProperty(this, "username");
    public StringProperty usernameProperty() {
        return username ;
    }
    public final String getUsername() {
        return usernameProperty().get();
    }
    public final void setUsername(String username) {
        usernameProperty().set(username);
    }

    private final StringProperty password = new SimpleStringProperty(this, "password");
    public StringProperty passwordProperty() {
        return password;
    }
    public final String getPassword() {
        return passwordProperty().get();
    }
    public final void setPassword(String password) {
        passwordProperty().set(password);
    }


    public User() {}

    public User(String username, String password) {
        setUsername(username);
        setPassword(password);
    }
}
