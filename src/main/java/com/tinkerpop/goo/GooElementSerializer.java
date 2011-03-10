package com.tinkerpop.goo;

import com.tinkerpop.blueprints.pgm.Element;
import jdbm.Serializer;
import jdbm.SerializerInput;
import jdbm.SerializerOutput;
import jdbm.helper.DefaultSerializer;

import java.io.IOException;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GooElementSerializer<T extends Element> implements Serializer<T> {

    private final GooGraph graph;

    public GooElementSerializer(final GooGraph graph) {
        this.graph = graph;
    }

    public T deserialize(SerializerInput in) throws IOException {
        T t = (T) DefaultSerializer.INSTANCE.deserialize(in);
        ((GooElement) t).graph = this.graph;
        return t;
    }

    public void serialize(SerializerOutput out, T t) throws IOException {
        DefaultSerializer.INSTANCE.serialize(out, t);
    }
}
