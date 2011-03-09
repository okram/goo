package com.tinkerpop.goo;

import com.basho.riak.client.RiakClient;
import com.basho.riak.client.RiakLink;
import com.basho.riak.client.RiakObject;
import com.basho.riak.client.plain.PlainClient;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.goo.util.GooGraphEdgeSequence;
import com.tinkerpop.goo.util.GooGraphVertexSequence;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GooGraph implements Graph {

    private final PlainClient plainClient;
    private final RiakClient riakClient;
    private final String url;

    private final Map<Object, Vertex> vertexMap = new HashMap<Object, Vertex>();
    private final Map<Object, Edge> edgeMap = new HashMap<Object, Edge>();

    public GooGraph(final String url) {
        this.url = url;
        this.riakClient = new RiakClient(this.url);
        this.plainClient = new PlainClient(this.riakClient);
    }

    public RiakClient getRawGraph() {
        return this.riakClient;
    }

    public Iterable<Edge> getEdges() {
        try {
            return new GooGraphEdgeSequence(this, this.plainClient.listBucket(GooTokens.E).getKeys().iterator());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Iterable<Vertex> getVertices() {
        try {
            return new GooGraphVertexSequence(this, this.plainClient.listBucket(GooTokens.V).getKeys().iterator());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Vertex getVertex(final Object id) {
        try {
            Vertex vertex = this.vertexMap.get(id);
            if (null == vertex) {
                final RiakObject rawVertex = plainClient.fetch(GooTokens.V, id.toString());
                if (null == rawVertex)
                    return null;
                else {
                    vertex = new GooVertex(this, rawVertex);
                    this.vertexMap.put(id, vertex);
                }
            }
            return vertex;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Edge getEdge(final Object id) {
        try {
            Edge edge = this.edgeMap.get(id);
            if (null == edge) {
                final RiakObject rawEdge = plainClient.fetch(GooTokens.E, id.toString());
                if (null == rawEdge)
                    return null;
                else {
                    edge = new GooEdge(this, rawEdge);
                    this.edgeMap.put(id, edge);
                }
            }
            return edge;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Vertex addVertex(Object id) {
        try {
            if (null == id) {
                id = UUID.randomUUID().toString();
            } else {
                if (null != this.plainClient.fetch(GooTokens.V, id.toString()))
                    throw new RuntimeException("Vertex with id " + id.toString() + " already exists");
            }
            final RiakObject rawVertex = new RiakObject(GooTokens.V, id.toString());
            this.plainClient.store(rawVertex);

            Vertex vertex = new GooVertex(this, rawVertex);
            this.vertexMap.put(id, vertex);
            return vertex;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Edge addEdge(Object id, final Vertex outVertex, final Vertex inVertex, final String label) {
        try {
            if (null == id) {
                id = UUID.randomUUID().toString();
            } else {
                if (null != this.plainClient.fetch(GooTokens.E, id.toString()))
                    throw new RuntimeException("Edge with id " + id.toString() + " already exists");
            }
            final RiakObject rawEdge = new RiakObject(GooTokens.E, id.toString());
            RiakObject rawOutVertex = ((GooVertex) outVertex).getRawVertex();
            final RiakObject rawInVertex = ((GooVertex) inVertex).getRawVertex();

            rawEdge.addLink(new RiakLink(GooTokens.V, outVertex.getId().toString(), GooTokens.OUT_V));
            rawEdge.addLink(new RiakLink(GooTokens.V, inVertex.getId().toString(), GooTokens.IN_V));
            rawEdge.addUsermeta(GooTokens.LABEL, label);
            rawOutVertex.addLink(new RiakLink(GooTokens.E, id.toString(), GooTokens.OUT_E));
            rawInVertex.addLink(new RiakLink(GooTokens.E, id.toString(), GooTokens.IN_E));

            this.plainClient.store(rawEdge);
            this.plainClient.store(rawOutVertex);
            this.plainClient.store(rawInVertex);

            Edge edge = new GooEdge(this, rawEdge, outVertex, inVertex);
            this.edgeMap.put(id, edge);
            return edge;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void removeVertex(final Vertex vertex) {
        try {
            for (final Edge edge : vertex.getOutEdges()) {
                this.removeEdge(edge);
            }
            for (final Edge edge : vertex.getInEdges()) {
                this.removeEdge(edge);
            }
            this.vertexMap.remove(vertex.getId());
            this.plainClient.delete(GooTokens.V, vertex.getId().toString());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void removeEdge(final Edge edge) {
        try {
            this.edgeMap.remove(edge.getId());
            this.plainClient.delete(GooTokens.E, edge.getId().toString());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void clear() {
        try {
            for (final String key : this.plainClient.listBucket(GooTokens.V).getKeys()) {
                this.plainClient.delete(GooTokens.V, key);
            }

            for (final String key : this.plainClient.listBucket(GooTokens.E).getKeys()) {
                this.plainClient.delete(GooTokens.E, key);
            }
            this.vertexMap.clear();
            this.edgeMap.clear();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void shutdown() {

    }

    public String toString() {
        return "goograph[" + this.url + "]";
    }
}
