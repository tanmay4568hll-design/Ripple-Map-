/**
 * RippleMap - ADSA Graph Algorithms & Computational Engine
 * Complete, handwritten implementation of graph algorithms, cascade propagation,
 * centrality metrics, greedy mitigation advisor, and sorting/searching benchmarks.
 */

class GraphAlgorithms {

    /**
     * 1. Cascade Impact Propagation Algorithm
     * Propagates failure shockwaves from sourceNodeId across directed influence edges.
     * Formula: impact(child) = impact(parent) * edgeWeight * decayFactor * (1.0 - child.resilience * 0.4)
     */
    static runCascadePropagation(nodes, edges, sourceId, options = {}) {
        const severity = options.severity !== undefined ? options.severity : 8.5;
        const decayFactor = options.decayFactor !== undefined ? options.decayFactor : 0.85;
        const threshold = options.threshold !== undefined ? options.threshold : 0.05;
        const modelType = options.modelType || 'MAX_PATH'; // 'MAX_PATH' or 'SUM_PATH'
        const shieldedIds = options.shieldedIds || new Set();

        const nodeMap = new Map();
        nodes.forEach(n => nodeMap.set(n.id, n));

        // Adjacency list
        const adj = new Map();
        nodes.forEach(n => adj.set(n.id, []));
        edges.forEach(e => {
            if (adj.has(e.sourceId)) {
                adj.get(e.sourceId).push(e);
            }
        });

        const initialImpact = Math.min(1.0, Math.max(0.1, severity / 10.0));
        const impactScores = new Map();
        const arrivalTimes = new Map();
        const hopLevels = new Map();
        const causalParent = new Map();
        const waves = []; // Array of wave arrays: [ [sourceId], [wave 1 nodes], ... ]

        const isSourceShielded = shieldedIds.has(sourceId);
        impactScores.set(sourceId, isSourceShielded ? 0.0 : initialImpact);
        arrivalTimes.set(sourceId, 0.0);
        hopLevels.set(sourceId, 0);

        if (isSourceShielded) {
            return {
                impactScores,
                arrivalTimes,
                hopLevels,
                causalParent,
                waves: [[sourceId]],
                impactedNodes: [],
                blastRadiusPct: 0.0,
                maxArrivalTime: 0.0,
                durationMs: 0.1
            };
        }

        const t0 = performance.now();
        const queue = [{ id: sourceId, impact: initialImpact, time: 0.0, hop: 0 }];
        const visitedInWave = new Set([sourceId]);
        waves.push([sourceId]);

        let currentHop = 0;
        let currentWaveNodes = [];

        while (queue.length > 0) {
            const curr = queue.shift();

            if (curr.hop > currentHop) {
                if (currentWaveNodes.length > 0) {
                    waves.push([...currentWaveNodes]);
                    currentWaveNodes = [];
                }
                currentHop = curr.hop;
            }

            const outEdges = adj.get(curr.id) || [];
            for (const edge of outEdges) {
                const targetId = edge.targetId;
                const targetNode = nodeMap.get(targetId);
                if (!targetNode) continue;

                // Shielded nodes completely absorb impact
                if (shieldedIds.has(targetId)) continue;

                const resilienceDamping = 1.0 - (targetNode.resilience * 0.4);
                const stepImpact = curr.impact * edge.weight * decayFactor * resilienceDamping;

                if (stepImpact < threshold) continue;

                const stepTime = curr.time + edge.delayHours;
                const nextHop = curr.hop + 1;

                let shouldEnqueue = false;
                const prevImpact = impactScores.get(targetId) || 0.0;

                if (modelType === 'MAX_PATH') {
                    if (stepImpact > prevImpact) {
                        impactScores.set(targetId, stepImpact);
                        arrivalTimes.set(targetId, stepTime);
                        hopLevels.set(targetId, nextHop);
                        causalParent.set(targetId, curr.id);
                        shouldEnqueue = true;
                    }
                } else {
                    // SUM_PATH: superposition with upper bound 1.0
                    const combined = Math.min(1.0, prevImpact + stepImpact);
                    if (combined > prevImpact + 0.02) {
                        impactScores.set(targetId, combined);
                        arrivalTimes.set(targetId, Math.min(arrivalTimes.get(targetId) || 999999, stepTime));
                        hopLevels.set(targetId, Math.min(hopLevels.get(targetId) || 999, nextHop));
                        causalParent.set(targetId, curr.id);
                        shouldEnqueue = true;
                    }
                }

                if (shouldEnqueue) {
                    queue.push({ id: targetId, impact: stepImpact, time: stepTime, hop: nextHop });
                    currentWaveNodes.push(targetId);
                }
            }
        }

        if (currentWaveNodes.length > 0) {
            waves.push([...currentWaveNodes]);
        }

        const durationMs = performance.now() - t0;
        const impactedNodes = [];
        let maxArrivalTime = 0.0;

        impactScores.forEach((score, nid) => {
            if (score > 0.0) {
                const arrTime = arrivalTimes.get(nid) || 0.0;
                if (arrTime > maxArrivalTime) maxArrivalTime = arrTime;
                impactedNodes.push({
                    id: nid,
                    node: nodeMap.get(nid),
                    impact: score,
                    arrivalTime: arrTime,
                    hop: hopLevels.get(nid) || 0,
                    parent: causalParent.get(nid) || null
                });
            }
        });

        // Sort descending by impact
        impactedNodes.sort((a, b) => b.impact - a.impact);
        const blastRadiusPct = (impactedNodes.length / Math.max(1, nodes.length)) * 100;

        return {
            impactScores,
            arrivalTimes,
            hopLevels,
            causalParent,
            waves,
            impactedNodes,
            blastRadiusPct: parseFloat(blastRadiusPct.toFixed(1)),
            maxArrivalTime: parseFloat(maxArrivalTime.toFixed(1)),
            durationMs: parseFloat(durationMs.toFixed(2))
        };
    }

