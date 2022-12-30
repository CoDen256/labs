#!/bin/bash

read T # a,b, ca or cb

sudo cp /home/student/kvpn/orig/installer-config.$T.yaml /etc/netplan/00-installer-config.yaml