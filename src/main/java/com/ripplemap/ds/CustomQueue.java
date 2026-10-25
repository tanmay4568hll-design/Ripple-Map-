package com.ripplemap.ds;

/**
 * Generic Queue interface representing First-In-First-Out (FIFO) behavior.
 * Used for BFS level traversal, wave simulation, and event ingestion pipeline.
 *
 * @param <T> Element type
 */
public interface CustomQueue<T> extends Iterable<T> {
    /**
     * Inserts the specified element into this queue.
     */
    void enqueue(T item);

    /**
     * Retrieves and removes the head of this queue.
     * @throws java.util.NoSuchElementException if queue is empty
     */
    T dequeue();

    /**
     * Retrieves, but does not remove, the head of this queue.
     * @throws java.util.NoSuchElementException if queue is empty
     */
    T peek();

    /**
     * Tests if this queue is empty.
     */
    boolean isEmpty();

    /**
     * Returns the number of elements in this queue.
     */
    int size();

    /**
     * Removes all elements from this queue.
     */
    void clear();
}
