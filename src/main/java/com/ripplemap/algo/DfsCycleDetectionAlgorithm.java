package com.ripplemap.algo;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.ds.CustomHashMap;
import com.ripplemap.ds.LinkedListStack;
import com.ripplemap.graph.Graph;
import com.ripplemap.graph.GraphEdge;

/**
 * Depth-First Search (DFS) Cycle Detection.
 * Identifies reinforcing / amplifying feedback loops where impact loops back on itself.
 *
 * Uses 3-color state machine:
 * 0 = UNVISITED (White)
 * 1 = VISITING / In Recursion Stack (Gray)
 * 2 = VISITED (Black)
 *
 * Complexities:
 * - Time: O(V + E)
 * - Space: O(V) for color map and DFS recursion stack
 */
public class DfsCycleDetectionAlgorithm {

    public static class CycleInfo {
        public final CustomArrayList<Integer> cycleNodes = new CustomArrayList<>();

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Cycle: ");
            for (int i = 0; i < cycleNodes.size(); i++) {
                sb.append(cycleNodes.get(i));
                if (i < cycleNodes.size() - 1) sb.append(" -> ");
            }
            return sb.toString();
        }
    }

    public static class DfsCycleResult {
        public boolean hasCycle = false;
        public final CustomArrayList<CycleInfo> detectedCycles = new CustomArrayList<>();
        public final AlgorithmExecutionReport report = new AlgorithmExecutionReport(
            "DFS Cycle Detection (Feedback Loops)",
            "O(V + E)",
            "O(V)"
        );
    }

    public DfsCycleResult execute(Graph graph) {
        DfsCycleResult result = new DfsCycleResult();
        long startTime = System.nanoTime();

        if (graph == null || graph.nodeCount() == 0) {
            return result;
        }

        CustomHashMap<Integer, Integer> color = new CustomHashMap<>(); // 0: White, 1: Gray, 2: Black
        CustomHashMap<Integer, Integer> parent = new CustomHashMap<>();
        LinkedListStack<Integer> pathStack = new LinkedListStack<>();

        for (int nodeId : graph.getNodeIds()) {
            color.put(nodeId, 0);
        }

        int visitedNodes = 0;
        int traversedEdges = 0;

        for (int nodeId : graph.getNodeIds()) {
            if (color.get(nodeId) == 0) {
                dfs(graph, nodeId, color, parent, pathStack, result);
            }
        }

        double durationMs = (System.nanoTime() - startTime) / 1e6;
        result.report.setDurationMs(durationMs);
        result.report.setNodesVisited(graph.nodeCount());
        result.report.setSummaryText(result.hasCycle
            ? "Detected " + result.detectedCycles.size() + " amplifying feedback loop cycles."
            : "Graph is a Directed Acyclic Graph (DAG) with 0 feedback loops.");

        return result;
    }

    private void dfs(Graph graph, int u, CustomHashMap<Integer, Integer> color,
                     CustomHashMap<Integer, Integer> parent, LinkedListStack<Integer> pathStack,
                     DfsCycleResult result) {
        color.put(u, 1); // Gray
        pathStack.push(u);

        for (GraphEdge edge : graph.getOutgoingEdges(u)) {
            int v = edge.getTargetId();
            Integer vColor = color.get(v);
            if (vColor == null) continue;

            if (vColor == 1) {
                // Back-edge detected -> cycle!
                result.hasCycle = true;
                CycleInfo cycle = new CycleInfo();
                cycle.cycleNodes.add(v);

                // Extract cycle path from stack
                CustomArrayList<Integer> reversed = new CustomArrayList<>();
                for (int node : pathStack) {
                    reversed.add(node);
                    if (node == v) break;
                }
                for (int i = reversed.size() - 1; i >= 0; i--) {
                    cycle.cycleNodes.add(reversed.get(i));
                }
                result.detectedCycles.add(cycle);
            } else if (vColor == 0) {
                parent.put(v, u);
                dfs(graph, v, color, parent, pathStack, result);
            }
        }

        pathStack.pop();
        color.put(u, 2); // Black
    }
}
