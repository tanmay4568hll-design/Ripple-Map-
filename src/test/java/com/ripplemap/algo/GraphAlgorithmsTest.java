package com.ripplemap.algo;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.graph.Graph;
import com.ripplemap.model.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Phase 5: Graph Algorithms Unit & Correctness Tests")
public class GraphAlgorithmsTest {

    private Graph sampleGraph;

    @BeforeEach
    void setUp() {
        // Build hand-verified directed graph:
        // 1 -> 2 (w=0.9, d=1.0)
        // 1 -> 3 (w=0.8, d=2.0)
        // 2 -> 4 (w=0.7, d=1.5)
        // 3 -> 4 (w=0.85, d=1.0)
        // 4 -> 5 (w=0.95, d=3.0)
        // Cycle feedback: 4 -> 2 (w=0.5, d=1.0)
        sampleGraph = new Graph();
        for (int i = 1; i <= 5; i++) {
            sampleGraph.addNode(new Node(i, 1, "Node" + i, "Category", 0, 0, 0.2, "Desc"));
        }

        sampleGraph.addEdge(1, 2, 0.9, 1.0, "LINK");
        sampleGraph.addEdge(1, 3, 0.8, 2.0, "LINK");
        sampleGraph.addEdge(2, 4, 0.7, 1.5, "LINK");
        sampleGraph.addEdge(3, 4, 0.85, 1.0, "LINK");
        sampleGraph.addEdge(4, 5, 0.95, 3.0, "LINK");
        sampleGraph.addEdge(4, 2, 0.5, 1.0, "FEEDBACK"); // forms cycle 2 -> 4 -> 2
    }

    @Test
    @DisplayName("BFS Ripple: Wave distance and level assignment")
    void testBfsRipple() {
        BfsRippleAlgorithm bfs = new BfsRippleAlgorithm();
        BfsRippleAlgorithm.BfsResult res = bfs.execute(sampleGraph, 1);

        assertEquals(0, res.hopLevels.get(1));
        assertEquals(1, res.hopLevels.get(2));
        assertEquals(1, res.hopLevels.get(3));
        assertEquals(2, res.hopLevels.get(4));
        assertEquals(3, res.hopLevels.get(5));
        assertEquals(4, res.waves.size());
    }

    @Test
    @DisplayName("Dijkstra: Shortest arrival latency")
    void testDijkstra() {
        DijkstraTimeAlgorithm dijkstra = new DijkstraTimeAlgorithm();
        DijkstraTimeAlgorithm.DijkstraResult res = dijkstra.execute(sampleGraph, 1);

        assertEquals(0.0, res.earliestArrival.get(1), 1e-6);
        assertEquals(1.0, res.earliestArrival.get(2), 1e-6); // 1 -> 2 = 1.0
        assertEquals(2.0, res.earliestArrival.get(3), 1e-6); // 1 -> 3 = 2.0
        // 1 -> 2 -> 4 is 1.0 + 1.5 = 2.5
        // 1 -> 3 -> 4 is 2.0 + 1.0 = 3.0
        // Shortest to 4 is 2.5 via Node 2!
        assertEquals(2.5, res.earliestArrival.get(4), 1e-6);
        assertEquals(5.5, res.earliestArrival.get(5), 1e-6); // 2.5 + 3.0 = 5.5
    }

    @Test
    @DisplayName("Cascade Propagation: Impact decay and threshold cutoff")
    void testCascadePropagation() {
        CascadePropagationAlgorithm propagator = new CascadePropagationAlgorithm();
        CascadePropagationAlgorithm.PropagationOutput out = propagator.execute(
            sampleGraph, 1, 10.0, 0.9, 0.05, CascadePropagationAlgorithm.ModelType.MAX_PATH
        );

        assertNotNull(out.impactScores.get(1));
        assertEquals(1.0, out.impactScores.get(1), 1e-6);
        assertTrue(out.impactScores.get(2) > 0.0);
        assertTrue(out.impactScores.get(4) > 0.0);
        assertTrue(out.impactScores.get(5) > 0.0);
    }

    @Test
    @DisplayName("DFS Cycle Detection: Detects amplifying feedback loops")
    void testCycleDetection() {
        DfsCycleDetectionAlgorithm cycleDetector = new DfsCycleDetectionAlgorithm();
        DfsCycleDetectionAlgorithm.DfsCycleResult res = cycleDetector.execute(sampleGraph);

        assertTrue(res.hasCycle, "Sample graph contains feedback loop 2 -> 4 -> 2");
        assertFalse(res.detectedCycles.isEmpty());
    }

    @Test
    @DisplayName("Tarjan's SCC: Strongly Connected Component grouping")
    void testTarjanSCC() {
        TarjanSCCAlgorithm scc = new TarjanSCCAlgorithm();
        TarjanSCCAlgorithm.SCCResult res = scc.execute(sampleGraph);

        // Nodes 2 and 4 form an SCC together because 2 -> 4 and 4 -> 2
        boolean foundPair2and4 = false;
        for (CustomArrayList<Integer> comp : res.components) {
            if (comp.contains(2) && comp.contains(4)) {
                foundPair2and4 = true;
                break;
            }
        }
        assertTrue(foundPair2and4, "Nodes 2 and 4 must belong to the same SCC");
    }

    @Test
    @DisplayName("Topological Sort: Kahn's DAG check")
    void testTopologicalSort() {
        TopologicalSortAlgorithm topo = new TopologicalSortAlgorithm();
        // Graph with cycle is not a DAG
        TopologicalSortAlgorithm.TopoResult res1 = topo.execute(sampleGraph);
        assertFalse(res1.isDAG);

        // Remove back-edge to make it a DAG
        sampleGraph.removeEdge(4, 2);
        TopologicalSortAlgorithm.TopoResult res2 = topo.execute(sampleGraph);
        assertTrue(res2.isDAG);
        assertEquals(5, res2.sortedOrder.size());
    }

