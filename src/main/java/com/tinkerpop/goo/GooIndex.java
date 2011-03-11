package com.tinkerpop.goo;

import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Index;
import com.tinkerpop.blueprints.pgm.TransactionalGraph;
import com.tinkerpop.blueprints.pgm.impls.StringFactory;
import jdbm.PrimaryTreeMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GooIndex<T extends Element> implements Index<T> {

    private final GooGraph graph;
    private final PrimaryTreeMap<String, Map<Object, Set<T>>> index;
    private final String indexName;
    private final Class<T> indexClass;

    public GooIndex(final String indexName, final Class<T> indexClass, final PrimaryTreeMap<String, Map<Object, Set<T>>> index, final GooGraph graph) {
        this.indexName = indexName;
        this.indexClass = indexClass;
        this.index = index;
        this.graph = graph;
    }

    public String getIndexName() {
        return this.indexName;
    }

    public Class<T> getIndexClass() {
        return this.indexClass;
    }

    public Type getIndexType() {
        return Type.MANUAL;
    }

    public void put(final String key, final Object value, final T element) {
        Map<Object, Set<T>> keyMap = this.index.get(key);
        if (keyMap == null) {
            keyMap = new HashMap<Object, Set<T>>();
            this.index.put(key, keyMap);
        }
        Set<T> objects = keyMap.get(value);
        if (null == objects) {
            objects = new HashSet<T>();
            keyMap.put(value, objects);
        }
        objects.add(element);
        this.index.put(key, keyMap);
        this.graph.autoStopTransaction(TransactionalGraph.Conclusion.SUCCESS);
    }

    public Iterable<T> get(final String key, final Object value) {
        Map<Object, Set<T>> keyMap = this.index.get(key);
        if (null == keyMap) {
            return new HashSet<T>();
        } else {
            Set<T> set = keyMap.get(value);
            if (null == set)
                return new HashSet<T>();
            else
                return set;
        }
    }

    public void remove(final String key, final Object value, final T element) {
        Map<Object, Set<T>> keyMap = this.index.get(key);
        if (null != keyMap) {
            Set<T> objects = keyMap.get(value);
            if (null != objects) {
                objects.remove(element);
                if (objects.size() == 0) {
                    keyMap.remove(value);
                }
            }
        }
        this.index.put(key, keyMap);
        this.graph.autoStopTransaction(TransactionalGraph.Conclusion.SUCCESS);
    }

    public void removeElement(final T element) {
        if (this.indexClass.isAssignableFrom(element.getClass())) {
            for (Map.Entry<String, Map<Object, Set<T>>> entry : index.entrySet()) {
                String key = entry.getKey();
                Map<Object, Set<T>> value = entry.getValue();
                for (Set<T> set : value.values()) {
                    set.remove(element);
                }
                this.index.put(key, value);
            }
        }

        this.graph.autoStopTransaction(TransactionalGraph.Conclusion.SUCCESS);
    }

    public String toString() {
        return StringFactory.indexString(this);
    }
}


