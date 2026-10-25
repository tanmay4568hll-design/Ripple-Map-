package com.ripplemap.ds;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Custom Doubly Linked List implementation built from scratch.
 * Essential for O(1) node removal in the LRU Cache and Undo/Redo operations.
 *
 * Complexities:
 * - Insert at head/tail: Time O(1), Space O(1)
 * - Remove head/tail: Time O(1), Space O(1)
 * - Remove known node: Time O(1), Space O(1)
 * - Move to head: Time O(1), Space O(1)
 * - Search by value: Time O(n), Space O(1)
 *
 * @param <T> Element type
 */
public class CustomDoublyLinkedList<T> implements Iterable<T> {

    public static class Node<T> {
        public T data;
        public Node<T> prev;
        public Node<T> next;

        public Node(T data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public CustomDoublyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Node<T> getHead() {
        return head;
    }

    public Node<T> getTail() {
        return tail;
    }

    /**
     * Inserts node at head. Returns newly created Node reference. Time: O(1).
     */
    public Node<T> addFirst(T data) {
        Node<T> node = new Node<>(data);
        if (isEmpty()) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
        return node;
    }

    /**
     * Inserts node at tail. Returns newly created Node reference. Time: O(1).
     */
    public Node<T> addLast(T data) {
        Node<T> node = new Node<>(data);
        if (isEmpty()) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        size++;
        return node;
    }

    public void add(T data) {
        addLast(data);
    }

    /**
     * Removes and returns first element. Time: O(1).
     */
    public T removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Cannot remove from empty list");
        T data = head.data;
        removeNode(head);
        return data;
    }

    /**
     * Removes and returns last element. Time: O(1).
     */
    public T removeLast() {
        if (isEmpty()) throw new NoSuchElementException("Cannot remove from empty list");
        T data = tail.data;
        removeNode(tail);
        return data;
    }

    /**
     * Removes a known node in O(1) time by re-linking its neighbors.
     */
    public void removeNode(Node<T> node) {
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        node.prev = null;
        node.next = null;
        size--;
    }

    /**
     * Moves an existing node to the head of the list (crucial for LRU cache hit). Time: O(1).
     */
    public void moveToHead(Node<T> node) {
        if (node == null || node == head) return;
        removeNode(node);
        // Re-attach at head without allocating new node
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            node.prev = null;
            head.prev = node;
            head = node;
        }
        size++;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> curr = head;

            @Override
            public boolean hasNext() {
                return curr != null;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T data = curr.data;
                curr = curr.next;
                return data;
            }
        };
    }
}
