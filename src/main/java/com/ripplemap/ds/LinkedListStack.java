package com.ripplemap.ds;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Singly linked list backed implementation of CustomStack.
 * Guaranteed O(1) worst-case time per operation without resizing spikes.
 *
 * Complexities:
 * - Push: Time O(1), Space O(1)
 * - Pop: Time O(1), Space O(1)
 * - Peek: Time O(1), Space O(1)
 * - Size / IsEmpty: Time O(1), Space O(1)
 *
 * @param <T> Element type
 */
public class LinkedListStack<T> implements CustomStack<T> {

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node<T> top;
    private int size;

    public LinkedListStack() {
        this.top = null;
        this.size = 0;
    }

    @Override
    public void push(T item) {
        top = new Node<>(item, top);
        size++;
    }

    @Override
    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return top.data;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        top = null;
        size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> curr = top;

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
