/**
 * RippleMap - High-Performance Canvas 2D Graph Visualizer
 * Supports smooth Pan & Zoom, Force-Directed Physics, Animated Edge Flow,
 * Shockwave Wavefront Ripples, and Radar Minimap.
 */

class GraphRenderer {
    constructor(canvas, options = {}) {
        this.canvas = canvas;
        this.ctx = canvas.getContext('2d');

        // Transform state
        this.transform = {
            x: 50,
            y: 50,
            k: 0.95
        };

        // Graph data
        this.nodes = [];
        this.edges = [];
        this.nodeMap = new Map();

        // Visual states
        this.selectedNodeId = null;
        this.hoveredNodeId = null;
        this.epicenterNodeId = null;
        this.impactScores = new Map();
        this.arrivalTimes = new Map();
        this.hopLevels = new Map();
        this.shieldedNodeIds = new Set();
        this.articulationPoints = new Set();
        this.highlightedEdges = new Set(); // Set of edge id or "u-v"
        this.highlightedNodes = new Set();
        this.sccColorMap = new Map(); // nodeId -> color

        // Animation states
        this.particles = [];
        this.shockwaves = []; // { x, y, radius, maxRadius, alpha, color }
        this.physicsEnabled = false;
        this.physicsIterations = 0;
        this.isDragging = false;
        this.draggedNode = null;
        this.dragStart = { x: 0, y: 0 };
        this.lastMouse = { x: 0, y: 0 };

        // Radar minimap
        this.radarAngle = 0;

        // Color palettes
        this.categoryColors = {
            "Raw Material": "#f59e0b",
            "Primary Source": "#f59e0b",
            "Energy": "#f59e0b",
            "Manufacturing": "#00f2fe",
            "Core Service": "#00f2fe",
            "Power Grid": "#00f2fe",
            "Assembly": "#6366f1",
            "Gateway": "#6366f1",
            "Water Grid": "#38bdf8",
            "Logistics Hub": "#10b981",
            "Logistics": "#10b981",
            "Search Tier": "#10b981",
            "Transportation": "#10b981",
            "Distribution": "#06b6d4",
            "Messaging Tier": "#06b6d4",
            "Healthcare": "#ec4899",
            "Retail": "#f43f5e",
            "External Partner": "#f43f5e",
            "Emergency": "#ef4444",
            "Telecom": "#a855f7",
            "Security": "#a855f7",
            "AI/Security": "#a855f7",
            "Regulators": "#a855f7",
            "Financial Market": "#eab308",
            "Commercial": "#eab308",
            "Civic": "#64748b",
            "Default": "#818cf8"
        };

        this.initCanvasSize();
        this.bindEvents();
        this.startLoop();
    }

    initCanvasSize() {
        const rect = this.canvas.parentElement.getBoundingClientRect();
        this.width = rect.width;
        this.height = rect.height;
        this.dpr = window.devicePixelRatio || 1;

        this.canvas.width = this.width * this.dpr;
        this.canvas.height = this.height * this.dpr;
        this.canvas.style.width = `${this.width}px`;
        this.canvas.style.height = `${this.height}px`;

        this.ctx.scale(this.dpr, this.dpr);
    }

    setData(nodes, edges) {
        this.nodes = nodes.map(n => ({
            ...n,
            x: n.xPos,
            y: n.yPos,
            vx: 0,
            vy: 0,
            radius: 22
        }));

        this.edges = edges.map(e => ({ ...e }));
        this.nodeMap.clear();
        this.nodes.forEach(n => this.nodeMap.set(n.id, n));

        // Create particles for animated flow along edges
        this.particles = [];
        this.edges.forEach((e, idx) => {
            const count = Math.max(1, Math.round(e.weight * 2));
            for (let i = 0; i < count; i++) {
                this.particles.push({
                    edge: e,
                    progress: Math.random(),
                    speed: 0.003 + (1.0 / Math.max(1, e.delayHours)) * 0.006
                });
            }
        });

        this.resetHighlights();
        this.fitToScreen();
    }

    resetHighlights() {
        this.highlightedEdges.clear();
        this.highlightedNodes.clear();
        this.sccColorMap.clear();
    }

    clearSimulation() {
        this.impactScores.clear();
        this.arrivalTimes.clear();
        this.hopLevels.clear();
        this.epicenterNodeId = null;
        this.shockwaves = [];
        this.resetHighlights();
    }

