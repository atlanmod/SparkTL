##/bin/bash

# https://towardsdatascience.com/diy-apache-spark-docker-bb4f11c10d24

BRIDGE=spark-net-bridge
IMAGE_MASTER=sdesilva26/spark_master:0.0.2
IMAGE_WORKER=sdesilva26/spark_worker:0.0.2

# Get the docker images
sudo docker pull $IMAGE_MASTER
sudo docker pull $IMAGE_WORKER

# Create a bridge network "spark-net-bridge "
sudo docker network create --driver bridge $BRIDGE
# To inspect the network: sudo docker network inspect $BRIDGE

# Run containers 
# To know running containers: sudo docker container ls
# To stop running containers: sudo docker stop {CONTAINER_ID}

# - a master: 172.18.0.2/16
sudo docker run -dit --name spark-master --network $BRIDGE --entrypoint /bin/bash $IMAGE_MASTER

# - a worker: 172.18.0.3/16
sudo docker run -dit --name spark-worker1 --network $BRIDGE --entrypoint /bin/bash $IMAGE_WORKER

# ------------------------------------------------------------------------

# Run a spark master 
$SPARK_HOME/bin/spark-class org.apache.spark.deploy.master.Master

# Run a spark worker (of a master running on localhost)
$SPARK_HOME/bin/spark-class org.apache.spark.deploy.worker.Worker -c 1 -m 3G spark://10.44.192.243:7077


# Run a spark worker (of a master running on localhost)
$SPARK_HOME/bin/spark-shell --master spark://10.44.192.243:7077
$SPARK_HOME/bin/spark-submit --master spark://10.44.192.243:7077

# Code snipet to run on the shell
val NUM_SAMPLES=10000
var count = sc.parallelize(1 to NUM_SAMPLES).filter { _ =>
  val x = math.random
  val y = math.random
  x*x + y*y < 1
}.count() * 4/(NUM_SAMPLES.toFloat)


# http://10.44.192.243:8080/
# http://10.44.192.243:4040/