#!/bin/bash


echo "Server Name (a/b): "
read T # a or b


sudo cp /home/student/kvpn/vpn_tunnel/ipsec.conf.$T /etc/ipsec.conf
sudo cp /home/student/kvpn/vpn_tunnel/ipsec.secrets.$T /etc/ipsec.secrets

sudo ipsec restart

echo "Run the same script on the second Server"
read A

if [ "$T" = "a" ] 
then
    # Server A -> Server B
    ping -c 4 192.168.80.100
else 
    # Server B -> Server A
    ping -c 4 192.168.60.100
fi

sudo ipsec statusall
sudo ip xfrm state
sudo ip xfrm policy


 
