# KeyValueStore
This is a simple Distributed Key-Value stored based on 'Chord' Protocol. 
It is a completely distributed peer-to-peer system (No central server).
It provides a simple 'get' & 'put' API to store or retrieve the objects. System can easily accomodate new nodes and can efficiently handle existing node failure. All data is replicated to prevent data
loss.

Project contains below important directories and files.

├── Chord
├── Client
├── Documents
│   └── Project Proposal.pdf
├── ObjectStore
├── README.md
└── Resources
    ├── Ansible_Scripts
    └── docker_scripts


Chord - The core chord module which implements chord protcol & exposes API for ObjectStore.
Client - The client modules which contains client library code for get/put API.
ObjectStore - The ObjectStore module which implements key-value store on top of chord. and exposes get/put API for client.
Resources - A set of ansible/docker/shell scripts used mainly for deploying multiple chord nodes.
