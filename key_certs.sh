#!/bin/bash


openssl genrsa -aes256 -out private/root.key.pem 4096
openssl genrsa -aes256 -out ca1/private/ca1.key.pem 4096