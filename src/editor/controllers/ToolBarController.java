package editor.controllers;

import editor.events.EditorEvent;
import editor.events.EventManager;
import editor.events.Subscriber;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

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

    private void setEventManager(EventManager manager) {
        this.manager = manager;
    }

    @FXML
    public void onCreate() {

    }

    @FXML
    public void onOpen() {

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
