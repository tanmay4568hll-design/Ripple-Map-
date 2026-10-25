package com.ripplemap.sort;

import com.ripplemap.ds.CustomArrayList;
import java.util.Comparator;

/**
 * Strategy interface for manual sorting algorithms.
 *
 * @param <T> Element type
 */
public interface SortStrategy<T> {
    /**
     * Sorts the provided CustomArrayList using the specified comparator.
     * Records statistics into the metrics object.
     */
    void sort(CustomArrayList<T> list, Comparator<T> comparator, SortMetrics metrics);

    String getName();
    String getTimeComplexity();
    String getSpaceComplexity();
    boolean isStable();
    boolean isInPlace();
}
