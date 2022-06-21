package implementation;

import java.util.*;
import java.util.Map.Entry;

/**
 * Implements a graph. We use two maps: one map for adjacency properties
 * (adjancencyMap) and one map (dataMap) to keep track of the data associated
 * with a vertex.
 * 
 * @author cmsc132
 * 
 * @param <E>
 */
public class Graph<E> {
	/* You must use the following maps in your implementation */
	private HashMap<String, HashMap<String, Integer>> adjacencyMap;
	private HashMap<String, E> dataMap;

	public Graph() {
		adjacencyMap = new HashMap<>();
		dataMap = new HashMap<>();
	}

	public void addVertex(String vertexName, E data) {
		if (adjacencyMap.containsKey(vertexName) || dataMap.containsKey(vertexName)) {
			throw new IllegalArgumentException("Vertex already exists in the graph!");
		} else {
			adjacencyMap.put(vertexName, new HashMap<String, Integer>());
			dataMap.put(vertexName, data);
		}
	}

	public void addDirectedEdge(String startVertexName, String endVertexName, int cost) {
		if (!adjacencyMap.containsKey(startVertexName) || !adjacencyMap.containsKey(endVertexName)) {
			throw new IllegalArgumentException("A vertex is not part of the graph!");
		} else {
			if (adjacencyMap.get(startVertexName).containsKey(endVertexName)) {
				adjacencyMap.get(startVertexName).replace(endVertexName, cost);
			} else {
				adjacencyMap.get(startVertexName).put(endVertexName, cost);
			}
		}
	}

	public String toString() {
		StringBuffer strB = new StringBuffer();
		strB.append("Vertices: [");
		TreeMap<String, HashMap<String, Integer>> treeAdjacencyMap = new TreeMap<>(adjacencyMap);
		ArrayList<String> vertices = new ArrayList<>();
		for (Entry<String, HashMap<String, Integer>> entry : treeAdjacencyMap.entrySet()) {
			vertices.add(entry.getKey());
		}
		int i;
		for (i = 0; i < vertices.size(); i++) {
			if (i == vertices.size() - 1) {
				strB.append(vertices.get(i));
			} else {
				strB.append(vertices.get(i) + ", ");
			}
		}
		strB.append("]\nEdges:\n");
		for (Entry<String, HashMap<String, Integer>> entry : treeAdjacencyMap.entrySet()) {
			strB.append("Vertex(" + entry.getKey() + ")--->{");
			ArrayList<String> connectedVertex = new ArrayList<>();
			ArrayList<Integer> connectedCost = new ArrayList<>();
			for (Entry<String, Integer> connectedEntry : entry.getValue().entrySet()) {
				connectedVertex.add(connectedEntry.getKey());
				connectedCost.add(connectedEntry.getValue());
			}
			for (i = 0; i < connectedVertex.size(); i++) {
				if (i == connectedVertex.size() - 1) {
					strB.append(connectedVertex.get(i) + "=" + connectedCost.get(i));
				} else {
					strB.append(connectedVertex.get(i) + "=" + connectedCost.get(i) + ", ");
				}
			}
			strB.append("}\n");
		}
		return strB.toString();
		// what if maps are empty?
	}

	public Map<String, Integer> getAdjacentVertices​(String vertexName) {
		return adjacencyMap.get(vertexName);
	}

	public int getCost​(String startVertexName, String endVertexName) {
		return adjacencyMap.get(startVertexName).get(endVertexName);
	}

	public Set<String> getVertices() {
		return new HashSet<String>(dataMap.keySet());
	}

	public E getData​(String vertex) {
		if (!dataMap.containsKey(vertex)) {
			throw new IllegalArgumentException("Vertex is not part of the graph!");
		} else {
			return dataMap.get(vertex);
		}
	}

