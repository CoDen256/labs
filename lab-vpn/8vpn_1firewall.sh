#!/bin/bash

# $IPT -t nat -A POSTROUTING -j SNAT -o $NIF --to $NIP #SNAT

# FIREWALL
# Server A
sudo cp /home/student/kvpn/vpn_forward_drop/firewall.sh.A /home/student/firewall.sh
sudo chmod +x ./firewall.sh
sudo ./firewall.sh

# Server B
# sudo cp /home/student/kvpn/vpn_forward_drop/firewall.sh.B /home/student/firewall.sh
# sudo chmod +x ./firewall.sh
# sudo ./firewall.sh

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