package com.ripplemap.ui.layout;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.ds.CustomHashMap;
import com.ripplemap.graph.Graph;
import com.ripplemap.graph.GraphEdge;
import com.ripplemap.model.Node;

/**
 * Fruchterman-Reingold Force-Directed Layout Algorithm.
 * Implemented manually from first principles for aesthetically pleasing node distribution.
 *
 * Physics Model:
 * 1. Repulsive forces between all node pairs: f_r(d) = k^2 / d
 * 2. Attractive spring forces between connected nodes: f_a(d) = d^2 / k
 * 3. Simulated annealing / cooling schedule: temperature decreases each iteration
 *
 * Complexities:
 * - Time: O(iterations * (V^2 + E))
 * - Space: O(V) for displacement vectors
 */
public class FruchtermanReingoldLayout {

    private static final int DEFAULT_ITERATIONS = 75;
    private static final double PADDING = 60.0;

    public static void applyLayout(Graph graph, double width, double height) {
        applyLayout(graph, width, height, DEFAULT_ITERATIONS);
    }

    public static void applyLayout(Graph graph, double width, double height, int iterations) {
        if (graph == null || graph.nodeCount() <= 1) return;

        double usableWidth = Math.max(width - 2 * PADDING, 200.0);
        double usableHeight = Math.max(height - 2 * PADDING, 200.0);
        double area = usableWidth * usableHeight;
        int V = graph.nodeCount();

        // Optimal pairwise distance
        double k = Math.sqrt(area / V) * 0.75;
        double temperature = usableWidth / 10.0;
        double coolingFactor = temperature / (iterations + 1);

        CustomArrayList<Node> nodes = graph.getAllNodes();

        // If nodes have zero/uninitialized coordinates, scatter them randomly across canvas first
        java.util.Random rand = new java.util.Random(1337);
        for (Node n : nodes) {
            if (n.getXPos() <= 0.0 || n.getYPos() <= 0.0) {
                n.setXPos(PADDING + rand.nextDouble() * usableWidth);
                n.setYPos(PADDING + rand.nextDouble() * usableHeight);
            }
        }

        CustomHashMap<Integer, double[]> disp = new CustomHashMap<>();
        for (Node n : nodes) {
            disp.put(n.getId(), new double[]{0.0, 0.0});
        }

        for (int iter = 0; iter < iterations; iter++) {
            // 1. Reset displacements
            for (Node n : nodes) {
                double[] d = disp.get(n.getId());
                d[0] = 0.0;
                d[1] = 0.0;
            }

            // 2. Calculate repulsive forces between all pairs of nodes: f_r(d) = k^2 / d
            for (int i = 0; i < nodes.size(); i++) {
                Node v = nodes.get(i);
                double[] dispV = disp.get(v.getId());

                for (int j = i + 1; j < nodes.size(); j++) {
                    Node u = nodes.get(j);
                    double[] dispU = disp.get(u.getId());

                    double dx = v.getXPos() - u.getXPos();
                    double dy = v.getYPos() - u.getYPos();
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist < 1e-4) {
                        dist = 0.1;
                        dx = 0.1;
                    }

                    double force = (k * k) / dist;
                    double fx = (dx / dist) * force;
                    double fy = (dy / dist) * force;

                    dispV[0] += fx;
                    dispV[1] += fy;
                    dispU[0] -= fx;
                    dispU[1] -= fy;
                }
            }

            // 3. Calculate attractive forces along edges: f_a(d) = d^2 / k
            for (GraphEdge edge : graph.getAllEdges()) {
                Node v = graph.getNode(edge.getSourceId());
                Node u = graph.getNode(edge.getTargetId());
                if (v == null || u == null || v == u) continue;

                double dx = v.getXPos() - u.getXPos();
                double dy = v.getYPos() - u.getYPos();
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < 1e-4) {
                    dist = 0.1;
                    dx = 0.1;
                }

                double force = (dist * dist) / k;
                double fx = (dx / dist) * force;
                double fy = (dy / dist) * force;

                double[] dispV = disp.get(v.getId());
                double[] dispU = disp.get(u.getId());
                dispV[0] -= fx;
                dispV[1] -= fy;
                dispU[0] += fx;
                dispU[1] += fy;
            }

            // 4. Update node positions bounded by temperature and canvas boundaries
            for (Node n : nodes) {
                double[] d = disp.get(n.getId());
                double dLen = Math.sqrt(d[0] * d[0] + d[1] * d[1]);
                if (dLen > 0) {
                    double step = Math.min(dLen, temperature);
                    double newX = n.getXPos() + (d[0] / dLen) * step;
                    double newY = n.getYPos() + (d[1] / dLen) * step;

                    // Clamping inside canvas margins
                    newX = Math.max(PADDING, Math.min(width - PADDING, newX));
                    newY = Math.max(PADDING, Math.min(height - PADDING, newY));

                    n.setXPos(newX);
                    n.setYPos(newY);
                }
            }

            // 5. Cool down temperature
            temperature = Math.max(0.1, temperature - coolingFactor);
        }
    }
}
