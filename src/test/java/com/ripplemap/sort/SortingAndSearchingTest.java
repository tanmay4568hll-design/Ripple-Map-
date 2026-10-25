package com.ripplemap.sort;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.ds.CustomSinglyLinkedList;
import com.ripplemap.search.BinarySearch;
import com.ripplemap.search.BinarySearchOnAnswer;
import com.ripplemap.search.InterpolationSearch;
import com.ripplemap.search.LinearSearch;
import com.ripplemap.search.SearchMetrics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Phase 3: Sorting and Searching Algorithms Unit Tests")
public class SortingAndSearchingTest {

    private final Comparator<Integer> intComparator = Integer::compareTo;

    @Test
    @DisplayName("Verify all sorting algorithms produce identical sorted output across multiple input patterns")
    void testAllSortsIdenticalOutput() {
        SortStrategy<Integer>[] strategies = new SortStrategy[] {
            new BubbleSort<Integer>(),
            new SelectionSort<Integer>(),
            new InsertionSort<Integer>(),
            new MergeSort<Integer>(),
            new QuickSort<Integer>(),
            new HeapSort<Integer>()
        };

        // Patterns: Empty, Single, Sorted, Reverse, Random with duplicates
        int[][] testDatasets = new int[][] {
            new int[] {},
            new int[] { 42 },
            new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 },
            new int[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 },
            new int[] { 4, 2, 7, 2, 9, 1, 4, 8, 0, 3, 5 }
        };

