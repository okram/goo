package com.tinkerpop.goo.util;

import com.tinkerpop.blueprints.pgm.Index;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class IndexMetadata implements Serializable {

    private final Class indexClass;
    private final String indexName;
    private final Index.Type indexType;
    private final Set<String> autoKeys;

    public IndexMetadata(final String indexName, final Class indexClass, final Index.Type indexType, final Set<String> autoKeys) {
        this.indexName = indexName;
        this.indexClass = indexClass;
        this.indexType = indexType;
        this.autoKeys = autoKeys;
    }

    public String getIndexName() {
        return this.indexName;
    }

    public Class getIndexClass() {
        return this.indexClass;
    }

    public Index.Type getIndexType() {
        return indexType;
    }

    public Set<String> getAutoKeys() {
        return this.autoKeys;
    }
}
