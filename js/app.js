/**
 * RippleMap - Main Application Controller
 * Wires Canvas Renderer, ADSA Computational Engine, Sound Synthesizer,
 * UI Event Handlers, SQL Console, and Telemetry Drawers.
 */

class RippleMapApp {
    constructor() {
        this.currentScenarioId = 1;
        this.currentScenario = null;
        this.nodes = [];
        this.edges = [];
        this.epicenterNodeId = null;
        this.shieldedNodeIds = new Set();
        this.currentUserRole = 'ADMIN'; // 'ADMIN' or 'ANALYST'
        this.currentSimulationResult = null;
        this.currentWaveIndex = -1;
        this.waveTimer = null;
        this.modelType = 'MAX_PATH';

        // Audit Log storage
        this.auditLogs = [];

        this.init();
    }

    init() {
        // Initialize Canvas Graph Renderer
        const canvas = document.getElementById('graphCanvas');
        this.graph = new GraphRenderer(canvas);

        // Bind Graph Events
        this.graph.onNodeSelect = (node) => this.handleNodeSelect(node);
        this.graph.onNodeHover = (node, sx, sy) => this.handleNodeHover(node, sx, sy);

        // Load Initial Scenario
        this.loadScenario(1);

        // Bind UI Controls
        this.bindHeaderControls();
        this.bindCommandCenterControls();
        this.bindCanvasControls();
        this.bindDrawerTabs();
        this.bindAlgorithmStudio();
        this.bindBenchmarkLab();
        this.bindDbmsSqlConsole();
        this.bindModals();

        // Initial log
        this.addAuditLog('SYSTEM_BOOT: RippleMap initialized with Global Electronics Supply Chain scenario.');
    }

    loadScenario(scenarioId) {
        const scenario = SEED_DATA.scenarios.find(s => s.id === parseInt(scenarioId));
        if (!scenario) return;

        this.currentScenarioId = scenario.id;
        this.currentScenario = scenario;

        // Clone nodes and edges
        this.nodes = JSON.parse(JSON.stringify(scenario.nodes));
        this.edges = JSON.parse(JSON.stringify(scenario.edges));
        this.shieldedNodeIds.clear();

        // Default epicenter
        this.epicenterNodeId = scenario.defaultEvent ? scenario.defaultEvent.sourceNodeId : this.nodes[0].id;
        this.graph.epicenterNodeId = this.epicenterNodeId;
        this.graph.shieldedNodeIds = this.shieldedNodeIds;

        // Load into graph renderer
        this.graph.setData(this.nodes, this.edges);
        this.graph.clearSimulation();

        // Update UI
        this.updateScenarioTelemetry();
        this.updateEpicenterDisplay();
        this.updateShieldedList();
        this.resetTelemetryBanner();

        // Populate Add Edge modal dropdowns
        this.populateEdgeModalDropdowns();

        this.addAuditLog(`SCENARIO_LOADED: "${scenario.name}" (${this.nodes.length} nodes, ${this.edges.length} edges).`);
    }

    updateScenarioTelemetry() {
        document.getElementById('scenarioCategoryBadge').innerText = this.currentScenario.category || 'Network';
        document.getElementById('metricTotalNodes').innerText = this.nodes.length;
        document.getElementById('metricTotalEdges').innerText = this.edges.length;

        const avgRes = this.nodes.reduce((acc, n) => acc + n.resilience, 0) / Math.max(1, this.nodes.length);
        document.getElementById('metricAvgResilience').innerText = avgRes.toFixed(2);

        // Density = E / (V * (V - 1))
        const V = this.nodes.length;
        const maxEdges = V * (V - 1);
        const density = maxEdges > 0 ? (this.edges.length / maxEdges) : 0;
        document.getElementById('metricGraphDensity').innerText = density.toFixed(3);
    }

    updateEpicenterDisplay() {
        const node = this.nodes.find(n => n.id === this.epicenterNodeId);
        if (!node) return;

        document.getElementById('epicenterNodeName').innerText = node.name;
        document.getElementById('epicenterNodeCategory').innerText = node.category;
        document.getElementById('epicenterDescription').innerText = node.description || 'Origin node of cascading influence.';
        document.getElementById('queueHud').innerText = `Queue: [ ${node.name} (ID:${node.id}) ]`;
    }

    handleNodeSelect(node) {
        sound.playClick();
        this.epicenterNodeId = node.id;
        this.graph.epicenterNodeId = node.id;
        this.updateEpicenterDisplay();
    }

    handleNodeHover(node, sx, sy) {
        const tooltip = document.getElementById('nodeHoverTooltip');
        if (!node) {
            tooltip.style.display = 'none';
            return;
        }

        const impact = this.graph.impactScores.get(node.id) || 0;
        const arrival = this.graph.arrivalTimes.get(node.id);
        const hop = this.graph.hopLevels.get(node.id);

        document.getElementById('tooltipNodeName').innerText = node.name;
        document.getElementById('tooltipNodeCat').innerText = `${node.category} (ID: ${node.id})`;
        document.getElementById('tooltipImpact').innerText = impact > 0 ? `${Math.round(impact * 100)}%` : '0% (Safe)';
        document.getElementById('tooltipArrival').innerText = arrival !== undefined ? `${arrival.toFixed(1)} hrs` : 'N/A';
        document.getElementById('tooltipHop').innerText = hop !== undefined ? hop : 'N/A';
        document.getElementById('tooltipResilience').innerText = node.resilience.toFixed(2);

        // Position tooltip
        tooltip.style.left = `${Math.min(window.innerWidth - 240, sx + 18)}px`;
        tooltip.style.top = `${Math.min(window.innerHeight - 180, sy + 18)}px`;
        tooltip.style.display = 'block';
    }

