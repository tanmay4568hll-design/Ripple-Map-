package com.ripplemap.algo;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.ds.CustomHashMap;
import com.ripplemap.ds.CustomHashSet;
import com.ripplemap.ds.LinkedListQueue;
import com.ripplemap.graph.Graph;
import com.ripplemap.graph.GraphEdge;

/**
 * Breadth-First Search (BFS) Ripple Wave Algorithm.
 * Simulates concentric impact waves propagating outwards level-by-level from a source event node.
 *
 * Complexities:
 * - Time: O(V + E)
 * - Space: O(V) for queue and visited set
 */
public class BfsRippleAlgorithm {

    public static class WaveResult {
        public final int hopLevel;
        public final CustomArrayList<Integer> nodeIds;

        public WaveResult(int hopLevel) {
            this.hopLevel = hopLevel;
            this.nodeIds = new CustomArrayList<>();
        }
    }

    public static class BfsResult {
        public final CustomHashMap<Integer, Integer> hopLevels = new CustomHashMap<>();
        public final CustomArrayList<WaveResult> waves = new CustomArrayList<>();
        public final AlgorithmExecutionReport report = new AlgorithmExecutionReport(
            "BFS Ripple Wave Propagation",
            "O(V + E)",
            "O(V)"
        );
    }

    public BfsResult execute(Graph graph, int sourceNodeId) {
        BfsResult result = new BfsResult();
        long startTime = System.nanoTime();

        if (graph == null || !graph.containsNode(sourceNodeId)) {
            return result;
        }

        CustomHashSet<Integer> visited = new CustomHashSet<>();
        LinkedListQueue<Integer> queue = new LinkedListQueue<>();

        queue.enqueue(sourceNodeId);
        visited.add(sourceNodeId);
        result.hopLevels.put(sourceNodeId, 0);

        int currentLevel = 0;
        int visitedNodes = 0;
        int traversedEdges = 0;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            WaveResult wave = new WaveResult(currentLevel);

            for (int i = 0; i < levelSize; i++) {
                int u = queue.dequeue();
                wave.nodeIds.add(u);
                visitedNodes++;

                for (GraphEdge edge : graph.getOutgoingEdges(u)) {
                    traversedEdges++;
                    int v = edge.getTargetId();
                    if (!visited.contains(v)) {
                        visited.add(v);
                        result.hopLevels.put(v, currentLevel + 1);
                        queue.enqueue(v);
                    }
                }
            }

            result.waves.add(wave);
            currentLevel++;
        }

        double durationMs = (System.nanoTime() - startTime) / 1e6;
        result.report.setDurationMs(durationMs);
        result.report.setNodesVisited(visitedNodes);
        result.report.setEdgesTraversed(traversedEdges);
        result.report.setSummaryText("BFS traversed " + visitedNodes + " nodes across " + result.waves.size() + " wave levels.");

        return result;
    }
}
