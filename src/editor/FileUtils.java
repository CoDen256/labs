package editor;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;

public class FileUtils {
    public static File loadDirectoryDialog() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("./"));
        return directoryChooser.showDialog(null);
    }

    public static File saveFileDialog(FileChooser.ExtensionFilter filter) {

        FileChooser fileChooser = getFileChooser();
        fileChooser.getExtensionFilters().add(filter);

        return  fileChooser.showSaveDialog(null);

    }

    public static File loadFileDialog() {
        return getFileChooser().showOpenDialog(null);
    }


    private static FileChooser getFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        return  fileChooser;
    }
}
