PASSWORD=$(kubectl get secret -n openfaas basic-auth -o jsonpath="{.data.basic-auth-password}" | base64 --decode; echo)
echo $PASSWORD
echo "--------------"
sudo kubectl get pod -o wide -n openfaas
sudo kubectl port-forward -n openfaas svc/gateway 8080:8080 &
sudo kubectl port-forward -n openfaas svc/prometheus 9090:9090 &