    fitToScreen() {
        if (this.nodes.length === 0) return;

        let minX = Infinity, maxX = -Infinity, minY = Infinity, maxY = -Infinity;
        this.nodes.forEach(n => {
            if (n.x < minX) minX = n.x;
            if (n.x > maxX) maxX = n.x;
            if (n.y < minY) minY = n.y;
            if (n.y > maxY) maxY = n.y;
        });

        const graphW = maxX - minX + 160;
        const graphH = maxY - minY + 160;

        const scaleX = (this.width - 60) / graphW;
        const scaleY = (this.height - 60) / graphH;
        this.transform.k = Math.max(0.4, Math.min(1.4, Math.min(scaleX, scaleY)));

        this.transform.x = (this.width - (maxX + minX) * this.transform.k) / 2;
        this.transform.y = (this.height - (maxY + minY) * this.transform.k) / 2;
    }

    triggerShockwave(x, y, color = 'rgba(239, 68, 68, 0.8)') {
        this.shockwaves.push({
            x,
            y,
            radius: 5,
            maxRadius: Math.max(this.width, this.height) * 0.85,
            alpha: 1.0,
            color
        });
    }

    screenToWorld(sx, sy) {
        return {
            x: (sx - this.transform.x) / this.transform.k,
            y: (sy - this.transform.y) / this.transform.k
        };
    }

    worldToScreen(wx, wy) {
        return {
            x: wx * this.transform.k + this.transform.x,
            y: wy * this.transform.k + this.transform.y
        };
    }

    findNodeAt(screenX, screenY) {
        const world = this.screenToWorld(screenX, screenY);
        for (let i = this.nodes.length - 1; i >= 0; i--) {
            const n = this.nodes[i];
            const dx = n.x - world.x;
            const dy = n.y - world.y;
            if (dx * dx + dy * dy <= (n.radius + 6) * (n.radius + 6)) {
                return n;
            }
        }
        return null;
    }

    bindEvents() {
        window.addEventListener('resize', () => this.initCanvasSize());

        // Mouse down
        this.canvas.addEventListener('mousedown', (e) => {
            const rect = this.canvas.getBoundingClientRect();
            const sx = e.clientX - rect.left;
            const sy = e.clientY - rect.top;

            this.lastMouse = { x: sx, y: sy };
            const node = this.findNodeAt(sx, sy);

            if (node) {
                this.draggedNode = node;
                this.selectedNodeId = node.id;
                this.dragStart = { x: node.x, y: node.y };
                if (typeof this.onNodeSelect === 'function') {
                    this.onNodeSelect(node);
                }
            } else {
                this.isDragging = true;
            }
        });

        // Mouse move
        this.canvas.addEventListener('mousemove', (e) => {
            const rect = this.canvas.getBoundingClientRect();
            const sx = e.clientX - rect.left;
            const sy = e.clientY - rect.top;

            if (this.draggedNode) {
                const world = this.screenToWorld(sx, sy);
                this.draggedNode.x = world.x;
                this.draggedNode.y = world.y;
                this.draggedNode.xPos = world.x;
                this.draggedNode.yPos = world.y;
            } else if (this.isDragging) {
                const dx = sx - this.lastMouse.x;
                const dy = sy - this.lastMouse.y;
                this.transform.x += dx;
                this.transform.y += dy;
            } else {
                const hover = this.findNodeAt(sx, sy);
                this.hoveredNodeId = hover ? hover.id : null;
                this.canvas.style.cursor = hover ? 'pointer' : 'default';

                if (typeof this.onNodeHover === 'function') {
                    this.onNodeHover(hover, sx, sy);
                }
            }

            this.lastMouse = { x: sx, y: sy };
        });

        // Mouse up
        window.addEventListener('mouseup', () => {
            this.draggedNode = null;
            this.isDragging = false;
        });

        // Zoom via mouse wheel
        this.canvas.addEventListener('wheel', (e) => {
            e.preventDefault();
            const rect = this.canvas.getBoundingClientRect();
            const sx = e.clientX - rect.left;
            const sy = e.clientY - rect.top;

            const zoomFactor = e.deltaY < 0 ? 1.12 : 0.89;
            const newK = Math.max(0.2, Math.min(3.5, this.transform.k * zoomFactor));

            // Zoom centered on cursor
            this.transform.x = sx - (sx - this.transform.x) * (newK / this.transform.k);
            this.transform.y = sy - (sy - this.transform.y) * (newK / this.transform.k);
            this.transform.k = newK;
        }, { passive: false });
    }

