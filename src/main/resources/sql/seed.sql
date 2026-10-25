-- ====================================================================
-- RippleMap Seed Data: 4 Complete Realistic Scenarios + Seed Users
-- ====================================================================

-- Default Users:
-- admin / admin123 (SHA-256 with salt 'salt_admin_2026')
-- Hash of 'salt_admin_2026admin123' = 4a123f898fbb984fb657cbaec74bb7c66914f7b60589d81648a70a84594c3ceb
-- analyst / analyst123 (SHA-256 with salt 'salt_analyst_2026')
-- Hash of 'salt_analyst_2026analyst123' = b7fa4f0081d4e0e47c1b527715f5e43a9b70b55214ecfc3f7aa36ecfdb61e687

INSERT OR IGNORE INTO users (id, username, password_hash, salt, role) VALUES 
(1, 'admin', '1ccda83bd2d79ebda951f38ccdd805787f3369f08598168f1ba9ad3e47c30a5e', 'salt_admin_2026', 'ADMIN'),
(2, 'analyst', '950a4459334c336612c06cb68af7a484a0e5d7c91d98325f8d2f8bbe3aef8f5d', 'salt_analyst_2026', 'ANALYST');

-- ====================================================================
-- SCENARIO 1: Global Electronics Supply Chain
-- ====================================================================
INSERT OR IGNORE INTO scenarios (id, name, description) VALUES 
(1, 'Global Electronics Supply Chain', 'High-tech electronics supply network from rare-earth extraction to retail shelves');

-- Nodes for Scenario 1 (25 nodes)
INSERT OR IGNORE INTO nodes (id, scenario_id, name, category, x_pos, y_pos, resilience, description) VALUES
(101, 1, 'Lithium Mines (Chile)', 'Raw Material', 100, 150, 0.4, 'Primary brine extraction for battery cells'),
(102, 1, 'Cobalt Refinery (Congo)', 'Raw Material', 100, 300, 0.3, 'Key cathode material producer'),
(103, 1, 'Silicon Smelter (Norway)', 'Raw Material', 100, 450, 0.6, 'High-purity polysilicon for wafer fabrication'),
(104, 1, 'Wafer Foundry TSMC (Taiwan)', 'Manufacturing', 280, 200, 0.8, 'Advanced 3nm/5nm logic wafer fabrication'),
(105, 1, 'Memory Fab Samsung (S.Korea)', 'Manufacturing', 280, 350, 0.7, 'DRAM and NAND Flash solid-state memory'),
(106, 1, 'Battery Cell Plant CATL (China)', 'Manufacturing', 280, 500, 0.5, 'EV and device lithium-ion pack assembly'),
(107, 1, 'PCB Fabrication (Shenzhen)', 'Manufacturing', 450, 180, 0.6, 'Multilayer printed circuit board production'),
(108, 1, 'Foxconn Assembly Hub (Zhengzhou)', 'Assembly', 480, 350, 0.7, 'Final smartphone and consumer device integration'),
(109, 1, 'Pegatron Facility (Shanghai)', 'Assembly', 480, 520, 0.5, 'Laptop and wearable system assembly'),
(110, 1, 'Port of Shanghai', 'Logistics Hub', 650, 200, 0.8, 'World highest throughput container seaport'),
(111, 1, 'Port of Singapore', 'Logistics Hub', 650, 350, 0.85, 'Major international transshipment maritime hub'),
(112, 1, 'Port of Rotterdam', 'Logistics Hub', 650, 500, 0.75, 'Primary gateway for European freight distribution'),
(113, 1, 'Port of Los Angeles', 'Logistics Hub', 820, 180, 0.6, 'North American west coast trans-pacific terminal'),
(114, 1, 'Air Cargo Hub Frankfurt', 'Air Logistics', 820, 330, 0.8, 'High-priority express air freight terminal'),
(115, 1, 'Air Cargo Hub Anchorage', 'Air Logistics', 820, 480, 0.7, 'Trans-polar refueling and cargo transfer hub'),
(116, 1, 'FedEx SuperHub (Memphis)', 'Distribution', 980, 220, 0.85, 'Overnight sorting and nationwide fulfillment'),
(117, 1, 'DHL Central Hub (Leipzig)', 'Distribution', 980, 380, 0.8, 'European continent express parcel sorting'),
(118, 1, 'Amazon Fulfillment (Dallas)', 'Distribution', 980, 540, 0.7, 'Mega-warehouse for regional e-commerce stock'),
(119, 1, 'BestBuy US West Retailers', 'Retail', 1150, 150, 0.5, 'Brick-and-mortar retail outlets across western US'),
(120, 1, 'Apple Stores North America', 'Retail', 1150, 270, 0.8, 'Flagship direct consumer electronics stores'),
(121, 1, 'MediaMarkt Europe Stores', 'Retail', 1150, 390, 0.6, 'Major European consumer tech store network'),
(122, 1, 'Amazon Prime Delivery Fleet', 'Last-Mile', 1150, 510, 0.65, 'Direct doorstep delivery vehicle network'),
(123, 1, 'EV Auto Assembly (Munich)', 'Automotive', 1150, 630, 0.5, 'Automaker relying on microchips and batteries'),
(124, 1, 'Telecom Tower Rollout (Global)', 'Enterprise', 1300, 300, 0.7, '5G infrastructure base station upgrades'),
(125, 1, 'Healthcare Equipment Vendor', 'Enterprise', 1300, 480, 0.9, 'Hospital ventilators and diagnostic scanners');