    /**
     * 2. BFS Concentric Ripple Wave Traversal
     * Simulates concentric circular waves level-by-level with explicit queue history.
     */
    static runBfsRipple(nodes, edges, sourceId) {
        const adj = new Map();
        nodes.forEach(n => adj.set(n.id, []));
        edges.forEach(e => {
            if (adj.has(e.sourceId)) adj.get(e.sourceId).push(e.targetId);
        });

        const visited = new Set([sourceId]);
        const queue = [sourceId];
        const waves = [];
        const queueHistory = [];

        let currentLevel = 0;

        while (queue.length > 0) {
            const levelSize = queue.length;
            const currentWave = [];
            queueHistory.push({
                level: currentLevel,
                queueSnapshot: [...queue]
            });

            for (let i = 0; i < levelSize; i++) {
                const u = queue.shift();
                currentWave.push(u);

                const neighbors = adj.get(u) || [];
                for (const v of neighbors) {
                    if (!visited.has(v)) {
                        visited.add(v);
                        queue.push(v);
                    }
                }
            }

            waves.push({
                level: currentLevel,
                nodeIds: currentWave
            });
            currentLevel++;
        }

        return { waves, queueHistory, totalVisited: visited.size };
    }

    /**
     * 3. Dijkstra Shortest Propagation Time Algorithm
     * Computes minimum arrival delay (in hours) across edge latency weights.
     */
    static runDijkstraTime(nodes, edges, sourceId) {
        const adj = new Map();
        nodes.forEach(n => adj.set(n.id, []));
        edges.forEach(e => {
            if (adj.has(e.sourceId)) {
                adj.get(e.sourceId).push({ targetId: e.targetId, delay: e.delayHours, weight: e.weight });
            }
        });

        const dist = new Map();
        const prev = new Map();
        nodes.forEach(n => dist.set(n.id, Infinity));
        dist.set(sourceId, 0.0);

        // Min-heap / priority queue
        const pq = [{ id: sourceId, dist: 0.0 }];

        while (pq.length > 0) {
            pq.sort((a, b) => a.dist - b.dist);
            const { id: u, dist: d } = pq.shift();

            if (d > dist.get(u)) continue;

            const neighbors = adj.get(u) || [];
            for (const edge of neighbors) {
                const v = edge.targetId;
                const newDist = d + edge.delay;
                if (newDist < dist.get(v)) {
                    dist.set(v, newDist);
                    prev.set(v, u);
                    pq.push({ id: v, dist: newDist });
                }
            }
        }

        const reachable = [];
        dist.forEach((d, nid) => {
            if (d !== Infinity) {
                reachable.push({ id: nid, delayHours: parseFloat(d.toFixed(2)), parent: prev.get(nid) || null });
            }
        });
        reachable.sort((a, b) => a.delayHours - b.delayHours);

        return { dist, prev, reachable };
    }

