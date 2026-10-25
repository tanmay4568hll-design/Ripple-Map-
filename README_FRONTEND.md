# 🌐 RippleMap — Interactive Dependency Radar & Cascade Failure Analysis Engine
## Advanced Data Structures & Algorithms (ADSA) and DBMS Major Course Project

An interactive, high-performance web frontend built with **HTML5, Vanilla CSS3, and Modern Vanilla JavaScript (ES6+)**.

---

## 🚀 Quick Launch Instructions

### Option 1: Double-Click Launcher
Double-click [run_frontend.bat](file:///c:/Users/lenovo/Desktop/adsa%20project/run_frontend.bat) in the project root directory. It automatically opens the browser at `http://127.0.0.1:5173`.

### Option 2: Command Line
```powershell
# From the project directory:
python -m http.server 5173 --bind 127.0.0.1
```
Then visit **`http://127.0.0.1:5173`** in Chrome, Edge, or Firefox.

### Option 3: Direct File Access
Double-click [index.html](file:///c:/Users/lenovo/Desktop/adsa%20project/index.html) to open directly in any browser (all code is self-contained with zero required external builds).

---

## 🎨 Visual Design & Architecture

- **Futuristic Cyberpunk Glassmorphism:** Deep obsidian background (`#060913`), frosted glass panels with `backdrop-filter: blur(16px)`, neon cyan (`#00f2fe`), danger crimson (`#ef4444`), toxic amber (`#f59e0b`), and matrix green (`#10b981`).
- **Typography:** Google Fonts (`Outfit` for geometric futuristic headings, `Inter` for clean UI, and `JetBrains Mono` for latency, complexity, and metrics).
- **Procedural Audio Synthesizer:** Built-in Web Audio API generator (`js/sound.js`) producing cyber pulses, wave pings, alert alarms, and shielding hums with zero external audio assets.
- **High-DPI 2D Canvas Visualizer:** Infinite pan & zoom, node dragging, directed Bézier curves with arrowheads, animated flow particles traveling along dependency edges, shockwave ripples, and radar minimap.

---

## 🧮 Implemented ADSA Algorithms & Features

| Algorithm | Complexity | Purpose in RippleMap |
| :--- | :---: | :--- |
| **Cascade Propagation** | $O(V + E)$ | Simulates attenuated failure shockwaves across directed dependencies with resilience damping. Supports **Max-Path** (peak single-route impact) and **Sum-Path** (converging cumulative impact). |
| **BFS Ripple Waves** | $O(V + E)$ | Concentric wave expansion level-by-level with step-through playback and FIFO queue inspection. |
| **Dijkstra Shortest Latency** | $O(E \log V)$ | Computes earliest failure arrival time across network delay weights using priority queues. |
| **Tarjan's SCC** | $O(V + E)$ | Identifies strongly connected components and circular dependency feedback loops/deadlocks. |
| **Topological Sort** | $O(V + E)$ | Kahn's algorithm determining system boot/build order and DAG integrity validation. |
| **Articulation Points & Bridges** | $O(V + E)$ | Discovers critical Single Points of Failure (SPF) whose breakdown disconnects the network. |
| **PageRank Centrality** | $O(k \cdot (V + E))$ | Iterative power iteration calculating structural node influence with damping factor $d = 0.85$. |
| **Kruskal's MST** | $O(E \log E)$ | Minimum Spanning Tree backbone extraction using handwritten Disjoint Set Union (DSU) with union by rank and path compression. |
| **Greedy Mitigation Advisor** | Greedy Heuristic | Evaluates candidate nodes to recommend top-K optimal components to shield, maximizing network blast radius reduction. |
| **Monte Carlo Simulation** | 500 Trials | Probabilistic failure trials generating cascade risk distribution histograms. |
| **Sorting Benchmark Suite** | Multiple | Live comparisons of QuickSort, MergeSort, HeapSort, InsertionSort, and BubbleSort with comparison counters and microsecond timers. |
| **Searching Benchmark Suite** | Multiple | Comparisons of Linear Search, Binary Search, and Interpolation Search on 100,000 records. |

---

## 🗄️ Database & Scenarios

Contains all 4 realistic seed scenarios ported from the DBMS 3NF schema:
1. **Global Electronics Supply Chain** (25 nodes, TSMC, Foxconn, Apple, Ports, Distribution).
2. **Cloud Microservices Architecture** (24 nodes, API Gateway, OAuth2, Kafka, Redis, Databases).
3. **Metropolitan City Critical Infrastructure** (23 nodes, Nuclear Plant, Substations, Hospitals, Water, Transit).
4. **Viral Social & Media Propagation** (22 nodes, Leaks, Botnets, News Wires, Markets, Regulators).

Includes an interactive **SQL Console** with preset queries and a **Live Event Audit Trail** that tracks simulated incidents and shielding actions.