-- Edges for Scenario 1 (35 directed edges)
INSERT OR IGNORE INTO edges (scenario_id, source_id, target_id, weight, delay_hours, relation_type) VALUES
(1, 101, 106, 0.85, 48.0, 'SUPPLIES'),
(1, 102, 106, 0.90, 72.0, 'SUPPLIES'),
(1, 103, 104, 0.95, 36.0, 'SUPPLIES'),
(1, 103, 105, 0.80, 36.0, 'SUPPLIES'),
(1, 104, 107, 0.75, 24.0, 'DELIVERS_TO'),
(1, 104, 108, 0.95, 18.0, 'DELIVERS_TO'),
(1, 104, 109, 0.70, 24.0, 'DELIVERS_TO'),
(1, 105, 108, 0.85, 20.0, 'DELIVERS_TO'),
(1, 105, 109, 0.80, 20.0, 'DELIVERS_TO'),
(1, 106, 108, 0.75, 30.0, 'DELIVERS_TO'),
(1, 106, 123, 0.90, 60.0, 'SUPPLIES'),
(1, 107, 108, 0.90, 12.0, 'INTEGRATES'),
(1, 107, 109, 0.85, 12.0, 'INTEGRATES'),
(1, 108, 110, 0.95, 16.0, 'SHIPS_VIA'),
(1, 108, 114, 0.70, 8.0,  'EXPRESS_AIR'),
(1, 109, 110, 0.85, 14.0, 'SHIPS_VIA'),
(1, 109, 111, 0.60, 28.0, 'SHIPS_VIA'),
(1, 110, 113, 0.95, 120.0, 'OCEAN_FREIGHT'),
(1, 110, 112, 0.85, 168.0, 'OCEAN_FREIGHT'),
(1, 111, 112, 0.75, 144.0, 'OCEAN_FREIGHT'),
(1, 111, 113, 0.65, 160.0, 'OCEAN_FREIGHT'),
(1, 112, 117, 0.80, 18.0, 'INLAND_FREIGHT'),
(1, 112, 121, 0.60, 24.0, 'DISTRIBUTES_TO'),
(1, 113, 116, 0.90, 24.0, 'INLAND_RAIL'),
(1, 113, 118, 0.85, 28.0, 'TRUCK_CORRIDOR'),
(1, 114, 117, 0.85, 6.0,  'AIR_TRANSFER'),
(1, 115, 116, 0.90, 8.0,  'AIR_CARGO'),
(1, 116, 119, 0.80, 12.0, 'FULFILLS'),
(1, 116, 120, 0.95, 10.0, 'FULFILLS'),
(1, 117, 121, 0.85, 14.0, 'FULFILLS'),
(1, 117, 123, 0.75, 16.0, 'DELIVERS_PARTS'),
(1, 118, 119, 0.60, 12.0, 'RESTOCKS'),
(1, 118, 122, 0.95, 6.0,  'LAST_MILE_DISPATCH'),
(1, 120, 124, 0.50, 48.0, 'ENTERPRISE_CONTRACT'),
(1, 104, 125, 0.85, 36.0, 'CRITICAL_CHIP_SUPPLY');

