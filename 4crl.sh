#!/bin/bash

# CRL

cd /home/student/kasy22_ca
cp /home/student/kvpn/openssl.cnf.ca1.crl ca1/openssl.cnf

openssl ca -config ca1/openssl.cnf -gencrl -out ca1/crl/ca1.crl.pem
openssl crl -in ca1/crl/ca1.crl.pem -noout -text


# REVOKE


openssl genrsa -out ca1/private/dragos.ilie@bth.se.key.pem 2048
# CN = dragos.ilie@bth.se
openssl req -config ca1/openssl.cnf -new -sha256 -key ca1/private/dragos.ilie@bth.se.key.pem -out ca1/csr/dragos.ilie@bth.se.scr.pem
openssl req -text -noout -verify -in ca1/csr/dragos.ilie@bth.se.scr.pem
openssl ca -config ca1/openssl.cnf -extensions usr_cert -notext -md sha256 -in ca1/csr/dragos.ilie@bth.se.scr.pem -out ca1/certs/dragos.ilie@bth.se.cert.pem
openssl x509 -noout -text -in ca1/certs/dragos.ilie@bth.se.cert.pem
openssl verify -CAfile certs/root.cert.pem -untrusted ca1/certs/ca1.cert.pem ca1/certs/dragos.ilie@bth.se.cert.pem

openssl ca -config ca1/openssl.cnf -revoke ca1/certs/dragos.ilie@bth.se.cert.pem
cat ca1/index.txt
openssl crl -in ca1/crl/ca1.crl.pem -noout -text 
