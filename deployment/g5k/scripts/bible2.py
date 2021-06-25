from enoslib.infra.enos_g5k.g5k_api_utils import get_api_username
from pathlib import Path
from enoslib import *
from operator import itemgetter
from execo_engine import ParamSweeper, sweep
from datetime import datetime

import logging
import os
import traceback


SITE="rennes"
CLUSTER="paranoia"

path_log = "/tmp/bible.log"
path_err = "/tmp/bible.err"

network = G5kNetworkConf(id="n1", type="prod", roles=["my_network"], site=SITE)
conf = (
    G5kConf.from_settings(job_type="allow_classic_ssh", job_name="SPARK_relational2class", walltime="00:30:00")
    .add_network_conf(network)
    .add_machine(
        roles=["master"], cluster=CLUSTER, nodes=1, primary_network=network
    )
    .add_machine(
        roles=["worker"], cluster=CLUSTER, nodes=4, primary_network=network
    )
    .add_machine(
        roles=["driver"], cluster=CLUSTER, nodes=4, primary_network=network
    )
    .finalize()
)
provider = G5k(conf)
roles, networks = provider.init()
username = get_api_username()
master = roles["master"][0].address


 with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
    p.shell(
        "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/start-master.sh -p 7077"
    )
with play_on(pattern_hosts="worker", roles=roles, run_as=username) as p:
    p.shell(
        "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/start-worker.sh spark://"+master+":7077"
    )

for i in range(30):    
    with play_on(pattern_hosts="driver", roles=roles, run_as=username) as p:
        p.shell("echo \"nb nodes=" + str(real_nb_worker) + ", nb cores="+ str(cores) +"\" >> " + path_log)
        p.shell(
            "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/bin/spark-submit --master spark://"+master+":7077 \
                --class org.atlanmod.Main /home/jphilippe/jars/SparkTE-1.0-SNAPSHOT.jar \
                    -path /home/jphilippe/doc/bible.txt >> " + path_log + " 2>> " + path_err
        )

with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
    p.shell(
        "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/stop-all.sh"
    )
with play_on(pattern_hosts="worker", roles=roles, run_as=username) as p:
    p.shell(
        "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/stop-all.sh"
    )