        for (int[] raw : testDatasets) {
            CustomArrayList<Integer> expected = toCustomList(raw);
            new MergeSort<Integer>().sort(expected, intComparator, null);

            for (SortStrategy<Integer> strategy : strategies) {
                CustomArrayList<Integer> list = toCustomList(raw);
                SortMetrics metrics = new SortMetrics();
                strategy.sort(list, intComparator, metrics);

                assertEquals(expected.size(), list.size(), strategy.getName() + " size mismatch");
                for (int i = 0; i < expected.size(); i++) {
                    assertEquals(expected.get(i), list.get(i),
                        strategy.getName() + " failed on dataset element index " + i);
                }
                assertNotNull(metrics.getAlgorithmName());
            }
        }
    }

    @Test
    @DisplayName("Verify stability of Merge Sort, Insertion Sort, and Counting Sort")
    void testSortStability() {
        // Elements with identical primary key (value) but different original order (id)
        class Pair {
            final int key;
            final int id;
            Pair(int key, int id) { this.key = key; this.id = id; }
        }

        Comparator<Pair> pairComparator = Comparator.comparingInt(p -> p.key);

        Pair[] pairs = new Pair[] {
            new Pair(2, 1),
            new Pair(1, 1),
            new Pair(2, 2),
            new Pair(1, 2),
            new Pair(2, 3)
        };

        // 1. Test MergeSort stability
        CustomArrayList<Pair> mergeList = new CustomArrayList<>();
        for (Pair p : pairs) mergeList.add(p);
        new MergeSort<Pair>().sort(mergeList, pairComparator, null);

        // All key=1 pairs must maintain original id order (1, 2)
        assertEquals(1, mergeList.get(0).id);
        assertEquals(2, mergeList.get(1).id);
        // All key=2 pairs must maintain original id order (1, 2, 3)
        assertEquals(1, mergeList.get(2).id);
        assertEquals(2, mergeList.get(3).id);
        assertEquals(3, mergeList.get(4).id);

        // 2. Test InsertionSort stability
        CustomArrayList<Pair> insertList = new CustomArrayList<>();
        for (Pair p : pairs) insertList.add(p);
        new InsertionSort<Pair>().sort(insertList, pairComparator, null);
        assertEquals(1, insertList.get(0).id);
        assertEquals(2, insertList.get(1).id);

        // 3. Test CountingSort stability
        CustomArrayList<Pair> countList = new CustomArrayList<>();
        for (Pair p : pairs) countList.add(p);
        new CountingSort<Pair>().sort(countList, p -> p.key, null);
        assertEquals(1, countList.get(0).id);
        assertEquals(2, countList.get(1).id);
        assertEquals(1, countList.get(2).id);
        assertEquals(2, countList.get(3).id);
        assertEquals(3, countList.get(4).id);
    }

    @Test
    @DisplayName("Linked List Merge Sort: in-place pointer sorting")
    void testLinkedListMergeSort() {
        CustomSinglyLinkedList<Integer> list = new CustomSinglyLinkedList<>();
        list.addLast(15);
        list.addLast(3);
        list.addLast(9);
        list.addLast(1);
        list.addLast(12);

        new MergeSort<Integer>().sortLinkedList(list, intComparator);

        assertEquals(5, list.size());
        assertEquals(1, list.get(0));
        assertEquals(3, list.get(1));
        assertEquals(9, list.get(2));
        assertEquals(12, list.get(3));
        assertEquals(15, list.get(4));
    }

    @Test
    @DisplayName("QuickSelect: O(n) k-th order statistic and top-K selection")
    void testQuickSelect() {
        QuickSelect<Integer> qs = new QuickSelect<>();
        CustomArrayList<Integer> list = toCustomList(new int[] { 25, 10, 5, 80, 45, 12, 90 });

        // Sorted would be: 5, 10, 12, 25, 45, 80, 90
        int minElem = qs.select(list, 0, intComparator);
        assertEquals(5, minElem);

        int medianElem = qs.select(list, 3, intComparator);
        assertEquals(25, medianElem);

        int maxElem = qs.select(list, 6, intComparator);
        assertEquals(90, maxElem);

        // Top 3 elements
        CustomArrayList<Integer> top3 = qs.getTopK(list, 3, intComparator);
        assertEquals(3, top3.size());
        assertTrue(top3.contains(5));
        assertTrue(top3.contains(10));
        assertTrue(top3.contains(12));
    }

    @Test
    @DisplayName("Linear Search: boundary and not-found cases")
    void testLinearSearch() {
        LinearSearch<String> ls = new LinearSearch<>();
        CustomArrayList<String> names = new CustomArrayList<>();
        names.add("TSMC");
        names.add("Foxconn");
        names.add("Apple");

        SearchMetrics metrics = new SearchMetrics();
        int found = ls.search(names, "Apple"::equals, metrics);
        assertEquals(2, found);
        assertEquals(3, metrics.getProbes());

        int notFound = ls.search(names, "Microsoft"::equals, metrics);
        assertEquals(-1, notFound);
    }

    @Test
    @DisplayName("Binary Search: Iterative, Recursive, and Lower Bound")
    void testBinarySearch() {
        BinarySearch<Integer> bs = new BinarySearch<>();
        CustomArrayList<Integer> sorted = toCustomList(new int[] { 10, 20, 30, 40, 50, 60, 70, 80 });

        SearchMetrics metrics = new SearchMetrics();
        assertEquals(3, bs.searchIterative(sorted, 40, intComparator, metrics));
        assertTrue(metrics.getProbes() <= 4);

        assertEquals(0, bs.searchRecursive(sorted, 10, intComparator, metrics));
        assertEquals(7, bs.searchRecursive(sorted, 80, intComparator, metrics));
        assertEquals(-1, bs.searchIterative(sorted, 999, intComparator, metrics));

        // Lower bound
        assertEquals(0, bs.lowerBound(sorted, 5, intComparator));
        assertEquals(2, bs.lowerBound(sorted, 25, intComparator)); // first >= 25 is 30 at index 2
        assertEquals(8, bs.lowerBound(sorted, 100, intComparator));
    }

    @Test
    @DisplayName("Binary Search on Answer: minimum cutoff threshold")
    void testBinarySearchOnAnswer() {
        // Monotonic affected node count:
        // threshold 0.0 -> 25 affected
        // threshold 0.5 -> 12 affected
        // threshold 0.8 -> 4 affected
        // threshold 1.0 -> 1 affected
        double threshold = BinarySearchOnAnswer.findMinThresholdForMaxAffected(
            t -> (int) (25.0 * (1.0 - t * 0.9)), // monotonically decreasing
            5, 0.0, 1.0, 1e-4
        );

        assertTrue(threshold > 0.7 && threshold < 1.0);
    }

    @Test
    @DisplayName("Interpolation Search: Uniformly spaced arrival times")
    void testInterpolationSearch() {
        InterpolationSearch is = new InterpolationSearch();
        CustomArrayList<Double> times = new CustomArrayList<>();
        for (double t = 0.0; t <= 100.0; t += 5.0) {
            times.add(t);
        }

        SearchMetrics metrics = new SearchMetrics();
        int idx = is.search(times, 45.0, metrics);
        assertEquals(9, idx);
        assertEquals(1, metrics.getProbes(), "Interpolation search on uniform data should find probe in O(1)");

        int notFound = is.search(times, 42.0, metrics);
        assertEquals(-1, notFound);
    }

    private CustomArrayList<Integer> toCustomList(int[] arr) {
        CustomArrayList<Integer> list = new CustomArrayList<>(arr.length);
        for (int v : arr) list.add(v);
        return list;
    }
}