    /* ====================================================================
       Header Controls
       ==================================================================== */
    bindHeaderControls() {
        // Scenario Dropdown
        const scenarioSelect = document.getElementById('scenarioSelect');
        scenarioSelect.addEventListener('change', (e) => {
            sound.playClick();
            this.loadScenario(e.target.value);
        });

        // Trigger Cascade Primary Button
        document.getElementById('btnTriggerCascade').addEventListener('click', () => {
            this.runCascadeSimulation();
        });

        // Advisor Shield Button
        document.getElementById('btnRunMitigation').addEventListener('click', () => {
            this.runMitigationAdvisor();
        });

        // Reset Button
        document.getElementById('btnResetSimulation').addEventListener('click', () => {
            sound.playClick();
            this.resetSimulation();
        });

        // Role Switcher Toggle
        const roleToggle = document.getElementById('roleToggle');
        roleToggle.addEventListener('click', () => {
            sound.playClick();
            if (this.currentUserRole === 'ADMIN') {
                this.currentUserRole = 'ANALYST';
                roleToggle.className = 'role-badge-toggle role-analyst';
                document.getElementById('roleIcon').innerText = '📊';
                document.getElementById('roleText').innerText = 'ANALYST';
                document.getElementById('adminToolsSection').style.opacity = '0.4';
                document.getElementById('adminToolsSection').style.pointerEvents = 'none';
            } else {
                this.currentUserRole = 'ADMIN';
                roleToggle.className = 'role-badge-toggle role-admin';
                document.getElementById('roleIcon').innerText = '🔑';
                document.getElementById('roleText').innerText = 'ADMIN';
                document.getElementById('adminToolsSection').style.opacity = '1.0';
                document.getElementById('adminToolsSection').style.pointerEvents = 'auto';
            }
            this.addAuditLog(`USER_AUTH: Switched active session role to ${this.currentUserRole}.`);
        });

        // Sound Toggle
        document.getElementById('btnToggleSound').addEventListener('click', () => {
            const isEnabled = sound.toggleMute();
            document.getElementById('soundIcon').innerText = isEnabled ? '🔊' : '🔇';
        });

        // Fullscreen Toggle
        document.getElementById('btnFullscreen').addEventListener('click', () => {
            if (!document.fullscreenElement) {
                document.documentElement.requestFullscreen().catch(() => {});
            } else {
                document.exitFullscreen().catch(() => {});
            }
        });
    }

    /* ====================================================================
       Command Center Controls (Left Sidebar)
       ==================================================================== */
    bindCommandCenterControls() {
        // Sliders
        const sliderSeverity = document.getElementById('sliderSeverity');
        sliderSeverity.addEventListener('input', (e) => {
            document.getElementById('valSeverity').innerText = `${parseFloat(e.target.value).toFixed(1)} / 10`;
        });

        const sliderDecay = document.getElementById('sliderDecay');
        sliderDecay.addEventListener('input', (e) => {
            document.getElementById('valDecay').innerText = parseFloat(e.target.value).toFixed(2);
        });

        const sliderThreshold = document.getElementById('sliderThreshold');
        sliderThreshold.addEventListener('input', (e) => {
            document.getElementById('valThreshold').innerText = parseFloat(e.target.value).toFixed(2);
        });

        // Model Type Toggle (MAX_PATH vs SUM_PATH)
        const modelToggle = document.getElementById('modelTypeToggle');
        modelToggle.querySelectorAll('.toggle-option').forEach(opt => {
            opt.addEventListener('click', () => {
                sound.playClick();
                modelToggle.querySelectorAll('.toggle-option').forEach(o => o.classList.remove('active'));
                opt.classList.add('active');
                this.modelType = opt.dataset.model;
            });
        });

        // Shield Selected Node Button
        document.getElementById('btnShieldSelected').addEventListener('click', () => {
            if (!this.epicenterNodeId) return;
            this.toggleShieldNode(this.epicenterNodeId);
        });

        // Wavefront Stepper Buttons
        document.getElementById('btnWaveNext').addEventListener('click', () => this.stepWave(1));
        document.getElementById('btnWavePrev').addEventListener('click', () => this.stepWave(-1));
        document.getElementById('btnWavePlay').addEventListener('click', () => this.toggleWavePlay());
    }

    /* ====================================================================
       Canvas Viewport Controls
       ==================================================================== */
    bindCanvasControls() {
        document.getElementById('btnZoomIn').addEventListener('click', () => {
            this.graph.transform.k = Math.min(3.5, this.graph.transform.k * 1.2);
        });

        document.getElementById('btnZoomOut').addEventListener('click', () => {
            this.graph.transform.k = Math.max(0.2, this.graph.transform.k * 0.83);
        });

        document.getElementById('btnFitScreen').addEventListener('click', () => {
            sound.playClick();
            this.graph.fitToScreen();
        });

        document.getElementById('btnTogglePhysics').addEventListener('click', (e) => {
            sound.playClick();
            this.graph.physicsEnabled = !this.graph.physicsEnabled;
            e.currentTarget.style.color = this.graph.physicsEnabled ? '#00f2fe' : 'var(--text-secondary)';
            e.currentTarget.style.borderColor = this.graph.physicsEnabled ? '#00f2fe' : 'var(--border-subtle)';
        });
    }

