package com.ripplemap.service;

import com.ripplemap.ds.ArrayStack;
import com.ripplemap.ds.CustomStack;

/**
 * Undo / Redo Manager powered by two manual CustomStack instances (undoStack and redoStack).
 */
public class UndoRedoManager {

    private final CustomStack<GraphEditCommand> undoStack;
    private final CustomStack<GraphEditCommand> redoStack;

    public UndoRedoManager() {
        this.undoStack = new ArrayStack<>();
        this.redoStack = new ArrayStack<>();
    }

    public void executeCommand(GraphEditCommand command) {
        if (command == null) return;
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // new action invalidates future redo history
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public String undo() {
        if (!canUndo()) return null;
        GraphEditCommand cmd = undoStack.pop();
        cmd.undo();
        redoStack.push(cmd);
        return cmd.getDescription();
    }

    public String redo() {
        if (!canRedo()) return null;
        GraphEditCommand cmd = redoStack.pop();
        cmd.execute();
        undoStack.push(cmd);
        return cmd.getDescription();
    }

    public int getUndoCount() {
        return undoStack.size();
    }

    public int getRedoCount() {
        return redoStack.size();
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
