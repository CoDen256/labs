package editor.controllers;

import editor.events.EditorEvent;
import editor.events.EventManager;
import editor.events.Subscriber;
import javafx.fxml.FXML;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;

import javax.swing.text.html.parser.Entity;

public class FooterController implements Subscriber {

    @FXML
    private Text username;

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    private EventManager eventManager;

    @FXML
    private Text currentPosition;
    @FXML
    private Text fileLength;

    private Integer len = 0;
    private Integer position = 0;

    @FXML
    public void initialize() {
        fileLength.setText("0");
        currentPosition.setText("0");
        username.setFont(Font.font("Verdana", FontPosture.ITALIC, 16));
    }


    @Override
    public void update(EditorEvent event) {
        switch (event) {
            case CURSOR_CHANGED: {
                Integer newPos = (Integer) event.getContent();
                currentPosition.setText((newPos).toString());
                break;
            }
            case FILE_LENGTH_CHANGED: {
                len = (Integer) event.getContent();
                fileLength.setText(len.toString());
                break;
            }
            case NULL_FOOTER: {
                initialize();
            }
        }
    }

    public void setUsername(String username) {
        this.username.setText(username);
    }
}
