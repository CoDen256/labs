#!/bin/bash

ping 192.168.70.6 -c 4

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
