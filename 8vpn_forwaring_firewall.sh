#!/bin/bash

# Server A
sudo cp /home/student/kvpn/vpn_forward_drop/sysctl.conf /etc/sysctl.conf

# Client A
scp /home/student/kvpn/vpn_forward_drop/interfaces.ca student@192.168.60.111:/tmp/interfaces
ssh -t student@192.168.60.111 "sudo mv /tmp/interfaces /etc/network/interfaces"


# Server B
scp /home/student/kvpn/vpn_forward_drop/sysctl.conf student@192.168.70.6:/tmp/sysctl.conf
ssh -t student@192.168.70.6 "sudo mv /tmp/sysctl.conf /etc/sysctl.conf"

# Client B

# scp /home/student/kvpn/vpn_forward_drop/interfaces.cb student@192.168.80.111:/tmp/interfaces
# ssh -t student@192.168.80.111 "sudo mv /tmp/interfaces /etc/network/interfaces"

scp /home/student/kvpn/vpn_forward_drop/interfaces.cb student@192.168.70.6:/tmp/interfaces
ssh -t student@192.168.70.6 "scp /tmp/interfaces student@192.168.80.111:/tmp/interfaces"
ssh -t student@192.168.70.6 "ssh -t student@192.168.80.111 \"sudo mv /tmp/interfaces /etc/network/interfaces\""


echo "Run on Client A: ping 192.168.80.111 -c 4 && traceroute 192.168.80.111"
echo "Run on Client B: ping 192.168.60.111 -c 4 && traceroute 192.168.60.111"
read T # a or b


# Client A
# ping 192.168.80.111 -c 4
# traceroute 192.168.80.111

# Server A + B
sudo ipsec statusall
sudo ip xfrm state
sudo ip xfrm policy


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