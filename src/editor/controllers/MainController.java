package editor.controllers;

import editor.EditorModel;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

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
    private TabController tabController;

    @FXML
    private FooterController footerController;

    private EditorModel model = new EditorModel();


    @FXML
    public void initialize() {
        System.out.println("borderPane"+ toolBarController);
        System.out.println("menubar: "+menuBarController);
        System.out.println("tree: "+treeViewController);
        System.out.println("footer: "+footerController);
        System.out.println("tab:"+tabController);
    }

}