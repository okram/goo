package com.tinkerpop.goo;

import com.basho.riak.client.RiakObject;
import com.tinkerpop.blueprints.pgm.Element;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GooElement implements Element {

    protected final RiakObject rawElement;
    protected final GooGraph graph;
    private Map<String, Object> properties;
    private boolean loadedProperties = false;


    public GooElement(final GooGraph graph, final RiakObject rawElement) {
        this.graph = graph;
        this.rawElement = rawElement;
        this.rawElement.setRiakClient(this.graph.getRawGraph());
    }

    public Object getId() {
        return rawElement.getKey();
    }

    public Set<String> getPropertyKeys() {
        if (!this.loadedProperties)
            this.loadRiakValue();

        return this.properties.keySet();
    }

    public Object removeProperty(final String key) {
        if (!this.loadedProperties)
            this.loadRiakValue();

        final Object value = this.properties.remove(key);
        this.saveRiakValue();
        return value;
    }

    public Object getProperty(final String key) {
        if (!this.loadedProperties)
            this.loadRiakValue();

        return this.properties.get(key);
    }

    public void setProperty(final String key, final Object value) {
        if (!this.loadedProperties)
            this.loadRiakValue();

        this.properties.put(key, value);
        this.saveRiakValue();
    }

    public RiakObject getRawElement() {
        return this.rawElement;
    }

    private void saveRiakValue() {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(this.properties);
            this.rawElement.setValue(bos.toByteArray());
            this.rawElement.store();
            bos.close();
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void loadRiakValue() {
        try {
            byte[] bytes = this.rawElement.getValueAsBytes();
            if (null == bytes) {
                this.properties = new HashMap<String, Object>();
                this.saveRiakValue();
            } else {
                final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                final ObjectInput in = new ObjectInputStream(bis);
                this.properties = (Map) in.readObject();
                in.close();
                bis.close();
                this.loadedProperties = true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    public boolean equals(final Object object) {
        return object instanceof GooElement && ((GooElement) object).getId().equals(this.getId());
    }

    public int hashCode() {
        return this.getId().hashCode();
    }


}
