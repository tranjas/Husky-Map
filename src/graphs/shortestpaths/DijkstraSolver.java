package graphs.shortestpaths;

import graphs.Edge;
import graphs.Graph;
import minpq.DoubleMapMinPQ;
import minpq.ExtrinsicMinPQ;

import java.util.*;

/**
 * Dijkstra's algorithm implementation of the {@link ShortestPathSolver} interface.
 *
 * @param <V> the type of vertices.
 * @see ShortestPathSolver
 */
public class DijkstraSolver<V> implements ShortestPathSolver<V> {
    private final Map<V, Edge<V>> edgeTo;
    private final Map<V, Double> distTo;

    /**
     * Constructs a new instance by executing Dijkstra's algorithm on the graph from the start.
     *
     * @param graph the input graph.
     * @param start the start vertex.
     */
    public DijkstraSolver(Graph<V> graph, V start) {
        this.edgeTo = new HashMap<>();
        this.distTo = new HashMap<>();
        ExtrinsicMinPQ<V> pq = new DoubleMapMinPQ<>();
        pq.add(start, 0.0);
        edgeTo.put(start, null);
        distTo.put(start, 0.0);
        while (!pq.isEmpty()) {
            V from = pq.removeMin();
            for (Edge<V> e : graph.neighbors(from)) {
                V to = e.to;
                double oldDist = distTo.getOrDefault(to, Double.POSITIVE_INFINITY);
                double newDist = distTo.get(from) + e.weight;
                if (newDist < oldDist) {
                    edgeTo.put(to, e);
                    distTo.put(to, newDist);
                    if (pq.contains(to)) {
                        pq.changePriority(to, newDist);
                    } else {
                        pq.add(to, newDist);
                    }
                }
            }
        }
    }

    @Override
    public List<V> solution(V goal) {
        List<V> path = new ArrayList<>();
        V curr = goal;
        path.add(curr);
        while (edgeTo.get(curr) != null) {
            curr = edgeTo.get(curr).from;
            path.add(curr);
        }
        Collections.reverse(path);
        return path;
    }
}
