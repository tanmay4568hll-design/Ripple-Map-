package com.ripplemap.ds;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Array-backed implementation of CustomStack.
 *
 * Complexities:
 * - Push: Time Amortized O(1), Worst O(n) upon array doubling; Space O(1)
 * - Pop: Time O(1), Space O(1)
 * - Peek: Time O(1), Space O(1)
 * - Size / IsEmpty: Time O(1), Space O(1)
 *
 * @param <T> Element type
 */
public class ArrayStack<T> implements CustomStack<T> {

    private Object[] data;
    private int top;

    public ArrayStack() {
        this(10);
    }

    public ArrayStack(int initialCapacity) {
        this.data = new Object[Math.max(initialCapacity, 1)];
        this.top = 0;
    }

    @Override
    public void push(T item) {
        ensureCapacity();
        data[top++] = item;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        T item = (T) data[--top];
        data[top] = null;
        return item;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return (T) data[top - 1];
    }

    @Override
    public boolean isEmpty() {
        return top == 0;
    }

    @Override
    public int size() {
        return top;
    }

    @Override
    public void clear() {
        for (int i = 0; i < top; i++) {
            data[i] = null;
        }
        top = 0;
    }

    private void ensureCapacity() {
        if (top == data.length) {
            Object[] newData = new Object[data.length * 2];
            System.arraycopy(data, 0, newData, 0, top);
            data = newData;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int cursor = top - 1;

            @Override
            public boolean hasNext() {
                return cursor >= 0;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (T) data[cursor--];
            }
        };
    }
}
