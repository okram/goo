package com.tinkerpop.goo;


import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.util.graphml.GraphMLReader;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class Play {

    public static void main(String[] args) throws Exception {
        Graph graph = new GooGraph("/tmp/goograph");
        GraphMLReader.inputGraph(graph, GraphMLReader.class.getResourceAsStream("graph-example-2.xml"));
        for (Edge edge : graph.getEdges()) {
            System.out.println(edge);
        }

    }
}
