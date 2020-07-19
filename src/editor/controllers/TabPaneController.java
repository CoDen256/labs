package editor.controllers;

import editor.TextFile;
import editor.events.*;
import javafx.fxml.FXML;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import javax.xml.soap.Text;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabPaneController implements Subscriber {
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    private TextFile currentFile;

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
            try {
                File file = ((LoadFileEvent) event).getContent();
                List<String> content = Files.readAllLines(file.toPath());
                currentFile = new TextFile(file.toPath(), content);
                areaText = new TextArea(String.join("\n", content));
                Tab tab = new Tab(file.getName(), areaText);
                if (tabPane.getSelectionModel().getSelectedItem() != null && tabPane.getSelectionModel().getSelectedItem().equals(tab)) {
                    tabPane.getSelectionModel().select(tab);
                }
                else {
                    tabPane.getTabs().add(0, tab);
                    tabPane.getSelectionModel().select(tab);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (event instanceof NewFileEvent) {
            TextArea textArea = new TextArea();
            int numTabs = tabPane.getTabs().filtered(tab -> tab.getText().contains("untitled")).size();
            Tab tab = numTabs == 0 ? new Tab("untitled") : new Tab("untitled (" + (numTabs + 1) + ")");
            tab.setContent(textArea);
            tab.setId("" + tabPane.getTabs().size());
            tabPane.getTabs().add(0, tab);
            SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
            selectionModel.select(tab);
            currentFile = null;
        }
        if (event instanceof SaveFileEvent) {
            try {
                Path path;
                if (currentFile == null) {
                    FileChooser fileChooser = new FileChooser();

                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                    fileChooser.getExtensionFilters().add(extFilter);

                    File file = fileChooser.showSaveDialog(null);
                    path = file.toPath();
                }
                else {
                    path = currentFile.getFile();
                }
                Tab tab = tabPane.getSelectionModel().getSelectedItem();
                System.out.println(tab);
                areaText = (TextArea) tab.getContent();
                currentFile = new TextFile(path, Arrays.asList(areaText.getText().split("\\\n")));
                System.out.println(currentFile.getFile().getFileName());
                Files.write(currentFile.getFile(), currentFile.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
