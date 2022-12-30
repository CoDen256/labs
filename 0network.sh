#!/bin/bash

echo "Machine Name (a/b/ca/cb): "

read T # a,b, ca or cb

sudo cp /home/student/kvpn/orig/installer-config.$T.yaml /etc/netplan/00-installer-config.yaml
sudo cp /home/student/kvpn/orig/interfaces.$T /etc/network/interfaces
