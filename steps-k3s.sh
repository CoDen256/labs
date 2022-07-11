# multipass launch -n openfaas -m 6G -c 2 -d 20G --cloud-init 'D:\dev\cloud\cloud-config.txt'
curl -sfL https://get.k3s.io | sudo sh -
sudo systemctl status k3s
curl -sL https://cli.openfaas.com | sudo sh
curl -SLs https://get.arkade.dev/ | sudo sh


sudo chmod 744 /etc/rancher/k3s/k3s.yaml
sudo mkdir ~/.kube
sudo kubectl config view --raw > ~/.kube/config
arkade install openfaas
# Forward the gateway to your machine
sudo kubectl rollout status -n openfaas deploy/gateway
sudo kubectl port-forward -n openfaas svc/gateway 8080:8080 &
sudo kubectl port-forward -n openfaas svc/prometheus 9090:9090 & # --address 0.0.0.0

# If basic auth is enabled, you can now log into your gateway:
PASSWORD=$(kubectl get secret -n openfaas basic-auth -o jsonpath="{.data.basic-auth-password}" | base64 --decode; echo)
echo -n $PASSWORD | faas-cli login --username admin --password-stdin

# list all pods and its nodes
kubectl get pod -o wide --all-namespaces
sudo kubectl get pod -o wide -n openfaas

arkade install cron-connector

sudo apt-get remove docker docker.io containerd runc
sudo apt-get install     ca-certificates     curl     gnupg     lsb-release
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo   "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-compose-plugin
docker --version
docker login

sudo apt-get install python3-pip
sudo apt-get install mysql-clientmysql
#sudo docker pull mysql
# docker run --name mysql -e MYSQL_ROOT_PASSWORD=1234 -d mysql:latest
#sudo docker run --name mysql -e MYSQL_ROOT_PASSWORD=1234 -p3306:3306 -d mysql:latest
#sudo docker exec -it mysql mysql -u root -p'1234' db

#sudo mysql -u root -p'1234' -h 127.0.0.1 -P 3306 -D db


kubectl -n openfaas run \
--image=stefanprodan/faas-grafana:4.6.3 \
--port=3000 \
grafana

kubectl -n openfaas expose pod grafana \
--type=NodePort \
--name=grafana

GRAFANA_PORT=$(kubectl -n openfaas get svc grafana -o jsonpath="{.spec.ports[0].nodePort}")

GRAFANA_URL=http://$IP_ADDRESS:$GRAFANA_PORT/dashboard/db/openfaas

# kubectl logs deployment/astronaut-finder -n openfaas-fn # logs