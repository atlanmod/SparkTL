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

SITE="rennes"
CLUSTER="paranoia"

path_log = "/tmp/bible.log"
path_err = "/tmp/bible.err"

logging.basicConfig(level=logging.DEBUG)

parameters = dict(
    nb_worker=[1,2],
    cores=[1, 2],
    # size=[100, 200, 300, 400],
    # method=["two_phase"]
)

# parameters = dict(
#     nb_worker=[1],
#     cores=[8],
#     size=[1],
#     method=["HM_parallel"]
# )

node_workers = max(parameters["nb_worker"])

#########################################################################

# claim the resources

network = G5kNetworkConf(id="n1", type="prod", roles=["my_network"], site=SITE)
conf = (
    # G5kConf.from_settings(job_type="allow_classic_ssh", job_name="SPARK_relational2class_full", reservation="2021-05-17 08:10:00", walltime="00:45:00")
    G5kConf.from_settings(job_type="allow_classic_ssh", job_name="SPARK_relational2class", walltime="00:20:00")
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

##########################################################################

now = datetime.now()

current_time = now.strftime("%H:%M:%S")
print("Current Time =", current_time)

##########################################################################

def bench(parameter, master, roles, username):
    os.system("rm -rf /home/jphilippe/Software/spark-3.1.1-bin-hadoop2.7/work/")
    now = datetime.now()

    current_time = now.strftime("%H:%M:%S")
    print(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    print("THE TEST WITH PARAMETER=" + str(parameter) + " HAS BEEN STARTED AT " + str(current_time))
    print(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    
    # path = "/home/jphilippe/r2c_results_"+str(ntest)
    nb_worker = parameter["nb_worker"]
    # size = parameter["size"]
    # method = parameter["method"]
    cores = parameter["cores"]
    real_nb_worker = nb_worker

    if real_nb_worker == 0:
        real_nb_worker = 1

    total_core = nb_worker * cores
    if total_core == 0:
        total_core = 1

    with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
        try:
            p.shell("echo \"nb nodes=" + str(real_nb_worker) + ", nb cores="+ str(cores) +"\" >> " + path_log)
            p.shell(
                "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/bin/spark-submit \
                    --master spark://"+master+":7077 \
                    --num-executors "+str(real_nb_worker)+" \
                    --executor-cores " +str(cores) +" \
                    --class org.atlanmod.transformation.Main \
                    /home/jphilippe/jars/SparkTE-1.0-SNAPSHOT.jar \
                    >> " + path_log + " 2>> " + path_err
            )
            os.system("rm -rf /home/jphilippe/Software/spark-3.1.1-bin-hadoop2.7/work")
        except Exception as e:
            print("CANNOT FINISH THE COMPUTATION OF " + str(parameter))
            print(e)
            os.system("rm -rf /home/jphilippe/Software/spark-3.1.1-bin-hadoop2.7/work")
        try:
            p.fetch(src= path_log, dest="~")
        except Exception as e:
            print("CANNOT FETCH OUTPUT LOG")
            print(e)
        try:
            p.fetch(src= path_err, dest="~")
        except Exception as e:
            print("CANNOT FETCH ERROR LOG")
            print(e)

    current_time = now.strftime("%H:%M:%S")
    print(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    print("THE TEST WITH PARAMETER=" + str(parameter) + " HAS BEEN ENDED AT " + str(current_time))
    print(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")

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

    for test in range(1):
        sweeps = sweep(parameters)
        sweeper = ParamSweeper(
            persistence_dir=str(Path("sweeps_"+str(test))), sweeps=sweeps, save_sweeps=True
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
    os.system("rm -rf /home/jphilippe/OAR.*")
    os.system("rm -rf /home/jphilippe/oarapi*")