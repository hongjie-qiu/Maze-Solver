package graphs.shortestpaths;

import graphs.BaseEdge;
import graphs.Graph;
import priorityqueues.DoubleMapMinPQ;
import priorityqueues.ExtrinsicMinPQ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DijkstraShortestPathFinder<G extends Graph<V, E>, V, E extends BaseEdge<V, E>>
    extends SPTShortestPathFinder<G, V, E> {

    protected <T> ExtrinsicMinPQ<T> createMinPQ() {
        return new DoubleMapMinPQ<>();
    }

    @Override
    protected Map<V, E> constructShortestPathsTree(G graph, V start, V end) {
        DoubleMapMinPQ<V> pq = new DoubleMapMinPQ<V>();
        HashMap<V, E> edges = new HashMap<>();
        HashMap<V, Double> distances = new HashMap<>();

        pq.add(start, 0);
        if (Objects.equals(start, end)) {
            return edges;
        }
        distances.put(start, 0.0);
        while (!pq.isEmpty()) {
            V curr = pq.removeMin();
            if (Objects.equals(curr, end)) {
                return edges;
            }
            for (E edge : graph.outgoingEdgesFrom(curr)) {
                V neighbor = edge.to();
                Double currDist = distances.get(curr) + edge.weight();
                if (distances.containsKey(neighbor)) {
                    if (currDist < distances.get(neighbor)) {
                        if (!pq.contains(neighbor)) {
                            pq.add(neighbor, currDist);
                        }
                        pq.changePriority(neighbor, currDist);
                        edges.put(neighbor, edge);
                                distances.put(neighbor, currDist);
                            }
                        } else {
                            distances.put(neighbor, currDist);
                            edges.put(neighbor, edge);
                            pq.add(neighbor, currDist);
                        }
                }
            }
        return edges;
    }

    @Override
    protected ShortestPath<V, E> extractShortestPath(Map<V, E> spt, V start, V end) {
        if (Objects.equals(start, end)) {
            return new ShortestPath.SingleVertex<>(start);
        }

        List<E> edges = new ArrayList<>();
        if (spt.get(end) == null) {
            return new ShortestPath.Failure<>();
        } else if (start == null) {
            edges.add(spt.get(end));
            return new ShortestPath.Success<>(edges);
        } else {
            List<V> vertices = new ArrayList<>();
            E edge = spt.get(end);
            V vertex = end;
            while (!Objects.equals(vertex, start)) {
                if (edge != null) {
                    edges.add(0, edge);
                    vertex = edge.from();
                    vertices.add(0, vertex);
                    edge = spt.get(vertex);
                } else {
                    return new ShortestPath.Failure<>();
                }
            }
            vertices.add(0, start);
            return new ShortestPath.Success<>(edges);
        }
    }

}

