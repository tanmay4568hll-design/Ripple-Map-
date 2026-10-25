package com.ripplemap.ds;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Array-based circular ring-buffer implementation of CustomQueue.
 * Utilizes modulo arithmetic for wrap-around and doubles capacity when full.
 *
 * Complexities:
 * - Enqueue: Time Amortized O(1), Worst O(n) on resize; Space O(1)
 * - Dequeue: Time O(1), Space O(1)
 * - Peek: Time O(1), Space O(1)
 * - Size / IsEmpty: Time O(1), Space O(1)
 *
 * @param <T> Element type
 */
public class ArrayCircularQueue<T> implements CustomQueue<T> {

    private Object[] elements;
    private int front;
    private int rear;
    private int size;

    public ArrayCircularQueue() {
        this(10);
    }

    public ArrayCircularQueue(int initialCapacity) {
        int cap = Math.max(initialCapacity, 4);
        this.elements = new Object[cap];
        this.front = 0;
        this.rear = 0;
        this.size = 0;
    }

    @Override
    public void enqueue(T item) {
        if (size == elements.length) {
            resize(elements.length * 2);
        }
        elements[rear] = item;
        rear = (rear + 1) % elements.length;
        size++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        T item = (T) elements[front];
        elements[front] = null;
        front = (front + 1) % elements.length;
        size--;
        return item;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return (T) elements[front];
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
        for (int i = 0; i < elements.length; i++) {
            elements[i] = null;
        }
        front = 0;
        rear = 0;
        size = 0;
    }

    private void resize(int newCapacity) {
        Object[] newElements = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newElements[i] = elements[(front + i) % elements.length];
        }
        this.elements = newElements;
        this.front = 0;
        this.rear = size;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T item = (T) elements[(front + index) % elements.length];
                index++;
                return item;
            }
        };
    }
}
