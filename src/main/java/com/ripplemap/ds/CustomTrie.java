package com.ripplemap.ds;

/**
 * Trie (Prefix Tree) implementation for real-time node name autocomplete search.
 *
 * Complexities:
 * - Insert word: Time O(m) where m is string length, Space O(m)
 * - Exact Search: Time O(m), Space O(1)
 * - Autocomplete prefix: Time O(p + k) where p is prefix length and k is number of matching descendants
 *
 */
public class CustomTrie {

    public static class AutocompleteResult {
        public final String word;
        public final int nodeId;

        public AutocompleteResult(String word, int nodeId) {
            this.word = word;
            this.nodeId = nodeId;
        }

        @Override
        public String toString() {
            return word + " (ID: " + nodeId + ")";
        }
    }

    private static class TrieNode {
        // Support full ASCII / extended character set via map or sparse array
        CustomHashMap<Character, TrieNode> children = new CustomHashMap<>();
        boolean isEndOfWord = false;
        int nodeId = -1;
        String fullWord = null;
    }

    private final TrieNode root;
    private int wordCount;

    public CustomTrie() {
        this.root = new TrieNode();
        this.wordCount = 0;
    }

    public int getWordCount() {
        return wordCount;
    }

    /**
     * Inserts a word and its associated node ID into the Trie. Time: O(m).
     */
    public void insert(String word, int nodeId) {
        if (word == null || word.trim().isEmpty()) return;
        String clean = word.trim().toLowerCase();

        TrieNode curr = root;
        for (int i = 0; i < clean.length(); i++) {
            char c = clean.charAt(i);
            TrieNode child = curr.children.get(c);
            if (child == null) {
                child = new TrieNode();
                curr.children.put(c, child);
            }
            curr = child;
        }

        if (!curr.isEndOfWord) {
            wordCount++;
        }
        curr.isEndOfWord = true;
        curr.nodeId = nodeId;
        curr.fullWord = word.trim(); // preserve original casing for display
    }

    /**
     * Checks if exact word exists in Trie. Time: O(m).
     */
    public boolean search(String word) {
        if (word == null) return false;
        TrieNode node = findNode(word.trim().toLowerCase());
        return node != null && node.isEndOfWord;
    }

    /**
     * Finds all words matching the specified prefix up to maxResults.
     */
    public CustomArrayList<AutocompleteResult> autocomplete(String prefix, int maxResults) {
        CustomArrayList<AutocompleteResult> results = new CustomArrayList<>();
        if (prefix == null) return results;

        String clean = prefix.trim().toLowerCase();
        TrieNode prefixNode = findNode(clean);
        if (prefixNode == null) {
            return results;
        }

        collectWords(prefixNode, results, maxResults);
        return results;
    }

    private void collectWords(TrieNode node, CustomArrayList<AutocompleteResult> results, int maxResults) {
        if (node == null || results.size() >= maxResults) return;

        if (node.isEndOfWord) {
            results.add(new AutocompleteResult(node.fullWord, node.nodeId));
            if (results.size() >= maxResults) return;
        }

        for (CustomHashMap.Entry<Character, TrieNode> entry : node.children) {
            collectWords(entry.value, results, maxResults);
            if (results.size() >= maxResults) break;
        }
    }

    private TrieNode findNode(String str) {
        TrieNode curr = root;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            TrieNode child = curr.children.get(c);
            if (child == null) return null;
            curr = child;
        }
        return curr;
    }

    public void clear() {
        root.children.clear();
        wordCount = 0;
    }
}
