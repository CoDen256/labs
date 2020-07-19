package editor.events;

import editor.TextFile;

import java.io.File;

public class LoadFileEvent implements EditorEvent {
    private File file;
    public LoadFileEvent(File file) {
        this.file = file;
    }

    @Override
    public File getContent() {
        return file;
    }
}
