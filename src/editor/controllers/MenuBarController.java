package editor.controllers;

import editor.IOResult;
import editor.TextFile;
import editor.events.EventManager;
import editor.events.LoadFileEvent;
import editor.events.NewFileEvent;
import editor.events.SaveFileEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Arrays;

public class MenuBarController {
    private EventManager eventManager;


    @FXML
    private void onSave() {
//        TextFile textFile = new TextFile();
        eventManager.notifySubscribers(new SaveFileEvent());
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @FXML
    private void onLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        File file = fileChooser.showOpenDialog(null);

        eventManager.notifySubscribers(new LoadFileEvent(file));
    }

    @FXML
    private void onAbout() {
    }


    @FXML
    public void onNew() {
        eventManager.notifySubscribers(new NewFileEvent());
    }
}
