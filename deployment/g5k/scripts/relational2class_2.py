from enoslib.infra.enos_g5k.g5k_api_utils import get_api_username
from pathlib import Path
from enoslib import *
from operator import itemgetter
from execo_engine import ParamSweeper, sweep
from datetime import datetime

import logging
import os
import traceback

# Change sizes in parameters
# Add reservation + walltime
# set 10 to the loops

##########################################################################

logging.basicConfig(level=logging.DEBUG)

parameters = dict(
    nb_worker=[0, 1, 2, 4],
    size=[500, 1000, 2000, 4000]
)

node_workers = max(parameters["nb_worker"])

#########################################################################

# claim the resources

network = G5kNetworkConf(id="n1", type="prod", roles=["my_network"], site="rennes")
conf = (
    G5kConf.from_settings(job_type="allow_classic_ssh", job_name="SPARK_relational2class_2", reservation="2021-03-30 19:00:00", walltime="10:00:00")
    .add_network_conf(network)
    .add_machine(
        roles=["master"], cluster="paravance", nodes=1, primary_network=network
    )
    .add_machine(
        roles=["worker"], cluster="paravance", nodes=node_workers, primary_network=network
    )
    .finalize()
)
provider = G5k(conf)
roles, networks = provider.init()

username = get_api_username()

master = roles["master"][0].address

##########################################################################

now = datetime.now()

current_time = now.strftime("%H:%M:%S")
print("Current Time =", current_time)

##########################################################################

def bench(parameter, master, roles, username, ntest):
    now = datetime.now()
    current_time = now.strftime("%H:%M:%S")
    print(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    print("THE TEST WITH PARAMETER=" + str(parameter) + " HAS BEEN DONE AT " + str(current_time))
    print(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    path = "/home/jphilippe/r2c_results_"+str(ntest)
    path_log = "/tmp/sparkte_r2c_2.log"
    path_err = "/tmp/sparkte_r2c_2.err"
    nb_worker = parameter["nb_worker"]
    size = parameter["size"]
    real_nb_worker = nb_worker

    if real_nb_worker == 0:
        real_nb_worker = 1

    with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
        p.shell(
            "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/bin/spark-submit \
                --master spark://"+master+":7077 \
                --num-executors "+str(real_nb_worker)+"\
                --class org.atlanmod.transformation.Main_Relational2Class \
                /home/jphilippe/jars/SparkTE-1.0-SNAPSHOT.jar --nexecutor "+str(nb_worker)+" --size "+str(size)+" --ntests 10 -csv -print --path " + path + " \
                >> " + path_log + " 2>> " + path_err
        )
        p.fetch(src= path_log, dest="~")
        p.fetch(src= path_err, dest="~")
        p.fetch(src=path+"/r2c_"+str(size)+"_"+str(nb_worker)+".csv", dest="~")

##########################################################################
try:

# start the machines

    grep_master = "ps -aux | grep  spark.deploy.master.Master"
    grep_worker = "ps -aux | grep  spark.deploy.worker.Worker"

    with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
        p.shell(
            "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/start-master.sh -p 7077"
        )

    with play_on(pattern_hosts="worker", roles=roles, run_as=username) as p:
        p.shell(
            "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/start-worker.sh spark://"+master+":7077"
        )

# Run the job

    for test in range(10):
        sweeps = sweep(parameters)
        sweeper = ParamSweeper(
            persistence_dir=str(Path("sweeps_2_"+str(test))), sweeps=sweeps, save_sweeps=True
        )
        parameter = sweeper.get_next()
        while parameter:
            try:
                bench(parameter, master, roles, username, test)
                sweeper.done(parameter)
            except Exception as e:
                traceback.print_exc()
                sweeper.skip(parameter)
            finally:
                parameter = sweeper.get_next()

# Stop all on spark machines 

    with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
        p.shell(
            "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/stop-all.sh"
        )
    with play_on(pattern_hosts="worker", roles=roles, run_as=username) as p:
        p.shell(
            "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/stop-all.sh"
        )

except Exception as e:
    print(e)
finally:
    provider.destroy()
