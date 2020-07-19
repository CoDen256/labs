package editor.controllers;

import editor.events.EditorEvent;
import editor.events.Subscriber;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class FooterController implements Subscriber {


    @FXML
    private Text fileLines;
    @FXML
    private Text currentLine;
    @FXML
    private Text currentColumn;
    @FXML
    private Text fileLength;

    @FXML
    public void initialize() {
        fileLength.setText("10");
        currentColumn.setText("10");
        currentLine.setText("10");
        fileLines.setText("10");
    }


    @Override
    public void update(EditorEvent event) {

    }
}
