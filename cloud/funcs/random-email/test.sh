#!/bin/bash

echo "Executing random function"
curl -sX POST http://gateway.openfaas:8080/function/random-fn -d "TEST" | tee data

echo "Sending email..."
curl -sX POST http://gateway.openfaas:8080/function/email -d @data