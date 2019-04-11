package tutorial;

// importing packages
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.utils.ParameterTool;

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


    }
}
