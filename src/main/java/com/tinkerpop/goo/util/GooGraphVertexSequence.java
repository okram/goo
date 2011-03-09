package com.tinkerpop.goo.util;

import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.goo.GooGraph;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GooGraphVertexSequence implements Iterable<Vertex>, Iterator<Vertex> {

    private final Iterator<String> elementKeys;
    private final GooGraph graph;

    public GooGraphVertexSequence(final GooGraph graph, final Iterator<String> elementKeys) {
        this.graph = graph;
        this.elementKeys = elementKeys;
    }

    public Iterator<Vertex> iterator() {
        return this;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
        return this.elementKeys.hasNext();
    }

    public Vertex next() {
        return this.graph.getVertex(this.elementKeys.next());
    }
}
