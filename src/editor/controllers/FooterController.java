package editor.controllers;

import javafx.fxml.FXML;

public class FooterController {

    @FXML
    private MainController controller;

    @FXML
    private TabController tabController;

    public void setMainController(MainController controller) {
        this.controller = controller;
    }

    @FXML
    public String text = "something else";

    @FXML
    public void initialize() {
        System.out.println(tabController);
        System.out.println(controller);
    }



}
