import sys
import json
import docker

verified_nodes = []
node_list = []
begin_time = ""
end_time = ""
total_num_of_nodes = 0


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
                if chord_id not in node_list:
                    node_list.append(chord_id)
                read_json = False
    node_list.sort()
    print "Nodes : " + str(node_list)


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
def log_analysis(log_file_path, number_of_joins):
    number = 0  # Count of Join statements encountered
    read_json = False
    with open(log_file_path) as f:
        for line in f:
            if number == int(number_of_joins):
                if "JSON-PAYLOAD" in line:
                    read_json = True
                    end_time = line[:8]
                elif read_json:
                    process_json(line)
                    read_json = False
                    if len(verified_nodes) == total_num_of_nodes:
                        print begin_time
                        print  end_time
                        break

            else:
                if "%JOIN%" in line:
                    if number == 0:
                        begin_time = line[:8]  # Begin time for calcualtion
                    number += 1


if __name__ == "__main__":
    if len(sys.argv) < 3:
        print 'Invalid Number Of Arguments'
        print 'Usage: python logAnalyzer.py <path_to_analysis_log> <number_of_new_nodes> [total_num_nodes]'
        sys.exit()
    if len(sys.argv) == 3:
        initialize_total_number_of_nodes()
    else:
        print "Total Number Of running Nodes : " + sys.argv[3]
        total_num_of_nodes = int(sys.argv[3])
    initialize_node_list(sys.argv[1])
    log_analysis(sys.argv[1], sys.argv[2])
