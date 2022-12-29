#!/bin/bash


cd /home/student/kasy22_ca

openssl genrsa -aes256 -out private/root.key.pem 4096
openssl genrsa -aes256 -out ca1/private/ca1.key.pem 4096

openssl rsa -in private/root.key.pem -pubout -out root.pub.pem
openssl rsa -in ca1/private/ca1.key.pem -pubout -out ca1/ca1.pub.pem

chmod 400 private/root.key.pem
chmod 400 ca1/private/ca1.key.pem

###
 # CN = Sydorenko Root
openssl req -config openssl.cnf -key private/root.key.pem -new -x509 -days 7300 -sha256 -extensions v3_ca -out certs/root.cert.pem
openssl x509 -noout -text -in certs/root.cert.pem

 # CN = Sydorenko CA1
openssl req -config ca1/openssl.cnf -new -sha256 -key ca1/private/ca1.key.pem -out ca1/csr/ca1.csr.pem
openssl req -text -noout -verify -in ca1/csr/ca1.csr.pem


###

openssl ca -config openssl.cnf -extensions v3_intermediate_ca -days 3650 -notext -md sha256 -in ca1/csr/ca1.csr.pem -out ca1/certs/ca1.cert.pem
openssl x509 -noout -text -in ca1/certs/ca1.cert.pem
openssl verify -CAfile certs/root.cert.pem ca1/certs/ca1.cert.pem

cat ca1/certs/ca1.cert.pem certs/root.cert.pem > ca1/certs/ca1.cert-chain.pem
chmod 444 ca1/certs/ca1.cert-chain.pem

# Server Certificate

openssl genrsa -out ca1/private/server.key.pem 2048

# CN = localhost
openssl req -config ca1/openssl.cnf -new -sha256 -key ca1/private/server.key.pem -out ca1/csr/server.csr.pem
openssl req -text -noout -verify -in ca1/csr/server.csr.pem

openssl ca -config ca1/openssl.cnf -extensions server_cert -days 365 -notext -md sha256 -in ca1/csr/server.csr.pem -out ca1/certs/server.cert.pem
openssl x509 -noout -text -in ca1/certs/server.cert.pem
openssl verify -CAfile certs/root.cert.pem -untrusted ca1/certs/ca1.cert.pem ca1/certs/server.cert.pem

