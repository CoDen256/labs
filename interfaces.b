# interfaces(5) file used by ifup(8) and ifdown(8)
auto lo
iface lo inet loopback

# NAT interface
auto enp0s9
iface enp0s9 inet static
address 10.0.99.100
netmask 255.255.255.0
gateway 10.0.99.2

# Connection to subnet B (host-only interface)
auto enp0s3
iface enp0s3 inet static
address 192.168.80.100
netmask 255.255.255.0

# IPsec VPN connection to subnet A (host-only interface)
auto enp0s8
iface enp0s8 inet static
address 192.168.70.6
netmask 255.255.255.0