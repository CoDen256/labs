package editor.events;

import java.nio.file.Path;

public class EditorPath{
    private Path path;

    public EditorPath(Path path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path.getFileName().toString();
    }

    public Path getPath() {
        return path;
    }
}