    /**
     * 4. Tarjan's Strongly Connected Components (SCC) Algorithm
     * Identifies cyclical dependency clusters and potential deadlock loops.
     */
    static runTarjanSCC(nodes, edges) {
        const adj = new Map();
        nodes.forEach(n => adj.set(n.id, []));
        edges.forEach(e => {
            if (adj.has(e.sourceId)) adj.get(e.sourceId).push(e.targetId);
        });

        let index = 0;
        const indices = new Map();
        const lowlink = new Map();
        const onStack = new Set();
        const stack = [];
        const sccs = [];

        function strongConnect(v) {
            indices.set(v, index);
            lowlink.set(v, index);
            index++;
            stack.push(v);
            onStack.add(v);

            const neighbors = adj.get(v) || [];
            for (const w of neighbors) {
                if (!indices.has(w)) {
                    strongConnect(w);
                    lowlink.set(v, Math.min(lowlink.get(v), lowlink.get(w)));
                } else if (onStack.has(w)) {
                    lowlink.set(v, Math.min(lowlink.get(v), indices.get(w)));
                }
            }

            if (lowlink.get(v) === indices.get(v)) {
                const scc = [];
                let w;
                do {
                    w = stack.pop();
                    onStack.delete(w);
                    scc.push(w);
                } while (w !== v);
                sccs.push(scc);
            }
        }

        nodes.forEach(n => {
            if (!indices.has(n.id)) {
                strongConnect(n.id);
            }
        });

        // Cycles are SCCs with > 1 node or a self-loop
        const cyclicClusters = sccs.filter(comp => comp.length > 1);

        return { sccs, cyclicClusters };
    }

    /**
     * 5. DFS Directed Cycle Detection
     * Identifies specific directed cycles in the graph.
     */
    static runCycleDetection(nodes, edges) {
        const adj = new Map();
        nodes.forEach(n => adj.set(n.id, []));
        edges.forEach(e => {
            if (adj.has(e.sourceId)) adj.get(e.sourceId).push(e.targetId);
        });

        const visited = new Set();
        const recStack = new Set();
        const parentMap = new Map();
        const cycles = [];

        function dfs(u, path = []) {
            visited.add(u);
            recStack.add(u);
            path.push(u);

            const neighbors = adj.get(u) || [];
            for (const v of neighbors) {
                if (!visited.has(v)) {
                    parentMap.set(v, u);
                    dfs(v, [...path]);
                } else if (recStack.has(v)) {
                    // Cycle found! Reconstruct cycle path from v onwards
                    const cycleStartIdx = path.indexOf(v);
                    if (cycleStartIdx !== -1) {
                        const cyclePath = path.slice(cycleStartIdx);
                        cyclePath.push(v);
                        cycles.push(cyclePath);
                    }
                }
            }

            recStack.delete(u);
        }

        nodes.forEach(n => {
            if (!visited.has(n.id)) {
                dfs(n.id, []);
            }
        });

        return { hasCycles: cycles.length > 0, cycles };
    }

    /**
     * 6. Topological Sort (Kahn's Algorithm)
     * Determines dependency compilation/boot sequence.
     */
    static runTopologicalSort(nodes, edges) {
        const inDegree = new Map();
        const adj = new Map();

        nodes.forEach(n => {
            inDegree.set(n.id, 0);
            adj.set(n.id, []);
        });

        edges.forEach(e => {
            if (adj.has(e.sourceId)) {
                adj.get(e.sourceId).push(e.targetId);
                inDegree.set(e.targetId, (inDegree.get(e.targetId) || 0) + 1);
            }
        });

        const queue = [];
        inDegree.forEach((deg, id) => {
            if (deg === 0) queue.push(id);
        });

        const order = [];
        while (queue.length > 0) {
            const u = queue.shift();
            order.push(u);

            const neighbors = adj.get(u) || [];
            for (const v of neighbors) {
                inDegree.set(v, inDegree.get(v) - 1);
                if (inDegree.get(v) === 0) {
                    queue.push(v);
                }
            }
        }

        const isDAG = order.length === nodes.length;
        return { isDAG, order, cycleRemaining: nodes.length - order.length };
    }

