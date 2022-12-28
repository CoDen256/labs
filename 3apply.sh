#!/bin/bash


sudo cp ca1/private/server.key.pem /etc/ssl/private
sudo cp ca1/certs/server.cert.pem /etc/ssl/certs
sudo mkdir /etc/apache2/ssl.cert
sudo cp ca1/certs/ca1.cert-chain.pem /etc/apache2/ssl.crt/
