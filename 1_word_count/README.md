# Word Count in Apache Flink (batch processing) using Java 
In this tutorial we will implement a word counting program (batch processing) on Apache Flink using Java. Make sure that any prerequisites are installed on your machine. For this tutorial [IntelliJ IDEA](https://www.jetbrains.com/help/idea/installation-guide.html?section=Windows) was used to write Java code. We recommend using  [IntelliJ IDEA](https://www.jetbrains.com/help/idea/installation-guide.html?section=Windows) or [Eclipse](https://www.eclipse.org/downloads/packages/installer) as an IDE. In this tutorial it is assumed that you have some knowledge in Java or any other [object-oriented programming](https://en.wikipedia.org/wiki/Object-oriented_programming) language. 

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
```

After importing the packages write the `main(String[] args)` function. This is the entry point for our Java program. 
```
public static void main(String[] args) throws Exception{}
```
Inside the `main(String[] args)` function include the following lines of code. The comments inside this code explains the use of each line of code. If the explanation in the comments are not enough to understand the following code, we suggest to read through the following sources.
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





