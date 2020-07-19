package editor.controllers;

import editor.IOResult;
import editor.TextFile;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Arrays;

public class MenuBarController {
    private MainController controller;

    @FXML
    private void onSave() {

    }

    @FXML
    private void onLoad() {

    }

    @FXML
    private void onAbout() {
    }


    public void setMainController(MainController controller) {
        this.controller = controller;
    }

}
