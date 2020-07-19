package editor.controllers;

import editor.FileUtils;
import editor.events.EditorEvent;
import editor.events.EventManager;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.io.File;

public class MenuBarController {
    private EventManager eventManager;


    @FXML
    private void onSave() {
//        TextFile textFile = new TextFile();
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
    }


    @FXML
    public void onNew() {
        eventManager.notifySubscribers(EditorEvent.NEW_FILE_EVENT);
    }
}
