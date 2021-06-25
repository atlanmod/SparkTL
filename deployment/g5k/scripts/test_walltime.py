from enoslib.infra.enos_g5k.g5k_api_utils import get_api_username
from pathlib import Path
from enoslib import *
from operator import itemgetter
from execo_engine import ParamSweeper, sweep
from datetime import datetime

import logging
import os
import time
import traceback

logging.basicConfig(level=logging.DEBUG)

##########################################################################

# claim the resources

network = G5kNetworkConf(id="n1", type="prod", roles=["my_network"], site="rennes")
conf = (
    G5kConf.from_settings(job_type="allow_classic_ssh", job_name="test_walltime", reservation="2021-03-17 09:45:00", walltime="00:10:00")
    .add_network_conf(network)
    .add_machine(
        roles=["master"], cluster="paravance", nodes=1, primary_network=network
    )
    .finalize()
)
provider = G5k(conf)
roles, networks = provider.init()

username = get_api_username()

master = roles["master"][0].address

##########################################################################

try:
    while(True):
        now = datetime.now()
        current_time = now.strftime("%H:%M:%S")
        with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
            p.shell(
                "echo \"" + str(current_time) + "\" >> /tmp/time.txt"
            )
            print(current_time)
            p.fetch(src="/tmp/time.txt", dest="~")
        time.sleep(10)
finally:
    provider.destroy()

