package editor.events;

import java.io.File;

public class OpenFileEvent implements EditorEvent {

    private File file;

    public OpenFileEvent(File file) {
        this.file = file;
    }

    @Override
    public File getContent() {
        return file;
    }
}
