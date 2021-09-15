from enoslib.infra.enos_g5k.g5k_api_utils import get_api_username
from pathlib import Path
from enoslib import *
from operator import itemgetter
from execo_engine import ParamSweeper, sweep
from datetime import datetime

import logging
import os
import traceback

SITE="nancy"
CLUSTER="gros"
JOB="IMDBSpark"
commit_number = "#fd3209d486d9ab9270b2dcb5a649115f1fcd4379" #TODO change with the current git commit signature
path_log = "/tmp/"+JOB+".log"
path_err = "/tmp/"+JOB+".err"

#########################################################################

RESERVATION="2021-09-14 23:10:00"
TIME="00:10:00"

logging.basicConfig(level=logging.DEBUG)

ntest = 10

#parameters = dict(nb_worker_cores=[(4,4)])
parameters = dict(nb_worker_cores=[(1,1), (1,2), (2,2), (2,4), (4,4), (8,4)])
json_actors = "/home/jphilippe/models/actors_sw.json"
json_movies = "/home/jphilippe/models/movies_sw.json"
txt_links = "/home/jphilippe/models/links_sw.txt"

node_workers = max(parameters ['nb_worker_cores'])[0]


#########################################################################

# claim the resources

network = G5kNetworkConf(id="n1", type="prod", roles=["my_network"], site=SITE)
conf = (
    G5kConf.from_settings(job_type="allow_classic_ssh", job_name=JOB, walltime=TIME)
    #G5kConf.from_settings(job_type="allow_classic_ssh", job_name=JOB, reservation=RESERVATION, walltime=TIME)
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
    with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
        try:
            p.shell(
                "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/bin/spark-submit \
                    --master spark://"+master+":7077 \
                    --num-executors "+str(nb_worker)+" --driver-memory 12g  \
                    --executor-cores " +str(nb_core) + " --conf spark.driver.memory=18g \
                    --class org.atlanmod.MainIMDB /home/jphilippe/jars/SparkIMDB-1.0-SNAPSHOT.jar -core "+str(nb_core)+" -executor " + str(nb_worker) + " \
                    -actors "+ json_actors +" -movies "+json_movies+" -links " + txt_links + " \
                    >> " + path_log + " 2>> " + path_err
            )
            p.fetch(src= path_log, dest="~")
            p.fetch(src= path_err, dest="~")
            os.system("rm -rf /home/jphilippe/Software/spark-3.1.1-bin-hadoop2.7/work")

        except Exception as e:
            p.fetch(src= path_log, dest="~")
            p.fetch(src= path_err, dest="~")
            print("CANNOT FINISH THE COMPUTATION OF " + str(parameter))
            print(e)
            os.system("rm -rf /home/jphilippe/Software/spark-3.1.1-bin-hadoop2.7/work")

#########################################################################

try:

# start the machines

    with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
        p.shell(
            "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/start-master.sh -p 7077"
        )
        p.shell(
            "echo \" "+str(commit_number)+" \" >> " + path_log
        )

    with play_on(pattern_hosts="worker", roles=roles, run_as=username) as p:
        p.shell(
            "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/start-worker.sh spark://"+master+":7077"
        )

# Run the job

   
    for test in range(ntest):
        sweeps = sweep(parameters)
        sweeper = ParamSweeper(
            persistence_dir=str(Path("sweeps_"+JOB+"_"+str(test))), sweeps=sweeps, save_sweeps=True
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
        p.fetch(src= path_log, dest="~")
        p.fetch(src= path_err, dest="~")
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









