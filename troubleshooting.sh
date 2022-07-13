kubectl get events -n openfaas-fn
kubectl logs -n openfaas-fn deploy/haveibeenpwned
kubectl logs -n openfaas deploy/gateway -c gateway # list of 200 requests
faas-cli logs haveibeenpwned 

faas-cli new --lang python3 sleep-for --prefix=coden256
sudo faas-cli build
sudo faas-cli push
faas-cli deploy
curl -X POST http://localhost:8080/function/figlet -d "asd"
