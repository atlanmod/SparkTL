from enoslib.infra.enos_g5k.g5k_api_utils import get_api_username
import logging
from pathlib import Path

from enoslib import *

logging.basicConfig(level=logging.DEBUG)

SITE = "rennes"
CLUSTER = "paravance"
JOB_NAME = "fetching"

# Create a network
network = G5kNetworkConf(id="n1", type="prod", roles=["my_network"], site=SITE)

# Ask for the resources
conf = (
    G5kConf.from_settings(job_type="allow_classic_ssh", job_name=JOB_NAME)
    .add_network_conf(network)
    .add_machine(
        roles=["master"], 
        cluster=CLUSTER, 
        nodes=1, 
        primary_network=network
    )
    .finalize()
)

username = get_api_username()

provider = G5k(conf)
roles, networks = provider.init()

# Create a file, and fetch it on the master
with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
    p.shell(f"echo \"Hello world\" > /tmp/hello.txt")
    p.fetch(src="/tmp/hello.txt", dest="~")
    # p.fetch(src="/tmp/hello.txt", dest="~")


provider.destroy()