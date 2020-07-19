package editor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class TreeViewController  {
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
}
