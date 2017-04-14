#!/bin/bash
sudo killall java
sudo rm -rf ~/log_server
sudo docker kill $(sudo docker ps -q)
sudo docker rm $(sudo docker ps -a -q)
sudo rm -rf ~/DockerImage
