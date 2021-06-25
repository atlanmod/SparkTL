# Launch an application in local mode

SparkTE works with Apache Spark. To run a job on Spark, a cluster must first be initialized. Then, the job must be submitted to the cluster.
More details can be found on https://spark.apache.org/docs/latest/spark-standalone.html

##Run a local Spark cluster in standalone mode

First, be sure you have defined ``$SPARK_HOME`` as the directory where your Spark files are.

Spark works with Master and Workers, as a Master/Slave architecture.

To start a master:
``$SPARK_HOME/bin/spark-class org.apache.spark.deploy.master.Master -p 7077``

 To start a worker for the master, with $CORES cores, and $MEM memory:
``$SPARK_HOME/bin/spark-class org.apache.spark.deploy.worker.Worker spark://{master_ip}:7077  --cores $CORES --memory $MEM``

## Submit a job on the Spark cluster

Once the cluster as been set, you can submit a job for the master as follows. The job must be defined in a class, as a ``main`` function, of the jar file. 

``$SPARK_HOME/bin/spark-submit --master spark://{master_ip}:7077 --class {class} SparkTL.jar``