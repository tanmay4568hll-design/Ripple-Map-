package com.ripplemap.ds;

/**
 * Custom Circular Linked List implementation built from scratch.
 * Specifically designed for the round-robin "Event Replay" simulation playlist.
 *
 * Complexities:
 * - Insert at tail: Time O(1), Space O(1)
 * - Next / Previous cursor navigation: Time O(1), Space O(1)
 * - Size / IsEmpty: Time O(1), Space O(1)
 *
 * @param <T> Element type
 */
public class CustomCircularLinkedList<T> {

    public static class Node<T> {
        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head = null;
    private Node<T> cursor = null;
    private int size = 0;

    public CustomCircularLinkedList() {}

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Inserts an element at the end of the circular ring. Time: O(1).
     */
    public void insert(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            head = newNode;
            head.next = head;
            head.prev = head;
            cursor = head;
        } else {
            Node<T> tail = head.prev;
            tail.next = newNode;
            newNode.prev = tail;
            newNode.next = head;
            head.prev = newNode;
        }
        size++;
    }

    /**
     * Returns current cursor data without advancing.
     */
    public T current() {
        if (cursor == null) return null;
        return cursor.data;
    }

    /**
     * Advances cursor to next element in round-robin loop and returns its data. Time: O(1).
     */
    public T next() {
        if (cursor == null) return null;
        cursor = cursor.next;
        return cursor.data;
    }

    /**
     * Moves cursor to previous element in round-robin loop and returns its data. Time: O(1).
     */
    public T previous() {
        if (cursor == null) return null;
        cursor = cursor.prev;
        return cursor.data;
    }

    /**
     * Resets cursor back to the head of the circular list.
     */
    public void resetCursor() {
        cursor = head;
    }

    public void clear() {
        head = null;
        cursor = null;
        size = 0;
    }
}