    stepPhysics() {
        if (!this.physicsEnabled) return;

        const k = 140; // ideal spring length
        const repulsive = 9000;
        const damping = 0.88;

        // 1. Repulsion between all node pairs
        for (let i = 0; i < this.nodes.length; i++) {
            const u = this.nodes[i];
            for (let j = i + 1; j < this.nodes.length; j++) {
                const v = this.nodes[j];
                const dx = v.x - u.x;
                const dy = v.y - u.y;
                const distSq = dx * dx + dy * dy || 1;
                const dist = Math.sqrt(distSq);

                if (dist < 450) {
                    const force = repulsive / distSq;
                    const fx = (dx / dist) * force;
                    const fy = (dy / dist) * force;
                    u.vx -= fx;
                    u.vy -= fy;
                    v.vx += fx;
                    v.vy += fy;
                }
            }
        }

        // 2. Attraction along edges
        for (const e of this.edges) {
            const u = this.nodeMap.get(e.sourceId);
            const v = this.nodeMap.get(e.targetId);
            if (!u || !v) continue;

            const dx = v.x - u.x;
            const dy = v.y - u.y;
            const dist = Math.sqrt(dx * dx + dy * dy) || 1;
            const force = (dist - k) * 0.04;
            const fx = (dx / dist) * force;
            const fy = (dy / dist) * force;

            u.vx += fx;
            u.vy += fy;
            v.vx -= fx;
            v.vy -= fy;
        }

        // 3. Update positions with damping
        for (const n of this.nodes) {
            if (this.draggedNode && this.draggedNode.id === n.id) continue;
            n.vx *= damping;
            n.vy *= damping;
            n.x += n.vx;
            n.y += n.vy;
            n.xPos = n.x;
            n.yPos = n.y;
        }
    }

    startLoop() {
        const render = () => {
            this.update();
            this.draw();
            requestAnimationFrame(render);
        };
        requestAnimationFrame(render);
    }

    update() {
        this.stepPhysics();

        // Update animated flow particles
        for (const p of this.particles) {
            p.progress += p.speed;
            if (p.progress > 1.0) p.progress = 0;
        }

        // Update shockwaves
        for (let i = this.shockwaves.length - 1; i >= 0; i--) {
            const sw = this.shockwaves[i];
            sw.radius += 14;
            sw.alpha = Math.max(0, 1.0 - (sw.radius / sw.maxRadius));
            if (sw.alpha <= 0.02) {
                this.shockwaves.splice(i, 1);
            }
        }

        // Radar minimap sweep
        this.radarAngle = (this.radarAngle + 0.04) % (Math.PI * 2);
    }

    draw() {
        const ctx = this.ctx;
        ctx.clearRect(0, 0, this.width, this.height);

        // Draw background grid
        this.drawGrid(ctx);

        ctx.save();
        ctx.translate(this.transform.x, this.transform.y);
        ctx.scale(this.transform.k, this.transform.k);

        // Draw animated shockwaves in world coordinates
        this.drawShockwaves(ctx);

        // Draw edges & flow particles
        this.drawEdges(ctx);

        // Draw nodes
        this.drawNodes(ctx);

        ctx.restore();

        // Draw HUD overlay: Radar Minimap
        this.drawMinimap(ctx);
    }

    drawGrid(ctx) {
        ctx.save();
        const step = 40 * this.transform.k;
        const offsetX = this.transform.x % step;
        const offsetY = this.transform.y % step;

        ctx.strokeStyle = 'rgba(255, 255, 255, 0.03)';
        ctx.lineWidth = 1;

        ctx.beginPath();
        for (let x = offsetX; x < this.width; x += step) {
            ctx.moveTo(x, 0);
            ctx.lineTo(x, this.height);
        }
        for (let y = offsetY; y < this.height; y += step) {
            ctx.moveTo(0, y);
            ctx.lineTo(this.width, y);
        }
        ctx.stroke();

        // Radial ambient spotlight
        const radGrad = ctx.createRadialGradient(
            this.width / 2, this.height / 2, 80,
            this.width / 2, this.height / 2, Math.max(this.width, this.height) * 0.75
        );
        radGrad.addColorStop(0, 'rgba(0, 242, 254, 0.03)');
        radGrad.addColorStop(1, 'rgba(0, 0, 0, 0)');
        ctx.fillStyle = radGrad;
        ctx.fillRect(0, 0, this.width, this.height);

        ctx.restore();
    }

