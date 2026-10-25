package com.ripplemap.graph;

import com.ripplemap.ds.CustomArrayList;
import com.ripplemap.ds.CustomHashMap;
import com.ripplemap.ds.CustomSinglyLinkedList;
import com.ripplemap.model.Node;

/**
 * Weighted Directed Graph representation built strictly using manual data structures:
 * - Nodes stored in CustomHashMap<Integer, Node>
 * - Adjacency lists stored in CustomHashMap<Integer, CustomSinglyLinkedList<GraphEdge>>
 * - Reverse adjacency lists for fast in-degree and topological sort
 *
 * Complexities:
 * - Add Node: Time O(1), Space O(1)
 * - Add Edge: Time O(1), Space O(1)
 * - Remove Edge: Time O(deg(u)), Space O(1)
 * - Remove Node: Time O(V + E)
 * - Outgoing / Incoming Edges: Time O(1) lookup
 * - Overall Space: O(V + E)
 */
public class Graph {

    private final CustomHashMap<Integer, Node> nodeMap;
    private final CustomHashMap<Integer, CustomSinglyLinkedList<GraphEdge>> adjList;
    private final CustomHashMap<Integer, CustomSinglyLinkedList<GraphEdge>> reverseAdjList;
    private int edgeCount;

    public Graph() {
        this.nodeMap = new CustomHashMap<>();
        this.adjList = new CustomHashMap<>();
        this.reverseAdjList = new CustomHashMap<>();
        this.edgeCount = 0;
    }

    public void addNode(Node node) {
        if (node == null) return;
        nodeMap.put(node.getId(), node);
        if (!adjList.containsKey(node.getId())) {
            adjList.put(node.getId(), new CustomSinglyLinkedList<>());
        }
        if (!reverseAdjList.containsKey(node.getId())) {
            reverseAdjList.put(node.getId(), new CustomSinglyLinkedList<>());
        }
    }

    public Node getNode(int id) {
        return nodeMap.get(id);
    }

    public boolean containsNode(int id) {
        return nodeMap.containsKey(id);
    }

    public void addEdge(int sourceId, int targetId, double weight, double delayHours, String relationType) {
        if (!nodeMap.containsKey(sourceId) || !nodeMap.containsKey(targetId)) {
            return;
        }

        GraphEdge edge = new GraphEdge(sourceId, targetId, weight, delayHours, relationType);
        
        CustomSinglyLinkedList<GraphEdge> outEdges = adjList.get(sourceId);
        if (outEdges == null) {
            outEdges = new CustomSinglyLinkedList<>();
            adjList.put(sourceId, outEdges);
        }
        outEdges.addLast(edge);

        CustomSinglyLinkedList<GraphEdge> inEdges = reverseAdjList.get(targetId);
        if (inEdges == null) {
            inEdges = new CustomSinglyLinkedList<>();
            reverseAdjList.put(targetId, inEdges);
        }
        inEdges.addLast(edge);

        edgeCount++;
    }

    public boolean removeEdge(int sourceId, int targetId) {
        CustomSinglyLinkedList<GraphEdge> outEdges = adjList.get(sourceId);
        if (outEdges == null) return false;

        boolean removed = false;
        CustomSinglyLinkedList.Node<GraphEdge> curr = outEdges.getHead();
        int idx = 0;
        while (curr != null) {
            if (curr.data.getTargetId() == targetId) {
                outEdges.remove(idx);
                removed = true;
                break;
            }
            curr = curr.next;
            idx++;
        }

        if (removed) {
            CustomSinglyLinkedList<GraphEdge> inEdges = reverseAdjList.get(targetId);
            if (inEdges != null) {
                CustomSinglyLinkedList.Node<GraphEdge> inCurr = inEdges.getHead();
                int inIdx = 0;
                while (inCurr != null) {
                    if (inCurr.data.getSourceId() == sourceId) {
                        inEdges.remove(inIdx);
                        break;
                    }
                    inCurr = inCurr.next;
                    inIdx++;
                }
            }
            edgeCount--;
        }
        return removed;
    }

    public void removeNode(int nodeId) {
        if (!nodeMap.containsKey(nodeId)) return;

        // Remove all outgoing edges
        CustomSinglyLinkedList<GraphEdge> out = adjList.get(nodeId);
        if (out != null) {
            for (GraphEdge e : out) {
                CustomSinglyLinkedList<GraphEdge> inList = reverseAdjList.get(e.getTargetId());
                if (inList != null) {
                    removeEdgeFromList(inList, nodeId, e.getTargetId());
                }
                edgeCount--;
            }
        }
        adjList.remove(nodeId);

        // Remove all incoming edges
        CustomSinglyLinkedList<GraphEdge> in = reverseAdjList.get(nodeId);
        if (in != null) {
            for (GraphEdge e : in) {
                CustomSinglyLinkedList<GraphEdge> outList = adjList.get(e.getSourceId());
                if (outList != null) {
                    removeEdgeFromList(outList, e.getSourceId(), nodeId);
                }
                edgeCount--;
            }
        }
        reverseAdjList.remove(nodeId);

        nodeMap.remove(nodeId);
    }

    private void removeEdgeFromList(CustomSinglyLinkedList<GraphEdge> list, int s, int t) {
        CustomSinglyLinkedList.Node<GraphEdge> curr = list.getHead();
        int idx = 0;
        while (curr != null) {
            if (curr.data.getSourceId() == s && curr.data.getTargetId() == t) {
                list.remove(idx);
                return;
            }
            curr = curr.next;
            idx++;
        }
    }

    public CustomSinglyLinkedList<GraphEdge> getOutgoingEdges(int nodeId) {
        CustomSinglyLinkedList<GraphEdge> list = adjList.get(nodeId);
        return list != null ? list : new CustomSinglyLinkedList<>();
    }

    public CustomSinglyLinkedList<GraphEdge> getIncomingEdges(int nodeId) {
        CustomSinglyLinkedList<GraphEdge> list = reverseAdjList.get(nodeId);
        return list != null ? list : new CustomSinglyLinkedList<>();
    }

    public CustomArrayList<Node> getAllNodes() {
        return nodeMap.values();
    }

    public CustomArrayList<Integer> getNodeIds() {
        return nodeMap.keySet();
    }

    public CustomArrayList<GraphEdge> getAllEdges() {
        CustomArrayList<GraphEdge> all = new CustomArrayList<>(edgeCount);
        for (CustomHashMap.Entry<Integer, CustomSinglyLinkedList<GraphEdge>> entry : adjList) {
            for (GraphEdge e : entry.value) {
                all.add(e);
            }
        }
        return all;
    }

    public int nodeCount() {
        return nodeMap.size();
    }

    public int edgeCount() {
        return edgeCount;
    }

    public void clear() {
        nodeMap.clear();
        adjList.clear();
        reverseAdjList.clear();
        edgeCount = 0;
    }
}
