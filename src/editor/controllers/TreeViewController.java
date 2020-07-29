package editor.controllers;

import editor.TreeFileVisitor;
import editor.database.User;
import editor.database.UserDataAccessor;
import editor.events.EditorEvent;
import editor.events.EditorPath;
import editor.events.EventManager;
import editor.events.Subscriber;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public class TreeViewController implements Subscriber {

    private  EventManager manager;

    private User user;

    private UserDataAccessor userDataAccessor = UserDataAccessor.getInstance();

    public TreeViewController() {
        System.out.println(getClass()+" created");
    }

    @FXML
    TreeView<EditorPath> treeView;

    @FXML
    public void initialize() {
    }

    public void setEventManager(EventManager manager) {
        this.manager = manager;
    }

    @Override
    public void update(EditorEvent event) {
        if (event.equals(EditorEvent.LOAD_DIR_EVENT)) {
            Path file = (Path) event.getContent();
            TreeFileVisitor treeFileVisitor = new TreeFileVisitor(new EditorPath(file), this::handleClick);
            treeView.setOnMouseClicked(this::handleClick);
            try {
                Files.walkFileTree(file, treeFileVisitor);
            } catch (IOException e) {
                e.printStackTrace();
            }
            treeView.setRoot(treeFileVisitor.getFirstRoot().getChildren().get(0));
            try {
                userDataAccessor.saveState(user, file.toString(), "dir");
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
        else if (event.equals(EditorEvent.SAVE_STATE)) {
            try {
                userDataAccessor.saveState(user, treeView.getRoot().getValue().getPath().toString(), "dir");
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private void handleClick(MouseEvent event) {
        if (event.getSource() instanceof TreeView && event.getClickCount() == 2){
            TreeView<EditorPath> source = (TreeView<EditorPath>) event.getSource();
            TreeItem<EditorPath> selected = source.getSelectionModel().getSelectedItem();

            if (selected != null) {
                manager.notifySubscribers(EditorEvent.LOAD_FILE_EVENT.setContent(
                        selected.getValue().getPath().toFile())
                );
            }
        }
    }

    public void setUser(User user) {
        this.user = user;
    }
}