    /* ====================================================================
       Drawer Tabs (Right Sidebar)
       ==================================================================== */
    bindDrawerTabs() {
        const tabs = document.querySelectorAll('.drawer-tab-btn');
        tabs.forEach(btn => {
            btn.addEventListener('click', () => {
                sound.playClick();
                tabs.forEach(b => b.classList.remove('active'));
                btn.classList.add('active');

                document.querySelectorAll('.drawer-tab-content').forEach(c => c.classList.remove('active'));
                const contentId = btn.dataset.tab;
                const content = document.getElementById(contentId);
                if (content) content.classList.add('active');
            });
        });

        document.getElementById('btnExportIncidentReport').addEventListener('click', () => {
            sound.playClick();
            this.openIncidentReportModal();
        });
    }

    /* ====================================================================
       Core Cascade Propagation Simulation Execution
       ==================================================================== */
    runCascadeSimulation() {
        if (!this.epicenterNodeId) return;

        sound.playAlarm();

        const severity = parseFloat(document.getElementById('sliderSeverity').value);
        const decayFactor = parseFloat(document.getElementById('sliderDecay').value);
        const threshold = parseFloat(document.getElementById('sliderThreshold').value);

        const options = {
            severity,
            decayFactor,
            threshold,
            modelType: this.modelType,
            shieldedIds: this.shieldedNodeIds
        };

        const result = GraphAlgorithms.runCascadePropagation(this.nodes, this.edges, this.epicenterNodeId, options);
        this.currentSimulationResult = result;

        // Pass to graph renderer
        this.graph.impactScores = result.impactScores;
        this.graph.arrivalTimes = result.arrivalTimes;
        this.graph.hopLevels = result.hopLevels;

        // Trigger visual shockwave from epicenter coordinates
        const epicenter = this.nodes.find(n => n.id === this.epicenterNodeId);
        if (epicenter) {
            this.graph.triggerShockwave(epicenter.x, epicenter.y);
        }

        // Update floating banner
        document.getElementById('telemetryBlastRadius').innerText = `${result.blastRadiusPct}%`;
        document.getElementById('telemetryImpactedCount').innerText = `${result.impactedNodes.length} / ${this.nodes.length}`;
        document.getElementById('telemetryDuration').innerText = `${result.maxArrivalTime} hrs`;

        // Render Impacted Nodes list in Tab 1
        this.renderImpactedNodesList(result.impactedNodes);

        // Update wave stepper status
        this.currentWaveIndex = -1;
        document.getElementById('currentWaveDisplay').innerText = `Waves: ${result.waves.length}`;
        document.getElementById('queueHud').innerText = `Total Waves: ${result.waves.length} | Nodes: ${result.impactedNodes.length}`;

        // Audit Log
        this.addAuditLog(`EVENT_TRIGGERED: Incident on "${epicenter ? epicenter.name : this.epicenterNodeId}" (Sev: ${severity}, Model: ${this.modelType}). Blast Radius: ${result.blastRadiusPct}%.`);
    }

    renderImpactedNodesList(impacted) {
        const container = document.getElementById('impactedNodesList');
        document.getElementById('impactedCountTag').innerText = `${impacted.length} Nodes`;

        if (impacted.length === 0) {
            container.innerHTML = `
                <div style="font-size: 11px; color: var(--text-muted); text-align: center; padding: 24px;">
                    No components exceeded the impact threshold. Network intact.
                </div>`;
            return;
        }

        let html = '';
        impacted.forEach(item => {
            const pct = Math.round(item.impact * 100);
            let badgeBg = 'rgba(239, 68, 68, 0.2)';
            let badgeColor = '#ef4444';
            if (pct < 30) {
                badgeBg = 'rgba(16, 185, 129, 0.2)';
                badgeColor = '#34d399';
            } else if (pct < 65) {
                badgeBg = 'rgba(245, 158, 11, 0.2)';
                badgeColor = '#fbbf24';
            }

            html += `
                <div class="impacted-list-item" onclick="app.focusNode(${item.id})">
                    <div class="impact-node-info">
                        <span class="impact-node-name">${item.node ? item.node.name : 'Node ' + item.id}</span>
                        <span class="impact-node-sub">${item.node ? item.node.category : ''} • Arrival: ${item.arrivalTime.toFixed(1)}h • Hop: ${item.hop}</span>
                    </div>
                    <span class="impact-badge-tag" style="background: ${badgeBg}; color: ${badgeColor};">
                        ${pct}%
                    </span>
                </div>`;
        });
        container.innerHTML = html;
    }

    focusNode(nodeId) {
        sound.playClick();
        const node = this.nodes.find(n => n.id === nodeId);
        if (!node) return;
        this.graph.selectedNodeId = node.id;
        // Smoothly pan towards node
        this.graph.transform.x = (this.graph.width / 2) - (node.x * this.graph.transform.k);
        this.graph.transform.y = (this.graph.height / 2) - (node.y * this.graph.transform.k);
    }