    /**
     * 7. Articulation Points and Critical Bridges (Tarjan's Cut-Vertex & Bridge)
     * Identifies Single Points of Failure (SPF) whose failure cuts the network.
     */
    static runArticulationPointsAndBridges(nodes, edges) {
        // Undirected graph representation for connectivity bottlenecks
        const adj = new Map();
        nodes.forEach(n => adj.set(n.id, []));
        edges.forEach(e => {
            if (adj.has(e.sourceId) && adj.has(e.targetId)) {
                adj.get(e.sourceId).push(e.targetId);
                adj.get(e.targetId).push(e.sourceId);
            }
        });

        let timer = 0;
        const tin = new Map();
        const low = new Map();
        const visited = new Set();
        const articulationPoints = new Set();
        const bridges = [];

        function dfs(u, p = -1) {
            visited.add(u);
            tin.set(u, timer);
            low.set(u, timer);
            timer++;
            let children = 0;

            const neighbors = adj.get(u) || [];
            for (const v of neighbors) {
                if (v === p) continue;
                if (visited.has(v)) {
                    low.set(u, Math.min(low.get(u), tin.get(v)));
                } else {
                    dfs(v, u);
                    low.set(u, Math.min(low.get(u), low.get(v)));
                    if (low.get(v) >= tin.get(u) && p !== -1) {
                        articulationPoints.add(u);
                    }
                    if (low.get(v) > tin.get(u)) {
                        bridges.push({ u, v });
                    }
                    children++;
                }
            }
            if (p === -1 && children > 1) {
                articulationPoints.add(u);
            }
        }

        nodes.forEach(n => {
            if (!visited.has(n.id)) {
                dfs(n.id);
            }
        });

        return {
            articulationPoints: Array.from(articulationPoints),
            bridges
        };
    }

    /**
     * 8. PageRank Centrality Algorithm
     * Computes node influence using power iteration with damping factor 0.85.
     */
    static runPageRank(nodes, edges, iterations = 25, d = 0.85) {
        const N = nodes.length;
        if (N === 0) return new Map();

        const ranks = new Map();
        const outDegree = new Map();
        const inNeighbors = new Map();

        nodes.forEach(n => {
            ranks.set(n.id, 1.0 / N);
            outDegree.set(n.id, 0);
            inNeighbors.set(n.id, []);
        });

        edges.forEach(e => {
            if (outDegree.has(e.sourceId) && inNeighbors.has(e.targetId)) {
                outDegree.set(e.sourceId, outDegree.get(e.sourceId) + 1);
                inNeighbors.get(e.targetId).push(e.sourceId);
            }
        });

        for (let iter = 0; iter < iterations; iter++) {
            const nextRanks = new Map();
            let sinkSum = 0;

            // Handle sink nodes (nodes with 0 out-degree)
            nodes.forEach(n => {
                if (outDegree.get(n.id) === 0) {
                    sinkSum += ranks.get(n.id);
                }
            });

            nodes.forEach(n => {
                let rankFromInNeighbors = 0.0;
                const ins = inNeighbors.get(n.id) || [];
                for (const inId of ins) {
                    const out = outDegree.get(inId);
                    if (out > 0) {
                        rankFromInNeighbors += ranks.get(inId) / out;
                    }
                }

                const newRank = ((1.0 - d) / N) + d * (rankFromInNeighbors + sinkSum / N);
                nextRanks.set(n.id, newRank);
            });

            nextRanks.forEach((val, id) => ranks.set(id, val));
        }

        const sorted = [];
        ranks.forEach((score, id) => {
            sorted.push({ id, score: parseFloat((score * 100).toFixed(2)) });
        });
        sorted.sort((a, b) => b.score - a.score);

        return { ranks, sortedRanking: sorted };
    }

