package com.tinkerpop.goo;

import com.tinkerpop.blueprints.pgm.AutomaticIndex;
import com.tinkerpop.blueprints.pgm.Element;
import jdbm.PrimaryTreeMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GooAutomaticIndex<T extends Element> extends GooIndex<T> implements AutomaticIndex<T> {

    private final Set<String> autoKeys;

    public GooAutomaticIndex(final String indexName, final Class<T> indexClass, final Set<String> autoKeys, final PrimaryTreeMap<String, Map<Object, Set<T>>> index, final GooGraph graph) {
        super(indexName, indexClass, index, graph);
        if (autoKeys == null)
            this.autoKeys = null;
        else {
            this.autoKeys = new HashSet<String>();
            this.autoKeys.addAll(autoKeys);
        }
    }

    protected void autoUpdate(final String key, final Object newValue, final Object oldValue, final T element) {
        if (this.getIndexClass().isAssignableFrom(element.getClass()) && (this.autoKeys == null || this.autoKeys.contains(key))) {
            if (oldValue != null)
                this.remove(key, oldValue, element);
            this.put(key, newValue, element);
        }
    }

    protected void autoRemove(final String key, final Object oldValue, final T element) {
        if (this.getIndexClass().isAssignableFrom(element.getClass()) && (this.autoKeys == null || this.autoKeys.contains(key))) {
            this.remove(key, oldValue, element);
        }
    }

    public Type getIndexType() {
        return Type.AUTOMATIC;
    }

    public Set<String> getAutoIndexKeys() {
        return this.autoKeys;
    }
}