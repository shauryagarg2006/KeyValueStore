import subprocess
import docker
import sys
from os.path import expanduser

client = docker.APIClient(base_url='unix://var/run/docker.sock')
container_list = client.containers()
home = expanduser("~")
target = open(home + "/DockerImage/keyalue/Resources/tests/node_ip_list", 'w')
for container_id in container_list:
        target.write(container_id['NetworkSettings']['Networks']['bridge']['IPAddress'])
        target.write("\n")
target.close()