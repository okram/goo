package com.tinkerpop.goo;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.TransactionalGraph;
import com.tinkerpop.blueprints.pgm.Vertex;
import jdbm.*;

import java.io.File;
import java.io.IOException;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GooGraph implements TransactionalGraph {

    private final String directory;
    protected final RecordManager manager;
    private final PrimaryTreeMap<Long, Vertex> vertices;
    private final PrimaryTreeMap<Long, Edge> edges;
    private Mode mode = Mode.AUTOMATIC;
    protected boolean inTransaction;


    public GooGraph(final String directory) {
        try {
            this.directory = directory;
            File file = new File(this.directory);
            if (!file.exists()) {
                if (!file.mkdir()) {
                    throw new RuntimeException("Unable to create or load Goo graph directory");
                }
            }

            this.manager = RecordManagerFactory.createRecordManager(this.directory + "/" + GooTokens.GOO);
            this.vertices = this.manager.treeMap(GooTokens.VERTICES, new GooElementSerializer<Vertex>(this));
            this.edges = this.manager.treeMap(GooTokens.EDGES, new GooElementSerializer<Edge>(this));

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected void updateStore(final GooElement element) {
        if (element instanceof Vertex)
            this.vertices.put((Long) element.getId(), (Vertex) element);
        else
            this.edges.put((Long) element.getId(), (Edge) element);
    }

    public Iterable<Edge> getEdges() {
        return this.edges.values();
    }

    public Iterable<Vertex> getVertices() {
        return this.vertices.values();
    }

    public Vertex getVertex(final Object id) {
        if (null == id)
            return null;
        try {
            final Long longId = Double.valueOf(id.toString()).longValue();
            return this.vertices.get(longId);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Goo vertex ids must be convertible to a long value", e);
        }
    }

    public Edge getEdge(final Object id) {
        if (null == id)
            return null;
        try {
            final Long longId = Double.valueOf(id.toString()).longValue();
            return this.edges.get(longId);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Goo edge ids must be convertible to a long value", e);
        }
    }

    public Vertex addVertex(Object id) {
        try {
            final Long longId = this.vertices.newLongKey();
            if (this.vertices.containsKey(longId)) {
                throw new RuntimeException("Vertex with id " + id + " already exists");
            } else {
                Vertex vertex = new GooVertex(this, longId);
                this.vertices.put(longId, vertex);
                this.autoStopTransaction(TransactionalGraph.Conclusion.SUCCESS);
                return vertex;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Edge addEdge(Object id, final Vertex outVertex, final Vertex inVertex, final String label) {
        try {
            final Long longId = this.edges.newLongKey();
            if (this.edges.containsKey(longId)) {
                throw new RuntimeException("Edge with id " + id + " already exists");
            } else {
                final Edge edge = new GooEdge(this, longId, outVertex, inVertex, label);
                ((GooVertex) outVertex).addOutEdges(edge);
                ((GooVertex) inVertex).addInEdges(edge);
                this.edges.put(longId, edge);
                this.vertices.put((Long) outVertex.getId(), outVertex);
                this.vertices.put((Long) inVertex.getId(), inVertex);
                this.autoStopTransaction(TransactionalGraph.Conclusion.SUCCESS);
                return edge;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void removeVertex(final Vertex vertex) {
        try {
            for (final Edge edge : vertex.getInEdges()) {
                this.removeEdge(edge);   // TODO: easy to optimize
            }
            for (final Edge edge : vertex.getOutEdges()) {
                this.removeEdge(edge); // TODO: easy to optimize
            }
            this.vertices.remove((Long) vertex.getId());
            this.autoStopTransaction(TransactionalGraph.Conclusion.SUCCESS);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void removeEdge(final Edge edge) {
        try {
            final GooVertex outVertex = (GooVertex) edge.getOutVertex();
            final GooVertex inVertex = (GooVertex) edge.getInVertex();
            outVertex.removeOutEdges(edge);
            inVertex.removeInEdges(edge);
            this.edges.remove((Long) edge.getId());
            this.vertices.put((Long) outVertex.getId(), outVertex);
            this.vertices.put((Long) inVertex.getId(), inVertex);

            this.autoStopTransaction(TransactionalGraph.Conclusion.SUCCESS);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void clear() {
        try {
            this.vertices.clear();
            this.edges.clear();
            this.autoStopTransaction(TransactionalGraph.Conclusion.SUCCESS);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void shutdown() {
        try {
            this.manager.commit();
            this.manager.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void startTransaction() {
        if (Mode.AUTOMATIC == this.mode)
            throw new RuntimeException(TransactionalGraph.TURN_OFF_MESSAGE);
        if (this.inTransaction)
            throw new RuntimeException(TransactionalGraph.NESTED_MESSAGE);
        this.inTransaction = true;
    }

    public void stopTransaction(final Conclusion conclusion) {
        if (Mode.AUTOMATIC == this.mode)
            throw new RuntimeException(TransactionalGraph.TURN_OFF_MESSAGE);

        try {
            this.inTransaction = false;
            if (Conclusion.SUCCESS == conclusion) {
                this.manager.commit();
            } else {
                this.manager.rollback();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected void autoStopTransaction(Conclusion conclusion) {
        if (this.mode == Mode.AUTOMATIC) {
            try {
                this.inTransaction = false;
                if (conclusion == Conclusion.SUCCESS)
                    this.manager.commit();
                else
                    this.manager.rollback();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    public void setTransactionMode(Mode mode) {
        try {
            this.manager.commit();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        this.inTransaction = false;
        this.mode = mode;
    }

    public Mode getTransactionMode() {
        return this.mode;
    }

    public String toString() {
        return "goograph[" + this.directory + "]";
    }
}
