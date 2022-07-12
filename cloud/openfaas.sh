#!/bin/bash      
set -e
echo "------------START--------------"
sudo apt install -y neovim ca-certificates curl gnupg lsb-release
echo "-------------K3S-------------"
curl -sfL https://get.k3s.io | sh -
echo "-------------OPENFAAS CLI + ARKADE-------------"
curl -sL https://cli.openfaas.com | sudo sh
curl -SLsf https://get.arkade.dev/ | sudo sh
echo "-------------INSTALL OPENFAAS-------------"
set +e
arkade install openfaas
set -e
sudo chmod 744 /etc/rancher/k3s/k3s.yaml
mkdir ~/.kube
kubectl config view --raw > ~/.kube/config
arkade install openfaas
echo "-------------DEPLOYING-------------"
sudo kubectl rollout status -n openfaas deploy/gateway
sudo kubectl port-forward -n openfaas svc/gateway --address 0.0.0.0 8080:8080 &
sudo kubectl port-forward -n openfaas svc/prometheus --address 0.0.0.0 9090:9090 & 
sudo kubectl get pod -o wide --all-namespaces
sudo kubectl get pod -o wide -n openfaas
sudo kubectl get svc -o wide gateway-external -n openfaas
echo "-------------DEPLOYMENT DONE-------------"
echo "-------------SECONDARY INSTALL-------------"
arkade install cron-connector
sudo apt-get remove docker docker.io containerd runc
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
sudo apt-get -y install docker-ce docker-ce-cli containerd.io docker-compose-plugin
sudo docker --version
echo "---------------SETUP AND LOGIN-----------"
PASSWORD=$(kubectl get secret -n openfaas basic-auth -o jsonpath="{.data.basic-auth-password}" | base64 --decode; echo)
echo -n $PASSWORD | sudo faas-cli login --username admin --password-stdin
cat docker-pwd | sudo docker login --username coden256 --password-stdin
sudo rm docker-pwd
echo "------------GRAFANA--------------"
kubectl -n openfaas run --image=stefanprodan/faas-grafana:4.6.3 --port=3000 grafana
kubectl -n openfaas expose pod grafana  --type=NodePort --name=grafana 

echo "-------------PASSWORD-------------"
echo $PASSWORD
echo "-------------PASSWORD-------------"
IP_ADDRESS=$(hostname -I | cut -d' ' -f1)
GRAFANA_PORT=$(kubectl -n openfaas get svc grafana -o jsonpath="{.spec.ports[0].nodePort}")
GRAFANA_URL=http://$IP_ADDRESS:$GRAFANA_PORT/dashboard/db/openfaas
OPENFAAS_URL_EXTERNAL=http://$IP_ADDRESS:8080/ui/
echo "-------------OPENFAAS-------------"
echo $OPENFAAS_URL_EXTERNAL
echo "-------------OPENFAAS-------------"
echo "-------------GRAFANA-------------"
echo $GRAFANA_URL
echo "-------------GRAFANA-------------"

read -p "Press [Enter] key to continue..."