-- ====================================================================
-- SCENARIO 2: Cloud Microservices Architecture
-- ====================================================================
INSERT OR IGNORE INTO scenarios (id, name, description) VALUES 
(2, 'Cloud Microservices Architecture', 'Resilient distributed backend handling user traffic, payments, caching, and stream processing');

-- Nodes for Scenario 2 (24 nodes)
INSERT OR IGNORE INTO nodes (id, scenario_id, name, category, x_pos, y_pos, resilience, description) VALUES
(201, 2, 'Cloudflare Edge CDN', 'Edge Network', 80, 300, 0.9, 'Anycast DNS and DDOS edge protection proxy'),
(202, 2, 'Kong API Gateway', 'Gateway', 220, 300, 0.8, 'Central routing, rate limiting, and SSL termination'),
(203, 2, 'OAuth2 Auth Service', 'Security', 380, 150, 0.85, 'JWT token generation and credential verification'),
(204, 2, 'User Profile Service', 'Core Service', 380, 300, 0.7, 'User accounts, preferences, and session data'),
(205, 2, 'Product Catalog API', 'Core Service', 380, 450, 0.75, 'Product information, categories, and tags'),
(206, 2, 'Redis Session Cache', 'Cache Tier', 550, 120, 0.6, 'In-memory distributed key-value cache'),
(207, 2, 'Order Management API', 'Core Service', 550, 260, 0.75, 'Order placement, workflows, and state machine'),
(208, 2, 'Elasticsearch Cluster', 'Search Tier', 550, 420, 0.7, 'Full-text indexing and faceted product search'),
(209, 2, 'Postgres Auth DB', 'Database Tier', 550, 560, 0.85, 'Relational ACID store for user credentials'),
(210, 2, 'Stripe Payment Gateway', 'External Partner', 720, 120, 0.9, 'Third-party PCI-compliant card processor'),
(211, 2, 'Payment Processing Worker', 'Fintech', 720, 240, 0.8, 'Asynchronous payment capture and webhook handler'),
(212, 2, 'Inventory Lock Service', 'Core Service', 720, 360, 0.7, 'Pessimistic inventory locking and stock allocation'),
(213, 2, 'Kafka Event Bus', 'Messaging Tier', 720, 500, 0.85, 'High-throughput distributed append-only event stream'),
(214, 2, 'Postgres Inventory DB', 'Database Tier', 900, 180, 0.8, 'Master-replica database for SKU quantities'),
(215, 2, 'Fraud Detection Engine', 'AI/Security', 900, 300, 0.75, 'Real-time ML risk scoring on suspicious orders'),
(216, 2, 'Notification Service', 'Worker Tier', 900, 440, 0.65, 'Transactional email, SMS, and mobile push dispatch'),
(217, 2, 'Fulfillment Dispatch API', 'Logistics', 900, 560, 0.7, 'Warehouse picking ticket generation service'),
(218, 2, 'SendGrid Email Gateway', 'External Partner', 1080, 180, 0.85, 'External SMTP delivery service'),
(219, 2, 'Twilio SMS Gateway', 'External Partner', 1080, 300, 0.85, 'Telecom SMS messaging provider'),
(220, 2, 'ClickHouse Analytics DB', 'Data Warehouse', 1080, 440, 0.75, 'Columnar analytical OLAP store'),
(221, 2, 'Datadog APM Collector', 'Observability', 1080, 580, 0.9, 'Metrics, distributed traces, and log aggregation'),
(222, 2, 'Customer iOS/Android App', 'Client Frontend', 80, 150, 0.6, 'Mobile native client application'),
(223, 2, 'Next.js Web Storefront', 'Client Frontend', 80, 450, 0.6, 'React server-side rendered web app'),
(224, 2, 'Admin Operations Portal', 'Client Frontend', 80, 600, 0.8, 'Internal staff dashboard for customer support');

