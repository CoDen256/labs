package editor.database;

import javafx.scene.control.PasswordField;

import java.awt.*;
import java.sql.*;

import java.util.List ;
import java.util.ArrayList ;

public class UserDataAccessor {
    private Connection connection ;

    public UserDataAccessor(String dbURL, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(dbURL, user, password);
    }

    public void shutdown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public List<User> getPersonList() throws SQLException {
        try (
                Statement stmnt = connection.createStatement();
                ResultSet rs = stmnt.executeQuery("select * from user");
        ){
            List<User> personList = new ArrayList<>();
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                User user = new User(username, password);
                personList.add(user);
            }
            return personList ;
        }
    }

    public Boolean checkUser(String username, String password) throws SQLException {
        try {
            PreparedStatement pstmnt = connection.prepareStatement("SELECT * FROM user where user.username = ? and user.password = ?");
            pstmnt.setString(1, username);
            pstmnt.setString(2, password);
            ResultSet rs = pstmnt.executeQuery();
            if (rs.next()) return Boolean.TRUE;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }
}