    /**
     * 9. Kruskal's Minimum Spanning Tree (MST) with Disjoint Set Union (DSU)
     * Finds the minimum-weight communication backbone of the network.
     */
    static runKruskalMST(nodes, edges) {
        // Disjoint Set Union (DSU) with Path Compression & Union by Rank
        const parent = new Map();
        const rank = new Map();

        nodes.forEach(n => {
            parent.set(n.id, n.id);
            rank.set(n.id, 0);
        });

        function find(i) {
            if (parent.get(i) === i) return i;
            parent.set(i, find(parent.get(i)));
            return parent.get(i);
        }

        function union(i, j) {
            const rootI = find(i);
            const rootJ = find(j);
            if (rootI !== rootJ) {
                if (rank.get(rootI) < rank.get(rootJ)) {
                    parent.set(rootI, rootJ);
                } else if (rank.get(rootI) > rank.get(rootJ)) {
                    parent.set(rootJ, rootI);
                } else {
                    parent.set(rootJ, rootI);
                    rank.set(rootI, rank.get(rootI) + 1);
                }
                return true;
            }
            return false;
        }

        // Sort edges ascending by weight (or delay)
        const sortedEdges = [...edges].sort((a, b) => a.weight - b.weight);
        const mstEdges = [];
        let totalWeight = 0;

        for (const edge of sortedEdges) {
            if (union(edge.sourceId, edge.targetId)) {
                mstEdges.push(edge);
                totalWeight += edge.weight;
            }
        }

        return {
            mstEdges,
            totalWeight: parseFloat(totalWeight.toFixed(2)),
            coverage: `${mstEdges.length} backbone edges spanning ${nodes.length} components`
        };
    }

    /**
     * 10. Greedy Mitigation Advisor
     * Greedily selects top-K components to shield to maximize blast radius reduction.
     */
    static runGreedyMitigation(nodes, edges, sourceId, budget = 3, cascadeOptions = {}) {
        // Baseline run without shielding
        const baseline = this.runCascadePropagation(nodes, edges, sourceId, { ...cascadeOptions, shieldedIds: new Set() });
        const baselineImpactSum = baseline.impactedNodes.reduce((acc, n) => acc + n.impact, 0);

        const currentShields = new Set();
        const candidatePool = nodes.filter(n => n.id !== sourceId);
        const steps = [];

        for (let k = 0; k < budget; k++) {
            let bestCandidate = null;
            let bestReduction = -1;
            let bestSimOutput = null;

            for (const cand of candidatePool) {
                if (currentShields.has(cand.id)) continue;

                const testShields = new Set(currentShields);
                testShields.add(cand.id);

                const sim = this.runCascadePropagation(nodes, edges, sourceId, { ...cascadeOptions, shieldedIds: testShields });
                const testImpactSum = sim.impactedNodes.reduce((acc, n) => acc + n.impact, 0);
                const reduction = baselineImpactSum - testImpactSum;

                if (reduction > bestReduction) {
                    bestReduction = reduction;
                    bestCandidate = cand;
                    bestSimOutput = sim;
                }
            }

            if (bestCandidate && bestReduction > 0) {
                currentShields.add(bestCandidate.id);
                steps.push({
                    step: k + 1,
                    node: bestCandidate,
                    savedImpact: parseFloat(bestReduction.toFixed(2)),
                    resultingBlastRadiusPct: bestSimOutput.blastRadiusPct
                });
            } else {
                break;
            }
        }

        const finalSim = this.runCascadePropagation(nodes, edges, sourceId, { ...cascadeOptions, shieldedIds: currentShields });
        const finalImpactSum = finalSim.impactedNodes.reduce((acc, n) => acc + n.impact, 0);
        const totalReductionPct = baselineImpactSum > 0 ? ((baselineImpactSum - finalImpactSum) / baselineImpactSum) * 100 : 0;

        return {
            recommendedShieldIds: Array.from(currentShields),
            steps,
            baselineBlastRadiusPct: baseline.blastRadiusPct,
            mitigatedBlastRadiusPct: finalSim.blastRadiusPct,
            totalReductionPct: parseFloat(totalReductionPct.toFixed(1))
        };
    }

