#!/bin/bash

worker=$1

# -d flag means the driver to use for creating the docker machine is oracleVM

echo "======> Creating manager machine ..."
#Create a manager machine
docker-machine create -d virtualbox manager

echo "======> Creating $worker worker machines ..."
for node in $(seq 1 $worker); do
  echo "======> Creating worker$node machine ..."
  #Create a worker machine
  docker-machine create -d virtualbox worker$node
done

#list all the machines
docker-machine ls