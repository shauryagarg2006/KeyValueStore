#!/bin/bash

# generate a list of IP addresses of all containers
sudo python ~/DockerImage/keyvalue/Resources/docker_scripts/FetchIPAddress.py

# run maven tests
sudo docker run -v ~/DockerImage/keyvalue:/root/KeyValueStore/ keyvalue bash -c "cd /root/KeyValueStore/Chord;mvn test"
