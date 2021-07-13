# SparkTE

Spark Transformation Engine (SparkTE), previously denoted as SparkTL, is an engine for model transformation written using Apache Spark.

SparkTE implementation is based on [CoqTL](https://anonymous.4open.science/r/CoqTL-SLE2021) specification. The two tools aims at evolving in symbiosis.

## Versions

The SparkTE library has been designed using the following versions

- Gradle:  6.4.1
- Java: 1.8
- Scala: 2.12
- Hadoop Spark: 3.0.0

## How to build

The whole transformation engine can be compiled using ``gradle jar``.

## How to use
 
 The application is designed for standalone Spark clusters

### Start a Spark cluster
 
Before running a Spark cluster, you need to define ``$SPARK_HOME`` as the directory where your Spark files are.
You can find them on: https://spark.apache.org/downloads.html
 
First start a master:
``$SPARK_HOME/bin/spark-class org.apache.spark.deploy.master.Master -p 7077``
  Be sure to note the ip adress of the master process. It will be useful to link your Workers, and submit your spark jobs.
  
  
 Start as many worker as wish as follows:
``$SPARK_HOME/bin/spark-class org.apache.spark.deploy.worker.Worker spark://{master_ip}:7077``

### Submit your job

You can submit your job on your Spark cluster using the following command:

``$SPARK_HOME/bin/spark-submit --master spark://{master_ip}:7077 --class {class} SparkTL.jar``

For instance, the following command run 30 class2relational transformations of an input model containg 10 times a default pattern, for each implemented transformation strategy, on 1 executor and produce csv containing computation times as output:

``$SPARK_HOME/bin/spark-submit --master spark://{master_ip}:7077  --num-executors 1 --executor-cores 1 --class org.atlanmod.Main_Relational2Class SparkTE-1.0-SNAPSHOT.jar --ncore 1 --executor 1 --size 1``

