package editor.controllers;

import editor.EditorModel;
import editor.LoginManager;
import editor.database.User;
import editor.events.EventManager;
import javafx.fxml.FXML;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;

import static editor.events.EditorEvent.SAVE_STATE;

public class MainController {

    private User user;

    public ToolBar toolBar;

    private EventManager manager;

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

    public void initManager(LoginManager loginManager) {
        menuBarController.getLogout().setOnAction(e -> {
            try {
                manager.notifySubscribers(SAVE_STATE);
                loginManager.showLoginView();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    @FXML
    public void initialize() {
        manager = new EventManager();
        System.out.println("borderPane"+ toolBarController);
        System.out.println("menubar: "+menuBarController);
        System.out.println("tree: "+treeViewController);
        System.out.println("footer: "+footerController);
        System.out.println("tab:"+ tabPaneController);

        menuBarController.setEventManager(manager);
        tabPaneController.setEventManager(manager);
        toolBarController.setEventManager(manager);
        treeViewController.setEventManager(manager);


        manager.subscribe(tabPaneController);
        manager.subscribe(toolBarController);
        manager.subscribe(treeViewController);
        manager.subscribe(footerController);
    }

    public void populateAllWithUser(User user) {
        footerController.setUsername(user.getUsername());
        tabPaneController.setUser(user);
        tabPaneController.loadState();
    }
}