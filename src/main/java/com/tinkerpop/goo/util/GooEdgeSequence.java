package com.tinkerpop.goo.util;

import com.basho.riak.client.RiakObject;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.goo.GooEdge;
import com.tinkerpop.goo.GooGraph;
import com.tinkerpop.goo.GooTokens;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GooEdgeSequence implements Iterable<Edge>, Iterator<Edge> {

    private final String label;
    private final Iterable<RiakObject> elements;
    private final Iterator<RiakObject> itty;
    private final GooGraph graph;
    private RiakObject nextObject;


    public GooEdgeSequence(final GooGraph graph, final Iterable<RiakObject> elements, final String label) {
        this.graph = graph;
        this.elements = elements;
        this.label = label;
        this.itty = elements.iterator();
    }

    public Iterator<Edge> iterator() {
        return new GooEdgeSequence(this.graph, this.elements, this.label);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
        if (null != this.nextObject)
            return true;
        else {
            while (this.itty.hasNext()) {
                RiakObject object = this.itty.next();
                if (null == this.label || object.getUsermeta().get(GooTokens.LABEL).equals(this.label)) {
                    this.nextObject = object;
                    return true;
                }
            }
        }
        return false;
    }

    public Edge next() {
        if (null != this.nextObject) {
            Edge edge = graph.getEdge(this.nextObject.getKey());
            this.nextObject = null;
            return edge;
        } else {
            while (this.itty.hasNext()) {
                RiakObject object = this.itty.next();
                if (null == this.label || object.getUsermeta().get(GooTokens.LABEL).equals(this.label)) {
                    return graph.getEdge(object.getKey());
                }
            }
        }
        throw new NoSuchElementException();
    }
}

