#!/bin/bash


sudo ipsec statusall
sudo ip xfrm state
sudo ip xfrm policy

# Server A -> Server B
ping -c 4 192.168.80.1 00
 
# Server B -> Server A
ping -c 4 192.168.60.100