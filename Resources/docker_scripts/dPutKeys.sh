#!/bin/bash

set -x

# run maven tests
sudo docker run -v ~/DockerImage/keyvalue:/root/KeyValueStore/ keyvalue bash -c "cd /root/KeyValueStore/Chord;mvn install -Dmaven.test.skip=true;cd /root/KeyValueStore/ObjectStore;mvn install -Dmaven.test.skip=true;cd /root/KeyValueStore/Client;mvn clean exec:java"
