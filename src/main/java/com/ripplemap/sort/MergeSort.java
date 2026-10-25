package com.ripplemap.sort;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.ds.CustomSinglyLinkedList;
import java.util.Comparator;

/**
 * Merge Sort implementation providing both Array and Linked List variants.
 *
 * Array Merge Sort Complexities:
 * - Best Time: O(n log n)
 * - Average Time: O(n log n)
 * - Worst Time: O(n log n)
 * - Space: O(n) auxiliary array
 * - Stable: Yes
 * - In-Place: No
 *
 * Linked List Merge Sort Complexities:
 * - Time: O(n log n)
 * - Auxiliary Space: O(1) (in-place pointer rewiring without node allocations)
 * - Call Stack Space: O(log n)
 * - Stable: Yes
 *
 * @param <T> Element type
 */
public class MergeSort<T> implements SortStrategy<T> {

    @Override
    public void sort(CustomArrayList<T> list, Comparator<T> comparator, SortMetrics metrics) {
        if (metrics != null) {
            metrics.reset();
            populateMetrics(metrics);
        }
        long startTime = System.nanoTime();

        if (list.size() > 1) {
            CustomArrayList<T> temp = new CustomArrayList<>(list.size());
            for (int i = 0; i < list.size(); i++) {
                temp.add(null);
            }
            mergeSortArray(list, temp, 0, list.size() - 1, comparator, metrics);
        }

        if (metrics != null) {
            metrics.setExecutionTimeMs((System.nanoTime() - startTime) / 1e6);
        }
    }

    private void mergeSortArray(CustomArrayList<T> list, CustomArrayList<T> temp, int left, int right,
                                Comparator<T> comparator, SortMetrics metrics) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;

        mergeSortArray(list, temp, left, mid, comparator, metrics);
        mergeSortArray(list, temp, mid + 1, right, comparator, metrics);
        mergeArray(list, temp, left, mid, right, comparator, metrics);
    }

    private void mergeArray(CustomArrayList<T> list, CustomArrayList<T> temp, int left, int mid, int right,
                            Comparator<T> comparator, SortMetrics metrics) {
        for (int i = left; i <= right; i++) {
            temp.set(i, list.get(i));
        }

        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (metrics != null) metrics.incrementComparisons();
            // Stable comparison: <= maintains relative order of equal items
            if (comparator.compare(temp.get(i), temp.get(j)) <= 0) {
                list.set(k++, temp.get(i++));
            } else {
                list.set(k++, temp.get(j++));
            }
            if (metrics != null) metrics.incrementSwaps();
        }

        while (i <= mid) {
            list.set(k++, temp.get(i++));
            if (metrics != null) metrics.incrementSwaps();
        }
        while (j <= right) {
            list.set(k++, temp.get(j++));
            if (metrics != null) metrics.incrementSwaps();
        }
    }

    /**
     * Sorts a CustomSinglyLinkedList in-place in O(n log n) time and O(1) auxiliary space.
     */
    public void sortLinkedList(CustomSinglyLinkedList<T> list, Comparator<T> comparator) {
        if (list.size() <= 1) return;
        CustomSinglyLinkedList.Node<T> newHead = mergeSortLinkedListNode(list.getHead(), comparator);
        
        // Re-establish head and tail
        list.clear();
        CustomSinglyLinkedList.Node<T> curr = newHead;
        while (curr != null) {
            list.addLast(curr.data);
            curr = curr.next;
        }
    }

    private CustomSinglyLinkedList.Node<T> mergeSortLinkedListNode(CustomSinglyLinkedList.Node<T> head, Comparator<T> comparator) {
        if (head == null || head.next == null) {
            return head;
        }

        // Split using Tortoise and Hare
        CustomSinglyLinkedList.Node<T> slow = head;
        CustomSinglyLinkedList.Node<T> fast = head.next;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        CustomSinglyLinkedList.Node<T> rightHead = slow.next;
        slow.next = null; // severed

        CustomSinglyLinkedList.Node<T> leftSorted = mergeSortLinkedListNode(head, comparator);
        CustomSinglyLinkedList.Node<T> rightSorted = mergeSortLinkedListNode(rightHead, comparator);

        return mergeLinkedLists(leftSorted, rightSorted, comparator);
    }

    private CustomSinglyLinkedList.Node<T> mergeLinkedLists(CustomSinglyLinkedList.Node<T> left,
                                                            CustomSinglyLinkedList.Node<T> right,
                                                            Comparator<T> comparator) {
        CustomSinglyLinkedList.Node<T> dummy = new CustomSinglyLinkedList.Node<>(null);
        CustomSinglyLinkedList.Node<T> tail = dummy;

        while (left != null && right != null) {
            if (comparator.compare(left.data, right.data) <= 0) {
                tail.next = left;
                left = left.next;
            } else {
                tail.next = right;
                right = right.next;
            }
            tail = tail.next;
        }

        tail.next = (left != null) ? left : right;
        return dummy.next;
    }

    private void populateMetrics(SortMetrics metrics) {
        metrics.setAlgorithmName(getName());
        metrics.setTimeComplexity(getTimeComplexity());
        metrics.setSpaceComplexity(getSpaceComplexity());
        metrics.setStable(isStable());
        metrics.setInPlace(isInPlace());
    }

    @Override public String getName() { return "Merge Sort"; }
    @Override public String getTimeComplexity() { return "Best O(n log n), Avg O(n log n), Worst O(n log n)"; }
    @Override public String getSpaceComplexity() { return "O(n) Array / O(1) Linked List"; }
    @Override public boolean isStable() { return true; }
    @Override public boolean isInPlace() { return false; }
}
