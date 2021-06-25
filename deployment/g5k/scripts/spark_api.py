from enoslib.infra.enos_g5k.g5k_api_utils import get_api_username
import logging
import os
from pathlib import Path

from enoslib import *


# Need of parameters:
# 	- path to spark directory
# 	- number of worker
# 	- site on which instantiate the spark cluster
# 	- cluster on which instantiate the spark cluster
# 	- job name


class Spark_Standalone:

	def __init__(self, path_to_spark, nworkers, site, cluster, job_name):
		self.spark = path_to_spark
		self.nworkers = nworkers
		self.network = G5kNetworkConf(id="n1", type="prod", roles=["my_network"], site=self.site)
		conf = (
			G5kConf.from_settings(job_type="allow_classic_ssh", job_name=self.job_name)
			.add_network_conf(self.network)
			.add_machine(
				roles=["master"], cluster=self.cluster, nodes=1, primary_network=network
			)
			.add_machine(
				roles=["worker"], cluster=self.cluster, nodes=self.nworkers, primary_network=network
			)
			.finalize()
		)
		self.provider = G5k(conf)
		self.roles, self.networks = self.provider.init()
		self.username = get_api_username()
		self.master = self.roles["master"][0].address


	def start(self):
		grep_master = "ps -aux | grep  spark.deploy.master.Master"
		grep_worker = "ps -aux | grep  spark.deploy.worker.Worker"

		with play_on(pattern_hosts="master", roles=self.roles, run_as=self.username) as p:
			p.shell(
				f"({grep_master}) || " + self.spark + "/sbin/start-master.sh -p 7077"
			)

		with play_on(pattern_hosts="worker", roles=self.roles, run_as=self.username) as p:
			p.shell(
				f"({grep_worker}) || " + self.spark + "/sbin/start-slave.sh " + self.master + ":7077"
			)


	def stop(self):
		with play_on(pattern_hosts="master", roles=self.roles, run_as=self.username) as p:
			p.shell(
				self.spark + "/sbin/stop-all.sh"
			)
		self.provider.destroy()


	def submit(self, job):
		# https://sparkbyexamples.com/spark/spark-submit-command/
		
		## Spark Submit Options
		# [optional] --class {CLASS} : class to run
		# [optional] --deploy-mode {MODE} : mode of deployement (e.g., cluster)
		# 					- cluster: the driver runs on one of the worker nodes
		# 					- client: the driver runs locally where you are submitting your application from
		# [optional] --supervise : mode supervise, or not (no argument)

		## Driver and Executor Resources
		### Driver
		# [optional] --driver-memory {memory}: memory for the spark driver
		# [optional] --driver-cores {cores}: CPU cores for the spark driver
		### Executor (= worker)
		# [optional] --num-executors {executors}: number of executor to use
		# [optional] --executor-memory {memory}:  memory to use for the executor
		# [optional] --executor-cores {cores}: CPU cores for the executors
		# [optional] --total-executor-cores {cores}: total CPU cores for the executors
		
		with play_on(pattern_hosts="master", roles=roles, run_as=username) as p:
			p.shell(
				self.spark + "/bin/spark-submit \
					--master spark://"+ self.master +":7077 \
					job"
			)
