package editor.controllers;

import editor.events.*;
import javafx.fxml.FXML;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class TabPaneController implements Subscriber {
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    private EventManager eventManager;

    @FXML
    private TextArea areaText;

    @FXML
    private MainController controller;

    @FXML
    private TabPane tabPane;

    public TextArea getAreaText() {
        return areaText;
    }

    public void setAreaText(String text) {
        areaText.setText(text);
    }

    @FXML
    public void initialize() {
        System.out.println(areaText);
        System.out.println(controller);
    }



    @Override
    public void update(EditorEvent event) {
        if (event instanceof LoadFileEvent) {
            File file = ((LoadFileEvent) event).getContent();
            try {
                List<String> content = Files.readAllLines(file.toPath());
                areaText.setText(String.join("\n", content));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (event instanceof NewFileEvent) {
            TextArea textArea = new TextArea();
            int numTabs = tabPane.getTabs().filtered(tab -> tab.getText().contains("untitled")).size();
            Tab tab = numTabs == 0 ? new Tab("untitled") : new Tab("untitled (" + (numTabs + 1) + ")");
            tab.setContent(textArea);
            tabPane.getTabs().add(tab);
            SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
            selectionModel.select(tab);
        }
    }
}
