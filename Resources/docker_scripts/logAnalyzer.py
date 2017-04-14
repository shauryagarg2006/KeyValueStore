import sys
import json
import docker
import datetime
import time
import subprocess
from os.path import expanduser


home = expanduser("~")
script_start_time = ""
verified_nodes = []
node_list = []
begin_time = ""
end_time = ""
total_num_of_nodes = 0
log_file_path = home + "/log_server/logs/analysis.log"
current_ip_list = []


def get_correct_chord_id(chord_id):
    max = node_list[len(node_list) - 1]
    if chord_id > max:
        return node_list[0]
    for id in node_list:
        if id >= chord_id:
            return id


# Function to get the total number of active nodes running in the machine
def initialize_total_number_of_nodes():
    global total_num_of_nodes
    client = docker.APIClient(base_url='unix://var/run/docker.sock')
    container_list = client.containers()
    for container_id in container_list:
        temp_ip = container_id['NetworkSettings']['Networks']['bridge']['IPAddress']
        current_ip_list.append(temp_ip)
    total_num_of_nodes = len(container_list)
    print "Total Number Of running Nodes : " + str(total_num_of_nodes)


# Function to fetch all the different ip address from logs and their chord id
def initialize_node_list(log_file_path):
    read_json = False
    with open(log_file_path) as f:
        for line in f:
            if len(node_list) == total_num_of_nodes:
                break
            if "JSON-PAYLOAD" in line:
                read_json = True
            elif read_json:
                js = json.loads(line)
                chord_id = int(json.dumps(js["selfChordID"]["hashValue"]))
                ip_addr = json.dumps(js["selfChordID"]["key"])
                ip_addr = ip_addr[1:-1]  # Stripping Of " quotes
                if ip_addr in current_ip_list:
                    node_list.append(chord_id)
                    print "Node : IP: " + ip_addr + " Chord Id: " + str(chord_id)
                    current_ip_list.remove(ip_addr)
                read_json = False
    node_list.sort()


def verify_finger_entry(js):
    finger_table = js["fingerTable"]["table"]
    chord_id = int(json.dumps(js["selfChordID"]["hashValue"]))
    for entry in finger_table:
        entry_id = int(entry["responsibleNodeID"]["hashValue"])
        valid_id = get_correct_chord_id(int(entry["hashRangeStart"]["hashValue"]))
        if entry_id != valid_id:
            return
    verified_nodes.append(chord_id)


def process_json(json_string):
    # print json_string
    js = json.loads(json_string)
    chord_id = int(json.dumps(js["selfChordID"]["hashValue"]))
    if chord_id not in verified_nodes:
        verify_finger_entry(js)


# This function will do the log analysis and calculate the stabilization time
def log_analysis(log_file_path, type_check, number_check):
    global begin_time
    timing = []
    number = 0  # Count of Join statements encountered
    read_json = False
    start_analysis = False
    FMT = '%H:%M:%S'
    is_stable = False

    with open(log_file_path) as f:
        for line in f:
            if "selfChordID" in line:
                continue
            temp_time = line[:8]
            time_diff = datetime.datetime.strptime(script_start_time, FMT) - datetime.datetime.strptime(temp_time, FMT)
            # print time_diff
            if time_diff.days < 0:
                begin_time = timing[-number_check]
                break
            if "JOIN" == type_check:
                if "%JOIN%" in line:
                    timing.append(line[:8])  # Begin time for calcualtion
            elif "FAIL" == type_check:
                if "closing" in line:
                    timing.append(line[:8])
    if begin_time == "":
        print "UNSTABLE"
        return
    with open(log_file_path) as f:
        for line in f:
            if start_analysis:
                if "JSON-PAYLOAD" in line:
                    read_json = True
                    end_time = line[:8]
                elif read_json:
                    process_json(line)
                    read_json = False
                    if len(verified_nodes) == total_num_of_nodes:
                        print "Start Time: " + begin_time
                        print "End Time: " + end_time
                        time_diff = datetime.datetime.strptime(end_time, FMT) - datetime.datetime.strptime(begin_time,
                                                                                                           FMT)
                        print "Seconds: " + str(time_diff.total_seconds())
                        is_stable = True
                        break
            else:
                if "selfChordID" in line:
                    continue
                temp_time = line[:8]
                time_diff = datetime.datetime.strptime(begin_time, FMT) - datetime.datetime.strptime(temp_time,
                                                                                                     FMT)
                # print time_diff.minute
                if time_diff.total_seconds() == 0.0:
                    start_analysis = True
    if not is_stable:
        print "UNSTABLE"


if __name__ == "__main__":
    if len(sys.argv) < 3:
        print 'Invalid Number Of Arguments'
        print 'Usage: python logAnalyzer.py <JOIN/FAIL> [Number]'
        sys.exit()
    script_start_time = datetime.datetime.now().time().strftime("%H:%M:%S")
    time.sleep(2)
    initialize_total_number_of_nodes()
    initialize_node_list(log_file_path)
    log_analysis(log_file_path, sys.argv[1], int(sys.argv[2]))