    drawShockwaves(ctx) {
        for (const sw of this.shockwaves) {
            ctx.save();
            ctx.beginPath();
            ctx.arc(sw.x, sw.y, sw.radius, 0, Math.PI * 2);
            ctx.strokeStyle = sw.color.replace(')', `, ${sw.alpha})`).replace('rgb', 'rgba');
            ctx.lineWidth = Math.max(2, 6 * (1 - sw.radius / sw.maxRadius));
            ctx.stroke();

            // Inner faint glow ring
            if (sw.radius > 40) {
                ctx.beginPath();
                ctx.arc(sw.x, sw.y, sw.radius - 20, 0, Math.PI * 2);
                ctx.strokeStyle = `rgba(239, 68, 68, ${sw.alpha * 0.4})`;
                ctx.lineWidth = 2;
                ctx.stroke();
            }
            ctx.restore();
        }
    }

    drawEdges(ctx) {
        for (const e of this.edges) {
            const u = this.nodeMap.get(e.sourceId);
            const v = this.nodeMap.get(e.targetId);
            if (!u || !v) continue;

            const isTraversed = this.impactScores.has(v.id) && this.impactScores.get(v.id) > 0;
            const isHighlighted = this.highlightedEdges.has(e.id) || this.highlightedEdges.has(`${u.id}-${v.id}`);

            ctx.save();

            // Line styling
            ctx.beginPath();
            ctx.moveTo(u.x, u.y);
            ctx.lineTo(v.x, v.y);

            if (isHighlighted) {
                ctx.strokeStyle = '#10b981'; // MST or critical path
                ctx.lineWidth = 3.5;
                ctx.shadowColor = '#10b981';
                ctx.shadowBlur = 10;
            } else if (isTraversed) {
                ctx.strokeStyle = 'rgba(239, 68, 68, 0.75)'; // cascade trail
                ctx.lineWidth = 2.5;
                ctx.shadowColor = '#ef4444';
                ctx.shadowBlur = 8;
            } else {
                ctx.strokeStyle = 'rgba(148, 163, 184, 0.22)';
                ctx.lineWidth = Math.max(1.2, e.weight * 2.2);
            }
            ctx.stroke();

            // Arrow head
            const angle = Math.atan2(v.y - u.y, v.x - u.x);
            const arrowDist = v.radius + 8;
            const arrowX = v.x - Math.cos(angle) * arrowDist;
            const arrowY = v.y - Math.sin(angle) * arrowDist;
            const headLen = 8;

            ctx.beginPath();
            ctx.moveTo(arrowX, arrowY);
            ctx.lineTo(
                arrowX - headLen * Math.cos(angle - Math.PI / 7),
                arrowY - headLen * Math.sin(angle - Math.PI / 7)
            );
            ctx.lineTo(
                arrowX - headLen * Math.cos(angle + Math.PI / 7),
                arrowY - headLen * Math.sin(angle + Math.PI / 7)
            );
            ctx.closePath();
            ctx.fillStyle = isHighlighted ? '#10b981' : (isTraversed ? '#ef4444' : 'rgba(148, 163, 184, 0.6)');
            ctx.fill();

            // Weight & Delay badges on zoom in
            if (this.transform.k > 0.85) {
                const midX = (u.x + v.x) / 2;
                const midY = (u.y + v.y) / 2;
                ctx.font = '9px "JetBrains Mono", monospace';
                ctx.fillStyle = 'rgba(148, 163, 184, 0.65)';
                ctx.textAlign = 'center';
                ctx.fillText(`${(e.weight).toFixed(2)} | ${e.delayHours}h`, midX, midY - 6);
            }

            ctx.restore();
        }

        // Draw animated flow particles along edges
        for (const p of this.particles) {
            const u = this.nodeMap.get(p.edge.sourceId);
            const v = this.nodeMap.get(p.edge.targetId);
            if (!u || !v) continue;

            const px = u.x + (v.x - u.x) * p.progress;
            const py = u.y + (v.y - u.y) * p.progress;

            const isTraversed = this.impactScores.has(v.id) && this.impactScores.get(v.id) > 0;

            ctx.save();
            ctx.beginPath();
            ctx.arc(px, py, isTraversed ? 3.0 : 2.0, 0, Math.PI * 2);
            ctx.fillStyle = isTraversed ? '#f97316' : '#00f2fe';
            ctx.shadowColor = isTraversed ? '#ef4444' : '#00f2fe';
            ctx.shadowBlur = 6;
            ctx.fill();
            ctx.restore();
        }
    }

