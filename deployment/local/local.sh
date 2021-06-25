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
$SPARK_HOME/bin/spark-class org.apache.spark.deploy.worker.Worker -c 2 -m 4g spark://192.168.1.17:7077


$SPARK_HOME/bin/spark-submit --master spark://192.168.1.17:7077 --num-executors 1 --executor-cores 1 --class org.atlanmod.Main_sleep SparkTE-1.0-SNAPSHOT.jar -core 1 -worker 1 -size 100 2>> test.log


  # with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
  #       try:
  #           p.shell(
  #               "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/bin/spark-submit \
  #                   --master spark://"+master+":7077 \
  #                   --num-executors "+str(real_nb_worker)+"\
  #                   --executor-cores " +str(cores) +"\
  #                   --class org.atlanmod.transformation.Main_Relational2Class_Simple \
  #                   /home/jphilippe/jars/SparkTE-1.0-SNAPSHOT.jar --m " +str(method)+ " --nexecutor "+str(total_core)+ \
  #                   " --size "+str(size)+" --ntests 30 -csv --path " + path + " --mode par \
  #                   >> " + path_log + " 2>> " + path_err

$SPARK_HOME/bin/spark-submit \
	--class org.atlanmod.transformation.Main_Relational2Class \
	--master spark://172.20.26.137:7077 \
	./SparkTE-1.0-SNAPSHOT.jar --ncore 0 --size 5 --ntests 1 -print -csv

$SPARK_HOME/bin/spark-submit \
	--master spark://172.20.26.137:7077 \
	--class org.atlanmod.transformation.Main_Relational2Class \
	SparkTE-1.0-SNAPSHOT.jar -ncore 4 --size 2 --ntests 1 -print -csv



# Run a spark worker (of a master running on localhost)

$SPARK_HOME/bin/spark-shell --master spark://127.0.0.1:7077


$SPARK_HOME/bin/spark-shell --master spark://10.44.192.243:7077
$SPARK_HOME/bin/spark-submit --master spark://$MASTER:7077 \
	--class jars/org.atlanmod.transformation.Main_Class2Relational \
	--num-executors $NCORE --executor-memory 100g \
	SparkTE-1.0-SNAPSHOT.jar --ncore $NCORE --size $size --ntests 30 -csv --path /home/jphilippe/c2r_results


$SPARK_HOME/bin/spark-submit --master spark://192.168.1.17:7078 
--executor-memory 1G --total-executor-cores 20 Class2Relational-1.0-SNAPSHOT.jar --ncore 1 --size 1 --ntests 1 -csv


~/Software/spark-3.1.1-bin-hadoop2.7/bin/spark-shell --master spark://127.0.0.1:7077
~/Software/spark-3.1.1-bin-hadoop2.7/bin/spark-shell --master spark://192.168.1.17:7077


~/Software/spark-3.1.1-bin-hadoop2.7/bin/spark-class org.apache.spark.deploy.master.Master
~/Software/spark-3.1.1-bin-hadoop2.7/bin/spark-class org.apache.spark.deploy.worker.Worker -c 1 -m 3G spark://172.16.111.106:7077
~/Software/spark-3.1.1-bin-hadoop2.7/bin/spark-submit --master spark://172.16.111.106:7077 Class2Relational-1.0-SNAPSHOT.jar --ncore 1 --size 2 --ntests 1 -csv

-ncore 1 --size 1 --ntests 1 -csv

spark://192.168.1.17:7011
spark://192.168.1.17:7078
spark://192.168.1.17:7077

# $SPARK_HOME/bin/spark-submit --master spark://192.168.1.17:7077 --name "Relational2Class" Relational2Class-1.0-SNAPSHOT.jar --ncore 1 --s 1 --ntests 1 -print -rfile

# Code snipet to run on the shell



# http://10.44.192.243:8080/
# http://10.44.192.243:4040/

172.16.96.8:7077


# spark://paravance-8.rennes.grid5000.fr:7077