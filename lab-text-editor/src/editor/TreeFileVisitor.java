package editor;

import editor.events.EditorPath;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import sun.reflect.generics.tree.Tree;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class TreeFileVisitor implements FileVisitor<Path> {

    private TreeItem<EditorPath> lastTreeRoot;
    private TreeItem<EditorPath> firstRoot;
    private EventHandler<MouseEvent> eventHandler;


    public TreeFileVisitor(EditorPath root, EventHandler<MouseEvent> eventHandler) {
        this.eventHandler = eventHandler;
        lastTreeRoot = new TreeItem<>(root);
        firstRoot = lastTreeRoot;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        System.out.println(dir);
        TreeItem<EditorPath> newRoot = new TreeItem<>(new EditorPath(dir));
        lastTreeRoot.getChildren().add(newRoot);
        lastTreeRoot.getChildren().sort(this::compare);
        lastTreeRoot = newRoot;
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        TreeItem<EditorPath> treeItem = new TreeItem<>(new EditorPath(file));
        treeItem.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);

        lastTreeRoot.getChildren().add(treeItem);
        lastTreeRoot.getChildren().sort(this::compare);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        lastTreeRoot = lastTreeRoot.getParent();
        return FileVisitResult.CONTINUE;
    }


    private int compare(TreeItem<EditorPath> one, TreeItem<EditorPath> another){
        if (one.isLeaf() && !another.isLeaf()){
            return 1;
        } else if (!one.isLeaf() && another.isLeaf()) {
            return -1;
        }
        return one.toString().compareTo(another.toString());
    }

    public TreeItem<EditorPath> getFirstRoot() {
        return firstRoot;
    }
}
