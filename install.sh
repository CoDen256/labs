#!/bin/sh
curl -sL https://cli.openfaas.com | sh
echo $1 | ./faas-cli login --username admin --password-stdin --gateway=$2
