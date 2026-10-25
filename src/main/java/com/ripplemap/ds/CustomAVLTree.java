package com.ripplemap.ds;

/**
 * Self-Balancing AVL Tree (Adelson-Velsky and Landis) implementation.
 * Guarantees O(log n) worst-case time for insertions, lookups, and deletions.
 *
 * Implements all 4 rotation cases:
 * - Left Rotation (LL)
 * - Right Rotation (RR)
 * - Left-Right Rotation (LR)
 * - Right-Left Rotation (RL)
 *
 * Complexities:
 * - Search: Time O(log n) worst case
 * - Insert: Time O(log n) worst case
 * - Delete: Time O(log n) worst case
 * - Space: O(n)
 *
 * @param <K> Comparable key
 * @param <V> Value type
 */
public class CustomAVLTree<K extends Comparable<K>, V> {

    public static class AVLNode<K, V> {
        public K key;
        public V value;
        public int height;
        public AVLNode<K, V> left;
        public AVLNode<K, V> right;

        public AVLNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }

        public int getBalanceFactor() {
            int leftH = (left != null) ? left.height : 0;
            int rightH = (right != null) ? right.height : 0;
            return leftH - rightH;
        }
    }

    private AVLNode<K, V> root;
    private int size;
    private int rotationCount; // tracked for DSA Lab metrics

    public CustomAVLTree() {
        this.root = null;
        this.size = 0;
        this.rotationCount = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public AVLNode<K, V> getRoot() {
        return root;
    }

    public int getRotationCount() {
        return rotationCount;
    }

    public void put(K key, V value) {
        root = insertRec(root, key, value);
    }

    private AVLNode<K, V> insertRec(AVLNode<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new AVLNode<>(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insertRec(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, key, value);
        } else {
            node.value = value;
            return node;
        }

        // Update height
        node.height = 1 + Math.max(height(node.left), height(node.right));

        // Balance check
        int balance = node.getBalanceFactor();

        // 1. Left-Left (Right rotation)
        if (balance > 1 && key.compareTo(node.left.key) < 0) {
            rotationCount++;
            return rotateRight(node);
        }

        // 2. Right-Right (Left rotation)
        if (balance < -1 && key.compareTo(node.right.key) > 0) {
            rotationCount++;
            return rotateLeft(node);
        }

        // 3. Left-Right (Left rotation on child, then Right rotation on root)
        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            rotationCount += 2;
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // 4. Right-Left (Right rotation on child, then Left rotation on root)
        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            rotationCount += 2;
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private AVLNode<K, V> rotateRight(AVLNode<K, V> y) {
        AVLNode<K, V> x = y.left;
        AVLNode<K, V> T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = 1 + Math.max(height(y.left), height(y.right));
        x.height = 1 + Math.max(height(x.left), height(x.right));

        return x;
    }

    private AVLNode<K, V> rotateLeft(AVLNode<K, V> x) {
        AVLNode<K, V> y = x.right;
        AVLNode<K, V> T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        x.height = 1 + Math.max(height(x.left), height(x.right));
        y.height = 1 + Math.max(height(y.left), height(y.right));

        return y;
    }

    private int height(AVLNode<K, V> node) {
        return (node == null) ? 0 : node.height;
    }

    public V get(K key) {
        AVLNode<K, V> curr = root;
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

    public K floorKey(K key) {
        return floorHelper(root, key);
    }

    private K floorHelper(AVLNode<K, V> node, K key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp == 0) return node.key;
        if (cmp < 0) return floorHelper(node.left, key);

        K rightFloor = floorHelper(node.right, key);
        return (rightFloor != null) ? rightFloor : node.key;
    }

    public K ceilingKey(K key) {
        return ceilingHelper(root, key);
    }

    private K ceilingHelper(AVLNode<K, V> node, K key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp == 0) return node.key;
        if (cmp > 0) return ceilingHelper(node.right, key);

        K leftCeiling = ceilingHelper(node.left, key);
        return (leftCeiling != null) ? leftCeiling : node.key;
    }

    public CustomArrayList<K> inOrderTraversal() {
        CustomArrayList<K> result = new CustomArrayList<>(size);
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(AVLNode<K, V> node, CustomArrayList<K> result) {
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

    private void preOrderRec(AVLNode<K, V> node, CustomArrayList<K> result) {
        if (node != null) {
            result.add(node.key);
            preOrderRec(node.left, result);
            preOrderRec(node.right, result);
        }
    }

    public CustomArrayList<K> levelOrderTraversal() {
        CustomArrayList<K> result = new CustomArrayList<>(size);
        if (root == null) return result;

        CustomQueue<AVLNode<K, V>> queue = new LinkedListQueue<>();
        queue.enqueue(root);

        while (!queue.isEmpty()) {
            AVLNode<K, V> curr = queue.dequeue();
            result.add(curr.key);
            if (curr.left != null) queue.enqueue(curr.left);
            if (curr.right != null) queue.enqueue(curr.right);
        }
        return result;
    }

    public void clear() {
        root = null;
        size = 0;
        rotationCount = 0;
    }
}
