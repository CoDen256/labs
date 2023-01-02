#!/bin/bash

sudo cp /home/student/kvpn/orig/ipsec.conf.a /etc/ipsec.conf
sudo cp /home/student/kvpn/orig/ipsec.secrets.a /etc/ipsec.secrets

sudo ipsec restart

cd /home/student/kasy22_ca
openssl ca -config ca1/openssl.cnf -revoke ca1/certs/192.168.70.5.cert.pem
openssl ca -config ca1/openssl.cnf -revoke ca1/certs/192.168.70.6.cert.pem