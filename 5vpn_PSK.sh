#!/bin/bash

# server A -> adapter 1 : .60 (#2) + adapter 2 : .70 (#3)
# server B -> adapter 1 : .80 (#4) + adapter 2 : .70 (#3)

# server A: .60.100  + .70.5
# client A: .60.111  

# server B: .80.100  + .70.6
# client B: .80.111
read T

sudo apt-get update
sudo apt-get install -y strongswan
sudo apt-get install -y charon-systemd

sudo cp /home/student/kvpn/vpn_psk/ipsec.conf.$T /etc/ipsec.conf
sudo cp /home/student/kvpn/vpn_psk/ipsec.secrets.$T /etc/ipsec.secrets

#sudo nano /etc/ipsec.conf
#sudo nano /etc/ipsec.secrets

#sudo systemctl enable strongswan-starter
#sudo systemctl start strongswan-starter
sudo systemctl enable strongswan.service
sudo systemctl enable strongswan-swanctl
sudo systemctl start strongswan.service
sudo systemctl start strongswan-swanctl
sudo ipsec start
sudo ipsec restart

sudo ipsec statusall

## DECRYPT with wrieshark
sudo ipsec statusall


# 192.168.70.5...192.168.70.6
# AES_CBC_128/HMAC_SHA2_256_128
# 192.168.70.5/32 === 192.168.70.6/32

sudo ip xfrm state
# 5->6
# spi 0xc0598138
# auth 0x333df0e4656d39213c0c17ec69226ce49dfd0c3f3092e8799460a7539c0e4ed8
# enc 0x817525c7650897b8dfb58b595ae4b073

# 6->5
# spi 0xcc6fa0fc
# auth 0x459c9b6c93671c6e20a53ff26c33fa51f8d401a249bfa5deb51c7ab527c8a256
# enc 0x4023e4b2b541915c3d55b64ad5e8599c


# Wireshark -> select ESP packet -> Protocol Preferences -> ESP SAs
# check Attempt to detec/decode encrypted ESP payloads
# check attempt to check ESP Auth


### List the entries in the SPD
sudo ip xfrm policy
# out tmpl esp reqid 1 transport
# in tmpl 
