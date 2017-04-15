#!/bin/bash
set -x
sudo docker run -v ~/DockerImage/keyvalue:/root/KeyValueStore/ keyvalue &
sleep 10
for i in `seq 2 $1`
do
    echo "$i"
    sudo docker run -v ~/DockerImage/keyvalue:/root/KeyValueStore/ keyvalue &
done