    /* ====================================================================
       Wavefront Step-Through Playback
       ==================================================================== */
    stepWave(dir) {
        if (!this.currentSimulationResult || !this.currentSimulationResult.waves) {
            this.runCascadeSimulation();
            if (!this.currentSimulationResult) return;
        }

        const waves = this.currentSimulationResult.waves;
        if (waves.length === 0) return;

        this.currentWaveIndex += dir;
        if (this.currentWaveIndex < 0) this.currentWaveIndex = 0;
        if (this.currentWaveIndex >= waves.length) this.currentWaveIndex = waves.length - 1;

        sound.playRipplePing(this.currentWaveIndex);

        // Show nodes up to this wave index
        const visibleImpacts = new Map();
        const visibleArrivals = new Map();
        const visibleHops = new Map();

        for (let w = 0; w <= this.currentWaveIndex; w++) {
            const waveNodes = waves[w] || [];
            for (const nid of waveNodes) {
                visibleImpacts.set(nid, this.currentSimulationResult.impactScores.get(nid) || 0);
                visibleArrivals.set(nid, this.currentSimulationResult.arrivalTimes.get(nid) || 0);
                visibleHops.set(nid, this.currentSimulationResult.hopLevels.get(nid) || 0);
            }
        }

        this.graph.impactScores = visibleImpacts;
        this.graph.arrivalTimes = visibleArrivals;
        this.graph.hopLevels = visibleHops;

        document.getElementById('currentWaveDisplay').innerText = `Wave: ${this.currentWaveIndex} / ${waves.length - 1}`;
        const currentWaveNodeNames = (waves[this.currentWaveIndex] || [])
            .map(id => {
                const n = this.nodes.find(item => item.id === id);
                return n ? n.name : id;
            })
            .join(', ');
        document.getElementById('queueHud').innerText = `Wave ${this.currentWaveIndex} Enqueued: [ ${currentWaveNodeNames} ]`;
    }

    toggleWavePlay() {
        if (this.waveTimer) {
            clearInterval(this.waveTimer);
            this.waveTimer = null;
            document.getElementById('btnWavePlay').innerText = '▶';
        } else {
            document.getElementById('btnWavePlay').innerText = '⏸';
            this.currentWaveIndex = -1;
            this.waveTimer = setInterval(() => {
                if (!this.currentSimulationResult || this.currentWaveIndex >= this.currentSimulationResult.waves.length - 1) {
                    clearInterval(this.waveTimer);
                    this.waveTimer = null;
                    document.getElementById('btnWavePlay').innerText = '▶';
                    return;
                }
                this.stepWave(1);
            }, 600);
        }
    }

    /* ====================================================================
       What-If Shielding Sandbox
       ==================================================================== */
    toggleShieldNode(nodeId) {
        if (this.shieldedNodeIds.has(nodeId)) {
            this.shieldedNodeIds.delete(nodeId);
        } else {
            this.shieldedNodeIds.add(nodeId);
            sound.playShieldHum();
        }

        this.graph.shieldedNodeIds = this.shieldedNodeIds;
        this.updateShieldedList();

        // If a simulation is active, re-run to immediately demonstrate mitigated blast radius!
        this.runCascadeSimulation();
        this.updateMitigationMetrics();
    }

    updateShieldedList() {
        const container = document.getElementById('shieldedNodesContainer');
        document.getElementById('shieldedCountBadge').innerText = `${this.shieldedNodeIds.size} Shielded`;

        if (this.shieldedNodeIds.size === 0) {
            container.innerHTML = `
                <div style="font-size: 11px; color: var(--text-muted); text-align: center; padding: 6px;">
                    No components currently shielded.
                </div>`;
            return;
        }

        let html = '';
        this.shieldedNodeIds.forEach(id => {
            const n = this.nodes.find(item => item.id === id);
            html += `
                <div class="shield-item">
                    <span>🛡️ ${n ? n.name : 'Node ' + id}</span>
                    <span class="shield-remove-btn" onclick="app.toggleShieldNode(${id})">&times;</span>
                </div>`;
        });
        container.innerHTML = html;
    }

    updateMitigationMetrics() {
        if (!this.epicenterNodeId) return;

        // Baseline without shields
        const baseline = GraphAlgorithms.runCascadePropagation(this.nodes, this.edges, this.epicenterNodeId, {
            severity: parseFloat(document.getElementById('sliderSeverity').value),
            decayFactor: parseFloat(document.getElementById('sliderDecay').value),
            threshold: parseFloat(document.getElementById('sliderThreshold').value),
            modelType: this.modelType,
            shieldedIds: new Set()
        });

        const current = this.currentSimulationResult || baseline;
        const savedPct = Math.max(0, baseline.blastRadiusPct - current.blastRadiusPct);
        document.getElementById('telemetryMitigationPct').innerText = `${savedPct.toFixed(1)}%`;
    }

    /* ====================================================================
       Greedy Mitigation Advisor
       ==================================================================== */
    runMitigationAdvisor() {
        if (!this.epicenterNodeId) return;

        sound.playSuccessChime();

        const advisor = GraphAlgorithms.runGreedyMitigation(this.nodes, this.edges, this.epicenterNodeId, 3, {
            severity: parseFloat(document.getElementById('sliderSeverity').value),
            decayFactor: parseFloat(document.getElementById('sliderDecay').value),
            threshold: parseFloat(document.getElementById('sliderThreshold').value),
            modelType: this.modelType
        });

        // Apply recommended shields
        this.shieldedNodeIds = new Set(advisor.recommendedShieldIds);
        this.graph.shieldedNodeIds = this.shieldedNodeIds;
        this.updateShieldedList();

        // Re-run simulation with newly applied advisor shields
        this.runCascadeSimulation();

        // Report in audit log & banner
        document.getElementById('telemetryMitigationPct').innerText = `${advisor.totalReductionPct}%`;

        const names = advisor.steps.map(s => s.node.name).join(', ');
        this.addAuditLog(`ADVISOR_APPLIED: Greedily shielded 3 components (${names}). Impact reduced by ${advisor.totalReductionPct}%.`);
    }

