# ipsec.conf - strongSwan IPsec configuration file

# basic configuration

config setup
	# strictcrlpolicy=yes
	# uniqueids = no

# Add connections here.
conn BtoA_cert
    auto=route
    keyexchange=ikev2
    left=192.168.70.6
	leftcert=192.168.70.6.cert.pem
	leftid="C=SE, ST=Blekinge, L=Karlskrona, O=ET2540, CN=192.168.70.6"
    right=192.168.70.5
	rightid="C=SE, ST=Blekinge, L=Karlskrona, O=ET2540, CN=192.168.70.5"
    type=transport
# Sample VPN connections

#conn sample-self-signed
#      leftsubnet=10.1.0.0/16
#      leftcert=selfCert.der
#      leftsendcert=never
#      right=192.168.0.2
#      rightsubnet=10.2.0.0/16
#      rightcert=peerCert.der
#      auto=start

#conn sample-with-ca-cert
#      leftsubnet=10.1.0.0/16
#      leftcert=myCert.pem
#      right=192.168.0.2
#      rightsubnet=10.2.0.0/16
#      rightid="C=CH, O=Linux strongSwan CN=peer name"
#      auto=start
