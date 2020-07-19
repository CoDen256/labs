package editor.controllers;

import editor.FileUtils;
import editor.TextFile;
import editor.events.EditorEvent;
import editor.events.EventManager;
import editor.events.Subscriber;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
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

public class TabPaneController implements Subscriber {
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    private Map<Tab, TextFile> tabTextFileMap = new HashMap<>();

    private EventManager eventManager;

    private double currentFont = Font.getDefault().getSize();

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
            TextArea areaText = createTextArea(String.join("\n", content));


            for (Map.Entry<Tab, TextFile> entry : tabTextFileMap.entrySet()) {
                if (entry.getValue() == null) continue;
                if (entry.getValue().getFile().equals(currentFile.getFile())) {
                    tabPane.getSelectionModel().select(entry.getKey());
                    return;
                }
            }

            Tab tab = createTab(file.getName(), areaText);
            tabPane.getTabs().add(0, tab);
            tabTextFileMap.put(tab, currentFile);
            tabPane.getSelectionModel().select(tab);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleNew() {
        TextArea textArea = createTextArea("");
        int numTabs = tabPane.getTabs().filtered(tab -> tab.getText().contains("untitled")).size();
        Tab tab = numTabs == 0 ? createTab("untitled", textArea) :
                                 createTab("untitled (" + (numTabs + 1) + ")", textArea);
        tabPane.getTabs().add(0, tab);
        tabPane.getSelectionModel().select(tab);
        tabTextFileMap.put(tab, null);
    }

    private void handleSave() {
            try {
                Path path;
                TextFile currentFile = tabTextFileMap.get(getCurrentTab());
                if (currentFile == null) {
                    File file = FileUtils.saveFileDialog(new FileChooser.ExtensionFilter("TXT", "*.txt"));
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
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    private Tab createTab(String name, TextArea textArea ) {


        Tab tab = new Tab(name, textArea);

        tab.setOnCloseRequest(event -> {
            eventManager.notifySubscribers(NULL_FOOTER);
            tabTextFileMap.remove(tab);
        });

        return tab;
    }

    private TextArea createTextArea(String content) {
        TextArea textArea = new TextArea(content);
        textArea.setFont(new Font(currentFont));
        textArea.positionCaret(0);
        textArea.caretPositionProperty().addListener((ob, old1, new1) -> {
            eventManager.notifySubscribers(EditorEvent.CURSOR_CHANGED.setContent(new1));
            eventManager.notifySubscribers(EditorEvent.FILE_LENGTH_CHANGED.setContent(textArea.getText().length()));
        });
        return textArea;
    }

    private void updateFont(double fontSize) {
        tabTextFileMap.keySet().forEach(tab -> ((TextArea)tab.getContent()).setFont(new Font(fontSize)));
    }
}
