#!/bin/bash


cd /home/student/kasy22_ca

read T

# Server A
openssl genrsa -out ca1/private/192.168.70.5.key.pem 2048
openssl req -config ca1/openssl.cnf -key ca1/private/192.168.70.5.key.pem -new -sha256 -out ca1/csr/192.168.70.5.csr.pem
openssl req -text -noout -verify -in ca1/csr/192.168.70.5.csr.pem 
openssl ca -config ca1/openssl.cnf -extensions server_cert -days 365 -notext -md sha256 -in ca1/csr/192.168.70.5.csr.pem -out ca1/certs/192.168.70.5.cert.pem
openssl verify -CAfile certs/root.cert.pem -untrusted ca1/certs/ca1.cert.pem ca1/certs/192.168.70.5.cert.pem

sudo ipsec listcacerts

# Server B
openssl genrsa -out ca1/private/192.168.70.6.key.pem 2048
openssl req -config ca1/openssl.cnf -key ca1/private/192.168.70.6.key.pem -new -sha256 -out ca1/csr/192.168.70.6.csr.pem
openssl req -text -noout -verify -in ca1/csr/192.168.70.6.csr.pem 
openssl ca -config ca1/openssl.cnf -extensions server_cert -days 365 -notext -md sha256 -in ca1/csr/192.168.70.6.csr.pem -out ca1/certs/192.168.70.6.cert.pem
openssl verify -CAfile certs/root.cert.pem -untrusted ca1/certs/ca1.cert.pem ca1/certs/192.168.70.6.cert.pem

# Copy CA and reread Server B and Server A
sudo cp ca1.cert.pem /etc/ipsec.d/cacerts/
sudo cp root.cert.pem /etc/ipsec.d/cacerts/

sudo ipsec rereadcacerts

sudo ipsec listcacerts

# where to put certs ?


# nano ipsec conf and secrets

sudo ipsec statusall
sudo ip xfrm state
sudo ip xfrm policy

ping -c 7 192.168.70.6