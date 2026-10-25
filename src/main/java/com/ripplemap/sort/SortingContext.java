package com.ripplemap.sort;

import com.ripplemap.ds.CustomArrayList;
import java.util.Comparator;

/**
 * Context manager providing unified access to all sorting strategies for the UI and DSA Lab.
 */
public class SortingContext<T> {

    private SortStrategy<T> strategy;

    public SortingContext(SortStrategy<T> strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(SortStrategy<T> strategy) {
        this.strategy = strategy;
    }

    public SortStrategy<T> getStrategy() {
        return strategy;
    }

    public SortMetrics sort(CustomArrayList<T> list, Comparator<T> comparator) {
        SortMetrics metrics = new SortMetrics();
        if (strategy != null) {
            strategy.sort(list, comparator, metrics);
        }
        return metrics;
    }
}
