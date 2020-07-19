package editor.controllers;

import editor.events.EditorEvent;
import editor.events.EventManager;
import editor.events.LoadFileEvent;
import editor.events.Subscriber;
import javafx.fxml.FXML;
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
    }
}
