# Word Count in Apache Flink (batch processing) using Java 
In this tutorial we will implement a word counting program (batch processing) on Apache Flink using Java. Make sure that any prerequisites required are installed on your machine. For this tutorial [IntelliJ IDEA](https://www.jetbrains.com/help/idea/installation-guide.html?section=Windows) was used to write Java code. We recommend using  [IntelliJ IDEA](https://www.jetbrains.com/help/idea/installation-guide.html?section=Windows) or [Eclipse](https://www.eclipse.org/downloads/packages/installer) as an IDE. In this tutorial it is assumed that you have some knowledge in Java or any other [object-oriented programming](https://en.wikipedia.org/wiki/Object-oriented_programming) language. 

Main resources used in this tutorial;
* [WordCount.java](https://github.com/achmand/flink-java-tutorials/blob/master/1_word_count/src/main/java/tutorial/WordCount.java)
* [pom.xml](https://github.com/achmand/flink-java-tutorials/blob/master/1_word_count/pom.xml)

## Tutorial
### Step 1: Setting up a new Maven project. 
In your IDE create a new Maven Project. Once the project is created, create a new package in `src/main/java` and name it `tutorial`. Inside the newly created package, create a new class and name it `WordCount`. 

### Step 2: Setting up dependencies. 
Now we need to add some dependencies which will be needed to write our word count logic. We will need the following dependencies: 
* flink-java 
* flink-streaming 
* flink-clients 

Open the `pom.xml` and include the following dependencies.
```
<dependencies>
    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-java</artifactId>
        <version>1.8.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-streaming-java_2.12</artifactId>
        <version>1.8.0</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-clients_2.12</artifactId>
        <version>1.8.0</version>
    </dependency>
</dependencies>
```
Once you added these dependencies, clean Maven project and install. The image below shows how to clean and install a Maven project when using IntelliJ, if you are using Eclipse just right click on the `pom.xml` go to 'Run As' and click on 'Maven clean' and then 'Maven install'. This will download and install the dependencies which were included.

![IntelliJ Maven](https://github.com/achmand/flink-java-tutorials/blob/master/images/tutorial_1/intellij_maven.png?raw=true)

*NOTE: You can Enable Auto-Import, to automatically download and install any dependencies included in the `pom.xml` file.*

### Step 3: Implementing the WordCount class. 
Open the `WordCount` class and import the following packages.
```
// importing packages
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.util.Collector;
```

After importing the packages write the `main(String[] args)` function. This is the entry point for our Java program. 
```
public static void main(String[] args) throws Exception{}
```
#### Step 3.1: Setting up Execution Environment & Global Job Params. 
Inside the `main(String[] args)` function include the following lines of code. The comments inside this code explains the use of each line. If the explanation in the comments is not enough to understand the following code, we suggest to read through the following sources.
* [ExecutionEnvironment](https://ci.apache.org/projects/flink/flink-docs-master/api/java/org/apache/flink/api/java/ExecutionEnvironment.html)
* [ParameterTool](https://ci.apache.org/projects/flink/flink-docs-master/api/java/org/apache/flink/api/java/utils/ParameterTool.html)
* [setGlobalJobParameters](https://ci.apache.org/projects/flink/flink-docs-master/api/java/org/apache/flink/api/common/ExecutionConfig.html#setGlobalJobParameters-org.apache.flink.api.common.ExecutionConfig.GlobalJobParameters-)

In summary this code will; get the execution environment depending if you are running it remotely or locally, reads/parses the arguments passed and registers these parameters as global job parameters to be available to each node in the cluster. 
```
// returns the execution environment (the context 'Local or Remote' in which a program is executed)
// LocalEnvironment will cause execution in the current JVM
// RemoteEnvironment will cause execution on a remote setup
final ExecutionEnvironment environment = ExecutionEnvironment.getExecutionEnvironment();

// provides utility methods for reading and parsing the program arguments
// in this tutorial we will have to provide the input file and the output file as arguments
final ParameterTool parameters = ParameterTool.fromArgs(args);

// register parameters globally so it can be available for each node in the cluster
environment.getConfig().setGlobalJobParameters(parameters);
```
#### Step 3.2: Dataset Transformations. 
The next step now is to read the text file from the given input path. One of the parameters passed to the program will be `input` which will be the input path for our text file. The following code will read the text file from the `input` specified and it will convert it into a `DataSet<String>` instance. 
```
// read text file from the parameter 'input' passed in args
// line-by-line and returns them as Strings
DataSet<String> textLines = environment.readTextFile(parameters.get("input"));
```
Now we need to take the `DataSet<String>` instance and tokenize each word as a tuple of `(word, 1)`. To do so we need to apply the `map` operation to the `DataSet<String>` instance. The `map` operation takes a class which implements the `MapFunction` interface as an argument. So inside the `WordCount` class we will implement a new class called `Tokenizer` which implements the `MapFunction` interface. 

*NOTE: In our implementation we used the `FlatMapFunction` interface, since operations that produce multiple result elements from a single input element are implemented using this interface.*

For more information on the `MapFunction` interface take a look at this [source](https://ci.apache.org/projects/flink/flink-docs-master/api/java/org/apache/flink/api/common/functions/MapFunction.html) and for the `FlatMapFunction` interface take a look at this [source](https://ci.apache.org/projects/flink/flink-docs-master/api/java/org/apache/flink/api/common/functions/FlatMapFunction.html).
```
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
```
Now we call the `map` operation and pass an instance of the newly created `Tokenizer` class as an argument. Then we need to group by the word and sum up by the value which, is shown in the following code. 

```
// tokenize the lines from textLines to (word, 1), groups and then counts
DataSet<Tuple2<String, Integer>> counts =
        // split up the lines in pairs (2-tuples) containing: (word,1)
        textLines.flatMap(new Tokenizer())
                // group by the tuple field "0" which is the word
                .groupBy(0)
                // sum up tuple field "1"
                .sum(1);
```
#### Step 3.3: Output the Result.
The final bit for this implementation is to output the results. 
```
// output the final result
// check that the argument 'output' was passed to save in that path
if(parameters.has("output")){
    // write result as CSV row delimiter is a line break, field delimiter is a space
    result.writeAsCsv(parameters.get("output"), "\n", " ");

    // execute program, triggers execution, 'Tutorial_1' is the job name
    environment.execute("Tutorial_1");
}
```
### Step 4: Building JAR and Execute on Flink.
To build the JAR using IntelliJ follow these steps;
* File -> Project Structure -> Project Settings -> Artifacts -> Click green plus sign -> Jar -> From modules with dependencies...
* Select 'Extract to the target Jar'
* Click on 'OK'
* Build | Build Artifact

Once the JAR is built, we need a `txt` file to read as an input. A file located in this repository called `pride_and_prejudice.txt` will be used as an input. This file can be downloaded from [Project Gutenberg](https://www.gutenberg.org/ebooks/1342). 

Now open a terminal and execute the following commands to run the JAR file on Apache Flink. First start a new cluster by typing the following command in the terminal `flink/build-target/bin/start-cluster.sh`. After doing so open a browser and go to `http://localhost:8081/` to make sure that the cluster is running. 

*NOTE: Make sure the paths match your locations on your machine.*

Once the cluster is running on the machine execute the JAR file by executing the following (replace with your paths) `FLINKPATH RUN -c MAINCLASS JARPATH --input INPUTPATH --output OUTPUTPATH`. 

On our machine this command was as follows:
`flink/build-target/bin/flink run -c tutorial.WordCount flink-java-tutorials/1_word_count/out/artifacts/1_word_count_jar/1_word_count.jar --input flink-java-tutorials/1_word_count/pride_and_prejudice.txt --output flink-java-tutorials/1_word_count/ouput`.

Once the job is finished the following output is shown in the terminal and the output is saved in the specified path passed in `--output`.
```
Starting execution of program
Program execution finished
Job with JobID 7a64e43b07fa0711fbb6285fb95837e2 has finished.
Job Runtime: 2174 ms
```
To stop a running cluster execute the following command `flink/build-target/bin/stop-cluster.sh`. 

## Exercises 
* Re-run the code but provide another input file, you can download another source from [Project Gutenberg](https://www.gutenberg.org/ebooks/1342).
* Clean the words (numbers, punctuation, stop-words, cases, etc..)
* Count only words starting with a specific letter, this letter must be passed as an argument similiar to the arguments passed for the input/output paths.


