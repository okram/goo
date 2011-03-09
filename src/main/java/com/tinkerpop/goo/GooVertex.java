package com.tinkerpop.goo;


import com.basho.riak.client.RiakObject;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.StringFactory;
import com.tinkerpop.goo.util.GooEdgeSequence;

import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GooVertex extends GooElement implements Vertex {

    Iterable<Edge> outEdges;
    Iterable<Edge> inEdges;

    public GooVertex(final GooGraph graph, final RiakObject rawVertex) {
        super(graph, rawVertex);
    }

    public Iterable<Edge> getInEdges() {
        try {
            if (null == inEdges) {
                inEdges = new GooEdgeSequence(this.graph, this.rawElement.walk(GooTokens.E, GooTokens.IN_E, true).run().getSteps().get(0), null);
            }
            return inEdges;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Iterable<Edge> getOutEdges() {
        try {
            if (null == outEdges) {
                outEdges = new GooEdgeSequence(this.graph, this.rawElement.walk(GooTokens.E, GooTokens.OUT_E, true).run().getSteps().get(0), null);
            }
            return outEdges;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Iterable<Edge> getInEdges(final String label) {
        try {
            return new GooEdgeSequence(this.graph, this.rawElement.walk(GooTokens.E, GooTokens.IN_E, true).run().getSteps().get(0), label);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Iterable<Edge> getOutEdges(final String label) {
        try {
            return new GooEdgeSequence(this.graph, this.rawElement.walk(GooTokens.E, GooTokens.OUT_E, true).run().getSteps().get(0), label);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected RiakObject getRawVertex() {
        return this.rawElement;
    }

    public String toString() {
        return StringFactory.vertexString(this);
    }

}
