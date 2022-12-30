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
# Copy CA and reread Server A and Server B

# Server A
sudo cp ca1/certs/ca1.cert.pem /etc/ipsec.d/cacerts/
sudo cp certs/root.cert.pem /etc/ipsec.d/cacerts/
sudo ipsec rereadcacerts
sudo ipsec listcacerts


# Server B
scp ca1/certs/ca1.cert.pem student@192.168.70.6:/home/student/ca1.cert.pem 
scp certs/root.cert.pem student@192.168.70.6:/home/student/root.cert.pem

scp ca1/private/192.168.70.6.key.pem student@192.168.70.6:/home/student/192.168.70.6.key.pem
scp ca1/certs/192.168.70.6.cert.pem student@192.168.70.6:/home/student/192.168.70.6.cert.pem

ssh student@192.168.70.6
mkdir private 
mv *.pem private
mv ipsec.* private
cd private
ls private

sudo cp ca1.cert.pem /etc/ipsec.d/cacerts/
sudo cp root.cert.pem /etc/ipsec.d/cacerts/
sudo ipsec rereadcacerts
sudo ipsec listcacerts

exit
# where to put certs ?


# nano ipsec conf and secrets

sudo ipsec statusall
sudo ip xfrm state
sudo ip xfrm policy

ping -c 7 192.168.70.6