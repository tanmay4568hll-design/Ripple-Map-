# 🌐 RippleMap — Dependency Radar & Cascade Failure Analysis Engine

<p align="center">
  <a href="https://ripple-map-1.onrender.com/"><img src="https://img.shields.io/badge/%F0%9F%9A%80%20Live%20Demo-ripple--map--1.onrender.com-00f2fe?style=for-the-badge&logo=render&logoColor=white" alt="Live Demo on Render"/></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21%20LTS-007396?logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Frontend-HTML5%20%7C%20CSS3%20%7C%20Vanilla%20JS-F7DF1E?logo=javascript&logoColor=black" alt="Web Frontend"/>
  <img src="https://img.shields.io/badge/Database-SQLite%203%20%2F%20MySQL%208-4479A1?logo=mysql&logoColor=white" alt="DBMS"/>
  <img src="https://img.shields.io/badge/Deployment-Render.com-46E3B7?logo=render&logoColor=white" alt="Render"/>
  <img src="https://img.shields.io/badge/Algorithms-ADSA%20Graph%20Suite-00f2fe" alt="Algorithms"/>
  <img src="https://img.shields.io/badge/License-MIT-green.svg" alt="License"/>
</p>

> **🔗 Live Deployment URL**: **[https://ripple-map-1.onrender.com/](https://ripple-map-1.onrender.com/)**  
> **B.Tech Major Course Project for Advanced Data Structures & Algorithms (ADSA) and Database Management Systems (DBMS)**  
> *RippleMap* is an interactive graph dependency modeling and cascade failure simulation system. It enables software architects and system engineers to model complex interdependent systems, simulate failure propagation shockwaves across directed dependencies, and evaluate algorithmic mitigation strategies.

---

## 📌 System Overview

Modern distributed architectures, enterprise supply chains, and municipal infrastructures fail non-linearly: a single component outage (e.g., TSMC chip foundry, OAuth2 Authentication microservice, or a high-voltage substation) triggers cascading failures across dependent subsystems.

**RippleMap** models these ecosystems as directed influence graphs $G = (V, E)$, calculating dynamic shockwave propagation, arrival latencies, single points of failure (SPF), cyclic deadlocks, and greedy mitigation shields.

```
[ Epicenter Event ] ──(Cascade Impact)──► [ Interdependent Nodes ] ──(Damped Shockwave)──► [ Blast Radius ]
        │                                             ▲
        ▼                                             │
 [ Greedy Advisor ] ───(Applies Redundancy Shield)────┘ (Absorbs Downstream Shock)
```

---

## 🚀 Key Features

### 1. 🎛️ Interactive Cyberpunk Radar Canvas
- **High-DPI Retina Canvas**: Infinite smooth pan & zoom, drag-and-drop node repositioning, and auto-centering.
- **Animated Edge Traffic**: Dynamic luminous particles flowing along directed dependencies representing real-time message and resource flow.
- **Wavefront Shockwave Rings**: Concentric animated ripples expanding outwards from failure epicenters upon incident triggers.
- **Radar Scope Minimap**: High-tech HUD minimap with continuous rotating 360° sweep line in the corner.
- **Force-Directed Layout**: Built-in Fruchterman-Reingold physics engine (repulsive Coulomb forces + attractive Hooke springs).

### 2. 💥 Cascade Shockwave Propagation Engine
- **Resilience Damping Model**: Each node has a physical resilience score $R \in [0.0, 1.0]$.
  $$\text{Impact}(v) = \text{Impact}(u) \times \text{Weight}(u, v) \times \text{Decay} \times (1.0 - 0.4 \cdot R_v)$$
- **Superposition Strategies**:
  - `Max-Path`: Peak single-route blast impact.
  - `Sum-Path`: Superimposed cumulative converging impact with upper saturation limit.
- **Wavefront Playback Stepper**: Step through concentric ripple waves level-by-level with live FIFO queue inspection (`[TSMC] -> [Foxconn, Pegatron, CATL]`).

### 3. 🛡️ What-If Shielding & Greedy Mitigation Advisor
- **Fault-Tolerance Sandbox**: 1-click shield activation adds fault isolation/redundancy to any node, completely absorbing shockwaves and halting downstream cascades.
- **Greedy Mitigation Advisor**: Set a budget $K$ (e.g., 3 nodes). The algorithm automatically evaluates candidates to find the combination that maximizes network blast radius reduction.

### 4. 🧮 Comprehensive ADSA Algorithm Suite

| Algorithm | Asymptotic Complexity | Domain Role in RippleMap |
| :--- | :---: | :--- |
| **Cascade Propagation** | $O(V + E)$ | Core attenuated shockwave ripple engine with resilience damping |
| **BFS Ripple Waves** | $O(V + E)$ | Concentric wave expansion level-by-level with custom queue inspection |
| **Dijkstra Shortest Latency** | $O(E \log V)$ | Computes earliest failure arrival times and critical bottleneck latency paths |
| **Tarjan's SCC** | $O(V + E)$ | Discovers Strongly Connected Components and circular dependency deadlocks |
| **Kahn's Topological Sort** | $O(V + E)$ | Computes linear system boot/compilation order and verifies DAG integrity |
| **Articulation Points & Bridges** | $O(V + E)$ | Discovers Single Points of Failure (SPF) and critical connectivity bridges |
| **PageRank Centrality** | $O(k \cdot (V + E))$ | Iterative power iteration ($d=0.85$) scoring structural component influence |
| **Kruskal's MST** | $O(E \log E)$ | Minimum Spanning Tree backbone extraction using handwritten Disjoint Set Union (DSU) |
| **Greedy Mitigation Advisor** | Greedy Heuristic | Optimal $K$-node defense selection for maximal blast radius minimization |
| **Monte Carlo Simulator** | 500 Stochastic Trials | Probabilistic failure analysis plotting cascade risk distribution histograms |

### 5. ⚡ Sorting & Searching Benchmark Lab
- **Sorting Benchmarks**: Real-time microsecond timing, swap count, and comparison counters comparing:
  - `QuickSort` ($O(N \log N)$)
  - `MergeSort` ($O(N \log N)$)
  - `HeapSort` ($O(N \log N)$)
  - `InsertionSort` ($O(N^2)$)
  - `BubbleSort` ($O(N^2)$)
  - `CountingSort` ($O(N + K)$)
  - `RadixSort` ($O(d \cdot (N + K))$)
- **Searching Benchmarks**: Compares `Linear Search` ($O(N)$), `Binary Search` ($O(\log N)$), and `Interpolation Search` ($O(\log \log N)$) on 100,000 records.

### 6. 🗄️ Normalized 3NF Relational Database Model
- Strict **Third Normal Form (3NF)** relational database schema:
  - `scenarios` (Root domains)
  - `nodes` (Entities with coordinates, categories, and resilience)
  - `edges` (Directed links with weights and propagation latency)
  - `events` (Triggered incident records)
  - `simulation_runs` & `simulation_results` (Per-node historical impact metrics)
  - `mitigation_plans` (Advisor outputs)
  - `audit_log` (Populated automatically via SQL database triggers)
- **Interactive SQL Console**: In-app query editor with preloaded analytical queries and live audit trail log.

---

## 🌐 4 Realistic Preloaded Scenarios

1. **Global Electronics Supply Chain (25 nodes, 35 dependencies)**
   - Rare earth extraction in Chile/Congo $\to$ TSMC wafer foundry $\to$ Foxconn/Pegatron assembly $\to$ Seaports & Air cargo hubs $\to$ FedEx/DHL distribution $\to$ Retailers & Automotive lines.
   - *Default Incident:* Taiwan 7.4 Earthquake Halting TSMC Cleanrooms.

2. **Cloud Microservices Architecture (24 nodes, 36 dependencies)**
   - Mobile/Web Clients $\to$ Cloudflare Edge CDN $\to$ Kong API Gateway $\to$ OAuth2 Auth Service, User Service, Product Catalog $\to$ Kafka, Redis, PostgreSQL $\to$ Stripe & Notification workers.
   - *Default Incident:* JVM Heap Exhaustion in Auth Pods.

3. **Metropolitan City Critical Infrastructure (23 nodes, 34 dependencies)**
   - Nuclear Plant & Gas Turbines $\to$ High-Voltage Substations $\to$ Water filtration & booster pumps $\to$ Metro Rail, Hospitals, 911 Dispatch, Telecom fiber exchanges.
   - *Default Incident:* Nuclear Reactor Emergency Grid Scram.

4. **Viral Social & Media Cascade (22 nodes, 32 dependencies)**
   - Whistleblower leaks $\to$ Tech blogs & imageboards $\to$ Influencers & botnet amplification $\to$ Cable news, Trending algorithm feeds $\to$ Public sentiment, SEC regulators, and Wall St short-sellers.
   - *Default Incident:* Anonymous Whistleblower Leaks Executive Memos.

---

## 📁 Repository Structure

```
RippleMap/
├── index.html                   # Semantic HTML5 Application Dashboard
├── css/
│   └── style.css                # Cyberpunk Glassmorphism UI Design System
├── js/
│   ├── app.js                   # Main Controller & UI Event Dispatcher
│   ├── algorithms.js            # Handwritten ADSA Algorithm Suite
│   ├── graph.js                 # 2D Canvas Graph Visualizer & Physics Engine
│   ├── sound.js                 # Web Audio API Procedural Synthesizer
│   └── data.js                  # Complete 4 Scenarios & 3NF Schema Datasets
├── run_frontend.bat             # One-Click Windows Web App Launcher
├── push_to_github.bat           # GitHub Push Script
├── pom.xml                      # Maven Build Configuration
├── src/
│   ├── main/
│   │   ├── java/com/ripplemap/
│   │   │   ├── algo/            # Java Graph Algorithms (Cascade, Dijkstra, Tarjan, Kruskal, etc.)
│   │   │   ├── dao/             # JDBC Data Access Objects
│   │   │   ├── db/              # Database Connection Pool & Schema Initializer
│   │   │   ├── ds/              # Custom Data Structures (Lists, Stacks, Queues, Heaps, AVL, DSU)
│   │   │   ├── graph/           # Graph & GraphEdge domain models
│   │   │   ├── model/           # Node, Edge, Scenario, Event, SimulationRun
│   │   │   ├── search/          # Linear, Binary, Interpolation Search
│   │   │   ├── sort/            # Quick, Merge, Heap, Counting, Radix, QuickSelect
│   │   │   ├── ui/layout/       # Fruchterman-Reingold Force-Directed Layout
│   │   │   └── util/            # Password Hashing & Security Utilities
│   │   └── resources/
│   │       ├── db.properties    # Database configuration
│   │       └── sql/
│   │           ├── schema.sql   # 3NF DDL Schema & Triggers
│   │           └── seed.sql     # Seed Scenarios, Nodes, Edges, and Users
│   └── test/java/com/ripplemap/ # JUnit 5 Comprehensive Test Suite
└── README.md                    # Project Documentation
```

---

## ⚡ Quick Start Guide

### Option A: Open Live Web Application (Zero Setup)
Visit the live deployed application in any web browser:  
👉 **[https://ripple-map-1.onrender.com/](https://ripple-map-1.onrender.com/)**

### Option B: Run Locally on Your Machine
Double-click **`run_frontend.bat`** in the root folder, or run:

```powershell
python -m http.server 5173 --bind 127.0.0.1
```
Then visit **`http://127.0.0.1:5173`**.

*(You can also directly double-click `index.html` to run in any browser with zero dependencies).*

### Option C: Build & Run Java Backend Tests
```powershell
mvn clean test
```

---

## 🎓 Academic Viva Defense Guide

### Q1: What is the primary difference between Max-Path and Sum-Path models?
- **Max-Path** identifies the single most catastrophic route of failure to any node ($\max_{p \in \text{paths}} \text{Impact}(p)$). It highlights peak critical vulnerability.
- **Sum-Path** calculates cumulative stress across converging dependencies ($\sum \text{Impact}(p_i)$) with an upper bound of 1.0, modeling systemic multi-vector collapse.

### Q2: How does the Greedy Mitigation Advisor make recommendations?
The Advisor utilizes a greedy heuristic over candidate components. For each available node, it temporarily applies a shield, runs a complete cascade propagation, and measures the total network impact score reduction. It selects the candidate yielding the highest marginal reduction, repeats for budget $K$, and achieves substantial risk mitigation in polynomial time $O(K \cdot V \cdot (V + E))$.

### Q3: Why is Tarjan's SCC critical in dependency networks?
In dependency mapping, cycles represent circular dependencies (e.g., Service A requires Service B which requires Service A). Under failure conditions, circular dependencies cause recursive deadlock or infinite retry storms. Tarjan's SCC identifies all cyclic clusters in optimal $O(V + E)$ time.

---

## 📄 License
This project is open-source under the MIT License.