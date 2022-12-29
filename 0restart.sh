#!/bin/bash

cd /home/student/
rm -rf kasy22_ca/
sudo rm /etc/ssl/private/server.key.pem
sudo rm /etc/ssl/certs/server.cert.pem
sudo rm -r /etc/apache2/ssl.crt/

sudo cp /home/student/kvpn/orig/default-ssl.conf /etc/apache2/sites-enabled/default-ssl.conf