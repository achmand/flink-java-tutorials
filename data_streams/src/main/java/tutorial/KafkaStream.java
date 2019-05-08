package tutorial;

/*
Consume Kafka data stream using Flink.
*/

import com.google.gson.Gson;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import java.util.ArrayList;
import java.util.Properties;

public class KafkaStream {

    // used to parse JSON
    final static Gson gson = new Gson();

    public static void main(String[] args) throws Exception {

        // returns the execution environment (the context 'Local or Remote' in which a program is executed)
        // LocalEnvironment will cause execution in the current JVM
        // RemoteEnvironment will cause execution on a remote setup
        final StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();

        // provides utility methods for reading and parsing the program arguments
        // in this tutorial we will have to provide the input file and the output file as arguments
        final ParameterTool parameters = ParameterTool.fromArgs(args);

        // register parameters globally so it can be available for each node in the cluster
        environment.getConfig().setGlobalJobParameters(parameters);

        // set properties for kafka
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092"); // IP address where Kafka is running

        // pull datastreams from kafka to flink's datastream
        // must specify topic name, deserializer, properties
        DataStream<String> kafkaData = environment.addSource(new FlinkKafkaConsumer<String>("test", new SimpleStringSchema(), properties));

        // keyword count from stream and saves to textfile
        DataStream<Tuple2<String, Integer>> result = kafkaData.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {

                // convert each line/json object to Publication
                Publication publication = gson.fromJson(value, Publication.class);

                // get all keywords
                ArrayList<String> keywords = publication.getKeywords();

                if(keywords == null){
                    return;
                }

                for (String keyword : keywords) {
                    out.collect(new Tuple2<String, Integer>(keyword, 1));
                }
            }
        });

        result.keyBy(0).sum(1).writeAsText(parameters.get("output"));
        environment.execute("Kafka stream keyword count");
    }
}
