package com.ripplemap.ds;

/**
 * Generic Stack interface representing Last-In-First-Out (LIFO) behavior.
 * Used for Undo/Redo history, iterative DFS, Tarjan SCC, and expression evaluation.
 *
 * @param <T> Element type
 */
public interface CustomStack<T> extends Iterable<T> {
    /**
     * Pushes an item onto the top of this stack.
     */
    void push(T item);

    /**
     * Removes and returns the item at the top of this stack.
     * @throws java.util.NoSuchElementException if stack is empty
     */
    T pop();

    /**
     * Looks at the object at the top of this stack without removing it.
     * @throws java.util.NoSuchElementException if stack is empty
     */
    T peek();

    /**
     * Tests if this stack is empty.
     */
    boolean isEmpty();

    /**
     * Returns the number of items in this stack.
     */
    int size();

    /**
     * Removes all items from the stack.
     */
    void clear();
}
