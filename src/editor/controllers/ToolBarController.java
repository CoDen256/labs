package editor.controllers;

import javafx.fxml.FXML;

public class ToolBarController {
    public ToolBarController() {
        System.out.println(getClass()+" created");
    }

    @FXML
    private void onAbout(){
    }

    @FXML
    public void initialize() {
        System.out.println(getClass()+" initialized");
    }
}
