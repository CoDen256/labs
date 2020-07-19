package editor.controllers;

import editor.EditorModel;
import editor.IOResult;
import editor.TextFile;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Window;

import java.io.File;

public class MainController {

    @FXML
    private BorderPane borderPane;

    @FXML
    private HBox footer;

    @FXML
    private MenuBarController menuBarController;

    @FXML
    private ToolBarController toolBarController;

    @FXML
    private TreeViewController treeViewController;

    @FXML
    private TextAreaController textAreaController;

    @FXML
    private FooterController footerController;

    private EditorModel model = new EditorModel();



    public TextAreaController getTextAreaController() {
        return textAreaController;
    }

    @FXML
    public void initialize() {
        System.out.println("borderPane"+ toolBarController);
        System.out.println("footer: "+menuBarController);
        System.out.println(textAreaController);
    }

}