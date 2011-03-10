package com.tinkerpop.goo;


import com.tinkerpop.blueprints.pgm.AutomaticIndex;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.TransactionalGraph;

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
        Object oldValue = this.properties.remove(key);
        if (null != oldValue) {
            for (AutomaticIndex<? extends Element> index : this.graph.getAutoIndices()) {
                ((GooAutomaticIndex) index).autoRemove(key, oldValue, this);
            }
        }
        this.graph.updateElementStores(this);
        this.graph.autoStopTransaction(TransactionalGraph.Conclusion.SUCCESS);
        return oldValue;
    }

    public void setProperty(final String key, final Object value) {
        Object oldValue = this.properties.put(key, value);
        for (AutomaticIndex<? extends Element> index : this.graph.getAutoIndices()) {
            ((GooAutomaticIndex) index).autoUpdate(key, value, oldValue, this);
        }
        this.graph.updateElementStores(this);
        this.graph.autoStopTransaction(TransactionalGraph.Conclusion.SUCCESS);
    }

    public Object getId() {
        return this.id;
    }

    public int hashCode() {
        return this.getId().hashCode();
    }
}
