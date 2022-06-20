package graphs.minspantrees;

import disjointsets.DisjointSets;
import disjointsets.QuickFindDisjointSets;
import graphs.BaseEdge;
import graphs.KruskalGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Computes minimum spanning trees using Kruskal's algorithm.
 * @see MinimumSpanningTreeFinder for more documentation.
 */
public class KruskalMinimumSpanningTreeFinder<G extends KruskalGraph<V, E>, V, E extends BaseEdge<V, E>>
    implements MinimumSpanningTreeFinder<G, V, E> {

    protected DisjointSets<V> createDisjointSets() {
        return new QuickFindDisjointSets<>();
        //return new UnionBySizeCompressingDisjointSets<>();
    }

    @Override
    public MinimumSpanningTree<V, E> findMinimumSpanningTree(G graph) {
        List<E> edges = new ArrayList<>(graph.allEdges());
        edges.sort(Comparator.comparingDouble(E::weight));

        Set<E> result = new HashSet<>();
        if (edges.isEmpty()) {
            if (graph.allVertices().size() <= 1) {
                return new MinimumSpanningTree.Success<>(result);
            } else {
                return new MinimumSpanningTree.Failure<>();
            }
        } else {
            DisjointSets<V> sets = createDisjointSets();
            for (V curr : graph.allVertices()) {
                sets.makeSet(curr);
            }
            for (E curr : edges) {
                if (sets.findSet(curr.to()) != sets.findSet(curr.from())) {
                    sets.union(curr.to(), curr.from());
                    result.add(curr);
                }
            }
            int checker = sets.findSet(edges.get(0).to());
            for (V curr : graph.allVertices()) {
                if (checker != sets.findSet(curr)) {
                    return new MinimumSpanningTree.Failure<>();
                }
            }
            return new MinimumSpanningTree.Success<>(result);
        }
    }
}
