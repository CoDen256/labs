package editor.controllers;

import editor.events.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.io.File;

public class ToolBarController implements Subscriber {
    public ToolBarController() {
        System.out.println(getClass()+" created");
    }

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
        manager.notifySubscribers(new NewFileEvent());
    }

    @FXML
    public void onLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        File file = fileChooser.showOpenDialog(null);

        manager.notifySubscribers(new LoadFileEvent(file));
    }

    @FXML
    public void onOpenDirectory() {

    }

    @FXML
    public void onSave() {

    }

    @FXML
    public void onCut() {

    }

    @FXML
    public void onCopy() {

    }

    @FXML
    public void onPaste() {

    }

    @FXML
    public void onUndo() {

    }

    @FXML
    public void onRedo() {

    }

    @FXML
    public void onBigger() {

    }

    @FXML
    public void onSmaller() {

    }

    @Override
    public void update(EditorEvent event) {

    }
}
