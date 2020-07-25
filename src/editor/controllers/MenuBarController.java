package editor.controllers;

import editor.FileUtils;
import editor.events.EditorEvent;
import editor.events.EventManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

import java.io.File;

public class MenuBarController {
    private EventManager eventManager;


    @FXML
    private MenuItem logout;

    @FXML
    private void onSave() {
        eventManager.notifySubscribers(EditorEvent.SAVE_FILE_EVENT);
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @FXML
    private void onLoad() {
        File file = FileUtils.loadFileDialog();
        eventManager.notifySubscribers(EditorEvent.LOAD_FILE_EVENT.setContent(file));
    }

    @FXML
    private void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "This is a simple Text Editor", ButtonType.OK);
        alert.showAndWait();
    }

    @FXML
    public void onNew() {
        eventManager.notifySubscribers(EditorEvent.NEW_FILE_EVENT);
    }

    @FXML
    public void onCut() {
        eventManager.notifySubscribers(EditorEvent.CUT_EVENT);
    }

    @FXML
    public void onCopy(ActionEvent actionEvent) {
        eventManager.notifySubscribers(EditorEvent.COPY_EVENT);
    }

    @FXML
    public void onPaste() {
        eventManager.notifySubscribers(EditorEvent.PASTE_EVENT);
    }

    @FXML
    public void onRedo() {
        eventManager.notifySubscribers(EditorEvent.REDO_EVENT);
    }

    @FXML
    public void onUndo() {
        eventManager.notifySubscribers(EditorEvent.UNDO_EVENT);
    }

    @FXML
    public void onLarger() {
        eventManager.notifySubscribers(EditorEvent.BIGGER_EVENT);
    }

    @FXML
    public void onSmaller() {
        eventManager.notifySubscribers(EditorEvent.SMALLER_EVENT);
    }

    @FXML
    public void onDark() {
        eventManager.notifySubscribers(EditorEvent.DARK_EVENT);
    }

    @FXML
    public void onBright() {
        eventManager.notifySubscribers(EditorEvent.BRIGHT_EVENT);
    }


    public MenuItem getLogout() {
        return logout;
    }
}
