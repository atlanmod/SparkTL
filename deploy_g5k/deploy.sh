#!/bin/bash

NODES=$1
echo "Reserving $NODES nodes..."
ssh rennes.g5k "echo $(oarsub \"sleep 10d\" -l nodes=%NODES% -t allow_classic_ssh | sed -n 's/OAR_JOB_ID=\(.*\)/\1/p')" > jobid
JOBID=<jobid

echo "Job ID is: $JOBID"