#!/bin/bash
sudo docker kill $(sudo docker ps -a -q)
sudo docker rm $(sudo docker ps -a -q)
sudo docker rmi keyvalue
sudo rm -rf ~/DockerImage
