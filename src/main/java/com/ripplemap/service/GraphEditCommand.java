package com.ripplemap.service;

import com.ripplemap.ds.ArrayStack;
import com.ripplemap.ds.CustomStack;

/**
 * Command interface for Undo/Redo actions on graph topology.
 */
public interface GraphEditCommand {
    void execute();
    void undo();
    String getDescription();
}
