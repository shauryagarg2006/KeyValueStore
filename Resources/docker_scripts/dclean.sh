#!/bin/bash
sudo docker rm $(sudo docker ps -a -q)
sudo docker rmi keyvalue
sudo rm -rf ~/DockerImage
