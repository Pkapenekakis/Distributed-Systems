#!/bin/bash

#Connects the manager container via ssh
#initialise a swarm cluster
# $(docker-machine ip manager) is a subcommand that retrieves the manager's ip
# --listen-addr specifies the address the swarm manager listens for connections
# --advertise-addr specifies the address the swarm manager should advertise to other nodes in the cluster
#
docker-machine ssh manager \
  "docker swarm init \
        --listen-addr $(docker-machine ip manager) \
        --advertise-addr $(docker-machine ip manager)"


