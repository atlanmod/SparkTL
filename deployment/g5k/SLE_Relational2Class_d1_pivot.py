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
CLUSTER="paravance"

my_job = "Relational2Class_d1_pivot"
logging.basicConfig(level=logging.DEBUG)

ntest = 10 # number of results
nstep = 5 # to compute the full transformation
nb_partition_core = 4 # number of partition of data per core

parameters = dict(nb_worker_cores=[(1,1), (1,2), (2,2), (2,4)], sleep=[0,50,120,250,500,1000,2000], size=[10], pivot=["simple","complex","n2"])

path_log = "/tmp/"+my_job+".log"
path_err = "/tmp/"+my_job+".err"

node_workers = max(parameters ['nb_worker_cores'])[0]
commit_number = "#fd3209d486d9ab9270b2dcb5a649115f1fcd4379"

#########################################################################

# claim the resources

network = G5kNetworkConf(id="n1", type="prod", roles=["my_network"], site=SITE)
conf = (
    G5kConf.from_settings(job_type="allow_classic_ssh", job_name=my_job, reservation="2022-01-01 19:00:00", walltime="13:00:00")
    .add_network_conf(network)
    .add_machine(
        roles=["master"], cluster=CLUSTER, nodes=1, primary_network=network
    )
    .add_machine(
        roles=["worker"], cluster=CLUSTER, nodes=node_workers, primary_network=network
    )
    .finalize()
)
provider = G5k(conf)
roles, networks = provider.init()

username = get_api_username()

master = roles["master"][0].address

#########################################################################


def bench(parameter, master, roles, username):
    nb_worker = parameter["nb_worker_cores"][0]
    nb_core = parameter["nb_worker_cores"][1]
    sleeping = parameter["sleep"]
    size = parameter["size"]
    pivot = parameter["pivot"]
    nb_partition = nb_worker * nb_core * nb_partition_core

    with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
        try:
            p.shell(
                "$SPARK_HOME/bin/spark-submit \
                    --master spark://"+master+":7077 \
                    --num-executors "+str(nb_worker)+" \
                    --executor-cores " +str(nb_core) +" \
                    --class org.atlanmod.Main_Relational2Class \
                    SparkTE-1.0-SNAPSHOT.jar -size "+str(size)+" -core "+str(nb_core)+" -executor " + str(nb_worker) + " \
                        -partition " + str(nb_partition)+ " -sleep_instantiate " +str(sleeping)+" -sleep_guard " + str(sleeping) +" -sleep_apply " + str(sleeping) + " -pivot " + pivot + " -mode by_rule -step " + str(nstep)+ " \
                    >> " + path_log + " 2>> " + path_err
            )
            p.fetch(src= path_log, dest="~")
            p.fetch(src= path_err, dest="~")

        except Exception as e:
            print("CANNOT FINISH THE COMPUTATION OF " + str(parameter))
            print(e)

#########################################################################

try:

# start the machines

    with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
        p.shell(
            "$SPARK_HOME/sbin/start-master.sh -p 7077"
        )
        p.shell(
            "echo \" "+str(commit_number)+" \" >> " + path_log
        )

    with play_on(pattern_hosts="worker", roles=roles, run_as=username) as p:
        p.shell(
            "$SPARK_HOME/sbin/start-worker.sh spark://"+master+":7077"
        )

# Run the job

    for test in range(ntest):
        sweeps = sweep(parameters)
        sweeper = ParamSweeper(
            persistence_dir=str(Path("sweeps_"+my_job+"_"+str(test))), sweeps=sweeps, save_sweeps=True
        )
        parameter = sweeper.get_next()

        while parameter:
            try:
                bench(parameter, master, roles, username)
                sweeper.done(parameter)
            except Exception as e:
                traceback.print_exc()
                sweeper.skip(parameter)
            finally:
                parameter = sweeper.get_next()

# Stop all on spark machines 

    with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
        p.shell(
            "$SPARK_HOME/sbin/stop-all.sh"
        )
    with play_on(pattern_hosts="worker", roles=roles, run_as=username) as p:
        p.shell(
            "/$SPARK_HOME/sbin/stop-all.sh"
        )

except Exception as e:
    print(e)
finally:
    provider.destroy()
