package editor.controllers;

import editor.events.EditorEvent;
import editor.events.EventManager;
import editor.events.Subscriber;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.nio.file.Path;

public class TreeViewController implements Subscriber {

    private  EventManager manager;
    public TreeViewController() {
        System.out.println(getClass()+" created");
    }

    @FXML
    TreeView<String> treeView;


    @FXML
    public void initialize() {
        TreeItem<String> root = new TreeItem<>("root");
        TreeItem<String> root1 = new TreeItem<>("root1");
        TreeItem<String> root2 = new TreeItem<>("root2");
        TreeItem<String> root3 = new TreeItem<>("root3");

        System.out.println(getClass()+" initialized");

        root.getChildren().addAll(root1, root2, root3);

        treeView.setRoot(root);
    }

    public void setHandler() {

    }

    public void setEventManager(EventManager manager) {
        this.manager = manager;
    }

    @Override
    public void update(EditorEvent event) {
        if (event.equals(EditorEvent.LOAD_DIR_EVENT)) { ;
            Path file = (Path) event.getContent();


    }
}}
