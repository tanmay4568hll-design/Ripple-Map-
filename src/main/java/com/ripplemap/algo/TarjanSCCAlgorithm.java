package com.ripplemap.algo;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.ds.CustomHashMap;
import com.ripplemap.ds.LinkedListStack;
import com.ripplemap.graph.Graph;
import com.ripplemap.graph.GraphEdge;

/**
 * Tarjan's Strongly Connected Components (SCC) Algorithm.
 * Identifies tightly coupled subgraphs where every node can reach every other node.
 *
 * Answering the viva question: Why does Tarjan use a stack?
 * The stack tracks all nodes visited in the current DFS subtree that have not yet been
 * assigned to a finalized SCC. When a node 'u' with low[u] == dfn[u] is discovered,
 * it represents the root of an SCC; popping elements from the stack until 'u' is popped
 * isolates precisely the nodes belonging to this component in O(1) per node.
 *
 * Complexities:
 * - Time: O(V + E) single-pass DFS
 * - Space: O(V) for dfn, low-link, inStack, and DFS stack
 */
public class TarjanSCCAlgorithm {

    public static class SCCResult {
        public final CustomArrayList<CustomArrayList<Integer>> components = new CustomArrayList<>();
        public final AlgorithmExecutionReport report = new AlgorithmExecutionReport(
            "Tarjan's Strongly Connected Components",
            "O(V + E)",
            "O(V)"
        );
    }

    private int time;

    public SCCResult execute(Graph graph) {
        SCCResult result = new SCCResult();
        long startTime = System.nanoTime();

        if (graph == null || graph.nodeCount() == 0) {
            return result;
        }

        time = 0;
        CustomHashMap<Integer, Integer> dfn = new CustomHashMap<>();
        CustomHashMap<Integer, Integer> low = new CustomHashMap<>();
        CustomHashMap<Integer, Boolean> inStack = new CustomHashMap<>();
        LinkedListStack<Integer> stack = new LinkedListStack<>();

        for (int nodeId : graph.getNodeIds()) {
            dfn.put(nodeId, -1);
            low.put(nodeId, -1);
            inStack.put(nodeId, false);
        }

        for (int nodeId : graph.getNodeIds()) {
            if (dfn.get(nodeId) == -1) {
                tarjanDFS(graph, nodeId, dfn, low, inStack, stack, result);
            }
        }

        double durationMs = (System.nanoTime() - startTime) / 1e6;
        result.report.setDurationMs(durationMs);
        result.report.setNodesVisited(graph.nodeCount());
        result.report.setSummaryText("Tarjan identified " + result.components.size() + " strongly connected component clusters.");

        return result;
    }

    private void tarjanDFS(Graph graph, int u,
                           CustomHashMap<Integer, Integer> dfn,
                           CustomHashMap<Integer, Integer> low,
                           CustomHashMap<Integer, Boolean> inStack,
                           LinkedListStack<Integer> stack,
                           SCCResult result) {
        dfn.put(u, time);
        low.put(u, time);
        time++;

        stack.push(u);
        inStack.put(u, true);

        for (GraphEdge edge : graph.getOutgoingEdges(u)) {
            int v = edge.getTargetId();
            if (dfn.get(v) == -1) {
                tarjanDFS(graph, v, dfn, low, inStack, stack, result);
                low.put(u, Math.min(low.get(u), low.get(v)));
            } else if (Boolean.TRUE.equals(inStack.get(v))) {
                low.put(u, Math.min(low.get(u), dfn.get(v)));
            }
        }

        // If u is root of an SCC, pop all members from stack
        if (low.get(u).equals(dfn.get(u))) {
            CustomArrayList<Integer> scc = new CustomArrayList<>();
            while (true) {
                int w = stack.pop();
                inStack.put(w, false);
                scc.add(w);
                if (w == u) break;
            }
            result.components.add(scc);
        }
    }
}
