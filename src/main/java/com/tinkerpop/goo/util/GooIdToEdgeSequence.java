package com.tinkerpop.goo.util;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.goo.GooGraph;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GooIdToEdgeSequence implements Iterable<Edge>, Iterator<Edge> {

    private final GooGraph graph;
    private final Iterator<Long> edgeIds;
    private final Set<Edge> edges;

    public GooIdToEdgeSequence(final GooGraph graph, final Iterator<Long> edgeIds, final Set<Edge> edges) {
        this.graph = graph;
        this.edgeIds = edgeIds;
        this.edges = edges;
    }

    public Iterator<Edge> iterator() {
        return this;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
        return this.edgeIds.hasNext();
    }

    public Edge next() {
        final Edge edge = this.graph.getEdge(this.edgeIds.next());
        if (null != edges) {
            this.edges.add(edge);
        }
        return edge;
    }
}