    @Test
    @DisplayName("Articulation Points and Bridges: Single points of failure")
    void testArticulationPointsAndBridges() {
        ArticulationPointsAndBridgesAlgorithm spof = new ArticulationPointsAndBridgesAlgorithm();
        ArticulationPointsAndBridgesAlgorithm.SpofResult res = spof.execute(sampleGraph);

        // Node 4 is critical bottleneck to Node 5
        assertTrue(res.articulationPoints.contains(4), "Node 4 should be an articulation point");
        boolean bridge4to5 = false;
        for (ArticulationPointsAndBridgesAlgorithm.BridgeEdge b : res.bridges) {
            if ((b.u == 4 && b.v == 5) || (b.u == 5 && b.v == 4)) {
                bridge4to5 = true;
                break;
            }
        }
        assertTrue(bridge4to5, "Edge between 4 and 5 is a critical bridge link");
    }

    @Test
    @DisplayName("PageRank Centrality: Influence distribution")
    void testPageRankCentrality() {
        PageRankCentralityAlgorithm pr = new PageRankCentralityAlgorithm();
        PageRankCentralityAlgorithm.CentralityResult res = pr.execute(sampleGraph);

        assertNotNull(res.centralityScores);
        assertEquals(5, res.centralityScores.size());
        for (int id : sampleGraph.getNodeIds()) {
            assertTrue(res.centralityScores.get(id) > 0.0);
        }
    }

    @Test
    @DisplayName("Causal Path Reconstruction: Strongest path from source to target")
    void testCausalPathReconstruction() {
        CausalPathReconstructionAlgorithm causal = new CausalPathReconstructionAlgorithm();
        CausalPathReconstructionAlgorithm.CausalPathResult res = causal.findStrongestPath(sampleGraph, 1, 5);

        assertTrue(res.pathFound);
        assertEquals(1, res.path.get(0));
        assertEquals(5, res.path.get(res.path.size() - 1));
        assertTrue(res.finalImpactMultiplier > 0.0);
    }

    @Test
    @DisplayName("Kruskal's MST: Backbone calculation")
    void testKruskalMST() {
        KruskalMSTAlgorithm mst = new KruskalMSTAlgorithm();
        KruskalMSTAlgorithm.MstResult res = mst.execute(sampleGraph);

        assertFalse(res.mstEdges.isEmpty());
        assertTrue(res.totalCost > 0.0);
    }

    @Test
    @DisplayName("Greedy Mitigation Advisor: Shielding reduces cumulative downstream shock")
    void testGreedyMitigationAdvisor() {
        GreedyMitigationAdvisor advisor = new GreedyMitigationAdvisor();
        GreedyMitigationAdvisor.MitigationPlanResult plan = advisor.generatePlan(
            sampleGraph, 1, 9.0, 0.85, 0.05,
            CascadePropagationAlgorithm.ModelType.MAX_PATH, 2
        );

        assertNotNull(plan);
        assertTrue(plan.protectedNodeIds.size() > 0);
        assertTrue(plan.impactReductionPct > 0.0);
    }

    @Test
    @DisplayName("Monte Carlo Mode: Statistical mean and variance computation")
    void testMonteCarlo() {
        MonteCarloSimulationAlgorithm mc = new MonteCarloSimulationAlgorithm();
        MonteCarloSimulationAlgorithm.MonteCarloResult res = mc.execute(
            sampleGraph, 1, 8.0, 0.85, 0.05,
            CascadePropagationAlgorithm.ModelType.MAX_PATH, 20
        );

        assertEquals(5, res.nodeStatistics.size());
        for (MonteCarloSimulationAlgorithm.NodeStats stat : res.nodeStatistics) {
            assertTrue(stat.meanImpact >= 0.0);
            assertTrue(stat.variance >= 0.0);
        }
    }

    @Test
    @DisplayName("Scalability Benchmark: 10,000 Nodes / 50,000 Edges Simulation")
    void testScalability10kNodes50kEdges() {
        System.out.println("\n--- Starting Scalability Benchmark: 10,000 Nodes, 50,000 Edges ---");
        Graph largeGraph = new Graph();
        int V = 10000;
        int E = 50000;

        for (int i = 0; i < V; i++) {
            largeGraph.addNode(new Node(i, 99, "ScaleNode" + i, "Scale", i % 1000, i / 1000, 0.5, ""));
        }

        java.util.Random rand = new java.util.Random(12345);
        for (int i = 0; i < E; i++) {
            int src = rand.nextInt(V);
            int tgt = rand.nextInt(V);
            if (src != tgt) {
                largeGraph.addEdge(src, tgt, 0.5 + rand.nextDouble() * 0.45, 1.0 + rand.nextDouble() * 5.0, "SCALE");
            }
        }

        long t0 = System.currentTimeMillis();
        CascadePropagationAlgorithm propagator = new CascadePropagationAlgorithm();
        CascadePropagationAlgorithm.PropagationOutput out = propagator.execute(
            largeGraph, 0, 10.0, 0.85, 0.01, CascadePropagationAlgorithm.ModelType.MAX_PATH
        );
        long t1 = System.currentTimeMillis();

        long elapsedMs = t1 - t0;
        System.out.println("Scalability Benchmark Complete: " + out.results.size() +
                           " nodes affected in " + elapsedMs + " ms.");
        assertTrue(elapsedMs < 5000, "Simulation on 10,000 nodes should finish in under 5 seconds, took " + elapsedMs + " ms");
    }
}