    /**
     * 11. Monte Carlo Stochastic Cascade Simulator
     * Runs N trials with probabilistic edge failure triggers.
     */
    static runMonteCarlo(nodes, edges, sourceId, trials = 400) {
        const results = [];
        const blastRadiusCounts = new Array(11).fill(0); // 0-10%, 10-20% ... 90-100%

        for (let t = 0; t < trials; t++) {
            // Probabilistic edges
            const activeEdges = edges.filter(e => Math.random() <= e.weight);
            const sim = this.runCascadePropagation(nodes, activeEdges, sourceId, { severity: 8.0, decayFactor: 0.85, threshold: 0.05 });
            results.push(sim.blastRadiusPct);

            const bin = Math.min(10, Math.floor(sim.blastRadiusPct / 10));
            blastRadiusCounts[bin]++;
        }

        const avgBlast = results.reduce((a, b) => a + b, 0) / trials;
        const sorted = [...results].sort((a, b) => a - b);
        const p50 = sorted[Math.floor(trials * 0.5)];
        const p95 = sorted[Math.floor(trials * 0.95)];

        return {
            trials,
            avgBlastRadius: parseFloat(avgBlast.toFixed(1)),
            p50: parseFloat(p50.toFixed(1)),
            p95: parseFloat(p95.toFixed(1)),
            distribution: blastRadiusCounts
        };
    }

    /**
     * 12. Sorting Algorithms Benchmark Suite
     * Evaluates Quick, Merge, Heap, Bubble, Insertion, Selection, Counting, and Radix Sort.
     */
    static runSortingBenchmark(arraySize = 2000) {
        // Generate uniform random dataset
        const raw = [];
        for (let i = 0; i < arraySize; i++) {
            raw.push(Math.floor(Math.random() * 10000));
        }

        const results = [];

        // 1. QuickSort
        {
            const arr = [...raw];
            let comparisons = 0, swaps = 0;
            const t0 = performance.now();

            function qs(low, high) {
                if (low < high) {
                    const pi = partition(low, high);
                    qs(low, pi - 1);
                    qs(pi + 1, high);
                }
            }

            function partition(low, high) {
                const pivot = arr[high];
                let i = low - 1;
                for (let j = low; j < high; j++) {
                    comparisons++;
                    if (arr[j] < pivot) {
                        i++;
                        const temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
                        swaps++;
                    }
                }
                const temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp;
                swaps++;
                return i + 1;
            }

            qs(0, arr.length - 1);
            const timeMs = performance.now() - t0;
            results.push({ name: 'QuickSort', complexity: 'O(N log N)', timeUs: Math.round(timeMs * 1000), comparisons, swaps });
        }

        // 2. MergeSort
        {
            const arr = [...raw];
            let comparisons = 0, swaps = 0;
            const t0 = performance.now();

            function ms(low, high) {
                if (low < high) {
                    const mid = Math.floor((low + high) / 2);
                    ms(low, mid);
                    ms(mid + 1, high);
                    merge(low, mid, high);
                }
            }

            function merge(low, mid, high) {
                const left = arr.slice(low, mid + 1);
                const right = arr.slice(mid + 1, high + 1);
                let i = 0, j = 0, k = low;

                while (i < left.length && j < right.length) {
                    comparisons++;
                    if (left[i] <= right[j]) {
                        arr[k++] = left[i++];
                    } else {
                        arr[k++] = right[j++];
                    }
                    swaps++;
                }
                while (i < left.length) { arr[k++] = left[i++]; swaps++; }
                while (j < right.length) { arr[k++] = right[j++]; swaps++; }
            }

            ms(0, arr.length - 1);
            const timeMs = performance.now() - t0;
            results.push({ name: 'MergeSort', complexity: 'O(N log N)', timeUs: Math.round(timeMs * 1000), comparisons, swaps });
        }

        // 3. HeapSort
        {
            const arr = [...raw];
            let comparisons = 0, swaps = 0;
            const t0 = performance.now();

            function heapify(n, i) {
                let largest = i;
                const l = 2 * i + 1;
                const r = 2 * i + 2;

                if (l < n) {
                    comparisons++;
                    if (arr[l] > arr[largest]) largest = l;
                }
                if (r < n) {
                    comparisons++;
                    if (arr[r] > arr[largest]) largest = r;
                }
                if (largest !== i) {
                    const temp = arr[i]; arr[i] = arr[largest]; arr[largest] = temp;
                    swaps++;
                    heapify(n, largest);
                }
            }

            const n = arr.length;
            for (let i = Math.floor(n / 2) - 1; i >= 0; i--) heapify(n, i);
            for (let i = n - 1; i > 0; i--) {
                const temp = arr[0]; arr[0] = arr[i]; arr[i] = temp;
                swaps++;
                heapify(i, 0);
            }

            const timeMs = performance.now() - t0;
            results.push({ name: 'HeapSort', complexity: 'O(N log N)', timeUs: Math.round(timeMs * 1000), comparisons, swaps });
        }

        // 4. InsertionSort (on a subset to keep browser responsive)
        {
            const subsetSize = Math.min(600, arraySize);
            const arr = raw.slice(0, subsetSize);
            let comparisons = 0, swaps = 0;
            const t0 = performance.now();

            for (let i = 1; i < arr.length; i++) {
                const key = arr[i];
                let j = i - 1;
                while (j >= 0) {
                    comparisons++;
                    if (arr[j] > key) {
                        arr[j + 1] = arr[j];
                        swaps++;
                        j--;
                    } else break;
                }
                arr[j + 1] = key;
            }

            const timeMs = performance.now() - t0;
            results.push({ name: 'InsertionSort', complexity: 'O(N²)', timeUs: Math.round(timeMs * 1000), comparisons, swaps, note: `N=${subsetSize}` });
        }

        // 5. BubbleSort
        {
            const subsetSize = Math.min(500, arraySize);
            const arr = raw.slice(0, subsetSize);
            let comparisons = 0, swaps = 0;
            const t0 = performance.now();

            for (let i = 0; i < arr.length - 1; i++) {
                for (let j = 0; j < arr.length - i - 1; j++) {
                    comparisons++;
                    if (arr[j] > arr[j + 1]) {
                        const temp = arr[j]; arr[j] = arr[j + 1]; arr[j + 1] = temp;
                        swaps++;
                    }
                }
            }

            const timeMs = performance.now() - t0;
            results.push({ name: 'BubbleSort', complexity: 'O(N²)', timeUs: Math.round(timeMs * 1000), comparisons, swaps, note: `N=${subsetSize}` });
        }

        return results;
    }

