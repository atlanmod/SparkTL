#!/bin/bash

. g5k.config

export FILE="../../experiments/build/libs/SparkTE-1.0-SNAPSHOT.jar"
scp $FILE $USER@access.grid5000.fr:rennes/jars/SparkTE-1.0-SNAPSHOT.jar