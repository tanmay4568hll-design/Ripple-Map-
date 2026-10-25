package com.ripplemap.ds;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Custom Singly Linked List implementation built from scratch.
 * 
 * Complexities:
 * - Insert at head: Time O(1), Space O(1)
 * - Insert at tail: Time O(1), Space O(1) (tail pointer maintained)
 * - Insert at index: Time O(n), Space O(1)
 * - Delete by value: Time O(n), Space O(1)
 * - Delete by index: Time O(n), Space O(1)
 * - Search: Time O(n), Space O(1)
 * - Reverse (Iterative): Time O(n), Space O(1) [in-place]
 * - Reverse (Recursive): Time O(n), Space O(n) [call stack]
 * - Find Middle: Time O(n), Space O(1) [Tortoise and Hare fast/slow pointers]
 *
 * @param <T> Element type
 */
public class CustomSinglyLinkedList<T> implements Iterable<T> {

    public static class Node<T> {
        public T data;
        public Node<T> next;

        public Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public CustomSinglyLinkedList() {
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
     * Inserts an element at the beginning of the list. Time: O(1).
     */
    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head = newNode;
        }
        size++;
    }

    /**
     * Inserts an element at the end of the list. Time: O(1).
     */
    public void addLast(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    /**
     * Default add appends to tail. Time: O(1).
     */
    public void add(T data) {
        addLast(data);
    }

    /**
     * Inserts element at 0-indexed position. Time: O(n).
     */
    public void add(int index, T data) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
        if (index == 0) {
            addFirst(data);
            return;
        }
        if (index == size) {
            addLast(data);
            return;
        }

        Node<T> prev = head;
        for (int i = 0; i < index - 1; i++) {
            prev = prev.next;
        }
        Node<T> newNode = new Node<>(data);
        newNode.next = prev.next;
        prev.next = newNode;
        size++;
    }

    /**
     * Retrieves element at specified index. Time: O(n).
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
        Node<T> curr = head;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        return curr.data;
    }

    /**
     * Removes and returns first element. Time: O(1).
     */
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot remove from empty list");
        }
        T data = head.data;
        head = head.next;
        size--;
        if (isEmpty()) {
            tail = null;
        }
        return data;
    }

    /**
     * Removes element at specified index. Time: O(n).
     */
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
        if (index == 0) {
            return removeFirst();
        }

        Node<T> prev = head;
        for (int i = 0; i < index - 1; i++) {
            prev = prev.next;
        }
        T data = prev.next.data;
        if (prev.next == tail) {
            tail = prev;
        }
        prev.next = prev.next.next;
        size--;
        return data;
    }

    /**
     * Deletes first occurrence of the specified value. Returns true if found and removed. Time: O(n).
     */
    public boolean removeValue(T data) {
        if (isEmpty()) return false;
        if (equals(head.data, data)) {
            removeFirst();
            return true;
        }

        Node<T> curr = head;
        while (curr.next != null && !equals(curr.next.data, data)) {
            curr = curr.next;
        }

        if (curr.next != null) {
            if (curr.next == tail) {
                tail = curr;
            }
            curr.next = curr.next.next;
            size--;
            return true;
        }
        return false;
    }

    /**
     * Linear search for value. Returns 0-based index or -1 if not found. Time: O(n).
     */
    public int indexOf(T data) {
        Node<T> curr = head;
        int idx = 0;
        while (curr != null) {
            if (equals(curr.data, data)) {
                return idx;
            }
            curr = curr.next;
            idx++;
        }
        return -1;
    }

    public boolean contains(T data) {
        return indexOf(data) != -1;
    }

    /**
     * In-place iterative reversal of the linked list. Time: O(n), Space: O(1).
     */
    public void reverse() {
        if (size <= 1) return;
        Node<T> prev = null;
        Node<T> curr = head;
        tail = head; // old head becomes new tail

        while (curr != null) {
            Node<T> next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        head = prev; // new head
    }

    /**
     * Recursive reversal of the linked list. Time: O(n), Space: O(n) call stack.
     */
    public void reverseRecursive() {
        if (size <= 1) return;
        tail = head;
        head = reverseRecursiveHelper(head);
    }

    private Node<T> reverseRecursiveHelper(Node<T> node) {
        if (node == null || node.next == null) {
            return node;
        }
        Node<T> newHead = reverseRecursiveHelper(node.next);
        node.next.next = node;
        node.next = null;
        return newHead;
    }

    /**
     * Finds middle element using Tortoise and Hare (slow & fast pointer) technique.
     * Returns null if empty. For even length, returns the second middle element.
     * Time: O(n), Space: O(1).
     */
    public T findMiddle() {
        if (isEmpty()) return null;
        Node<T> slow = head;
        Node<T> fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow.data;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    private boolean equals(T a, T b) {
        if (a == null) return b == null;
        return a.equals(b);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }
}