-- Edges for Scenario 2 (36 directed edges)
INSERT OR IGNORE INTO edges (scenario_id, source_id, target_id, weight, delay_hours, relation_type) VALUES
(2, 222, 201, 0.95, 0.05, 'HTTP_REQUEST'),
(2, 223, 201, 0.95, 0.05, 'HTTP_REQUEST'),
(2, 224, 202, 0.90, 0.1,  'INTERNAL_PROXY'),
(2, 201, 202, 0.98, 0.02, 'FORWARD_TRAFFIC'),
(2, 202, 203, 0.90, 0.05, 'VALIDATE_TOKEN'),
(2, 202, 204, 0.85, 0.08, 'ROUTE_USER_CALLS'),
(2, 202, 205, 0.85, 0.06, 'ROUTE_CATALOG'),
(2, 202, 207, 0.95, 0.1,  'ROUTE_ORDERS'),
(2, 203, 206, 0.85, 0.02, 'CHECK_SESSION_CACHE'),
(2, 203, 209, 0.90, 0.08, 'QUERY_CREDENTIALS'),
(2, 204, 206, 0.70, 0.03, 'CACHE_PROFILE'),
(2, 205, 208, 0.90, 0.05, 'SEARCH_PRODUCTS'),
(2, 207, 211, 0.95, 0.15, 'INVOKE_PAYMENT'),
(2, 207, 212, 0.90, 0.12, 'RESERVE_STOCK'),
(2, 207, 213, 0.95, 0.05, 'PUBLISH_ORDER_PLACED'),
(2, 211, 210, 0.95, 0.5,  'EXECUTE_CHARGE'),
(2, 211, 215, 0.80, 0.1,  'RISK_SCORE_CHECK'),
(2, 212, 214, 0.95, 0.08, 'ACQUIRE_LOCK'),
(2, 213, 216, 0.85, 0.1,  'CONSUME_EVENT'),
(2, 213, 217, 0.90, 0.15, 'CONSUME_EVENT'),
(2, 213, 220, 0.75, 0.3,  'ETL_INGEST'),
(2, 216, 218, 0.85, 0.2,  'SEND_EMAIL_CONFIRMATION'),
(2, 216, 219, 0.85, 0.2,  'SEND_SMS_ALERT'),
(2, 201, 221, 0.50, 0.05, 'EMIT_METRICS'),
(2, 202, 221, 0.60, 0.05, 'EMIT_METRICS'),
(2, 207, 221, 0.70, 0.05, 'EMIT_METRICS'),
(2, 213, 221, 0.65, 0.05, 'EMIT_METRICS'),
(2, 206, 207, 0.40, 0.02, 'FAST_LOOKUP'),
(2, 214, 205, 0.50, 0.1,  'SYNC_AVAILABILITY'),
(2, 217, 216, 0.60, 0.15, 'SHIPPED_EVENT'),
(2, 210, 207, 0.90, 0.2,  'WEBHOOK_CALLBACK'),
(2, 208, 220, 0.40, 1.0,  'CLICKSTREAM_AUDIT'),
(2, 209, 221, 0.40, 0.1,  'DB_TELEMETRY'),
(2, 214, 221, 0.45, 0.1,  'DB_TELEMETRY'),
(2, 215, 207, 0.85, 0.08, 'FLAG_FRAUDULENT'),
(2, 220, 221, 0.30, 0.2,  'HEALTH_CHECK');

