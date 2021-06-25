""" Minimal example to deploy a spark cluster on G5k.

[This is just one onboarding example: one need to iterate a bit on this ;)]

For instance: it assumes that spark files are available in the user home directory of Grid'5000.
Processes are started from here as the g5k user (non-root) on production nodes.
As a consequence all state files written by spark will be most likely compete in this shared directory.
Don't know how bad it is.

A better approach would probably be to get the spark files in the /tmp of each nodes
and start the process from there.
e.g by downloading the tar.gz from internet (or copying the ones from the home dir).
An ideal (but maybe not realistic here) would be to use some docker image for spark
(that eases the deployment == easy to start/stop/destroying).
Working with raw processes can be a bit painful when it comes to ensure they
are stopped/started/ not restarted when the script is ran several times in a row...
"""
from enoslib.infra.enos_g5k.g5k_api_utils import get_api_username
import logging
import os
from pathlib import Path

from enoslib import *

logging.basicConfig(level=logging.DEBUG)



# claim the resources

network = G5kNetworkConf(id="n1", type="prod", roles=["my_network"], site="rennes")
conf = (
    G5kConf.from_settings(job_type="allow_classic_ssh", job_name="SPARK")
    .add_network_conf(network)
    .add_machine(
        roles=["master"], cluster="paravance", nodes=1, primary_network=network
    )
    .add_machine(
        roles=["worker"], cluster="paravance", nodes=1, primary_network=network
    )
    .finalize()
)
provider = G5k(conf)
roles, networks = provider.init()

username = get_api_username()

master = roles["master"][0].address

##########################################################################
try:

    grep_master = "ps -aux | grep  spark.deploy.master.Master"
    grep_worker = "ps -aux | grep  spark.deploy.worker.Worker"

    with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
        p.shell(
            f"({grep_master}) || /home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/start-master.sh -p 7077"
        )

    with play_on(pattern_hosts="worker", roles=roles, run_as=username) as p:
        p.shell(
            f"({grep_worker}) ||/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/start-slave.sh "+master+":7077 \
            > /tmp/sparkte.log 2> /tmp/sparkte.err"
        )

    ##########################################################################


    # $SPARK_HOME/bin/spark-submit --master spark://$MASTER:7077 \
    #     --class jars/org.atlanmod.transformation.Main_Class2Relational \
    #     --num-executors $NCORE --executor-memory 100g \
    #     SparkTE-1.0-SNAPSHOT.jar --ncore $NCORE --size $size --ntests 30 -csv --path /home/jphilippe/c2r_results
    ncore = "1"
    size = "1"
    path = "/home/jphilippe/c2r_results"

    with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
        p.shell(
            "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/bin/spark-submit \
                --master spark://"+master+":7077 \
                --class org.atlanmod.transformation.Main_Class2Relational \
                /home/jphilippe/jars/SparkTE-1.0-SNAPSHOT.jar --ncore "+ncore+" --size "+size+" --ntests 1 -csv -print --path " + path + " \
                > /tmp/sparkte.log 2> /tmp/sparkte.err"
        )
        p.fetch(src= "/tmp/sparkte.log", dest="~")
        p.fetch(src= "/tmp/sparkte.err", dest="~")
        p.fetch(src=path+"/c2r_"+size+"_"+ncore+".csv", dest="~")

    with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
        p.shell(
            "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/stop-all.sh"
        )

##########################################################################

except Exception as e:
    print(e)
finally:
    provider.destroy()

