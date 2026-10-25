package com.ripplemap.ds;

/**
 * Binary Search Tree (BST) implementation.
 *
 * Complexities:
 * - Search / Insert / Delete: Average O(log n), Worst O(n) when skewed
 * - Traversals: Time O(n), Space O(h) recursion stack
 *
 * @param <K> Comparable key type
 * @param <V> Value type
 */
public class CustomBST<K extends Comparable<K>, V> {

    public static class Node<K, V> {
        public K key;
        public V value;
        public Node<K, V> left;
        public Node<K, V> right;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    protected Node<K, V> root;
    protected int size;

    public CustomBST() {
        this.root = null;
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Node<K, V> getRoot() {
        return root;
    }

    public void put(K key, V value) {
        root = insertRec(root, key, value);
    }

    private Node<K, V> insertRec(Node<K, V> current, K key, V value) {
        if (current == null) {
            size++;
            return new Node<>(key, value);
        }
        int cmp = key.compareTo(current.key);
        if (cmp < 0) {
            current.left = insertRec(current.left, key, value);
        } else if (cmp > 0) {
            current.right = insertRec(current.right, key, value);
        } else {
            current.value = value; // update
        }
        return current;
    }

    public V get(K key) {
        Node<K, V> curr = root;
        while (curr != null) {
            int cmp = key.compareTo(curr.key);
            if (cmp < 0) curr = curr.left;
            else if (cmp > 0) curr = curr.right;
            else return curr.value;
        }
        return null;
    }

    public boolean contains(K key) {
        return get(key) != null;
    }

    public CustomArrayList<K> inOrderTraversal() {
        CustomArrayList<K> result = new CustomArrayList<>(size);
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(Node<K, V> node, CustomArrayList<K> result) {
        if (node != null) {
            inOrderRec(node.left, result);
            result.add(node.key);
            inOrderRec(node.right, result);
        }
    }

    public CustomArrayList<K> preOrderTraversal() {
        CustomArrayList<K> result = new CustomArrayList<>(size);
        preOrderRec(root, result);
        return result;
    }

    private void preOrderRec(Node<K, V> node, CustomArrayList<K> result) {
        if (node != null) {
            result.add(node.key);
            preOrderRec(node.left, result);
            preOrderRec(node.right, result);
        }
    }

    public CustomArrayList<K> postOrderTraversal() {
        CustomArrayList<K> result = new CustomArrayList<>(size);
        postOrderRec(root, result);
        return result;
    }

    private void postOrderRec(Node<K, V> node, CustomArrayList<K> result) {
        if (node != null) {
            postOrderRec(node.left, result);
            postOrderRec(node.right, result);
            result.add(node.key);
        }
    }

    public CustomArrayList<K> levelOrderTraversal() {
        CustomArrayList<K> result = new CustomArrayList<>(size);
        if (root == null) return result;

        CustomQueue<Node<K, V>> queue = new LinkedListQueue<>();
        queue.enqueue(root);

        while (!queue.isEmpty()) {
            Node<K, V> curr = queue.dequeue();
            result.add(curr.key);
            if (curr.left != null) queue.enqueue(curr.left);
            if (curr.right != null) queue.enqueue(curr.right);
        }
        return result;
    }

    /**
     * Finds the largest key less than or equal to the given key, or null if no such key exists.
     */
    public K floorKey(K key) {
        return floorHelper(root, key);
    }

    private K floorHelper(Node<K, V> node, K key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp == 0) return node.key;
        if (cmp < 0) return floorHelper(node.left, key);

        K rightFloor = floorHelper(node.right, key);
        return (rightFloor != null) ? rightFloor : node.key;
    }

    /**
     * Finds the smallest key greater than or equal to the given key, or null if no such key exists.
     */
    public K ceilingKey(K key) {
        return ceilingHelper(root, key);
    }

    private K ceilingHelper(Node<K, V> node, K key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp == 0) return node.key;
        if (cmp > 0) return ceilingHelper(node.right, key);

        K leftCeiling = ceilingHelper(node.left, key);
        return (leftCeiling != null) ? leftCeiling : node.key;
    }

    public void clear() {
        root = null;
        size = 0;
    }
}
