package editor.controllers;

import editor.FileUtils;
import editor.events.EditorEvent;
import editor.events.EventManager;

import editor.events.Subscriber;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javax.swing.*;
import java.io.File;

import static editor.events.EditorEvent.*;

public class ToolBarController implements Subscriber {
    public ToolBarController() {
        System.out.println(getClass()+" created");
    }

    @FXML
    private Button copyBtn;

    @FXML
    private Button cutBtn;

    @FXML
    private Button saveBtn;

    private EventManager manager;

    @FXML
    private void onAbout(){
    }

    @FXML
    public void initialize() {
        System.out.println(getClass()+" initialized");
    }

    public void setEventManager(EventManager manager) {
        this.manager = manager;
    }

    @FXML
    public void onCreate() {
        manager.notifySubscribers(NEW_FILE_EVENT);
    }

    @FXML
    public void onLoad() {
        File file = FileUtils.loadFileDialog();
        manager.notifySubscribers(LOAD_FILE_EVENT.setContent(file));
    }

    @FXML
    public void onOpenDirectory() {
        File file = FileUtils.loadDirectoryDialog();
        manager.notifySubscribers(LOAD_DIR_EVENT.setContent(file.toPath()));
    }

    @FXML
    public void onSave() {
        manager.notifySubscribers(SAVE_FILE_EVENT);
    }

    @FXML
    public void onCut() {
        manager.notifySubscribers(CUT_EVENT);

    }

    @FXML
    public void onCopy() {
        manager.notifySubscribers(COPY_EVENT);
    }

    @FXML
    public void onPaste() {
        manager.notifySubscribers(PASTE_EVENT);
    }

    @FXML
    public void onUndo() {
        manager.notifySubscribers(UNDO_EVENT);
    }

    @FXML
    public void onRedo() {
        manager.notifySubscribers(REDO_EVENT);
    }

    @FXML
    public void onBigger() {
        manager.notifySubscribers(BIGGER_EVENT);
    }

    @FXML
    public void onSmaller() {
        manager.notifySubscribers(SMALLER_EVENT);
    }

    private void changeButtonsState(boolean state) {
        copyBtn.setDisable(state);
        cutBtn.setDisable(state);
    }

    @Override
    public void update(EditorEvent event) {
        switch (event) {
            case TEXT_SELECTED: changeButtonsState(false); break;
            case TEXT_UNSELECTED: changeButtonsState(true); break;
            case TEXT_MODIFIED: saveBtn.setDisable(false); break;
        }
    }

}
