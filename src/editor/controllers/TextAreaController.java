package editor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class TextAreaController {
    @FXML
    private TextArea areaText;

    @FXML
    private MainController controller;

    public TextArea getAreaText() {
        return areaText;
    }

    public void setAreaText(String text) {
        areaText.setText(text);
    }

    @FXML
    public void initialize() {
        System.out.println(areaText);
        System.out.println(controller);
    }
}
