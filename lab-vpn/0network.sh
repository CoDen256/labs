#!/bin/bash

echo "Machine Name (a/b): "

read T # a,b

sudo cp /home/student/kvpn/orig/installer-config.$T.yaml /etc/netplan/00-installer-config.yaml
sudo cp /home/student/kvpn/orig/interfaces.$T /etc/network/interfaces
