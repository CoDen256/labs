#!/bin/bash

sudo cd /home/student/kassy22_ca
sudo cp ca1/private/server.key.pem /etc/ssl/private
sudo cp ca1/certs/server.cert.pem /etc/ssl/certs
sudo mkdir /etc/apache2/ssl.crt
sudo cp ca1/certs/ca1.cert-chain.pem /etc/apache2/ssl.crt/

sudo cp /home/student/kvpn/default-ssl.conf /etc/apache2/sites-enabled/default-ssl.conf
#nano /etc/apache2/sites-enabled/default-ssl.conf

sudo service apache2 restart