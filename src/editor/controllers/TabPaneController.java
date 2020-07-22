package editor.controllers;

import editor.FileUtils;
import editor.TextFile;
import editor.database.User;
import editor.database.UserDataAccessor;
import editor.events.EditorEvent;
import editor.events.EventManager;
import editor.events.Subscriber;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import javax.swing.text.Caret;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static editor.events.EditorEvent.NULL_FOOTER;
import static editor.events.EditorEvent.TEXT_MODIFIED;

public class TabPaneController implements Subscriber {
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    private Map<Tab, TextFile> tabTextFileMap = new HashMap<>();

    private EventManager eventManager;

    private User user;

    private double currentFont = Font.getDefault().getSize();

    private UserDataAccessor userDataAccessor = UserDataAccessor.getInstance();

    //private Clipboard clipboard = Clipboard.getSystemClipboard();

    @FXML
    private TabPane tabPane;


    @FXML
    public void initialize() {

    }

    public Tab getCurrentTab() {
        return tabPane.getSelectionModel().getSelectedItem();
    }

    public TextArea getCurrentTextArea() {
        return ((TextArea) getCurrentTab().getContent());
    }


    @Override
    public void update(EditorEvent event) {

        switch (event) {
            case LOAD_FILE_EVENT: handleLoad((File) event.getContent()); break;
            case NEW_FILE_EVENT: handleNew(); break;
            case SAVE_FILE_EVENT: handleSave(); break;
            case CUT_EVENT: getCurrentTextArea().cut();break;
            case COPY_EVENT: getCurrentTextArea().copy();break;
            case PASTE_EVENT: getCurrentTextArea().paste();break;
            case REDO_EVENT: getCurrentTextArea().redo();break;
            case UNDO_EVENT:getCurrentTextArea().undo();break;
            case BIGGER_EVENT:{
                currentFont += 5;
                updateFont(currentFont);
                break;
            }
            case SMALLER_EVENT:{
                currentFont -= 5;
                updateFont(currentFont);
                break;
            }
            case DARK_EVENT: {
                tabPane.getScene().getRoot().setStyle("-fx-base:#323232");
                break;
            }
            case BRIGHT_EVENT: {
                tabPane.getScene().getRoot().setStyle("-fx-base:white");
                break;
            }

        }


    }

    private void handleLoad(File file){
        try {
            List<String> content = Files.readAllLines(file.toPath());
            TextFile currentFile = new TextFile(file.toPath(), content);
            for (Map.Entry<Tab, TextFile> entry : tabTextFileMap.entrySet()) {
                if (entry.getValue() == null) continue;
                if (entry.getValue().getFile().equals(currentFile.getFile())) {
                    tabPane.getSelectionModel().select(entry.getKey());
                    return;
                }
            }
            Tab tab = createTab(file.getName());
            TextArea areaText = createTextArea(String.join("\n", content), tab);

            tab.setContent(areaText);

            tabPane.getTabs().add(0, tab);
            tabTextFileMap.put(tab, currentFile);
            tabPane.getSelectionModel().select(tab);
            if (!userDataAccessor.checkForFile(user, file.toPath().toString())) {
                areaText.setEditable(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleNew() {

        int numTabs = tabPane.getTabs().filtered(tab -> tab.getText().contains("untitled")).size();
        Tab tab = numTabs == 0 ? createTab("untitled *") :
                                 createTab("untitled (" + (numTabs + 1) + ") *");
        TextArea textArea = createTextArea("", tab);
        tab.setContent(textArea);
        tabPane.getTabs().add(0, tab);
        tabPane.getSelectionModel().select(tab);
        tabTextFileMap.put(tab, null);

    }

    private void handleSave() {
        try {
            Path path;
            TextFile currentFile = tabTextFileMap.get(getCurrentTab());
            if (currentFile == null) {
                File file = FileUtils.saveFileDialog(new FileChooser.ExtensionFilter("TXT, XML", "*.txt", "*.xml"));
                path = file.toPath();
            }
            else {
                path = currentFile.getFile();
            }
            TextArea areaText = getCurrentTextArea();
            currentFile = new TextFile(path, Arrays.asList(areaText.getText().split("\\\n")));

            tabTextFileMap.putIfAbsent(getCurrentTab(), currentFile);
            Files.write(currentFile.getFile(), currentFile.getContent());
            getCurrentTab().setText(path.getFileName().toString());
            // Put file in DB
            userDataAccessor.createFile(user, path.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Tab createTab(String name) {


        Tab tab = new Tab(name);

        tab.textProperty().addListener(e -> {
            if (tab.getText().contains("*")) eventManager.notifySubscribers(TEXT_MODIFIED);

        });

        tab.setOnCloseRequest(event -> {
            if (tab.getText().contains("*")) {
                popDialog(tab);
            }
            else {
                eventManager.notifySubscribers(NULL_FOOTER);
                tabTextFileMap.remove(tab);
            }
        });

        return tab;
    }

    private void popDialog(Tab tab) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Current file is modified");
        alert.setContentText("Save?");
        ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
        alert.showAndWait().ifPresent(type -> {
            if (type.getText() == "Yes") {
                eventManager.notifySubscribers(NULL_FOOTER);
                tabTextFileMap.remove(tab);
                handleSave();
            }
            else if (type.getText() == "No") {
                eventManager.notifySubscribers(NULL_FOOTER);
                tabTextFileMap.remove(tab);
            }
            else if (type.getText() == "Cancel")  {

            }
        });
    }

    private TextArea createTextArea(String content, Tab parent) {
        TextArea textArea = new TextArea(content);
        textArea.setFont(new Font(currentFont));
        textArea.positionCaret(0);
        decorateTextArea(textArea, parent);
        return textArea;
    }

    private void decorateTextArea(TextArea textArea, Tab parent) {
        textArea.caretPositionProperty().addListener((ob, old1, new1) -> {
            eventManager.notifySubscribers(EditorEvent.CURSOR_CHANGED.setContent(new1));
            eventManager.notifySubscribers(EditorEvent.FILE_LENGTH_CHANGED.setContent(textArea.getText().length()));
        });
        textArea.textProperty().addListener(e -> {
            if (!parent.getText().contains("*")) parent.setText(parent.getText() + " *");
        });
        textArea.setOnContextMenuRequested(e -> System.out.println(textArea.getSelectedText()));
        textArea.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (textArea.getSelectedText().length() > 0) {
                eventManager.notifySubscribers(EditorEvent.TEXT_SELECTED);
            }
            else {
                eventManager.notifySubscribers(EditorEvent.TEXT_UNSELECTED);
            }
        });
    }

    private void updateFont(double fontSize) {
        tabTextFileMap.keySet().forEach(tab -> ((TextArea)tab.getContent()).setFont(new Font(fontSize)));
    }

    public void setUser(User user) {
        this.user = user;
    }
}
