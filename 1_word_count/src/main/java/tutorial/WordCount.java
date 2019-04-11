package tutorial;

// importing packages
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.util.Collector;

/*
Implementing WordCount on Apache Flink using batch processing
*/
public class WordCount {

    public static void main(String[] args) throws Exception{

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

        // tokenize the lines from textLines to (word, 1), groups and then counts
        DataSet<Tuple2<String, Integer>> result =
                // split up the lines in pairs (2-tuples) containing: (word,1)
                textLines.flatMap(new Tokenizer())
                        // group by the tuple field "0" which is the word
                        .groupBy(0)
                        // sum up tuple field "1"
                        .sum(1);

        // output the final result
        // check that the argument 'output' was passed to save in that path
        if(parameters.has("output")){
            // write result as CSV row delimiter is a line break, field delimiter is a space
            result.writeAsCsv(parameters.get("output"), "\n", " ");

            // execute program, triggers execution, 'Tutorial_1' is the job name
            environment.execute("Tutorial_1");
        }
    }

    // user-defined tokenizer, splits the lines into words and emits tuple of (word, 1)
    public static class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {

            // normalize/convert to lower case and split the line into words
            String[] words = value.toLowerCase().split("\\W+");

            // emit the pairs (word, 1)
            for (String word : words) {
                // check that the length is greater than 0
                if (word.length() > 0) {
                    // append tuple (word, 1)
                    out.collect(new Tuple2<String, Integer>(word, 1));
                }
            }
        }
    }
}
