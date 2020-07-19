package editor.events;

import java.nio.file.Path;

public class LoadDirectoryEvent implements EditorEvent {

    Path path;

    public LoadDirectoryEvent(Path path) {
        this.path = path;
    }

    @Override
    public Path getContent() {
        return path;
    }
}