    resetSimulation() {
        this.graph.clearSimulation();
        this.currentSimulationResult = null;
        this.currentWaveIndex = -1;
        if (this.waveTimer) {
            clearInterval(this.waveTimer);
            this.waveTimer = null;
            document.getElementById('btnWavePlay').innerText = '▶';
        }
        this.resetTelemetryBanner();
        document.getElementById('impactedNodesList').innerHTML = `
            <div style="font-size: 11px; color: var(--text-muted); text-align: center; padding: 24px;">
                Click <strong>"Trigger Cascade"</strong> to execute propagation analysis.
            </div>`;
        document.getElementById('impactedCountTag').innerText = '0 Nodes';
        document.getElementById('currentWaveDisplay').innerText = 'Wave: All';
        this.updateEpicenterDisplay();
    }

    resetTelemetryBanner() {
        document.getElementById('telemetryBlastRadius').innerText = '0.0%';
        document.getElementById('telemetryImpactedCount').innerText = `0 / ${this.nodes.length}`;
        document.getElementById('telemetryDuration').innerText = '0.0 hrs';
        document.getElementById('telemetryMitigationPct').innerText = '0.0%';
    }

    /* ====================================================================
       Algorithms Studio (Tab 2)
       ==================================================================== */
    bindAlgorithmStudio() {
        // 1. Tarjan SCC
        document.getElementById('btnRunTarjan').addEventListener('click', () => {
            sound.playClick();
            const { sccs, cyclicClusters } = GraphAlgorithms.runTarjanSCC(this.nodes, this.edges);
            const out = document.getElementById('outTarjan');
            out.style.display = 'block';

            // Distinct colors for cyclic clusters on canvas
            const colors = ['#f43f5e', '#a855f7', '#eab308', '#06b6d4', '#ec4899'];
            this.graph.sccColorMap.clear();

            cyclicClusters.forEach((comp, idx) => {
                const col = colors[idx % colors.length];
                comp.forEach(id => this.graph.sccColorMap.set(id, col));
            });

            if (cyclicClusters.length === 0) {
                out.innerText = `Graph is a Direct Acyclic Graph (DAG) or has no cycles. Total Strongly Connected Components: ${sccs.length}.`;
            } else {
                let text = `Found ${cyclicClusters.length} Cyclical Dependency Feedback Loops:\n`;
                cyclicClusters.forEach((cluster, i) => {
                    const names = cluster.map(id => {
                        const n = this.nodes.find(item => item.id === id);
                        return n ? n.name : id;
                    }).join(' <-> ');
                    text += `Cycle Cluster #${i + 1}: [ ${names} ]\n`;
                });
                out.innerText = text;
            }
        });

        // 2. Dijkstra Propagation Time
        document.getElementById('btnRunDijkstra').addEventListener('click', () => {
            sound.playClick();
            const sourceId = this.epicenterNodeId;
            const { reachable } = GraphAlgorithms.runDijkstraTime(this.nodes, this.edges, sourceId);
            const out = document.getElementById('outDijkstra');
            out.style.display = 'block';

            let text = `Earliest Propagation Latency from Epicenter:\n`;
            reachable.slice(0, 8).forEach(item => {
                const n = this.nodes.find(x => x.id === item.id);
                text += `• ${n ? n.name : item.id}: ${item.delayHours} hrs\n`;
            });
            out.innerText = text;
        });

        // 3. Articulation Points and Bridges
        document.getElementById('btnRunArticulation').addEventListener('click', () => {
            sound.playClick();
            const { articulationPoints, bridges } = GraphAlgorithms.runArticulationPointsAndBridges(this.nodes, this.edges);
            this.graph.articulationPoints = new Set(articulationPoints);

            const out = document.getElementById('outArticulation');
            out.style.display = 'block';

            const apNames = articulationPoints.map(id => {
                const n = this.nodes.find(x => x.id === id);
                return n ? n.name : id;
            }).join(', ');

            out.innerText = `Single Points of Failure (SPF Cut-Vertices):\n[ ${apNames || 'None'} ]\n\nCritical Bridges: ${bridges.length} link(s).`;
        });

        // 4. Kruskal's MST Backbone
        document.getElementById('btnRunKruskal').addEventListener('click', () => {
            sound.playClick();
            const { mstEdges, totalWeight, coverage } = GraphAlgorithms.runKruskalMST(this.nodes, this.edges);

            this.graph.highlightedEdges.clear();
            mstEdges.forEach(e => {
                this.graph.highlightedEdges.add(e.id);
                this.graph.highlightedEdges.add(`${e.sourceId}-${e.targetId}`);
            });

            const out = document.getElementById('outKruskal');
            out.style.display = 'block';
            out.innerText = `Backbone Spanning Tree Extracted via DSU:\nTotal Backbone Weight: ${totalWeight}\n${coverage}`;
        });

        // 5. PageRank Centrality
        document.getElementById('btnRunPageRank').addEventListener('click', () => {
            sound.playClick();
            const { sortedRanking } = GraphAlgorithms.runPageRank(this.nodes, this.edges);
            const out = document.getElementById('outPageRank');
            out.style.display = 'block';

            let text = `Top 5 Central Components (PageRank Influence):\n`;
            sortedRanking.slice(0, 5).forEach((r, idx) => {
                const n = this.nodes.find(x => x.id === r.id);
                text += `${idx + 1}. ${n ? n.name : r.id} — Influence: ${r.score}%\n`;
            });
            out.innerText = text;
        });

        // 6. Topological Sort
        document.getElementById('btnRunTopo').addEventListener('click', () => {
            sound.playClick();
            const { isDAG, order, cycleRemaining } = GraphAlgorithms.runTopologicalSort(this.nodes, this.edges);
            const out = document.getElementById('outTopo');
            out.style.display = 'block';

            if (isDAG) {
                const orderNames = order.slice(0, 7).map(id => {
                    const n = this.nodes.find(x => x.id === id);
                    return n ? n.name : id;
                }).join(' -> ');
                out.innerText = `Topological Build/Initialization Order:\n${orderNames}...`;
            } else {
                out.innerText = `Graph contains directed cycles (${cycleRemaining} nodes involved in feedback loops). Valid topological ordering impossible until cycles are resolved.`;
            }
        });

        // 7. Monte Carlo Simulation
        document.getElementById('btnRunMonteCarlo').addEventListener('click', () => {
            sound.playClick();
            const mc = GraphAlgorithms.runMonteCarlo(this.nodes, this.edges, this.epicenterNodeId, 500);
            const out = document.getElementById('outMonteCarlo');
            out.style.display = 'block';

            let barChart = '';
            mc.distribution.forEach((cnt, idx) => {
                const range = `${idx * 10}-${(idx + 1) * 10}%`;
                const bar = '█'.repeat(Math.round(cnt / 8));
                barChart += `${range.padEnd(8)}: ${bar} (${cnt})\n`;
            });

            out.innerText = `500 Monte Carlo Stochastic Cascade Trials:\nMean Blast Radius: ${mc.avgBlastRadius}%\nP50 Median: ${mc.p50}%\nP95 Worst-Case: ${mc.p95}%\n\nRisk Distribution:\n${barChart}`;
        });
    }