-- ====================================================================
-- SCENARIO 3: Metropolitan City Critical Infrastructure
-- ====================================================================
INSERT OR IGNORE INTO scenarios (id, name, description) VALUES 
(3, 'Metropolitan City Critical Infrastructure', 'Interdependent power, water, hospital, transit, emergency, and telecom grids');

-- Nodes for Scenario 3 (23 nodes)
INSERT OR IGNORE INTO nodes (id, scenario_id, name, category, x_pos, y_pos, resilience, description) VALUES
(301, 3, 'Nuclear Power Plant (North)', 'Power Grid', 100, 200, 0.9, 'Base-load 1200MW nuclear electrical generation facility'),
(302, 3, 'Gas Turbine Peaker Plant', 'Power Grid', 100, 400, 0.7, 'Rapid-dispatch natural gas peak electrical generator'),
(303, 3, 'High-Voltage Substation A', 'Power Grid', 280, 200, 0.8, '500kV step-down transmission substation'),
(304, 3, 'Regional Substation B (East)', 'Power Grid', 280, 420, 0.75, 'Substation feeding commercial and transit districts'),
(305, 3, 'Water Filtration Plant', 'Water Grid', 460, 150, 0.65, 'Main drinking water treatment and chlorination plant'),
(306, 3, 'Potable Water Pumping Hub', 'Water Grid', 460, 300, 0.6, 'Pressurized booster pump station for municipal water'),
(307, 3, 'Wastewater Treatment Center', 'Sanitation', 460, 500, 0.7, 'Sewage treatment and flood storm discharge station'),
(308, 3, 'Metro Rail Central Station', 'Transportation', 650, 180, 0.7, 'Central subway intersection and electrical rail transit'),
(309, 3, 'Traffic Management Center', 'Transportation', 650, 320, 0.8, 'Smart signal synchronization and tunnel ventilation control'),
(310, 3, 'International Airport Hub', 'Transportation', 650, 480, 0.85, 'Major regional passenger and cargo aviation terminal'),
(311, 3, 'St. Jude General Hospital', 'Healthcare', 840, 150, 0.85, 'Level 1 trauma hospital, ICU, and surgical operating rooms'),
(312, 3, 'Mercy Children Hospital', 'Healthcare', 840, 280, 0.8, 'Pediatric critical care and emergency facility'),
(313, 3, 'Municipal 911 Dispatch Hub', 'Emergency', 840, 420, 0.9, 'Computer-aided dispatch for police, fire, and ambulance'),
(314, 3, 'Fire Department Headquarters', 'Emergency', 840, 560, 0.85, 'Coordinated hazardous material and structural firefighting'),
(315, 3, 'Telecom Fiber Central Office', 'Telecom', 1020, 220, 0.85, 'Primary backbone fiber interchange and cellular core network'),
(316, 3, 'Cellular Tower Cluster East', 'Telecom', 1020, 380, 0.6, 'Cell towers supporting commercial mobile communications'),
(317, 3, 'Financial District Bank Core', 'Commercial', 1020, 520, 0.8, 'High-frequency banking clearinghouses and ATMs'),
(318, 3, 'Residential District West', 'Civic', 1200, 160, 0.4, 'Dense urban population zone (450,000 residents)'),
(319, 3, 'Residential District South', 'Civic', 1200, 320, 0.45, 'Suburban residential and educational campus sector'),
(320, 3, 'Industrial Chemical Park', 'Industrial', 1200, 480, 0.55, 'Refineries and plastics manufacturing plant'),
(321, 3, 'Natural Gas Pipeline Feed', 'Energy', 100, 550, 0.8, 'Interstate high-pressure gas distribution pipeline'),
(322, 3, 'Harbor Maritime Terminal', 'Logistics', 460, 620, 0.7, 'Deepwater port handling containerized fuel and dry goods'),
(323, 3, 'City Hall & Civil Defense', 'Government', 1020, 80, 0.9, 'Crisis command center and emergency management authority');

