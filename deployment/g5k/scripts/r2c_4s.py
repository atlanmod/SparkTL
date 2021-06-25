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
CLUSTER="paravance"


logging.basicConfig(level=logging.DEBUG)

parameters = dict(
    # nb_worker_cores=[(2,4),(2,2),(2,1),(1,1),(1,2),(1,4)]
    nb_worker_cores=[(2,4),(1,1)]
)

# parameters = dict(
#     nb_worker=[2],
#     cores=[4]
# )
# parameters = dict(
#     nb_worker=[1, 2],
#     cores=[1, 2, 4]
# )
size = 10
sleep =4000
node_workers = 2
my_job = "r2c_byrule_4s"


path_log = "/tmp/"+my_job+".log"
path_err = "/tmp/"+my_job+".err"
#########################################################################

# claim the resources

network = G5kNetworkConf(id="n1", type="prod", roles=["my_network"], site=SITE)
conf = (
    # G5kConf.from_settings(job_type="allow_classic_ssh", job_name="SPARK_r2c_bystep", reservation="2021-06-22 19:00:00", walltime="13:00:00")
    G5kConf.from_settings(job_type="allow_classic_ssh", job_name=my_job, walltime="2:00:00")
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

    nb_worker = parameter["nb_worker_cores"][0]
    nb_core = parameter["nb_worker_cores"][1]

    with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
        try:
            p.shell(
                "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/bin/spark-submit \
                    --master spark://"+master+":7077 \
                    --num-executors "+str(nb_worker)+" \
                    --executor-cores " +str(nb_core) +" \
                    --class org.atlanmod.Main_Relational2Class_Instantiate_ByRule \
                    /home/jphilippe/jars/SparkTE-1.0-SNAPSHOT.jar -core "+str(nb_core)+" -executor "+str(nb_worker)+" -size " + str(size) + " -mode sleeping_instantiate -sleep " +str(sleep)+" \
                    >> " + path_log + " 2>> " + path_err
            )
            p.fetch(src= path_log, dest="~")
            p.fetch(src= path_err, dest="~")
            os.system("rm -rf /home/jphilippe/Software/spark-3.1.1-bin-hadoop2.7/work")

        except Exception as e:
            print("CANNOT FINISH THE COMPUTATION OF " + str(parameter))
            print(e)
            os.system("rm -rf /home/jphilippe/Software/spark-3.1.1-bin-hadoop2.7/work")

##########################################################################
try:

# start the machines

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
    os.system("rm -rf /home/jphilippe/OAR*")
    os.system("rm -rf /home/jphilippe/oarapi*")
