package sample.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;

public class TreeViewController  {
    @FXML
    TreeView<String> treeView;


    @FXML
    public void initialize() {
        TreeItem<String> root = new TreeItem<>("root");
        TreeItem<String> root1 = new TreeItem<>("root1");
        TreeItem<String> root2 = new TreeItem<>("root2");
        TreeItem<String> root3 = new TreeItem<>("root3");

        System.out.println("hui");

        root.getChildren().addAll(root1, root2, root3);

        treeView.setRoot(root);


    }
}
