package editor.events;

import java.io.File;
import java.nio.file.Path;

public enum  EditorEvent {
    NEW_FILE_EVENT,
    LOAD_FILE_EVENT,
    LOAD_DIR_EVENT,
    SAVE_FILE_EVENT,

    CUT_EVENT,
    COPY_EVENT,
    PASTE_EVENT,

    REDO_EVENT,
    UNDO_EVENT,

    BIGGER_EVENT,
    SMALLER_EVENT
    ;

    protected Object content;

    public Object getContent(){
        return content;
    }

    public EditorEvent setContent(Object content){
        this.content = content;
        return this;
    }
}
