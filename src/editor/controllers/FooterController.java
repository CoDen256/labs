package editor.controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class FooterController {


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



}
