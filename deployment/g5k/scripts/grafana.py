from enoslib import *
from enoslib.infra.enos_g5k.g5k_api_utils import get_api_username


import logging

logging.basicConfig(level=logging.INFO)

network = G5kNetworkConf(id="n1", type="prod", roles=["my_network"], site="rennes")
username = get_api_username()

# Cluster reservation

conf = (
    G5kConf.from_settings(job_type="allow_classic_ssh", job_name="Spark_grafana", walltime="02:00:00")
    .add_network_conf(network)
    .add_machine(
        roles=["control"], cluster="paravance", nodes=1, primary_network=network
    )
    .add_machine(
        roles=["master", "controlable"], cluster="paravance", nodes=1, primary_network=network
    )
    .add_machine(
        roles=["worker", "controlable"], cluster="paravance", nodes=4, primary_network=network
    )
    .finalize()
)
provider = G5k(conf)
roles, networks = provider.init()

master = roles["master"][0].address
ui_address = roles["control"][0].address

# Monitoring deployment

print("The UI will be available at http://%s:3000" % ui_address)

monitoring = TIGMonitoring(collector=roles["control"][0], agent=roles["controlable"], ui=roles["control"][0])
monitoring.deploy()

print("The UI is available at http://%s:3000" % ui_address)
print("user=admin, password=admin")

path="/home/jphilippe/grafana_results"

# Start the Spark cluster

with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
    p.shell(
        "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/start-master.sh -p 7077"
    )

with play_on(pattern_hosts="worker", roles=roles, run_as=username) as p:
    p.shell(
        "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/start-worker.sh spark://"+master+":7077"
    )

# Submit the job

with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
    p.shell(
            "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/bin/spark-submit \
                --master spark://"+master+":7077 \
                --executor-memory 120g \
                --class org.atlanmod.transformation.Main_Class2Relational \
                /home/jphilippe/jars/SparkTE-1.0-SNAPSHOT.jar --nexecutor 4 --size 100000 --ntests 3 -csv -print --path " + path + " \
                >> /tmp/sparkte_c2r.log 2>> /tmp/sparkte_c2r.err"
        )
    p.fetch(src="/tmp/sparkte_c2r.log", dest="~")
    p.fetch(src="/tmp/sparkte_c2r.err", dest="~")


# Stop all

with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
    p.shell(
        "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/stop-all.sh"
    )
with play_on(pattern_hosts="worker", roles=roles, run_as=username) as p:
    p.shell(
        "/home/"+username+"/Software/spark-3.1.1-bin-hadoop2.7/sbin/stop-all.sh"
    )
monitoring.destroy()
provider.destroy()
