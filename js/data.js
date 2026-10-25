/**
 * RippleMap - Seed Scenarios, Initial Events, and Database Schema
 * Formatted from ADSA & DBMS course project database schema and seeds.
 */

const SEED_DATA = {
    scenarios: [
        {
            id: 1,
            name: "Global Electronics Supply Chain",
            icon: "🌐",
            category: "Supply Chain",
            description: "High-tech electronics supply network from rare-earth extraction to retail shelves",
            defaultEvent: {
                name: "Taiwan Wafer Fab Earthquake Shutdown",
                sourceNodeId: 104,
                severity: 9.5,
                description: "Major seismic activity halts 3nm/5nm wafer cleanrooms, halting chip deliveries"
            },
            nodes: [
                { id: 101, scenarioId: 1, name: "Lithium Mines (Chile)", category: "Raw Material", xPos: 80, yPos: 140, resilience: 0.40, description: "Primary brine extraction for battery cells" },
                { id: 102, scenarioId: 1, name: "Cobalt Refinery (Congo)", category: "Raw Material", xPos: 80, yPos: 280, resilience: 0.30, description: "Key cathode material producer" },
                { id: 103, scenarioId: 1, name: "Silicon Smelter (Norway)", category: "Raw Material", xPos: 80, yPos: 420, resilience: 0.60, description: "High-purity polysilicon for wafer fabrication" },
                { id: 104, scenarioId: 1, name: "Wafer Foundry TSMC (Taiwan)", category: "Manufacturing", xPos: 260, yPos: 180, resilience: 0.80, description: "Advanced 3nm/5nm logic wafer fabrication" },
                { id: 105, scenarioId: 1, name: "Memory Fab Samsung (S.Korea)", category: "Manufacturing", xPos: 260, yPos: 320, resilience: 0.70, description: "DRAM and NAND Flash solid-state memory" },
                { id: 106, scenarioId: 1, name: "Battery Cell Plant CATL (China)", category: "Manufacturing", xPos: 260, yPos: 480, resilience: 0.50, description: "EV and device lithium-ion pack assembly" },
                { id: 107, scenarioId: 1, name: "PCB Fabrication (Shenzhen)", category: "Manufacturing", xPos: 430, yPos: 160, resilience: 0.60, description: "Multilayer printed circuit board production" },
                { id: 108, scenarioId: 1, name: "Foxconn Assembly Hub (Zhengzhou)", category: "Assembly", xPos: 470, yPos: 320, resilience: 0.70, description: "Final smartphone and consumer device integration" },
                { id: 109, scenarioId: 1, name: "Pegatron Facility (Shanghai)", category: "Assembly", xPos: 470, yPos: 490, resilience: 0.50, description: "Laptop and wearable system assembly" },
                { id: 110, scenarioId: 1, name: "Port of Shanghai", category: "Logistics Hub", xPos: 650, yPos: 180, resilience: 0.80, description: "World highest throughput container seaport" },
                { id: 111, scenarioId: 1, name: "Port of Singapore", category: "Logistics Hub", xPos: 650, yPos: 330, resilience: 0.85, description: "Major international transshipment maritime hub" },
                { id: 112, scenarioId: 1, name: "Port of Rotterdam", category: "Logistics Hub", xPos: 650, yPos: 480, resilience: 0.75, description: "Primary gateway for European freight distribution" },
                { id: 113, scenarioId: 1, name: "Port of Los Angeles", category: "Logistics Hub", xPos: 830, yPos: 160, resilience: 0.60, description: "North American west coast trans-pacific terminal" },
                { id: 114, scenarioId: 1, name: "Air Cargo Hub Frankfurt", category: "Air Logistics", xPos: 830, yPos: 310, resilience: 0.80, description: "High-priority express air freight terminal" },
                { id: 115, scenarioId: 1, name: "Air Cargo Hub Anchorage", category: "Air Logistics", xPos: 830, yPos: 460, resilience: 0.70, description: "Trans-polar refueling and cargo transfer hub" },
                { id: 116, scenarioId: 1, name: "FedEx SuperHub (Memphis)", category: "Distribution", xPos: 1000, yPos: 200, resilience: 0.85, description: "Overnight sorting and nationwide fulfillment" },
                { id: 117, scenarioId: 1, name: "DHL Central Hub (Leipzig)", category: "Distribution", xPos: 1000, yPos: 360, resilience: 0.80, description: "European continent express parcel sorting" },
                { id: 118, scenarioId: 1, name: "Amazon Fulfillment (Dallas)", category: "Distribution", xPos: 1000, yPos: 520, resilience: 0.70, description: "Mega-warehouse for regional e-commerce stock" },
                { id: 119, scenarioId: 1, name: "BestBuy US West Retailers", category: "Retail", xPos: 1180, yPos: 130, resilience: 0.50, description: "Brick-and-mortar retail outlets across western US" },
                { id: 120, scenarioId: 1, name: "Apple Stores North America", category: "Retail", xPos: 1180, yPos: 250, resilience: 0.80, description: "Flagship direct consumer electronics stores" },
                { id: 121, scenarioId: 1, name: "MediaMarkt Europe Stores", category: "Retail", xPos: 1180, yPos: 370, resilience: 0.60, description: "Major European consumer tech store network" },
                { id: 122, scenarioId: 1, name: "Amazon Prime Delivery Fleet", category: "Last-Mile", xPos: 1180, yPos: 490, resilience: 0.65, description: "Direct doorstep delivery vehicle network" },
                { id: 123, scenarioId: 1, name: "EV Auto Assembly (Munich)", category: "Automotive", xPos: 1180, yPos: 610, resilience: 0.50, description: "Automaker relying on microchips and batteries" },
                { id: 124, scenarioId: 1, name: "Telecom Tower Rollout (Global)", category: "Enterprise", xPos: 1340, yPos: 280, resilience: 0.70, description: "5G infrastructure base station upgrades" },
                { id: 125, scenarioId: 1, name: "Healthcare Equipment Vendor", category: "Enterprise", xPos: 1340, yPos: 460, resilience: 0.90, description: "Hospital ventilators and diagnostic scanners" }
            ],
            edges: [
                { id: 1, scenarioId: 1, sourceId: 101, targetId: 106, weight: 0.85, delayHours: 48.0, relationType: "SUPPLIES" },
                { id: 2, scenarioId: 1, sourceId: 102, targetId: 106, weight: 0.90, delayHours: 72.0, relationType: "SUPPLIES" },
                { id: 3, scenarioId: 1, sourceId: 103, targetId: 104, weight: 0.95, delayHours: 36.0, relationType: "SUPPLIES" },
                { id: 4, scenarioId: 1, sourceId: 103, targetId: 105, weight: 0.80, delayHours: 36.0, relationType: "SUPPLIES" },
                { id: 5, scenarioId: 1, sourceId: 104, targetId: 107, weight: 0.75, delayHours: 24.0, relationType: "DELIVERS_TO" },
                { id: 6, scenarioId: 1, sourceId: 104, targetId: 108, weight: 0.95, delayHours: 18.0, relationType: "DELIVERS_TO" },
                { id: 7, scenarioId: 1, sourceId: 104, targetId: 109, weight: 0.70, delayHours: 24.0, relationType: "DELIVERS_TO" },
                { id: 8, scenarioId: 1, sourceId: 105, targetId: 108, weight: 0.85, delayHours: 20.0, relationType: "DELIVERS_TO" },
                { id: 9, scenarioId: 1, sourceId: 105, targetId: 109, weight: 0.80, delayHours: 20.0, relationType: "DELIVERS_TO" },
                { id: 10, scenarioId: 1, sourceId: 106, targetId: 108, weight: 0.75, delayHours: 30.0, relationType: "DELIVERS_TO" },
                { id: 11, scenarioId: 1, sourceId: 106, targetId: 123, weight: 0.90, delayHours: 60.0, relationType: "SUPPLIES" },
                { id: 12, scenarioId: 1, sourceId: 107, targetId: 108, weight: 0.90, delayHours: 12.0, relationType: "INTEGRATES" },
                { id: 13, scenarioId: 1, sourceId: 107, targetId: 109, weight: 0.85, delayHours: 12.0, relationType: "INTEGRATES" },
                { id: 14, scenarioId: 1, sourceId: 108, targetId: 110, weight: 0.95, delayHours: 16.0, relationType: "SHIPS_VIA" },
                { id: 15, scenarioId: 1, sourceId: 108, targetId: 114, weight: 0.70, delayHours: 8.0, relationType: "EXPRESS_AIR" },
                { id: 16, scenarioId: 1, sourceId: 109, targetId: 110, weight: 0.85, delayHours: 14.0, relationType: "SHIPS_VIA" },
                { id: 17, scenarioId: 1, sourceId: 109, targetId: 111, weight: 0.60, delayHours: 28.0, relationType: "SHIPS_VIA" },
                { id: 18, scenarioId: 1, sourceId: 110, targetId: 113, weight: 0.95, delayHours: 120.0, relationType: "OCEAN_FREIGHT" },
                { id: 19, scenarioId: 1, sourceId: 110, targetId: 112, weight: 0.85, delayHours: 168.0, relationType: "OCEAN_FREIGHT" },
                { id: 20, scenarioId: 1, sourceId: 111, targetId: 112, weight: 0.75, delayHours: 144.0, relationType: "OCEAN_FREIGHT" },
                { id: 21, scenarioId: 1, sourceId: 111, targetId: 113, weight: 0.65, delayHours: 160.0, relationType: "OCEAN_FREIGHT" },
                { id: 22, scenarioId: 1, sourceId: 112, targetId: 117, weight: 0.80, delayHours: 18.0, relationType: "INLAND_FREIGHT" },
                { id: 23, scenarioId: 1, sourceId: 112, targetId: 121, weight: 0.60, delayHours: 24.0, relationType: "DISTRIBUTES_TO" },
                { id: 24, scenarioId: 1, sourceId: 113, targetId: 116, weight: 0.90, delayHours: 24.0, relationType: "INLAND_RAIL" },
                { id: 25, scenarioId: 1, sourceId: 113, targetId: 118, weight: 0.85, delayHours: 28.0, relationType: "TRUCK_CORRIDOR" },
                { id: 26, scenarioId: 1, sourceId: 114, targetId: 117, weight: 0.85, delayHours: 6.0, relationType: "AIR_TRANSFER" },
                { id: 27, scenarioId: 1, sourceId: 115, targetId: 116, weight: 0.90, delayHours: 8.0, relationType: "AIR_CARGO" },
                { id: 28, scenarioId: 1, sourceId: 116, targetId: 119, weight: 0.80, delayHours: 12.0, relationType: "FULFILLS" },
                { id: 29, scenarioId: 1, sourceId: 116, targetId: 120, weight: 0.95, delayHours: 10.0, relationType: "FULFILLS" },
                { id: 30, scenarioId: 1, sourceId: 117, targetId: 121, weight: 0.85, delayHours: 14.0, relationType: "FULFILLS" },
                { id: 31, scenarioId: 1, sourceId: 117, targetId: 123, weight: 0.75, delayHours: 16.0, relationType: "DELIVERS_PARTS" },
                { id: 32, scenarioId: 1, sourceId: 118, targetId: 119, weight: 0.60, delayHours: 12.0, relationType: "RESTOCKS" },
                { id: 33, scenarioId: 1, sourceId: 118, targetId: 122, weight: 0.95, delayHours: 6.0, relationType: "LAST_MILE_DISPATCH" },
                { id: 34, scenarioId: 1, sourceId: 120, targetId: 124, weight: 0.50, delayHours: 48.0, relationType: "ENTERPRISE_CONTRACT" },
                { id: 35, scenarioId: 1, sourceId: 104, targetId: 125, weight: 0.85, delayHours: 36.0, relationType: "CRITICAL_CHIP_SUPPLY" }
            ]
        },
        {
            id: 2,
            name: "Cloud Microservices Architecture",
            icon: "☁️",
            category: "Distributed Systems",
            description: "Resilient distributed backend handling user traffic, payments, caching, and stream processing",
            defaultEvent: {
                name: "OAuth2 Auth Service Memory Leak",
                sourceNodeId: 203,
                severity: 8.5,
                description: "JVM Heap exhaustion in Auth Pods fails token validations across microservices"
            },
            nodes: [
                { id: 201, scenarioId: 2, name: "Cloudflare Edge CDN", category: "Edge Network", xPos: 80, yPos: 300, resilience: 0.90, description: "Anycast DNS and DDOS edge protection proxy" },
                { id: 202, scenarioId: 2, name: "Kong API Gateway", category: "Gateway", xPos: 220, yPos: 300, resilience: 0.80, description: "Central routing, rate limiting, and SSL termination" },
                { id: 203, scenarioId: 2, name: "OAuth2 Auth Service", category: "Security", xPos: 380, yPos: 150, resilience: 0.85, description: "JWT token generation and credential verification" },
                { id: 204, scenarioId: 2, name: "User Profile Service", category: "Core Service", xPos: 380, yPos: 300, resilience: 0.70, description: "User accounts, preferences, and session data" },
                { id: 205, scenarioId: 2, name: "Product Catalog API", category: "Core Service", xPos: 380, yPos: 450, resilience: 0.75, description: "Product information, categories, and tags" },
                { id: 206, scenarioId: 2, name: "Redis Session Cache", category: "Cache Tier", xPos: 550, yPos: 120, resilience: 0.60, description: "In-memory distributed key-value cache" },
                { id: 207, scenarioId: 2, name: "Order Management API", category: "Core Service", xPos: 550, yPos: 260, resilience: 0.75, description: "Order placement, workflows, and state machine" },
                { id: 208, scenarioId: 2, name: "Elasticsearch Cluster", category: "Search Tier", xPos: 550, yPos: 420, resilience: 0.70, description: "Full-text indexing and faceted product search" },
                { id: 209, scenarioId: 2, name: "Postgres Auth DB", category: "Database Tier", xPos: 550, yPos: 560, resilience: 0.85, description: "Relational ACID store for user credentials" },
                { id: 210, scenarioId: 2, name: "Stripe Payment Gateway", category: "External Partner", xPos: 720, yPos: 120, resilience: 0.90, description: "Third-party PCI-compliant card processor" },
                { id: 211, scenarioId: 2, name: "Payment Processing Worker", category: "Fintech", xPos: 720, yPos: 240, resilience: 0.80, description: "Asynchronous payment capture and webhook handler" },
                { id: 212, scenarioId: 2, name: "Inventory Lock Service", category: "Core Service", xPos: 720, yPos: 360, resilience: 0.70, description: "Pessimistic inventory locking and stock allocation" },
                { id: 213, scenarioId: 2, name: "Kafka Event Bus", category: "Messaging Tier", xPos: 720, yPos: 500, resilience: 0.85, description: "High-throughput distributed append-only event stream" },
                { id: 214, scenarioId: 2, name: "Postgres Inventory DB", category: "Database Tier", xPos: 900, yPos: 180, resilience: 0.80, description: "Master-replica database for SKU quantities" },
                { id: 215, scenarioId: 2, name: "Fraud Detection Engine", category: "AI/Security", xPos: 900, yPos: 300, resilience: 0.75, description: "Real-time ML risk scoring on suspicious orders" },
                { id: 216, scenarioId: 2, name: "Notification Service", category: "Worker Tier", xPos: 900, yPos: 440, resilience: 0.65, description: "Transactional email, SMS, and mobile push dispatch" },
                { id: 217, scenarioId: 2, name: "Fulfillment Dispatch API", category: "Logistics", xPos: 900, yPos: 560, resilience: 0.70, description: "Warehouse picking ticket generation service" },
                { id: 218, scenarioId: 2, name: "SendGrid Email Gateway", category: "External Partner", xPos: 1080, yPos: 180, resilience: 0.85, description: "External SMTP delivery service" },
                { id: 219, scenarioId: 2, name: "Twilio SMS Gateway", category: "External Partner", xPos: 1080, yPos: 300, resilience: 0.85, description: "Telecom SMS messaging provider" },
                { id: 220, scenarioId: 2, name: "ClickHouse Analytics DB", category: "Data Warehouse", xPos: 1080, yPos: 440, resilience: 0.75, description: "Columnar analytical OLAP store" },
                { id: 221, scenarioId: 2, name: "Datadog APM Collector", category: "Observability", xPos: 1080, yPos: 580, resilience: 0.90, description: "Metrics, distributed traces, and log aggregation" },
                { id: 222, scenarioId: 2, name: "Customer iOS/Android App", category: "Client Frontend", xPos: 80, yPos: 150, resilience: 0.60, description: "Mobile native client application" },
                { id: 223, scenarioId: 2, name: "Next.js Web Storefront", category: "Client Frontend", xPos: 80, yPos: 450, resilience: 0.60, description: "React server-side rendered web app" },
                { id: 224, scenarioId: 2, name: "Admin Operations Portal", category: "Client Frontend", xPos: 80, yPos: 600, resilience: 0.80, description: "Internal staff dashboard for customer support" }
            ],
            edges: [
                { id: 101, scenarioId: 2, sourceId: 222, targetId: 201, weight: 0.95, delayHours: 0.05, relationType: "HTTP_REQUEST" },
                { id: 102, scenarioId: 2, sourceId: 223, targetId: 201, weight: 0.95, delayHours: 0.05, relationType: "HTTP_REQUEST" },
                { id: 103, scenarioId: 2, sourceId: 224, targetId: 202, weight: 0.90, delayHours: 0.10, relationType: "INTERNAL_PROXY" },
                { id: 104, scenarioId: 2, sourceId: 201, targetId: 202, weight: 0.98, delayHours: 0.02, relationType: "FORWARD_TRAFFIC" },
                { id: 105, scenarioId: 2, sourceId: 202, targetId: 203, weight: 0.90, delayHours: 0.05, relationType: "VALIDATE_TOKEN" },
                { id: 106, scenarioId: 2, sourceId: 202, targetId: 204, weight: 0.85, delayHours: 0.08, relationType: "ROUTE_USER_CALLS" },
                { id: 107, scenarioId: 2, sourceId: 202, targetId: 205, weight: 0.85, delayHours: 0.06, relationType: "ROUTE_CATALOG" },
                { id: 108, scenarioId: 2, sourceId: 202, targetId: 207, weight: 0.95, delayHours: 0.10, relationType: "ROUTE_ORDERS" },
                { id: 109, scenarioId: 2, sourceId: 203, targetId: 206, weight: 0.85, delayHours: 0.02, relationType: "CHECK_SESSION_CACHE" },
                { id: 110, scenarioId: 2, sourceId: 203, targetId: 209, weight: 0.90, delayHours: 0.08, relationType: "QUERY_CREDENTIALS" },
                { id: 111, scenarioId: 2, sourceId: 204, targetId: 206, weight: 0.70, delayHours: 0.03, relationType: "CACHE_PROFILE" },
                { id: 112, scenarioId: 2, sourceId: 205, targetId: 208, weight: 0.90, delayHours: 0.05, relationType: "SEARCH_PRODUCTS" },
                { id: 113, scenarioId: 2, sourceId: 207, targetId: 211, weight: 0.95, delayHours: 0.15, relationType: "INVOKE_PAYMENT" },
                { id: 114, scenarioId: 2, sourceId: 207, targetId: 212, weight: 0.90, delayHours: 0.12, relationType: "RESERVE_STOCK" },
                { id: 115, scenarioId: 2, sourceId: 207, targetId: 213, weight: 0.95, delayHours: 0.05, relationType: "PUBLISH_ORDER_PLACED" },
                { id: 116, scenarioId: 2, sourceId: 211, targetId: 210, weight: 0.95, delayHours: 0.50, relationType: "EXECUTE_CHARGE" },
                { id: 117, scenarioId: 2, sourceId: 211, targetId: 215, weight: 0.80, delayHours: 0.10, relationType: "RISK_SCORE_CHECK" },
                { id: 118, scenarioId: 2, sourceId: 212, targetId: 214, weight: 0.95, delayHours: 0.08, relationType: "ACQUIRE_LOCK" },
                { id: 119, scenarioId: 2, sourceId: 213, targetId: 216, weight: 0.85, delayHours: 0.10, relationType: "CONSUME_EVENT" },
                { id: 120, scenarioId: 2, sourceId: 213, targetId: 217, weight: 0.90, delayHours: 0.15, relationType: "CONSUME_EVENT" },
                { id: 121, scenarioId: 2, sourceId: 213, targetId: 220, weight: 0.75, delayHours: 0.30, relationType: "ETL_INGEST" },
                { id: 122, scenarioId: 2, sourceId: 216, targetId: 218, weight: 0.85, delayHours: 0.20, relationType: "SEND_EMAIL_CONFIRMATION" },
                { id: 123, scenarioId: 2, sourceId: 216, targetId: 219, weight: 0.85, delayHours: 0.20, relationType: "SEND_SMS_ALERT" },
                { id: 124, scenarioId: 2, sourceId: 201, targetId: 221, weight: 0.50, delayHours: 0.05, relationType: "EMIT_METRICS" },
                { id: 125, scenarioId: 2, sourceId: 202, targetId: 221, weight: 0.60, delayHours: 0.05, relationType: "EMIT_METRICS" },
                { id: 126, scenarioId: 2, sourceId: 207, targetId: 221, weight: 0.70, delayHours: 0.05, relationType: "EMIT_METRICS" },
                { id: 127, scenarioId: 2, sourceId: 213, targetId: 221, weight: 0.65, delayHours: 0.05, relationType: "EMIT_METRICS" },
                { id: 128, scenarioId: 2, sourceId: 206, targetId: 207, weight: 0.40, delayHours: 0.02, relationType: "FAST_LOOKUP" },
                { id: 129, scenarioId: 2, sourceId: 214, targetId: 205, weight: 0.50, delayHours: 0.10, relationType: "SYNC_AVAILABILITY" },
                { id: 130, scenarioId: 2, sourceId: 217, targetId: 216, weight: 0.60, delayHours: 0.15, relationType: "SHIPPED_EVENT" },
                { id: 131, scenarioId: 2, sourceId: 210, targetId: 207, weight: 0.90, delayHours: 0.20, relationType: "WEBHOOK_CALLBACK" },
                { id: 132, scenarioId: 2, sourceId: 208, targetId: 220, weight: 0.40, delayHours: 1.00, relationType: "CLICKSTREAM_AUDIT" },
                { id: 133, scenarioId: 2, sourceId: 209, targetId: 221, weight: 0.40, delayHours: 0.10, relationType: "DB_TELEMETRY" },
                { id: 134, scenarioId: 2, sourceId: 214, targetId: 221, weight: 0.45, delayHours: 0.10, relationType: "DB_TELEMETRY" },
                { id: 135, scenarioId: 2, sourceId: 215, targetId: 207, weight: 0.85, delayHours: 0.08, relationType: "FLAG_FRAUDULENT" },
                { id: 136, scenarioId: 2, sourceId: 220, targetId: 221, weight: 0.30, delayHours: 0.20, relationType: "HEALTH_CHECK" }
            ]
        },
        {
            id: 3,
            name: "Metropolitan City Critical Infrastructure",
            icon: "⚡",
            category: "Civil Infrastructure",
            description: "Interdependent power, water, hospital, transit, emergency, and telecom grids",
            defaultEvent: {
                name: "Nuclear Plant Grid Scram Incident",
                sourceNodeId: 301,
                severity: 9.0,
                description: "Emergency reactor shutdown cascades through regional 500kV step-down substations"
            },
            nodes: [
                { id: 301, scenarioId: 3, name: "Nuclear Power Plant (North)", category: "Power Grid", xPos: 100, yPos: 200, resilience: 0.90, description: "Base-load 1200MW nuclear electrical generation facility" },
                { id: 302, scenarioId: 3, name: "Gas Turbine Peaker Plant", category: "Power Grid", xPos: 100, yPos: 400, resilience: 0.70, description: "Rapid-dispatch natural gas peak electrical generator" },
                { id: 303, scenarioId: 3, name: "High-Voltage Substation A", category: "Power Grid", xPos: 280, yPos: 200, resilience: 0.80, description: "500kV step-down transmission substation" },
                { id: 304, scenarioId: 3, name: "Regional Substation B (East)", category: "Power Grid", xPos: 280, yPos: 420, resilience: 0.75, description: "Substation feeding commercial and transit districts" },
                { id: 305, scenarioId: 3, name: "Water Filtration Plant", category: "Water Grid", xPos: 460, yPos: 150, resilience: 0.65, description: "Main drinking water treatment and chlorination plant" },
                { id: 306, scenarioId: 3, name: "Potable Water Pumping Hub", category: "Water Grid", xPos: 460, yPos: 300, resilience: 0.60, description: "Pressurized booster pump station for municipal water" },
                { id: 307, scenarioId: 3, name: "Wastewater Treatment Center", category: "Sanitation", xPos: 460, yPos: 500, resilience: 0.70, description: "Sewage treatment and flood storm discharge station" },
                { id: 308, scenarioId: 3, name: "Metro Rail Central Station", category: "Transportation", xPos: 650, yPos: 180, resilience: 0.70, description: "Central subway intersection and electrical rail transit" },
                { id: 309, scenarioId: 3, name: "Traffic Management Center", category: "Transportation", xPos: 650, yPos: 320, resilience: 0.80, description: "Smart signal synchronization and tunnel ventilation control" },
                { id: 310, scenarioId: 3, name: "International Airport Hub", category: "Transportation", xPos: 650, yPos: 480, resilience: 0.85, description: "Major regional passenger and cargo aviation terminal" },
                { id: 311, scenarioId: 3, name: "St. Jude General Hospital", category: "Healthcare", xPos: 840, yPos: 150, resilience: 0.85, description: "Level 1 trauma hospital, ICU, and surgical operating rooms" },
                { id: 312, scenarioId: 3, name: "Mercy Children Hospital", category: "Healthcare", xPos: 840, yPos: 280, resilience: 0.80, description: "Pediatric critical care and emergency facility" },
                { id: 313, scenarioId: 3, name: "Municipal 911 Dispatch Hub", category: "Emergency", xPos: 840, yPos: 420, resilience: 0.90, description: "Computer-aided dispatch for police, fire, and ambulance" },
                { id: 314, scenarioId: 3, name: "Fire Department Headquarters", category: "Emergency", xPos: 840, yPos: 560, resilience: 0.85, description: "Coordinated hazardous material and structural firefighting" },
                { id: 315, scenarioId: 3, name: "Telecom Fiber Central Office", category: "Telecom", xPos: 1020, yPos: 220, resilience: 0.85, description: "Primary backbone fiber interchange and cellular core network" },
                { id: 316, scenarioId: 3, name: "Cellular Tower Cluster East", category: "Telecom", xPos: 1020, yPos: 380, resilience: 0.60, description: "Cell towers supporting commercial mobile communications" },
                { id: 317, scenarioId: 3, name: "Financial District Bank Core", category: "Commercial", xPos: 1020, yPos: 520, resilience: 0.80, description: "High-frequency banking clearinghouses and ATMs" },
                { id: 318, scenarioId: 3, name: "Residential District West", category: "Civic", xPos: 1200, yPos: 160, resilience: 0.40, description: "Dense urban population zone (450,000 residents)" },
                { id: 319, scenarioId: 3, name: "Residential District South", category: "Civic", xPos: 1200, yPos: 320, resilience: 0.45, description: "Suburban residential and educational campus sector" },
                { id: 320, scenarioId: 3, name: "Industrial Chemical Park", category: "Industrial", xPos: 1200, yPos: 480, resilience: 0.55, description: "Refineries and plastics manufacturing plant" },
                { id: 321, scenarioId: 3, name: "Natural Gas Pipeline Feed", category: "Energy", xPos: 100, yPos: 550, resilience: 0.80, description: "Interstate high-pressure gas distribution pipeline" },
                { id: 322, scenarioId: 3, name: "Harbor Maritime Terminal", category: "Logistics", xPos: 460, yPos: 620, resilience: 0.70, description: "Deepwater port handling containerized fuel and dry goods" },
                { id: 323, scenarioId: 3, name: "City Hall & Civil Defense", category: "Government", xPos: 1020, yPos: 80, resilience: 0.90, description: "Crisis command center and emergency management authority" }
            ],
            edges: [
                { id: 201, scenarioId: 3, sourceId: 301, targetId: 303, weight: 0.95, delayHours: 0.10, relationType: "TRANSMITS_POWER" },
                { id: 202, scenarioId: 3, sourceId: 302, targetId: 304, weight: 0.90, delayHours: 0.20, relationType: "TRANSMITS_POWER" },
                { id: 203, scenarioId: 3, sourceId: 321, targetId: 302, weight: 0.95, delayHours: 0.30, relationType: "FUELS_GENERATOR" },
                { id: 204, scenarioId: 3, sourceId: 303, targetId: 305, weight: 0.90, delayHours: 0.10, relationType: "POWERS_FACILITY" },
                { id: 205, scenarioId: 3, sourceId: 303, targetId: 306, weight: 0.90, delayHours: 0.10, relationType: "POWERS_FACILITY" },
                { id: 206, scenarioId: 3, sourceId: 303, targetId: 308, weight: 0.85, delayHours: 0.20, relationType: "POWERS_TRACTION" },
                { id: 207, scenarioId: 3, sourceId: 303, targetId: 311, weight: 0.95, delayHours: 0.10, relationType: "POWERS_HOSPITAL" },
                { id: 208, scenarioId: 3, sourceId: 304, targetId: 309, weight: 0.85, delayHours: 0.15, relationType: "POWERS_SYSTEMS" },
                { id: 209, scenarioId: 3, sourceId: 304, targetId: 310, weight: 0.90, delayHours: 0.10, relationType: "POWERS_AIRPORT" },
                { id: 210, scenarioId: 3, sourceId: 304, targetId: 313, weight: 0.95, delayHours: 0.05, relationType: "POWERS_DISPATCH" },
                { id: 211, scenarioId: 3, sourceId: 304, targetId: 315, weight: 0.90, delayHours: 0.10, relationType: "POWERS_TELECOM" },
                { id: 212, scenarioId: 3, sourceId: 305, targetId: 306, weight: 0.95, delayHours: 0.50, relationType: "FEEDS_WATER" },
                { id: 213, scenarioId: 3, sourceId: 306, targetId: 311, weight: 0.90, delayHours: 0.80, relationType: "SUPPLIES_WATER" },
                { id: 214, scenarioId: 3, sourceId: 306, targetId: 312, weight: 0.90, delayHours: 0.80, relationType: "SUPPLIES_WATER" },
                { id: 215, scenarioId: 3, sourceId: 306, targetId: 314, weight: 0.85, delayHours: 0.50, relationType: "FIRE_HYDRANT_PRESSURE" },
                { id: 216, scenarioId: 3, sourceId: 306, targetId: 318, weight: 0.95, delayHours: 1.00, relationType: "MUNICIPAL_WATER" },
                { id: 217, scenarioId: 3, sourceId: 306, targetId: 319, weight: 0.95, delayHours: 1.00, relationType: "MUNICIPAL_WATER" },
                { id: 218, scenarioId: 3, sourceId: 308, targetId: 318, weight: 0.70, delayHours: 0.50, relationType: "COMMUTE_TRANSIT" },
                { id: 219, scenarioId: 3, sourceId: 308, targetId: 317, weight: 0.80, delayHours: 0.50, relationType: "COMMUTE_TRANSIT" },
                { id: 220, scenarioId: 3, sourceId: 309, targetId: 313, weight: 0.75, delayHours: 0.20, relationType: "EMERGENCY_ROUTING" },
                { id: 221, scenarioId: 3, sourceId: 309, targetId: 314, weight: 0.80, delayHours: 0.20, relationType: "CLEAR_FIRE_LANES" },
                { id: 222, scenarioId: 3, sourceId: 310, targetId: 317, weight: 0.65, delayHours: 2.00, relationType: "BUSINESS_TRAVEL" },
                { id: 223, scenarioId: 3, sourceId: 313, targetId: 311, weight: 0.85, delayHours: 0.30, relationType: "DISPATCH_AMBULANCE" },
                { id: 224, scenarioId: 3, sourceId: 313, targetId: 314, weight: 0.95, delayHours: 0.10, relationType: "DISPATCH_RESCUE" },
                { id: 225, scenarioId: 3, sourceId: 315, targetId: 309, weight: 0.85, delayHours: 0.10, relationType: "TRAFFIC_SENSOR_DATA" },
                { id: 226, scenarioId: 3, sourceId: 315, targetId: 313, weight: 0.95, delayHours: 0.05, relationType: "911_CALL_ROUTING" },
                { id: 227, scenarioId: 3, sourceId: 315, targetId: 316, weight: 0.95, delayHours: 0.10, relationType: "BASEBAND_BACKHAUL" },
                { id: 228, scenarioId: 3, sourceId: 315, targetId: 317, weight: 0.90, delayHours: 0.05, relationType: "FINANCIAL_TRANSACTIONS" },
                { id: 229, scenarioId: 3, sourceId: 316, targetId: 318, weight: 0.80, delayHours: 0.20, relationType: "MOBILE_VOICE_DATA" },
                { id: 230, scenarioId: 3, sourceId: 316, targetId: 319, weight: 0.80, delayHours: 0.20, relationType: "MOBILE_VOICE_DATA" },
                { id: 231, scenarioId: 3, sourceId: 314, targetId: 320, weight: 0.90, delayHours: 0.40, relationType: "HAZMAT_PROTECTION" },
                { id: 232, scenarioId: 3, sourceId: 307, targetId: 320, weight: 0.60, delayHours: 2.00, relationType: "INDUSTRIAL_DISCHARGE" },
                { id: 233, scenarioId: 3, sourceId: 322, targetId: 321, weight: 0.70, delayHours: 6.00, relationType: "LNG_TANKER_OFFLOAD" },
                { id: 234, scenarioId: 3, sourceId: 323, targetId: 313, weight: 0.90, delayHours: 0.20, relationType: "GOVERNANCE_DIRECTIVE" }
            ]
        },
        {
            id: 4,
            name: "Viral Social & Information Propagation",
            icon: "📢",
            category: "Information Dynamics",
            description: "Dynamic spread of breaking news, viral rumors, and narrative cascades across networks",
            defaultEvent: {
                name: "Anonymous Whistleblower Leaks Executive Memos",
                sourceNodeId: 401,
                severity: 8.0,
                description: "Unverified explosive documents leaked, sparking viral speculation across platforms"
            },
            nodes: [
                { id: 401, scenarioId: 4, name: "Anonymous Whistleblower", category: "Primary Source", xPos: 100, yPos: 250, resilience: 0.30, description: "Original leaked document or allegation source" },
                { id: 402, scenarioId: 4, name: "Investigative Tech Blog", category: "Alternative Media", xPos: 280, yPos: 150, resilience: 0.60, description: "Independent technology journalism portal" },
                { id: 403, scenarioId: 4, name: "4chan / Reddit Anonymous Forum", category: "Social Forum", xPos: 280, yPos: 350, resilience: 0.20, description: "Unmoderated rapid speculation imageboard" },
                { id: 404, scenarioId: 4, name: "Tech Influencer X (2M Followers)", category: "Key Influencer", xPos: 460, yPos: 100, resilience: 0.50, description: "High-reach tech personality with podcast platform" },
                { id: 405, scenarioId: 4, name: "Automated Botnet Swarm", category: "Amplifier", xPos: 460, yPos: 260, resilience: 0.10, description: "Coordinated artificial amplification bots" },
                { id: 406, scenarioId: 4, name: "Specialized Subreddit Community", category: "Community", xPos: 460, yPos: 420, resilience: 0.40, description: "Enthusiast community with 800k active members" },
                { id: 407, scenarioId: 4, name: "Venture Capital Signal Group", category: "Private Group", xPos: 460, yPos: 560, resilience: 0.70, description: "Influential Silicon Valley angel and VC syndicate" },
                { id: 408, scenarioId: 4, name: "Associated Press Wire", category: "Mainstream News", xPos: 660, yPos: 120, resilience: 0.90, description: "Syndicated global journalism news agency" },
                { id: 409, scenarioId: 4, name: "Viral TikTok Creator", category: "Short-form Media", xPos: 660, yPos: 280, resilience: 0.30, description: "Short video content creator generating 5M daily views" },
                { id: 410, scenarioId: 4, name: "Independent Fact Checker", category: "Verifier", xPos: 660, yPos: 440, resilience: 0.85, description: "Accredited non-partisan debunking organization" },
                { id: 411, scenarioId: 4, name: "Reuters Financial Desk", category: "Mainstream News", xPos: 660, yPos: 580, resilience: 0.90, description: "Market-moving financial reporting desk" },
                { id: 412, scenarioId: 4, name: "Cable News Network Broadcast", category: "Television", xPos: 860, yPos: 140, resilience: 0.75, description: "Prime-time television cable news station" },
                { id: 413, scenarioId: 4, name: "Trending Algorithm Feed", category: "Platform Engine", xPos: 860, yPos: 280, resilience: 0.40, description: "Social media algorithmic discovery feed" },
                { id: 414, scenarioId: 4, name: "Public Relations Crisis Team", category: "Corporate Defense", xPos: 860, yPos: 440, resilience: 0.80, description: "Corporate communications mitigation office" },
                { id: 415, scenarioId: 4, name: "Stock Market Day Traders", category: "Financial Market", xPos: 860, yPos: 580, resilience: 0.30, description: "Retail trading community and retail options volume" },
                { id: 416, scenarioId: 4, name: "General Public Sentiment", category: "Public Sphere", xPos: 1060, yPos: 200, resilience: 0.30, description: "Broad consensus opinion among everyday consumers" },
                { id: 417, scenarioId: 4, name: "Government Regulatory Agency", category: "Regulators", xPos: 1060, yPos: 360, resilience: 0.85, description: "Federal trade and consumer protection commission" },
                { id: 418, scenarioId: 4, name: "Institutional Hedge Funds", category: "Financial Market", xPos: 1060, yPos: 520, resilience: 0.80, description: "Algorithmic quantitative investment funds" },
                { id: 419, scenarioId: 4, name: "Wikipedia Living Page", category: "Archive", xPos: 1220, yPos: 220, resilience: 0.80, description: "Crowdsourced encyclopedia entry updating in real time" },
                { id: 420, scenarioId: 4, name: "Congressional Oversight Hearing", category: "Legal/Political", xPos: 1220, yPos: 380, resilience: 0.90, description: "Subcommittee inquiry summoning executive witnesses" },
                { id: 421, scenarioId: 4, name: "Brand Equity Valuation", category: "Corporate Metric", xPos: 1220, yPos: 520, resilience: 0.50, description: "Market capitalization and customer trust rating" },
                { id: 422, scenarioId: 4, name: "University Research Lab", category: "Academic", xPos: 280, yPos: 520, resilience: 0.80, description: "Sociological and data science analysis group" }
            ],
            edges: [
                { id: 301, scenarioId: 4, sourceId: 401, targetId: 402, weight: 0.90, delayHours: 2.00, relationType: "LEAKS_TO" },
                { id: 302, scenarioId: 4, sourceId: 401, targetId: 403, weight: 0.95, delayHours: 0.50, relationType: "ANONYMOUS_POST" },
                { id: 303, scenarioId: 4, sourceId: 402, targetId: 404, weight: 0.85, delayHours: 1.00, relationType: "RETWEETED_BY" },
                { id: 304, scenarioId: 4, sourceId: 402, targetId: 408, weight: 0.75, delayHours: 4.00, relationType: "CITED_BY_AP" },
                { id: 305, scenarioId: 4, sourceId: 403, targetId: 405, weight: 0.90, delayHours: 0.20, relationType: "SCRAPED_BY_BOTS" },
                { id: 306, scenarioId: 4, sourceId: 403, targetId: 406, weight: 0.80, delayHours: 0.50, relationType: "CROSSPOSTED" },
                { id: 307, scenarioId: 4, sourceId: 404, targetId: 409, weight: 0.85, delayHours: 1.50, relationType: "REMIXED_ON_TIKTOK" },
                { id: 308, scenarioId: 4, sourceId: 404, targetId: 413, weight: 0.90, delayHours: 0.50, relationType: "BOOSTED_BY_ALGO" },
                { id: 309, scenarioId: 4, sourceId: 405, targetId: 413, weight: 0.95, delayHours: 0.20, relationType: "GAMING_METRICS" },
                { id: 310, scenarioId: 4, sourceId: 406, targetId: 407, weight: 0.65, delayHours: 2.00, relationType: "DISCUSSED_IN_CHAT" },
                { id: 311, scenarioId: 4, sourceId: 406, targetId: 410, weight: 0.70, delayHours: 3.00, relationType: "FLAGGED_FOR_CHECK" },
                { id: 312, scenarioId: 4, sourceId: 407, targetId: 411, weight: 0.80, delayHours: 1.00, relationType: "BACKGROUND_BRIEFING" },
                { id: 313, scenarioId: 4, sourceId: 408, targetId: 412, weight: 0.95, delayHours: 1.00, relationType: "CABLE_SEGMENT" },
                { id: 314, scenarioId: 4, sourceId: 409, targetId: 416, weight: 0.90, delayHours: 2.00, relationType: "VIRAL_EXPOSURE" },
                { id: 315, scenarioId: 4, sourceId: 410, targetId: 416, weight: 0.40, delayHours: 6.00, relationType: "NUANCED_DEBUNK" },
                { id: 316, scenarioId: 4, sourceId: 410, targetId: 414, weight: 0.70, delayHours: 4.00, relationType: "FACTUAL_CORRECTION" },
                { id: 317, scenarioId: 4, sourceId: 411, targetId: 415, weight: 0.90, delayHours: 0.50, relationType: "PANIC_BUY_OR_SELL" },
                { id: 318, scenarioId: 4, sourceId: 411, targetId: 418, weight: 0.85, delayHours: 0.20, relationType: "ALGO_EXECUTION" },
                { id: 319, scenarioId: 4, sourceId: 412, targetId: 416, weight: 0.90, delayHours: 1.50, relationType: "MASS_VIEWERSHIP" },
                { id: 320, scenarioId: 4, sourceId: 413, targetId: 416, weight: 0.95, delayHours: 0.50, relationType: "NOTIFICATION_POPUP" },
                { id: 321, scenarioId: 4, sourceId: 414, targetId: 412, weight: 0.60, delayHours: 3.00, relationType: "OFFICIAL_STATEMENT" },
                { id: 322, scenarioId: 4, sourceId: 415, targetId: 421, weight: 0.85, delayHours: 1.00, relationType: "STOCK_DROP" },
                { id: 323, scenarioId: 4, sourceId: 416, targetId: 417, weight: 0.75, delayHours: 24.0, relationType: "PUBLIC_PRESSURE" },
                { id: 324, scenarioId: 4, sourceId: 416, targetId: 419, weight: 0.70, delayHours: 8.00, relationType: "EDIT_WARS" },
                { id: 325, scenarioId: 4, sourceId: 417, targetId: 420, weight: 0.85, delayHours: 48.0, relationType: "CONGRESSIONAL_SUBPOENA" },
                { id: 326, scenarioId: 4, sourceId: 418, targetId: 421, weight: 0.90, delayHours: 0.50, relationType: "SHORT_SELLING" },
                { id: 327, scenarioId: 4, sourceId: 420, targetId: 421, weight: 0.95, delayHours: 12.0, relationType: "REPUTATIONAL_CRISIS" },
                { id: 328, scenarioId: 4, sourceId: 401, targetId: 422, weight: 0.50, delayHours: 8.00, relationType: "DATA_ARCHIVE" },
                { id: 329, scenarioId: 4, sourceId: 422, targetId: 410, weight: 0.80, delayHours: 12.0, relationType: "METHODOLOGY_PEER_REVIEW" },
                { id: 330, scenarioId: 4, sourceId: 408, targetId: 416, weight: 0.85, delayHours: 3.00, relationType: "GLOBAL_HEADLINES" },
                { id: 331, scenarioId: 4, sourceId: 414, targetId: 416, weight: 0.55, delayHours: 4.00, relationType: "DAMAGE_CONTROL_ADS" },
                { id: 332, scenarioId: 4, sourceId: 405, targetId: 404, weight: 0.70, delayHours: 0.30, relationType: "REPLY_SPAM" }
            ]
        }
    ],

    dbSchema: [
        {
            table: "scenarios",
            description: "Root scenarios modeling distinct domain dependency networks (3NF)",
            columns: [
                { name: "id", type: "INTEGER PRIMARY KEY", key: "PK" },
                { name: "name", type: "VARCHAR(100) UNIQUE", key: "" },
                { name: "description", type: "TEXT", key: "" },
                { name: "created_at", type: "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", key: "" }
            ]
        },
        {
            table: "nodes",
            description: "Network entities with coordinates, categories and resilience damping factor",
            columns: [
                { name: "id", type: "INTEGER PRIMARY KEY", key: "PK" },
                { name: "scenario_id", type: "INTEGER", key: "FK -> scenarios.id" },
                { name: "name", type: "VARCHAR(100)", key: "" },
                { name: "category", type: "VARCHAR(50)", key: "" },
                { name: "x_pos", type: "DOUBLE", key: "" },
                { name: "y_pos", type: "DOUBLE", key: "" },
                { name: "resilience", type: "DOUBLE [0.0 - 1.0]", key: "" },
                { name: "description", type: "TEXT", key: "" }
            ]
        },
        {
            table: "edges",
            description: "Directed influence/dependency links with multipliers and propagation delay",
            columns: [
                { name: "id", type: "INTEGER PRIMARY KEY", key: "PK" },
                { name: "scenario_id", type: "INTEGER", key: "FK -> scenarios.id" },
                { name: "source_id", type: "INTEGER", key: "FK -> nodes.id" },
                { name: "target_id", type: "INTEGER", key: "FK -> nodes.id" },
                { name: "weight", type: "DOUBLE [0.0 - 1.0]", key: "" },
                { name: "delay_hours", type: "DOUBLE", key: "" },
                { name: "relation_type", type: "VARCHAR(50)", key: "" }
            ]
        },
        {
            table: "events",
            description: "Triggered incident events that initiate cascade ripple simulations",
            columns: [
                { name: "id", type: "INTEGER PRIMARY KEY", key: "PK" },
                { name: "scenario_id", type: "INTEGER", key: "FK -> scenarios.id" },
                { name: "name", type: "VARCHAR(100)", key: "" },
                { name: "source_node_id", type: "INTEGER", key: "FK -> nodes.id" },
                { name: "severity", type: "DOUBLE (1.0 - 10.0)", key: "" },
                { name: "triggered_at", type: "TIMESTAMP", key: "" }
            ]
        },
        {
            table: "simulation_runs",
            description: "Log of executed graph propagation simulations and ADSA algorithms",
            columns: [
                { name: "id", type: "INTEGER PRIMARY KEY", key: "PK" },
                { name: "event_id", type: "INTEGER", key: "FK -> events.id" },
                { name: "algorithm_used", type: "VARCHAR(50)", key: "" },
                { name: "model_type", type: "VARCHAR(30)", key: "" },
                { name: "threshold", type: "DOUBLE", key: "" },
                { name: "duration_ms", type: "DOUBLE", key: "" },
                { name: "run_at", type: "TIMESTAMP", key: "" }
            ]
        },
        {
            table: "mitigation_plans",
            description: "Greedy Advisor output: recommended shielded components and % reduction",
            columns: [
                { name: "id", type: "INTEGER PRIMARY KEY", key: "PK" },
                { name: "run_id", type: "INTEGER", key: "FK -> simulation_runs.id" },
                { name: "protected_nodes", type: "TEXT", key: "" },
                { name: "impact_reduction_pct", type: "DOUBLE", key: "" },
                { name: "created_at", type: "TIMESTAMP", key: "" }
            ]
        },
        {
            table: "audit_log",
            description: "Immutable security & operation audit log populated via SQL Trigger",
            columns: [
                { name: "id", type: "INTEGER PRIMARY KEY", key: "PK" },
                { name: "user_id", type: "INTEGER", key: "FK -> users.id" },
                { name: "action", type: "TEXT", key: "" },
                { name: "timestamp", type: "TIMESTAMP", key: "" }
            ]
        }
    ],

    sampleQueries: [
        {
            title: "Top 5 Most Vulnerable Components (Resilience < 0.5)",
            sql: "SELECT id, name, category, resilience FROM nodes WHERE resilience < 0.50 ORDER BY resilience ASC LIMIT 5;"
        },
        {
            title: "High-Impact Bottleneck Edges (Weight >= 0.90)",
            sql: "SELECT e.id, s.name AS source_node, t.name AS target_node, e.weight, e.delay_hours, e.relation_type FROM edges e JOIN nodes s ON e.source_id = s.id JOIN nodes t ON e.target_id = t.id WHERE e.weight >= 0.90 ORDER BY e.weight DESC;"
        },
        {
            title: "Scenario Summary (Nodes, Edges, Risk Aggregation)",
            sql: "SELECT s.id, s.name, COUNT(DISTINCT n.id) AS total_nodes, COUNT(DISTINCT e.id) AS total_edges FROM scenarios s LEFT JOIN nodes n ON s.id = n.scenario_id LEFT JOIN edges e ON s.id = e.scenario_id GROUP BY s.id, s.name;"
        },
        {
            title: "Recent Audit Trail Events",
            sql: "SELECT id, action, timestamp FROM audit_log ORDER BY timestamp DESC LIMIT 10;"
        }
    ]
};
