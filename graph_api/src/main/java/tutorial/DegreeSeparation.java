package tutorial;

// importing packages

import com.google.gson.Gson;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.Graph;
import org.apache.flink.graph.Vertex;
import org.apache.flink.graph.library.SingleSourceShortestPaths;
import org.apache.flink.types.NullValue;
import org.apache.flink.util.Collector;
import scala.Tuple2;

import java.util.ArrayList;

// bipartite graph -> use projection -> for recommendations

/*
Implementing Degree of Separation using Flink's Gelly Graph API
*/
public class DegreeSeparation {
    final static Gson gson = new Gson();

    public static void main(String[] args) throws Exception {

        // returns the execution environment (the context 'Local or Remote' in which a program is executed)
        // LocalEnvironment will cause execution in the current JVM
        // RemoteEnvironment will cause execution on a remote setup
        final ExecutionEnvironment environment = ExecutionEnvironment.getExecutionEnvironment();

        // provides utility methods for reading and parsing the program arguments
        // in this tutorial we will have to provide the input file and the output file as arguments
        final ParameterTool parameters = ParameterTool.fromArgs(args);

        // register parameters globally so it can be available for each node in the cluster
        environment.getConfig().setGlobalJobParameters(parameters);

        // read text file from the parameter 'input' passed in args
        // line-by-line and returns them as Strings
        DataSet<String> textLines = environment.readTextFile(parameters.get("input"));

        // Author -> Collaborating Author
        DataSet<Tuple2<String, String>> authors = textLines.flatMap(new Tokenizer());

        // convert the dataset to edges in a graph
        DataSet<Edge<String, NullValue>> edges = authors.map(new MapFunction<Tuple2<String, String>, Edge<String, NullValue>>() {
            @Override
            public Edge<String, NullValue> map(Tuple2<String, String> value) {
                Edge<String, NullValue> edge = new Edge();
                edge.setSource(value._1()); // author
                edge.setTarget(value._2()); // collaboration
                return edge;
            }
        });

        // creates graph from the edges generated
        Graph<String, NullValue, NullValue> collaborationGraph = Graph.fromDataSet(edges, environment);

        // we need to add weights since we will apply SingleSourceShortestPaths
        Graph<String, NullValue, Double> wCollaborationGraph = collaborationGraph.mapEdges(new MapFunction<Edge<String, NullValue>, Double>() {
            @Override
            public Double map(Edge<String, NullValue> stringNullValueEdge) {
                return 1.0;
            }
        });

        // use the SingleSourceShortestPaths to get all the collaboration authors for the collaboration authors
        // for a specified authors (similar to friends of friends)
        SingleSourceShortestPaths<String, NullValue> singleSourceShortestPaths = new SingleSourceShortestPaths<String, NullValue>(parameters.get("author"), 1000);
        DataSet<Vertex<String, Double>> result = singleSourceShortestPaths.run(wCollaborationGraph);

        System.out.println(result.count());

        // the collaboration authors for the collaboration authors for a specified authors (similar to friends of friends)
        DataSet<Vertex<String, Double>> resultAuthor = result.filter(new FilterFunction<Vertex<String, Double>>() {
            @Override
            public boolean filter(Vertex<String, Double> value) {
                if (value.f1 == 2.0) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        // output the final result
        // check that the argument 'output' was passed to save in that path
        if (parameters.has("output")) {
            resultAuthor.writeAsText(parameters.get("output"), FileSystem.WriteMode.OVERWRITE);
            environment.execute("Graph API Tutorial");
        }
    }

    public static class Tokenizer implements FlatMapFunction<String, Tuple2<String, String>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, String>> out) {
            Publication publication = gson.fromJson(value, Publication.class);
            ArrayList<Author> authors = publication.getAuthors();

            // no collaboration (one author)
            if (authors.size() <= 1) {
                return;
            }

            for (int i = 0; i < authors.size() - 1; i++) {
                String currentAuthor = authors.get(i).name;
                for (int j = i + 1; j < authors.size(); j++) {
                    String collaboration = authors.get(j).name;

                    // must output two tuples since we need to create
                    // two edges for an undirected edge
                    out.collect(new Tuple2<String, String>(currentAuthor, collaboration));
                    out.collect(new Tuple2<String, String>(collaboration, currentAuthor));
                }
            }
        }
    }
}
