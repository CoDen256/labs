#!/bin/bash

# Server A + B
#sudo ipsec statusall
sudo ip xfrm state
#sudo ip xfrm policy

# Client A
ping 192.168.80.111 -c 4
traceroute 192.168.80.111


# FIREWALL

# nano firewall.sh.template

# Client A
ping 192.168.80.111 -c 4

# Client B
ping 192.168.80.111 -c 4


# Client B -> Client A
ssh clientA


# Client B -> Server A
ssh serverA


# Server A
ipsec statusall
sudo ip xfrm state


# Client A + Client B
ping 8.8.8.8