-- Edges for Scenario 3 (34 directed edges)
INSERT OR IGNORE INTO edges (scenario_id, source_id, target_id, weight, delay_hours, relation_type) VALUES
(3, 301, 303, 0.95, 0.1, 'TRANSMITS_POWER'),
(3, 302, 304, 0.90, 0.2, 'TRANSMITS_POWER'),
(3, 321, 302, 0.95, 0.3, 'FUELS_GENERATOR'),
(3, 303, 305, 0.90, 0.1, 'POWERS_FACILITY'),
(3, 303, 306, 0.90, 0.1, 'POWERS_FACILITY'),
(3, 303, 308, 0.85, 0.2, 'POWERS_TRACTION'),
(3, 303, 311, 0.95, 0.1, 'POWERS_HOSPITAL'),
(3, 304, 309, 0.85, 0.15, 'POWERS_SYSTEMS'),
(3, 304, 310, 0.90, 0.1,  'POWERS_AIRPORT'),
(3, 304, 313, 0.95, 0.05, 'POWERS_DISPATCH'),
(3, 304, 315, 0.90, 0.1,  'POWERS_TELECOM'),
(3, 305, 306, 0.95, 0.5,  'FEEDS_WATER'),
(3, 306, 311, 0.90, 0.8,  'SUPPLIES_WATER'),
(3, 306, 312, 0.90, 0.8,  'SUPPLIES_WATER'),
(3, 306, 314, 0.85, 0.5,  'FIRE_HYDRANT_PRESSURE'),
(3, 306, 318, 0.95, 1.0,  'MUNICIPAL_WATER'),
(3, 306, 319, 0.95, 1.0,  'MUNICIPAL_WATER'),
(3, 308, 318, 0.70, 0.5,  'COMMUTE_TRANSIT'),
(3, 308, 317, 0.80, 0.5,  'COMMUTE_TRANSIT'),
(3, 309, 313, 0.75, 0.2,  'EMERGENCY_ROUTING'),
(3, 309, 314, 0.80, 0.2,  'CLEAR_FIRE_LANES'),
(3, 310, 317, 0.65, 2.0,  'BUSINESS_TRAVEL'),
(3, 313, 311, 0.85, 0.3,  'DISPATCH_AMBULANCE'),
(3, 313, 314, 0.95, 0.1,  'DISPATCH_RESCUE'),
(3, 315, 309, 0.85, 0.1,  'TRAFFIC_SENSOR_DATA'),
(3, 315, 313, 0.95, 0.05, '911_CALL_ROUTING'),
(3, 315, 316, 0.95, 0.1,  'BASEBAND_BACKHAUL'),
(3, 315, 317, 0.90, 0.05, 'FINANCIAL_TRANSACTIONS'),
(3, 316, 318, 0.80, 0.2,  'MOBILE_VOICE_DATA'),
(3, 316, 319, 0.80, 0.2,  'MOBILE_VOICE_DATA'),
(3, 314, 320, 0.90, 0.4,  'HAZMAT_PROTECTION'),
(3, 307, 320, 0.60, 2.0,  'INDUSTRIAL_DISCHARGE'),
(3, 322, 321, 0.70, 6.0,  'LNG_TANKER_OFFLOAD'),
(3, 323, 313, 0.90, 0.2,  'GOVERNANCE_DIRECTIVE');

-- ====================================================================
-- SCENARIO 4: Viral Social & Information Propagation
-- ====================================================================
INSERT OR IGNORE INTO scenarios (id, name, description) VALUES 
(4, 'Viral Social and Information Propagation', 'Dynamic spread of breaking news, viral rumors, and narrative cascades across networks');

