package editor.controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class FooterController {

    @FXML
    private MainController controller;

    @FXML
    private TextAreaController textAreaController;

    public void setMainController(MainController controller) {
        this.controller = controller;
    }

    @FXML
    public String text = "something else";

    @FXML
    public void initialize() {
        System.out.println(textAreaController);
        System.out.println(controller);
    }



}
