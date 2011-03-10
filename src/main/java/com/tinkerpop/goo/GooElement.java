package com.tinkerpop.goo;


import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.TransactionalGraph;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class GooElement implements Element, Serializable {

    private final Map<String, Object> properties = new HashMap<String, Object>();
    protected Long id;
    protected transient GooGraph graph;

    public GooElement(final GooGraph graph, final Long id) {
        this.id = id;
        this.graph = graph;
    }

    public Object getProperty(final String key) {
        return this.properties.get(key);
    }

    public Set<String> getPropertyKeys() {
        return this.properties.keySet();

    }

    public Object removeProperty(final String key) {
        Object value = this.properties.remove(key);
        this.graph.updateStore(this);
        this.graph.autoStopTransaction(TransactionalGraph.Conclusion.SUCCESS);
        return value;
    }

    public void setProperty(final String key, final Object value) {
        this.properties.put(key, value);
        this.graph.updateStore(this);
        this.graph.autoStopTransaction(TransactionalGraph.Conclusion.SUCCESS);
    }

    public Object getId() {
        return this.id;
    }

    public int hashCode() {
        return this.getId().hashCode();
    }
}