-- Nodes for Scenario 4 (22 nodes)
INSERT OR IGNORE INTO nodes (id, scenario_id, name, category, x_pos, y_pos, resilience, description) VALUES
(401, 4, 'Anonymous Whistleblower', 'Primary Source', 100, 250, 0.3, 'Original leaked document or allegation source'),
(402, 4, 'Investigative Tech Blog', 'Alternative Media', 280, 150, 0.6, 'Independent technology journalism portal'),
(403, 4, '4chan / Reddit Anonymous Forum', 'Social Forum', 280, 350, 0.2, 'Unmoderated rapid speculation imageboard'),
(404, 4, 'Tech Influencer X (2M Followers)', 'Key Influencer', 460, 100, 0.5, 'High-reach tech personality with podcast platform'),
(405, 4, 'Automated Botnet Swarm', 'Amplifier', 460, 260, 0.1, 'Coordinated artificial amplification bots'),
(406, 4, 'Specialized Subreddit Community', 'Community', 460, 420, 0.4, 'Enthusiast community with 800k active members'),
(407, 4, 'Venture Capital Signal Group', 'Private Group', 460, 560, 0.7, 'Influential Silicon Valley angel and VC syndicate'),
(408, 4, 'Associated Press Wire', 'Mainstream News', 660, 120, 0.9, 'Syndicated global journalism news agency'),
(409, 4, 'Viral TikTok Creator', 'Short-form Media', 660, 280, 0.3, 'Short video content creator generating 5M daily views'),
(410, 4, 'Independent Fact Checker', 'Verifier', 660, 440, 0.85, 'Accredited non-partisan debunking organization'),
(411, 4, 'Reuters Financial Desk', 'Mainstream News', 660, 580, 0.9, 'Market-moving financial reporting desk'),
(412, 4, 'Cable News Network Broadcast', 'Television', 860, 140, 0.75, 'Prime-time television cable news station'),
(413, 4, 'Trending Algorithm Feed', 'Platform Engine', 860, 280, 0.4, 'Social media algorithmic discovery feed'),
(414, 4, 'Public Relations Crisis Team', 'Corporate Defense', 860, 440, 0.8, 'Corporate communications mitigation office'),
(415, 4, 'Stock Market Day Traders', 'Financial Market', 860, 580, 0.3, 'Retail trading community and retail options volume'),
(416, 4, 'General Public Sentiment', 'Public Sphere', 1060, 200, 0.3, 'Broad consensus opinion among everyday consumers'),
(417, 4, 'Government Regulatory Agency', 'Regulators', 1060, 360, 0.85, 'Federal trade and consumer protection commission'),
(418, 4, 'Institutional Hedge Funds', 'Financial Market', 1060, 520, 0.8, 'Algorithmic quantitative investment funds'),
(419, 4, 'Wikipedia Living Page', 'Archive', 1220, 220, 0.8, 'Crowdsourced encyclopedia entry updating in real time'),
(420, 4, 'Congressional Oversight Hearing', 'Legal/Political', 1220, 380, 0.9, 'Subcommittee inquiry summoning executive witnesses'),
(421, 4, 'Brand Equity Valuation', 'Corporate Metric', 1220, 520, 0.5, 'Market capitalization and customer trust rating'),
(422, 4, 'University Research Lab', 'Academic', 280, 520, 0.8, 'Sociological and data science analysis group');

