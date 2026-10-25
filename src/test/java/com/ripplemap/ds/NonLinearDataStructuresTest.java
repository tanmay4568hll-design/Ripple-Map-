package com.ripplemap.ds;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Phase 4: Non-Linear Data Structures Unit Tests")
public class NonLinearDataStructuresTest {

    @Test
    @DisplayName("CustomHeap & PriorityQueue: Min-Heap property and Decrease-Key")
    void testHeapAndPriorityQueue() {
        class Task {
            final String name;
            int priority;
            Task(String name, int priority) { this.name = name; this.priority = priority; }
        }

        Comparator<Task> comp = Comparator.comparingInt(t -> t.priority);
        CustomPriorityQueue<Task> pq = new CustomPriorityQueue<>(comp);

        Task t1 = new Task("T1", 50);
        Task t2 = new Task("T2", 20);
        Task t3 = new Task("T3", 80);
        Task t4 = new Task("T4", 10);

        pq.offer(t1);
        pq.offer(t2);
        pq.offer(t3);
        pq.offer(t4);

        assertEquals(4, pq.size());
        assertEquals("T4", pq.peek().name); // min is 10

        // Decrease key on t3 from 80 to 5 -> should become new min!
        t3.priority = 5;
        pq.updatePriority(t3);

        assertEquals("T3", pq.poll().name);
        assertEquals("T4", pq.poll().name);
        assertEquals("T2", pq.poll().name);
        assertEquals("T1", pq.poll().name);
        assertTrue(pq.isEmpty());
    }

    @Test
    @DisplayName("CustomBST: Traversals and Floor/Ceiling queries")
    void testBST() {
        CustomBST<Integer, String> bst = new CustomBST<>();
        bst.put(50, "Fifty");
        bst.put(30, "Thirty");
        bst.put(70, "Seventy");
        bst.put(20, "Twenty");
        bst.put(40, "Forty");
        bst.put(60, "Sixty");
        bst.put(80, "Eighty");

        assertEquals(7, bst.size());
        assertEquals("Fifty", bst.get(50));
        assertEquals("Forty", bst.get(40));

        // In-Order must be sorted: [20, 30, 40, 50, 60, 70, 80]
        CustomArrayList<Integer> inOrder = bst.inOrderTraversal();
        assertEquals(7, inOrder.size());
        for (int i = 0; i < inOrder.size() - 1; i++) {
            assertTrue(inOrder.get(i) < inOrder.get(i + 1));
        }

        // Level-Order should be breadth first: [50, 30, 70, 20, 40, 60, 80]
        CustomArrayList<Integer> levelOrder = bst.levelOrderTraversal();
        assertEquals(50, levelOrder.get(0));
        assertEquals(30, levelOrder.get(1));
        assertEquals(70, levelOrder.get(2));

        // Floor and ceiling
        assertEquals(40, bst.floorKey(45));
        assertEquals(60, bst.ceilingKey(55));
        assertEquals(80, bst.ceilingKey(80));
    }

    @Test
    @DisplayName("CustomAVLTree: Self-Balancing Rotations (LL, RR, LR, RL) and Balance Invariant")
    void testAVLTreeRotations() {
        CustomAVLTree<Integer, String> avl = new CustomAVLTree<>();

        // Inserting elements in sorted order into standard BST would create O(n) line degenerate tree.
        // AVL must perform rotations and keep height logarithmic: height <= 1.44 * log2(n)
        for (int i = 1; i <= 31; i++) {
            avl.put(i, "Val" + i);
        }

        assertEquals(31, avl.size());
        assertTrue(avl.getRotationCount() > 0, "AVL tree should perform balancing rotations");

        // Verify balance factor at root and overall height
        int rootHeight = avl.getRoot().height;
        assertTrue(rootHeight <= 6, "AVL tree height for 31 elements should be <= 6, was " + rootHeight);
        int bf = avl.getRoot().getBalanceFactor();
        assertTrue(bf >= -1 && bf <= 1, "Root balance factor must be in [-1, 1], was " + bf);

        // Verify In-Order traversal is strictly ascending
        CustomArrayList<Integer> inOrder = avl.inOrderTraversal();
        assertEquals(31, inOrder.size());
        for (int i = 0; i < 30; i++) {
            assertEquals(i + 1, inOrder.get(i));
        }
    }

