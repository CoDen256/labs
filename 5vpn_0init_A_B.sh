#!/bin/bash

# server A -> adapter 1 : .60 (#2) + adapter 2 : .70 (#3)
# server B -> adapter 1 : .80 (#4) + adapter 2 : .70 (#3)

# server A: .60.100  + .70.5
# client A: .60.111  

# server B: .80.100  + .70.6
# client B: .80.111
echo "Server Name (a/b): "
read T # a or b


sudo apt-get update
sudo apt-get install -y strongswan

sudo cp /home/student/kvpn/vpn_psk/ipsec.conf.$T /etc/ipsec.conf
sudo cp /home/student/kvpn/vpn_psk/ipsec.secrets.$T /etc/ipsec.secrets

sudo ipsec restart

sudo ipsec statusall

echo $T | bash -x /home/student/kvpn/0network.sh