-- Edges for Scenario 4 (32 directed edges)
INSERT OR IGNORE INTO edges (scenario_id, source_id, target_id, weight, delay_hours, relation_type) VALUES
(4, 401, 402, 0.90, 2.0,  'LEAKS_TO'),
(4, 401, 403, 0.95, 0.5,  'ANONYMOUS_POST'),
(4, 402, 404, 0.85, 1.0,  'RETWEETED_BY'),
(4, 402, 408, 0.75, 4.0,  'CITED_BY_AP'),
(4, 403, 405, 0.90, 0.2,  'SCRAPED_BY_BOTS'),
(4, 403, 406, 0.80, 0.5,  'CROSSPOSTED'),
(4, 404, 409, 0.85, 1.5,  'REMIXED_ON_TIKTOK'),
(4, 404, 413, 0.90, 0.5,  'BOOSTED_BY_ALGO'),
(4, 405, 413, 0.95, 0.2,  'GAMING_METRICS'),
(4, 406, 407, 0.65, 2.0,  'DISCUSSED_IN_CHAT'),
(4, 406, 410, 0.70, 3.0,  'FLAGGED_FOR_CHECK'),
(4, 407, 411, 0.80, 1.0,  'BACKGROUND_BRIEFING'),
(4, 408, 412, 0.95, 1.0,  'CABLE_SEGMENT'),
(4, 409, 416, 0.90, 2.0,  'VIRAL_EXPOSURE'),
(4, 410, 416, 0.40, 6.0,  'NUANCED_DEBUNK'),
(4, 410, 414, 0.70, 4.0,  'FACTUAL_CORRECTION'),
(4, 411, 415, 0.90, 0.5,  'PANIC_BUY_OR_SELL'),
(4, 411, 418, 0.85, 0.2,  'ALGO_EXECUTION'),
(4, 412, 416, 0.90, 1.5,  'MASS_VIEWERSHIP'),
(4, 413, 416, 0.95, 0.5,  'NOTIFICATION_POPUP'),
(4, 414, 412, 0.60, 3.0,  'OFFICIAL_STATEMENT'),
(4, 415, 421, 0.85, 1.0,  'STOCK_DROP'),
(4, 416, 417, 0.75, 24.0, 'PUBLIC_PRESSURE'),
(4, 416, 419, 0.70, 8.0,  'EDIT_WARS'),
(4, 417, 420, 0.85, 48.0, 'CONGRESSIONAL_SUBPOENA'),
(4, 418, 421, 0.90, 0.5,  'SHORT_SELLING'),
(4, 420, 421, 0.95, 12.0, 'REPUTATIONAL_CRISIS'),
(4, 401, 422, 0.50, 8.0,  'DATA_ARCHIVE'),
(4, 422, 410, 0.80, 12.0, 'METHODOLOGY_PEER_REVIEW'),
(4, 408, 416, 0.85, 3.0,  'GLOBAL_HEADLINES'),
(4, 414, 416, 0.55, 4.0,  'DAMAGE_CONTROL_ADS'),
(4, 405, 404, 0.70, 0.3,  'REPLY_SPAM');

-- Sample Initial Events for immediate testing & demonstration
INSERT OR IGNORE INTO events (id, scenario_id, name, source_node_id, severity, triggered_at) VALUES
(1, 1, 'Taiwan Wafer Fab Earthquake Shutdown', 104, 9.5, CURRENT_TIMESTAMP),
(2, 2, 'OAuth2 Auth Service Memory Leak', 203, 8.5, CURRENT_TIMESTAMP),
(3, 3, 'Nuclear Plant Grid Scram Incident', 301, 9.0, CURRENT_TIMESTAMP),
(4, 4, 'Anonymous Whistleblower Leaks Executive Memos', 401, 8.0, CURRENT_TIMESTAMP);

-- Sample Simulation Run & Results for Seed History & SQL Reports
INSERT OR IGNORE INTO simulation_runs (id, event_id, algorithm_used, model_type, threshold, duration_ms, run_at) VALUES
(1, 1, 'CASCADE_PROPAGATION', 'MAX_PATH', 0.05, 14.2, CURRENT_TIMESTAMP);

INSERT OR IGNORE INTO simulation_results (run_id, node_id, impact_score, arrival_time, hop_level) VALUES
(1, 104, 1.000, 0.0, 0),
(1, 108, 0.855, 18.0, 1),
(1, 107, 0.675, 24.0, 1),
(1, 109, 0.630, 24.0, 1),
(1, 125, 0.765, 36.0, 1),
(1, 110, 0.812, 34.0, 2),
(1, 114, 0.598, 26.0, 2),
(1, 113, 0.771, 154.0, 3),
(1, 112, 0.690, 202.0, 3),
(1, 116, 0.694, 178.0, 4),
(1, 118, 0.655, 182.0, 4),
(1, 120, 0.659, 188.0, 5),
(1, 119, 0.555, 190.0, 5),
(1, 122, 0.622, 188.0, 5);

INSERT OR IGNORE INTO mitigation_plans (id, run_id, protected_nodes, impact_reduction_pct) VALUES
(1, 1, 'Foxconn Assembly Hub (Zhengzhou), Port of Shanghai', 42.8);
