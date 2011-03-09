package com.tinkerpop.goo;

import com.basho.riak.client.RiakLink;
import com.basho.riak.client.RiakObject;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.StringFactory;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GooEdge extends GooElement implements Edge {

    Vertex outVertex;
    Vertex inVertex;

    public GooEdge(final GooGraph graph, final RiakObject rawEdge, final Vertex outVertex, final Vertex inVertex) {
        super(graph, rawEdge);
        this.outVertex = outVertex;
        this.inVertex = inVertex;
    }

    public GooEdge(final GooGraph graph, final RiakObject rawEdge) {
        super(graph, rawEdge);
        for (RiakLink link : this.rawElement.getLinks()) {
            if (link.getTag().equals(GooTokens.OUT_V))
                this.outVertex = this.graph.getVertex(link.getKey());
            else
                this.inVertex = this.graph.getVertex(link.getKey());
        }
    }

    public Vertex getInVertex() {
        return inVertex;
    }

    public Vertex getOutVertex() {
        return outVertex;
    }

    public String getLabel() {
        return this.rawElement.getUsermeta().get(GooTokens.LABEL);
    }

    protected RiakObject getRawEdge() {
        return this.rawElement;
    }

    public String toString() {
        return StringFactory.edgeString(this);
    }

}