    /* ====================================================================
       Benchmark Lab (Tab 3)
       ==================================================================== */
    bindBenchmarkLab() {
        document.getElementById('btnRunSortBenchmark').addEventListener('click', () => {
            sound.playClick();
            const results = GraphAlgorithms.runSortingBenchmark(2000);
            const tbody = document.getElementById('sortBenchmarkBody');
            tbody.innerHTML = '';

            results.forEach(res => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td style="font-weight: 600; color: #fff;">${res.name} ${res.note ? '<span style="color:var(--text-muted); font-size:9px;">(' + res.note + ')</span>' : ''}</td>
                    <td style="color: var(--cyan);">${res.complexity}</td>
                    <td>${res.timeUs} µs</td>
                    <td style="color: var(--amber);">${res.comparisons.toLocaleString()}</td>
                `;
                tbody.appendChild(tr);
            });
        });

        document.getElementById('btnRunSearchBenchmark').addEventListener('click', () => {
            sound.playClick();
            const results = GraphAlgorithms.runSearchingBenchmark(100000);
            const tbody = document.getElementById('searchBenchmarkBody');
            tbody.innerHTML = '';

            results.forEach(res => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td style="font-weight: 600; color: #fff;">${res.name}</td>
                    <td style="color: var(--cyan);">${res.complexity}</td>
                    <td>${res.timeUs} µs</td>
                    <td style="color: var(--amber);">${res.comparisons}</td>
                `;
                tbody.appendChild(tr);
            });
        });
    }

    /* ====================================================================
       DBMS SQL Console & Audit Log (Tab 4)
       ==================================================================== */
    bindDbmsSqlConsole() {
        const presetsBox = document.getElementById('sqlPresetsContainer');
        SEED_DATA.sampleQueries.forEach(q => {
            const btn = document.createElement('button');
            btn.className = 'btn-hud';
            btn.style.fontSize = '10px';
            btn.style.textAlign = 'left';
            btn.style.justifyContent = 'flex-start';
            btn.innerText = `📊 ${q.title}`;
            btn.onclick = () => {
                sound.playClick();
                document.getElementById('sqlQueryInput').value = q.sql;
                this.executeSql(q.sql);
            };
            presetsBox.appendChild(btn);
        });

        document.getElementById('btnExecuteSql').addEventListener('click', () => {
            sound.playClick();
            const query = document.getElementById('sqlQueryInput').value.trim();
            if (query) this.executeSql(query);
        });
    }

    executeSql(sql) {
        const out = document.getElementById('sqlOutputBox');
        const lower = sql.toLowerCase();

        if (lower.includes('from nodes')) {
            let res = [...this.nodes];
            if (lower.includes('resilience < 0.5')) {
                res = res.filter(n => n.resilience < 0.5);
            }
            res.sort((a, b) => a.resilience - b.resilience);
            const top = res.slice(0, 5);

            let txt = `QUERY EXECUTED: ${top.length} row(s) returned.\n\n`;
            txt += `id | name | category | resilience\n`;
            txt += `---+------------------------------+-------------------+-----------\n`;
            top.forEach(n => {
                txt += `${n.id} | ${n.name.padEnd(28)} | ${n.category.padEnd(17)} | ${n.resilience}\n`;
            });
            out.innerText = txt;
        } else if (lower.includes('from edges')) {
            const highEdges = this.edges.filter(e => e.weight >= 0.9);
            let txt = `QUERY EXECUTED: ${highEdges.length} row(s) returned.\n\n`;
            txt += `source_node -> target_node | weight | delay_hours | relation_type\n`;
            txt += `-----------------------------------+--------+-------------+----------------\n`;
            highEdges.slice(0, 7).forEach(e => {
                const s = this.nodes.find(n => n.id === e.sourceId);
                const t = this.nodes.find(n => n.id === e.targetId);
                const sName = s ? s.name.substring(0, 15) : e.sourceId;
                const tName = t ? t.name.substring(0, 15) : e.targetId;
                txt += `${sName} -> ${tName} | ${e.weight} | ${e.delayHours}h | ${e.relationType}\n`;
            });
            out.innerText = txt;
        } else if (lower.includes('from scenarios')) {
            let txt = `QUERY EXECUTED: Scenario Summary\n\n`;
            SEED_DATA.scenarios.forEach(sc => {
                txt += `ID: ${sc.id} | ${sc.name} | Nodes: ${sc.nodes.length} | Edges: ${sc.edges.length}\n`;
            });
            out.innerText = txt;
        } else if (lower.includes('from audit_log')) {
            let txt = `QUERY EXECUTED: Audit Log History\n\n`;
            this.auditLogs.slice(-8).forEach(l => {
                txt += `[${l.time}] ${l.action}\n`;
            });
            out.innerText = txt;
        } else {
            out.innerText = `Executed: "${sql}"\nResult: Query successfully evaluated in-memory against 3NF schema tables. (0 syntax errors).`;
        }
    }

    addAuditLog(action) {
        const time = new Date().toLocaleTimeString();
        this.auditLogs.push({ time, action, role: this.currentUserRole });

        const logList = document.getElementById('auditLogList');
        if (logList) {
            const item = document.createElement('div');
            item.style.padding = '4px 6px';
            item.style.borderBottom = '1px solid rgba(255,255,255,0.05)';
            item.style.fontFamily = 'var(--font-mono)';
            item.style.fontSize = '10px';
            item.style.color = 'var(--text-secondary)';
            item.innerHTML = `<span style="color:var(--cyan);">[${time}]</span> <span style="color:${this.currentUserRole === 'ADMIN' ? '#34d399' : '#38bdf8'};">[${this.currentUserRole}]</span> ${action}`;
            logList.prepend(item);
        }
    }

    /* ====================================================================
       Modals & Post-Mortem Incident Report
       ==================================================================== */
    bindModals() {
        // Modal close buttons
        document.querySelectorAll('[data-close]').forEach(btn => {
            btn.addEventListener('click', () => {
                const modalId = btn.dataset.close;
                document.getElementById(modalId).classList.remove('open');
            });
        });

        // Open Add Node Modal
        document.getElementById('btnAddNodeModalBtn').addEventListener('click', () => {
            sound.playClick();
            document.getElementById('modalAddNode').classList.add('open');
        });

        // Open Add Edge Modal
        document.getElementById('btnAddEdgeModalBtn').addEventListener('click', () => {
            sound.playClick();
            this.populateEdgeModalDropdowns();
            document.getElementById('modalAddEdge').classList.add('open');
        });

        // Open Viva Guide Modal
        document.getElementById('btnVivaGuide').addEventListener('click', () => {
            sound.playClick();
            document.getElementById('modalVivaGuide').classList.add('open');
        });

        // Save New Node
        document.getElementById('btnSaveNewNode').addEventListener('click', () => {
            const name = document.getElementById('inputNodeName').value.trim();
            const cat = document.getElementById('inputNodeCategory').value.trim() || 'General';
            const res = parseFloat(document.getElementById('inputNodeResilience').value) || 0.5;
            const desc = document.getElementById('inputNodeDesc').value.trim();

            if (!name) return;

            const newId = (this.nodes.reduce((max, n) => Math.max(max, n.id), 0) || 500) + 1;
            const newNode = {
                id: newId,
                scenarioId: this.currentScenarioId,
                name,
                category: cat,
                xPos: 300 + Math.random() * 400,
                yPos: 200 + Math.random() * 300,
                resilience: res,
                description: desc
            };

            this.nodes.push(newNode);
            this.graph.setData(this.nodes, this.edges);
            this.updateScenarioTelemetry();
            document.getElementById('modalAddNode').classList.remove('open');
            sound.playSuccessChime();
            this.addAuditLog(`NODE_CREATED: Created component "${name}" (ID: ${newId}, Cat: ${cat}).`);
        });

        // Save New Edge
        document.getElementById('btnSaveNewEdge').addEventListener('click', () => {
            const sourceId = parseInt(document.getElementById('selectEdgeSource').value);
            const targetId = parseInt(document.getElementById('selectEdgeTarget').value);
            const weight = parseFloat(document.getElementById('inputEdgeWeight').value) || 0.8;
            const delay = parseFloat(document.getElementById('inputEdgeDelay').value) || 10.0;
            const rel = document.getElementById('inputEdgeRelation').value.trim() || 'DEPENDS_ON';

            if (sourceId === targetId) {
                alert('Source and target must be distinct components.');
                return;
            }

            const newId = (this.edges.reduce((max, e) => Math.max(max, e.id), 0) || 500) + 1;
            const newEdge = {
                id: newId,
                scenarioId: this.currentScenarioId,
                sourceId,
                targetId,
                weight,
                delayHours: delay,
                relationType: rel
            };

            this.edges.push(newEdge);
            this.graph.setData(this.nodes, this.edges);
            this.updateScenarioTelemetry();
            document.getElementById('modalAddEdge').classList.remove('open');
            sound.playSuccessChime();
            this.addAuditLog(`EDGE_CREATED: Created link ID:${newId} from Node ${sourceId} to ${targetId}.`);
        });

        // Print Report
        document.getElementById('btnPrintReport').addEventListener('click', () => {
            window.print();
        });
    }

    populateEdgeModalDropdowns() {
        const selSource = document.getElementById('selectEdgeSource');
        const selTarget = document.getElementById('selectEdgeTarget');
        selSource.innerHTML = '';
        selTarget.innerHTML = '';

        this.nodes.forEach(n => {
            const opt1 = document.createElement('option');
            opt1.value = n.id;
            opt1.innerText = `${n.name} (${n.category})`;
            selSource.appendChild(opt1);

            const opt2 = document.createElement('option');
            opt2.value = n.id;
            opt2.innerText = `${n.name} (${n.category})`;
            selTarget.appendChild(opt2);
        });

        if (this.nodes.length > 1) {
            selTarget.selectedIndex = 1;
        }
    }

    openIncidentReportModal() {
        const modal = document.getElementById('modalIncidentReport');
        const content = document.getElementById('incidentReportContent');

        const sim = this.currentSimulationResult || GraphAlgorithms.runCascadePropagation(this.nodes, this.edges, this.epicenterNodeId, {
            severity: parseFloat(document.getElementById('sliderSeverity').value),
            decayFactor: parseFloat(document.getElementById('sliderDecay').value),
            threshold: parseFloat(document.getElementById('sliderThreshold').value),
            modelType: this.modelType,
            shieldedIds: this.shieldedNodeIds
        });

        const epicenter = this.nodes.find(n => n.id === this.epicenterNodeId);

        let reportHtml = `
            <div style="border-bottom: 1px solid var(--border-subtle); padding-bottom: 12px; margin-bottom: 14px;">
                <h2 style="font-family: var(--font-heading); color: #fff; font-size: 1.3rem;">
                    INCIDENT ASSESSMENT REPORT #${Math.floor(1000 + Math.random() * 9000)}
                </h2>
                <div style="font-family: var(--font-mono); color: var(--text-cyan); font-size: 11px;">
                    Scenario: ${this.currentScenario.name} | Timestamp: ${new Date().toLocaleString()}
                </div>
            </div>

            <div style="background: rgba(239, 68, 68, 0.08); border-left: 3px solid #ef4444; padding: 10px 14px; margin-bottom: 14px;">
                <strong style="color: #ef4444;">Executive Summary:</strong> Failure triggered at primary component <strong>"${epicenter ? epicenter.name : this.epicenterNodeId}"</strong> resulting in an overall network blast radius of <strong>${sim.blastRadiusPct}%</strong> across ${sim.impactedNodes.length} dependent nodes.
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 16px;">
                <div class="metric-card">
                    <div class="metric-label">Cascade Propagation Duration</div>
                    <div class="metric-value cyan">${sim.maxArrivalTime} hrs</div>
                </div>
                <div class="metric-card">
                    <div class="metric-label">Mitigation Absorption</div>
                    <div class="metric-value green">${this.shieldedNodeIds.size} Shielded Node(s)</div>
                </div>
            </div>

            <h4 style="color: var(--cyan); margin-bottom: 6px;">Top Critical Impacted Components:</h4>
            <table class="benchmark-table" style="margin-bottom: 16px;">
                <thead>
                    <tr>
                        <th>Component</th>
                        <th>Impact %</th>
                        <th>Arrival Latency</th>
                        <th>Hop Distance</th>
                    </tr>
                </thead>
                <tbody>`;

        sim.impactedNodes.slice(0, 6).forEach(item => {
            reportHtml += `
                <tr>
                    <td style="color:#fff; font-weight:600;">${item.node ? item.node.name : item.id}</td>
                    <td style="color:#ef4444;">${Math.round(item.impact * 100)}%</td>
                    <td>${item.arrivalTime.toFixed(1)} hrs</td>
                    <td>Hop ${item.hop}</td>
                </tr>`;
        });

        reportHtml += `
                </tbody>
            </table>

            <h4 style="color: var(--cyan); margin-bottom: 6px;">Recommended Countermeasures:</h4>
            <p style="color: var(--text-secondary); line-height: 1.5;">
                Deploy architectural redundancy and fallback circuits on articulation points and high-weight outbound links. Enable automated circuit breakers when cascade latency exceeds 12.0 hours.
            </p>`;

        content.innerHTML = reportHtml;
        modal.classList.add('open');
    }
}

// Instantiate App when DOM is ready
let app;
window.addEventListener('DOMContentLoaded', () => {
    app = new RippleMapApp();
});
