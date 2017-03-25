#!/bin/bash
sudo docker kill $(sudo docker ps -a -q)