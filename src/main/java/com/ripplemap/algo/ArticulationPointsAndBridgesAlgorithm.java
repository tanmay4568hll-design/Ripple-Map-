package com.ripplemap.algo;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.ds.CustomHashMap;
import com.ripplemap.ds.CustomHashSet;
import com.ripplemap.graph.Graph;
import com.ripplemap.graph.GraphEdge;

/**
 * Articulation Points and Bridges Algorithm (Tarjan's DFS).
 * Discovers Single Points of Failure (SPOFs) in network connectivity.
 *
 * Complexities:
 * - Time: O(V + E)
 * - Space: O(V)
 */
public class ArticulationPointsAndBridgesAlgorithm {

    public static class BridgeEdge {
        public final int u;
        public final int v;

        public BridgeEdge(int u, int v) {
            this.u = u;
            this.v = v;
        }

        @Override
        public String toString() {
            return u + " -- " + v;
        }
    }

    public static class SpofResult {
        public final CustomHashSet<Integer> articulationPoints = new CustomHashSet<>();
        public final CustomArrayList<BridgeEdge> bridges = new CustomArrayList<>();
        public final AlgorithmExecutionReport report = new AlgorithmExecutionReport(
            "Articulation Points and Bridges (SPOF Analysis)",
            "O(V + E)",
            "O(V)"
        );
    }

    private int timer;

    public SpofResult execute(Graph graph) {
        SpofResult result = new SpofResult();
        long startTime = System.nanoTime();

        if (graph == null || graph.nodeCount() == 0) {
            return result;
        }

        timer = 0;
        CustomHashMap<Integer, Integer> tin = new CustomHashMap<>();
        CustomHashMap<Integer, Integer> low = new CustomHashMap<>();
        CustomHashSet<Integer> visited = new CustomHashSet<>();

        // Build undirected adjacency view for structural connectivity
        CustomHashMap<Integer, CustomHashSet<Integer>> undirectedAdj = new CustomHashMap<>();
        for (int id : graph.getNodeIds()) {
            undirectedAdj.put(id, new CustomHashSet<>());
            tin.put(id, -1);
            low.put(id, -1);
        }

        for (GraphEdge e : graph.getAllEdges()) {
            undirectedAdj.get(e.getSourceId()).add(e.getTargetId());
            undirectedAdj.get(e.getTargetId()).add(e.getSourceId());
        }

        for (int nodeId : graph.getNodeIds()) {
            if (!visited.contains(nodeId)) {
                dfs(nodeId, -1, undirectedAdj, visited, tin, low, result);
            }
        }

        double durationMs = (System.nanoTime() - startTime) / 1e6;
        result.report.setDurationMs(durationMs);
        result.report.setNodesVisited(graph.nodeCount());
        result.report.setSummaryText("SPOF analysis found " + result.articulationPoints.size() +
                                    " critical articulation nodes and " + result.bridges.size() + " critical bridge links.");

        return result;
    }

    private void dfs(int u, int p,
                     CustomHashMap<Integer, CustomHashSet<Integer>> adj,
                     CustomHashSet<Integer> visited,
                     CustomHashMap<Integer, Integer> tin,
                     CustomHashMap<Integer, Integer> low,
                     SpofResult result) {
        visited.add(u);
        tin.put(u, timer);
        low.put(u, timer);
        timer++;

        int children = 0;
        for (int v : adj.get(u)) {
            if (v == p) continue;

            if (visited.contains(v)) {
                // Back-edge
                low.put(u, Math.min(low.get(u), tin.get(v)));
            } else {
                // Forward tree-edge
                dfs(v, u, adj, visited, tin, low, result);
                low.put(u, Math.min(low.get(u), low.get(v)));

                // Bridge condition: low[v] > tin[u]
                if (low.get(v) > tin.get(u)) {
                    result.bridges.add(new BridgeEdge(u, v));
                }

                // Articulation point condition: low[v] >= tin[u] and not root
                if (low.get(v) >= tin.get(u) && p != -1) {
                    result.articulationPoints.add(u);
                }
                children++;
            }
        }

        // Root is articulation point if it has > 1 child
        if (p == -1 && children > 1) {
            result.articulationPoints.add(u);
        }
    }
}