    drawNodes(ctx) {
        for (const n of this.nodes) {
            const isSelected = this.selectedNodeId === n.id;
            const isHovered = this.hoveredNodeId === n.id;
            const isEpicenter = this.epicenterNodeId === n.id;
            const isShielded = this.shieldedNodeIds.has(n.id);
            const isArticulation = this.articulationPoints.has(n.id);
            const impactScore = this.impactScores.get(n.id) || 0.0;
            const arrivalTime = this.arrivalTimes.get(n.id);
            const sccColor = this.sccColorMap.get(n.id);

            const baseColor = this.categoryColors[n.category] || this.categoryColors.Default;

            ctx.save();

            // 1. Epicenter pulsating aura
            if (isEpicenter) {
                const pulse = 10 + Math.sin(Date.now() * 0.008) * 8;
                ctx.beginPath();
                ctx.arc(n.x, n.y, n.radius + pulse, 0, Math.PI * 2);
                ctx.fillStyle = 'rgba(239, 68, 68, 0.25)';
                ctx.fill();
            }

            // 2. Shielded force-field dome
            if (isShielded) {
                ctx.beginPath();
                ctx.arc(n.x, n.y, n.radius + 8, 0, Math.PI * 2);
                ctx.fillStyle = 'rgba(59, 130, 246, 0.2)';
                ctx.strokeStyle = '#3b82f6';
                ctx.lineWidth = 2;
                ctx.setLineDash([4, 4]);
                ctx.stroke();
                ctx.fill();
                ctx.setLineDash([]);
            }

            // 3. Impact halo
            if (impactScore > 0) {
                const haloRadius = n.radius + 4 + (impactScore * 12);
                ctx.beginPath();
                ctx.arc(n.x, n.y, haloRadius, 0, Math.PI * 2);
                ctx.fillStyle = this.getImpactColor(impactScore, 0.25);
                ctx.fill();
            }

            // 4. Node outer border / Articulation warning
            ctx.beginPath();
            ctx.arc(n.x, n.y, n.radius, 0, Math.PI * 2);

            if (isSelected) {
                ctx.strokeStyle = '#00f2fe';
                ctx.lineWidth = 3.5;
                ctx.shadowColor = '#00f2fe';
                ctx.shadowBlur = 14;
            } else if (isArticulation) {
                ctx.strokeStyle = '#f59e0b';
                ctx.lineWidth = 3;
                ctx.shadowColor = '#f59e0b';
                ctx.shadowBlur = 10;
            } else if (impactScore > 0) {
                ctx.strokeStyle = this.getImpactColor(impactScore, 1.0);
                ctx.lineWidth = 2.5;
                ctx.shadowColor = ctx.strokeStyle;
                ctx.shadowBlur = 8;
            } else {
                ctx.strokeStyle = sccColor || 'rgba(255, 255, 255, 0.18)';
                ctx.lineWidth = 2;
            }

            // 5. Node Body Fill (Gradient)
            const grad = ctx.createRadialGradient(n.x - 6, n.y - 6, 2, n.x, n.y, n.radius);
            if (impactScore > 0) {
                grad.addColorStop(0, '#1e1b2e');
                grad.addColorStop(1, this.getImpactColor(impactScore, 0.7));
            } else {
                grad.addColorStop(0, '#1e293b');
                grad.addColorStop(1, '#0f172a');
            }
            ctx.fillStyle = grad;
            ctx.fill();
            ctx.stroke();

            // 6. Resilience Arc (circumference indicator)
            ctx.beginPath();
            const startAngle = -Math.PI / 2;
            const endAngle = startAngle + (Math.PI * 2 * n.resilience);
            ctx.arc(n.x, n.y, n.radius - 3, startAngle, endAngle);
            ctx.strokeStyle = baseColor;
            ctx.lineWidth = 2.5;
            ctx.stroke();

            // 7. Center Category Dot or Icon
            ctx.beginPath();
            ctx.arc(n.x, n.y, 5, 0, Math.PI * 2);
            ctx.fillStyle = baseColor;
            ctx.fill();

            // 8. Shield Icon 🛡️ / Articulation Icon ⚠️
            if (isShielded) {
                ctx.font = '14px sans-serif';
                ctx.textAlign = 'center';
                ctx.textBaseline = 'middle';
                ctx.fillText('🛡️', n.x, n.y - 1);
            } else if (isEpicenter) {
                ctx.font = '14px sans-serif';
                ctx.textAlign = 'center';
                ctx.textBaseline = 'middle';
                ctx.fillText('💥', n.x, n.y - 1);
            } else if (isArticulation) {
                ctx.font = '11px sans-serif';
                ctx.textAlign = 'center';
                ctx.textBaseline = 'middle';
                ctx.fillText('⚠️', n.x, n.y - 1);
            }

            // 9. Node Label
            ctx.shadowBlur = 0;
            ctx.font = isSelected || isHovered ? 'bold 11px Inter, sans-serif' : '10px Inter, sans-serif';
            ctx.fillStyle = isSelected ? '#ffffff' : (impactScore > 0 ? '#fca5a5' : '#e2e8f0');
            ctx.textAlign = 'center';
            ctx.fillText(n.name, n.x, n.y + n.radius + 14);

            // 10. Impact % / Arrival Time sub-badge
            if (impactScore > 0) {
                ctx.font = '9px "JetBrains Mono", monospace';
                ctx.fillStyle = '#ef4444';
                const timeText = arrivalTime !== undefined ? ` | ${arrivalTime.toFixed(1)}h` : '';
                ctx.fillText(`${Math.round(impactScore * 100)}%${timeText}`, n.x, n.y + n.radius + 26);
            }

            ctx.restore();
        }
    }

