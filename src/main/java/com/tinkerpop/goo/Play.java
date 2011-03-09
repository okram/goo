package com.tinkerpop.goo;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.util.graphml.GraphMLReader;


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class Play {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 100; i++) {
            Graph graph = new GooGraph("http://localhost:8098/riak");
            graph.clear();
            GraphMLReader.inputGraph(graph, GraphMLReader.class.getResourceAsStream("graph-example-1.xml"));
            for (Vertex v : graph.getVertices()) {
                System.out.println(v);
                for (Edge e : v.getOutEdges()) {
                    System.out.println("\t" + e);
                    System.out.println("\t\t" + e.getProperty("weight"));
                }
            }
        }
    }
}
