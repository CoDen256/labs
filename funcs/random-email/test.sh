#!/bin/bash
echo "Randomly generated..."
echo $RANDOM | tee data
echo "Executing random function with randomnly generated parameter..."
curl -sX POST http://gateway.openfaas:8080/function/random-fn -d @data | tee data
echo "Sending email..."
curl -sX POST http://gateway.openfaas:8080/function/email -d @data