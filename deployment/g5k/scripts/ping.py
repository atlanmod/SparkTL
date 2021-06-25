from enoslib.api import run, run_command, gather_facts
from enoslib.infra.enos_g5k.provider import G5k
from enoslib.infra.enos_g5k.configuration import Configuration, NetworkConfiguration

import logging


logging.basicConfig(level=logging.INFO)


SITE = "rennes"
CLUSTER = "paravance"

network = NetworkConfiguration(id="n1",
                               type="prod",
                               roles=["my_network"],
                               site=SITE)

conf = Configuration.from_settings(job_name="enoslib_tutorial",
                                   job_type="allow_classic_ssh")\
                    .add_network_conf(network)\
                    .add_machine(roles=["server"],
                                 cluster=CLUSTER,
                                 nodes=1,
                                 primary_network=network)\
                    .add_machine(roles=["client"],
                                 cluster=CLUSTER,
                                 nodes=1,
                                 primary_network=network)\
                    .finalize()

provider = G5k(conf)
roles, networks =  provider.init()

result = run_command(f"ping -c 5 {server.address}",
                     pattern_hosts="client",
                     roles=roles)
pprint(result)

# result.get('results')[1].payload.get('stdout_lines')
