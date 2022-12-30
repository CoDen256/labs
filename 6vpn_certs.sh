#!/bin/bash


cd /home/student/kasy22_ca


# Server A Cert
openssl genrsa -out ca1/private/192.168.70.5.key.pem 2048
# 192.168.70.5
openssl req -config ca1/openssl.cnf -key ca1/private/192.168.70.5.key.pem -new -sha256 -out ca1/csr/192.168.70.5.csr.pem
openssl req -text -noout -verify -in ca1/csr/192.168.70.5.csr.pem 
openssl ca -config ca1/openssl.cnf -extensions server_cert -days 365 -notext -md sha256 -in ca1/csr/192.168.70.5.csr.pem -out ca1/certs/192.168.70.5.cert.pem
openssl verify -CAfile certs/root.cert.pem -untrusted ca1/certs/ca1.cert.pem ca1/certs/192.168.70.5.cert.pem


# Server B Cert
openssl genrsa -out ca1/private/192.168.70.6.key.pem 2048
# 192.168.70.6
openssl req -config ca1/openssl.cnf -key ca1/private/192.168.70.6.key.pem -new -sha256 -out ca1/csr/192.168.70.6.csr.pem
openssl req -text -noout -verify -in ca1/csr/192.168.70.6.csr.pem 
openssl ca -config ca1/openssl.cnf -extensions server_cert -days 365 -notext -md sha256 -in ca1/csr/192.168.70.6.csr.pem -out ca1/certs/192.168.70.6.cert.pem
openssl verify -CAfile certs/root.cert.pem -untrusted ca1/certs/ca1.cert.pem ca1/certs/192.168.70.6.cert.pem


### Copy to strongswan
# Copy CA and reread Server B and Server A


## Server B CAs, Cert, Private Key, ipsec.conf + ipsec.secrets
# SCP CAs + move to cacerts
scp ca1/certs/ca1.cert.pem student@192.168.70.6:/tmp
scp certs/root.cert.pem student@192.168.70.6:/tmp
ssh -t student@192.168.70.6 "sudo mv /tmp/*.pem /etc/ipsec.d/cacerts/"

# SCP key + move to private
scp ca1/private/192.168.70.6.key.pem student@192.168.70.6:/tmp
ssh -t student@192.168.70.6 "sudo mv /tmp/*.pem /etc/ipsec.d/private/"

# SCP key + move to private
scp ca1/certs/192.168.70.6.cert.pem student@192.168.70.6:/tmp
ssh -t student@192.168.70.6 "sudo mv /tmp/*.pem /etc/ipsec.d/certs/"

ssh -t student@192.168.70.6 "sudo ipsec rereadcacerts; sudo ipsec listcacerts"

# SCP conf + move to /etc/
scp /home/student/kvpn/vpn_cert/ipsec.conf.b student@192.168.70.6:/tmp/ipsec.conf
ssh -t student@192.168.70.6 "sudo mv /tmp/ipsec.conf /etc/ipsec.conf"

# SCP secrets + move to /etc/
scp /home/student/kvpn/vpn_cert/ipsec.secrets.b student@192.168.70.6:/tmp/ipsec.secrets
ssh -t student@192.168.70.6 "sudo mv /tmp/ipsec.secrets /etc/ipsec.secrets"

# Server B: sudo ipsec restart && sudo ipsec statusall
#ssh -t student@192.168.70.6 "sudo ipsec restart" &
echo "Run sudo ipsec restart on server B"
read T

## Server A CAs, Cert, Private Key
sudo cp ca1/certs/ca1.cert.pem /etc/ipsec.d/cacerts/
sudo cp certs/root.cert.pem /etc/ipsec.d/cacerts/

sudo cp ca1/private/192.168.70.5.key.pem /etc/ipsec.d/private/
sudo cp ca1/certs/192.168.70.5.cert.pem /etc/ipsec.d/certs/

sudo ipsec rereadcacerts
sudo ipsec listcacerts

sudo cp /home/student/kvpn/vpn_cert/ipsec.conf.a /etc/ipsec.conf
sudo cp /home/student/kvpn/vpn_cert/ipsec.secrets.a /etc/ipsec.secrets


sudo ipsec restart
sudo ipsec statusall

### RUN ###

ping -c 4 192.168.70.6

sudo ipsec statusall
sudo ip xfrm state
sudo ip xfrm policy