	public void doDepthFirstSearch(String startVertexName, CallBack<E> callback) {
		if (!adjacencyMap.containsKey(startVertexName)) {
			throw new IllegalArgumentException("Vertex is not part of the graph!");
		} else {
			HashSet<String> visited = new HashSet<>();
			Stack<String> discovered = new Stack<>();
			discovered.push(startVertexName);
			String vertex;
			while (!discovered.empty()) {
				vertex = discovered.pop();
				if (!visited.contains(vertex)) {
					visited.add(vertex);
					callback.processVertex(vertex, dataMap.get(vertex));
					for (Entry<String, Integer> entry : adjacencyMap.get(vertex).entrySet()) {
						if (!visited.contains(entry.getKey())) {
							discovered.push(entry.getKey());
						}
					}
				}
			}
		}
	}

	public void doBreadthFirstSearch(String startVertexName, CallBack<E> callback) {
		if (!adjacencyMap.containsKey(startVertexName)) {
			throw new IllegalArgumentException("Vertex is not part of the graph!");
		} else {
			HashSet<String> visited = new HashSet<>();
			ArrayList<String> discovered = new ArrayList<>();
			discovered.add(startVertexName);
			String vertex;
			while (discovered.size() != 0) {
				vertex = discovered.get(0);
				discovered.remove(0);
				if (!visited.contains(vertex)) {
					visited.add(vertex);
					callback.processVertex(vertex, dataMap.get(vertex));
					for (Entry<String, Integer> entry : adjacencyMap.get(vertex).entrySet()) {
						if (!visited.contains(entry.getKey())) {
							discovered.add(entry.getKey());
						}
					}
				}
			}
		}
	}

	public int doDijkstras(String startVertexName, String endVertexName, ArrayList<String> shortestPath) {
		if (!adjacencyMap.containsKey(startVertexName) || !adjacencyMap.containsKey(endVertexName)) {
			throw new IllegalArgumentException("A vertex is not part of the graph!");
		} else {
			HashSet<String> S = new HashSet<>();
			TreeMap<String, String> predecessorMap = new TreeMap<>();
			TreeMap<String, Integer> costMap = new TreeMap<>();
			for (Entry<String, E> entry : dataMap.entrySet()) {
				predecessorMap.put(entry.getKey(), "none");
				costMap.put(entry.getKey(), 10001);
			}
			costMap.replace(startVertexName, 0);
			int totalVertices = dataMap.size();
			while (S.size() < totalVertices) {

				if (S.contains(endVertexName)) {
					ArrayList<String> path = new ArrayList<>();
					path.add(endVertexName);
					String predecessor = predecessorMap.get(endVertexName);
					while (!predecessor.equals("none")) {
						path.add(predecessor);
						predecessor = predecessorMap.get(predecessor);
					}
					Collections.reverse(path);
					if (!path.get(0).equals(startVertexName)) {
						break;
					} else {
						for (String vertex : path) {
							shortestPath.add(vertex);
						}
						return costMap.get(endVertexName);
					}
				}

				// find smallest cost
				String smallestCost = "``````````";
				for (Entry<String, Integer> entry : costMap.entrySet()) {
					if (!S.contains(entry.getKey())) {
						if (smallestCost.equals("``````````")) {
							smallestCost = entry.getKey();
						} else {
							if (costMap.get(entry.getKey()) < costMap.get(smallestCost)) {
								smallestCost = entry.getKey();
							}
						}
					}
				}

				S.add(smallestCost);

				for (Entry<String, Integer> adjacentEntry : adjacencyMap.get(smallestCost).entrySet()) {
					if (!S.contains(adjacentEntry.getKey())) {
						if (costMap.get(smallestCost) + adjacencyMap.get(smallestCost)
								.get(adjacentEntry.getKey()) < costMap.get(adjacentEntry.getKey())) {
							costMap.replace(adjacentEntry.getKey(), costMap.get(smallestCost)
									+ adjacencyMap.get(smallestCost).get(adjacentEntry.getKey()));
							predecessorMap.replace(adjacentEntry.getKey(), smallestCost);
						}
					}
				}

			}
			shortestPath.add("None");
			return -1;
		}
	}

}
