#!/bin/bash

echo "======> sending map_reduce Image to Manager"
#Saves the map_reduce image after it is zipped by bzip2 to the manager machine
#After that the manager machine unzips the map_reduce image and loads it
docker save map_reduce | bzip2 | docker-machine ssh manager 'bunzip2 | docker load'

#Counts all the machines in the swarm that are mappers
nodes=$(docker-machine ssh manager "docker node ls | grep worker* | wc -l")

for node in $(seq 1 $nodes); do
#Save the map_reduce image, zip it to the worker node
#The worker node unzips the image and loads it
  echo "======> sending map_reduce Image to worker$node"
  docker save map_reduce | bzip2 | docker-machine ssh worker$node 'bunzip2 | docker load'

done