    @Test
    @DisplayName("CustomTrie: Autocomplete and Prefix Search")
    void testTrieAutocomplete() {
        CustomTrie trie = new CustomTrie();
        trie.insert("TSMC Wafer Fab", 104);
        trie.insert("Telecom Fiber Central", 315);
        trie.insert("Twilio SMS Gateway", 219);
        trie.insert("Traffic Control Center", 309);
        trie.insert("Port of Shanghai", 110);

        assertEquals(5, trie.getWordCount());
        assertTrue(trie.search("TSMC Wafer Fab"));
        assertFalse(trie.search("TSMC"));

        // Prefix autocomplete for "T" -> should match 4 nodes
        CustomArrayList<CustomTrie.AutocompleteResult> resultsT = trie.autocomplete("T", 10);
        assertEquals(4, resultsT.size());

        // Prefix autocomplete for "tra" -> matches "Traffic Control Center"
        CustomArrayList<CustomTrie.AutocompleteResult> resultsTra = trie.autocomplete("tra", 5);
        assertEquals(1, resultsTra.size());
        assertEquals(309, resultsTra.get(0).nodeId);
    }

    @Test
    @DisplayName("Disjoint Set Union (DSU): Path Compression and Union by Rank")
    void testDSU() {
        DisjointSetUnion dsu = new DisjointSetUnion(6);
        assertEquals(6, dsu.getComponentCount());

        assertTrue(dsu.union(0, 1));
        assertTrue(dsu.union(1, 2));
        assertFalse(dsu.union(0, 2), "0 and 2 are already connected, should return false");

        assertTrue(dsu.connected(0, 2));
        assertFalse(dsu.connected(0, 3));
        assertEquals(4, dsu.getComponentCount());

        dsu.union(3, 4);
        dsu.union(4, 5);
        assertEquals(2, dsu.getComponentCount());

        dsu.union(2, 5);
        assertEquals(1, dsu.getComponentCount());
        assertTrue(dsu.connected(0, 5));
    }

    @Test
    @DisplayName("CustomLRUCache: O(1) eviction and hit ratio")
    void testLRUCache() {
        CustomLRUCache<String, String> cache = new CustomLRUCache<>(3);
        assertEquals(0, cache.size());

        cache.put("K1", "V1");
        cache.put("K2", "V2");
        cache.put("K3", "V3");
        assertEquals(3, cache.size());

        // Access K1 -> makes K2 the LRU
        assertEquals("V1", cache.get("K1"));

        // Insert K4 -> should evict K2!
        cache.put("K4", "V4");
        assertEquals(3, cache.size());

        assertNull(cache.get("K2"), "K2 should have been evicted as LRU");
        assertEquals("V1", cache.get("K1"));
        assertEquals("V3", cache.get("K3"));
        assertEquals("V4", cache.get("K4"));

        assertTrue(cache.getHitRatio() > 0.6);
    }

    @Test
    @DisplayName("SegmentTree: Range Sum and Range Max queries on timeline")
    void testSegmentTree() {
        // Hourly impact data over 8 hours
        double[] timeline = { 0.2, 0.8, 1.5, 3.2, 4.0, 2.5, 1.1, 0.4 };
        SegmentTree segTree = new SegmentTree(timeline);

        // Sum between hour 2 and 4: 1.5 + 3.2 + 4.0 = 8.7
        assertEquals(8.7, segTree.querySum(2, 4), 1e-6);

        // Max peak between hour 1 and 3: max(0.8, 1.5, 3.2) = 3.2
        assertEquals(3.2, segTree.queryMax(1, 3), 1e-6);

        // Total impact across entire timeline
        assertEquals(13.7, segTree.querySum(0, 7), 1e-6);

        // Point update at hour 3 from 3.2 to 5.0
        segTree.update(3, 5.0);
        assertEquals(10.5, segTree.querySum(2, 4), 1e-6);
        assertEquals(5.0, segTree.queryMax(1, 3), 1e-6);
    }
}
