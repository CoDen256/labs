package editor.controllers;

import editor.EditorModel;
import editor.events.EventManager;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
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
    private TabPaneController tabPaneController;

    @FXML
    private FooterController footerController;

    private EditorModel model = new EditorModel();


    @FXML
    public void initialize() {
        EventManager manager = new EventManager();
        System.out.println("borderPane"+ toolBarController);
        System.out.println("menubar: "+menuBarController);
        System.out.println("tree: "+treeViewController);
        System.out.println("footer: "+footerController);
        System.out.println("tab:"+ tabPaneController);

        menuBarController.setEventManager(manager);
        tabPaneController.setEventManager(manager);
        manager.subscribe(tabPaneController);

        footer.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> System.out.println("hui"));
    }

}