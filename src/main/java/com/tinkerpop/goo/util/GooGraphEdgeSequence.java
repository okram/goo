package com.tinkerpop.goo.util;

import com.basho.riak.client.RiakObject;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.goo.GooGraph;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GooGraphEdgeSequence implements Iterable<Edge>, Iterator<Edge> {

    private final Iterator<String> elementKeys;
    private final GooGraph graph;

    public GooGraphEdgeSequence(final GooGraph graph, final Iterator<String> elementKeys) {
        this.graph = graph;
        this.elementKeys = elementKeys;
    }

    public Iterator<Edge> iterator() {
        return this;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
        return this.elementKeys.hasNext();
    }

    public Edge next() {
        return graph.getEdge(this.elementKeys.next());
    }
}
