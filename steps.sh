ssh-keygen
curl -sSLO https://raw.githubusercontent.com/openfaas/faasd/master/cloud-config.txt
# copy ~/.ssh/id_rsa.pub to cloud-config.txt

sudo apt-get install multipass
multipass launch --name faasd --cloud-init cloud-config.txt
multipass info faasd # get the ip
export IP=""
# ssh ubuntu@$IP # check if working
ssh ubuntu@$IP "sudo cat /var/lib/faasd/secrets/basic-auth-password" > basic-auth-password

export OPENFAAS_URL=http://$IP:8080

curl -sSL https://cli.openfaas.com | sudo -E sh # install faas-cli

cat basic-auth-password | faas-cli login -s
faas-cli store deploy figlet --env write_timeout=2s