    /**
     * 13. Searching Benchmark Suite
     * Compares Linear Search vs Binary Search vs Interpolation Search.
     */
    static runSearchingBenchmark(size = 100000) {
        const arr = [];
        for (let i = 0; i < size; i++) {
            arr.push(i * 3 + Math.floor(Math.random() * 2));
        }
        const target = arr[Math.floor(size * 0.73)]; // guaranteed present

        const results = [];

        // Linear Search
        {
            let comparisons = 0;
            const t0 = performance.now();
            let foundIdx = -1;
            for (let i = 0; i < arr.length; i++) {
                comparisons++;
                if (arr[i] === target) {
                    foundIdx = i;
                    break;
                }
            }
            const timeMs = performance.now() - t0;
            results.push({ name: 'Linear Search', complexity: 'O(N)', comparisons, timeUs: Math.round(timeMs * 1000), foundIdx });
        }

        // Binary Search
        {
            let comparisons = 0;
            const t0 = performance.now();
            let low = 0, high = arr.length - 1, foundIdx = -1;
            while (low <= high) {
                comparisons++;
                const mid = Math.floor((low + high) / 2);
                if (arr[mid] === target) {
                    foundIdx = mid;
                    break;
                } else if (arr[mid] < target) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
            const timeMs = performance.now() - t0;
            results.push({ name: 'Binary Search', complexity: 'O(log N)', comparisons, timeUs: Math.round(timeMs * 1000), foundIdx });
        }

        // Interpolation Search
        {
            let comparisons = 0;
            const t0 = performance.now();
            let low = 0, high = arr.length - 1, foundIdx = -1;
            while (low <= high && target >= arr[low] && target <= arr[high]) {
                comparisons++;
                if (low === high) {
                    if (arr[low] === target) foundIdx = low;
                    break;
                }
                const pos = low + Math.floor(((target - arr[low]) * (high - low)) / (arr[high] - arr[low]));
                if (pos < low || pos > high) break;

                if (arr[pos] === target) {
                    foundIdx = pos;
                    break;
                } else if (arr[pos] < target) {
                    low = pos + 1;
                } else {
                    high = pos - 1;
                }
            }
            const timeMs = performance.now() - t0;
            results.push({ name: 'Interpolation Search', complexity: 'O(log log N)', comparisons, timeUs: Math.round(timeMs * 1000), foundIdx });
        }

        return results;
    }
}