    getImpactColor(score, alpha = 1.0) {
        // Linear spectrum: Green -> Yellow -> Orange -> Red -> Purple
        if (score < 0.25) return `rgba(34, 197, 94, ${alpha})`;
        if (score < 0.50) return `rgba(234, 179, 8, ${alpha})`;
        if (score < 0.75) return `rgba(249, 115, 22, ${alpha})`;
        return `rgba(239, 68, 68, ${alpha})`;
    }

    drawMinimap(ctx) {
        const miniW = 140;
        const miniH = 100;
        const pad = 16;
        const x = this.width - miniW - pad;
        const y = this.height - miniH - pad;

        ctx.save();
        // Minimap panel background
        ctx.fillStyle = 'rgba(10, 15, 28, 0.85)';
        ctx.strokeStyle = 'rgba(0, 242, 254, 0.3)';
        ctx.lineWidth = 1;
        ctx.beginPath();
        ctx.roundRect(x, y, miniW, miniH, 8);
        ctx.fill();
        ctx.stroke();

        // Minimap header
        ctx.font = '8px "JetBrains Mono", monospace';
        ctx.fillStyle = 'rgba(0, 242, 254, 0.8)';
        ctx.fillText('RADAR RADIAL SCOPE', x + 8, y + 12);

        // Radar scanning beam line
        const centerX = x + miniW / 2;
        const centerY = y + miniH / 2;
        const sweepLen = 42;

        ctx.beginPath();
        ctx.moveTo(centerX, centerY);
        ctx.lineTo(centerX + Math.cos(this.radarAngle) * sweepLen, centerY + Math.sin(this.radarAngle) * sweepLen);
        ctx.strokeStyle = 'rgba(0, 242, 254, 0.4)';
        ctx.lineWidth = 1.5;
        ctx.stroke();

        // Target range rings
        ctx.beginPath();
        ctx.arc(centerX, centerY, 20, 0, Math.PI * 2);
        ctx.arc(centerX, centerY, 38, 0, Math.PI * 2);
        ctx.strokeStyle = 'rgba(255, 255, 255, 0.08)';
        ctx.stroke();

        // Node dots
        if (this.nodes.length > 0) {
            let minX = Infinity, maxX = -Infinity, minY = Infinity, maxY = -Infinity;
            this.nodes.forEach(n => {
                if (n.x < minX) minX = n.x;
                if (n.x > maxX) maxX = n.x;
                if (n.y < minY) minY = n.y;
                if (n.y > maxY) maxY = n.y;
            });
            const scaleX = (miniW - 24) / Math.max(1, maxX - minX);
            const scaleY = (miniH - 30) / Math.max(1, maxY - minY);

            for (const n of this.nodes) {
                const mx = x + 12 + (n.x - minX) * scaleX;
                const my = y + 18 + (n.y - minY) * scaleY;
                const impact = this.impactScores.get(n.id) || 0;

                ctx.beginPath();
                ctx.arc(mx, my, impact > 0 ? 2.5 : 1.5, 0, Math.PI * 2);
                ctx.fillStyle = impact > 0 ? '#ef4444' : (this.shieldedNodeIds.has(n.id) ? '#3b82f6' : '#00f2fe');
                ctx.fill();
            }
        }

        ctx.restore();
    }
}
