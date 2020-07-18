package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import sample.controllers.TreeViewController;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EditorController {

    @FXML
    private TextArea areaText;

    @FXML
    TreeView<String> treeView;

    @FXML
    TreeViewController treeViewController;

    private TextFile currentTextFile;

    private EditorModel model;

    @FXML
    public void initialize() {
        this.model = new EditorModel();
    }

    @FXML
    private void onSave() {
        TextFile textFile = new TextFile(currentTextFile.getFile(), Arrays.asList(areaText.getText().split("\n")));
        model.save(textFile);
    }

    @FXML
    private void onLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            IOResult<TextFile> io = model.load(file.toPath());

            if (io.isOk() && io.hasData()) {
                currentTextFile = io.getData();

                areaText.clear();
                currentTextFile.getContent().forEach(line -> areaText.appendText(line + "\n"));
            } else {
                System.out.println("Failed");
            }
        }
    }

    @FXML
    private void onClose() {
        model.close();
    }

    @FXML
    private void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("About");
        alert.setContentText("This is a simple text editor EditorApp");
        alert.show();
    }
}
