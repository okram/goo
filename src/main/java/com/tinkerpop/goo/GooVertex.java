package com.tinkerpop.goo;


import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.StringFactory;
import com.tinkerpop.goo.util.GooEdgeSequence;
import com.tinkerpop.goo.util.GooIdToEdgeSequence;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GooVertex extends GooElement implements Vertex {

    protected transient Set<Edge> outEdges;
    protected transient Set<Edge> inEdges;

    protected Set<Long> outEdgesById = new HashSet<Long>(); // todo: make list for efficiency
    protected Set<Long> inEdgesById = new HashSet<Long>();

    public GooVertex(final GooGraph graph, final Long id) {
        super(graph, id);
    }

    protected void addOutEdge(final Edge edge) {
        this.outEdgesById.add((Long) edge.getId());
        if (null != this.outEdges) {
            this.outEdges.add(edge);
        }
    }

    protected void addInEdge(Edge edge) {
        this.inEdgesById.add((Long) edge.getId());
        if (null != this.inEdges) {
            this.inEdges.add(edge);
        }
    }

    protected void removeOutEdge(final Edge edge) {
        this.outEdgesById.remove((Long) edge.getId());
        if (null != this.outEdges) {
            this.outEdges.remove(edge);
        }
    }

    protected void removeInEdge(Edge edge) {
        this.inEdgesById.remove((Long) edge.getId());
        if (null != this.inEdges) {
            this.inEdges.remove(edge);
        }
    }

    public Iterable<Edge> getOutEdges() {
        if (null == this.outEdges) {
            this.outEdges = new HashSet<Edge>();
            return new GooIdToEdgeSequence(this.graph, outEdgesById.iterator(), this.outEdges);
        } else
            return this.outEdges;
    }

    public Iterable<Edge> getInEdges() {
        if (null == this.inEdges) {
            this.inEdges = new HashSet<Edge>();
            return new GooIdToEdgeSequence(this.graph, inEdgesById.iterator(), this.inEdges);
        } else
            return this.inEdges;
    }

    public Iterable<Edge> getOutEdges(final String label) {
        if (null == this.outEdges) {
            this.outEdges = new HashSet<Edge>();
            return new GooEdgeSequence(new GooIdToEdgeSequence(this.graph, this.outEdgesById.iterator(), this.outEdges).iterator(), label);
        } else {
            return new GooEdgeSequence(outEdges.iterator(), label);
        }
    }

    public Iterable<Edge> getInEdges(final String label) {
        if (null == this.inEdges) {
            this.inEdges = new HashSet<Edge>();
            return new GooEdgeSequence(new GooIdToEdgeSequence(this.graph, this.inEdgesById.iterator(), this.inEdges).iterator(), label);
        } else {
            return new GooEdgeSequence(inEdges.iterator(), label);
        }
    }

    public String toString() {
        return StringFactory.vertexString(this);
    }

    public boolean equals(final Object object) {
        return object instanceof GooVertex && ((GooVertex) object).getId().equals(this.getId());
    }


}
