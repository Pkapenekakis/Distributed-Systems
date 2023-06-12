#!/bin/bash

numOfWorkers=$1
mapperOrReducer=$2

if [ "mapperOrReducer" == "reducer" ]; then
  name="reducer"
elif [ "mapperOrReducer" == "mapper" ]; then
  name="mapper"
else
  name="shuffler"
fi

#The script iterates over a range of worker nodes, joining them to an existing swarm cluster adding
# a label with its name on each
for node in $(seq 1 numOfWorkers); do
  echo "======> joining $name to the swarm ..."
  #The worker node gets connected to the swarm.
  # --token (expr) This option retrieves the join token for workers from the Swarm manager machine.
  #                 The join token is used to authenticate and authorise the node to join the swarm
  # --listen-addr specifies the address the node listens for connections
  # --advertise-addr specifies the address the node should advertise to other nodes in the cluster

  #--token $(docker-machine ssh manager "docker swarm join-token $name -q") \   TODO: CHECK IF THIS IS CORRECT
  docker-machine ssh $name$node \
    "docker swarm join \
        --token $(docker-machine ssh manager "docker swarm join-token worker -q") \
        --listen-addr $(docker-machine ip worker$node) \
        --advertise-addr $(docker-machine ip worker$node) \
        $(docker-machine ip manager)" #the manager on which the node joins

  echo "=====> adding label to $name$node"
  #This command connects to the Swarm manager machine and adds a label to the "worker" node.
  #docker-machine ssh manager "docker node update --label-add name=worker worker$node"
done
