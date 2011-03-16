package com.tinkerpop.goo;

import com.tinkerpop.blueprints.pgm.AutomaticIndex;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.StringFactory;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GooEdge extends GooElement implements Edge {

    private final String label;
    private final Long inVertexId;
    private final Long outVertexId;

    private transient Vertex outVertex;
    private transient Vertex inVertex;

    protected GooEdge(final GooGraph graph, final Long id, final Vertex outVertex, final Vertex inVertex, final String label) {
        super(graph, id);
        this.label = label;
        this.outVertexId = (Long) outVertex.getId();
        this.inVertexId = (Long) inVertex.getId();
        this.outVertex = outVertex;
        this.inVertex = inVertex;
        for (AutomaticIndex index : this.graph.getAutoIndices()) {
            ((GooAutomaticIndex) index).autoUpdate(AutomaticIndex.LABEL, this.label, null, this);
        }
    }

    public String getLabel() {
        return this.label;
    }

    public Vertex getOutVertex() {
        if (null == this.outVertex)
            this.outVertex = this.graph.getVertex(this.outVertexId);
        return this.outVertex;
    }

    public Vertex getInVertex() {
        if (null == this.inVertex)
            this.inVertex = this.graph.getVertex(this.inVertexId);
        return this.inVertex;
    }

    public String toString() {
        return StringFactory.edgeString(this);
    }

    public boolean equals(final Object object) {
        return object instanceof GooEdge && ((GooEdge) object).getId().equals(this.getId());
    }


}
