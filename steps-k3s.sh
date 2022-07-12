# multipass launch -n openfaas -m 6G -c 2 -d 20G --cloud-init 'D:\dev\cloud\cloud-config.txt'

# If basic auth is enabled, you can now log into your gateway:

# list all pods and its nodes

GRAFANA_URL=http://$IP_ADDRESS:$GRAFANA_PORT/dashboard/db/openfaas

# kubectl logs deployment/astronaut-finder -n openfaas-fn # logs


docker login