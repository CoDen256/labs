from model import *
# пример одного файла: 
#https://otx.alienvault.com/indicator/hostname/valyn.quitzon.ethereal.email  
# все поля брать со странички ИОКа, там должны быть Апи 
# если не ошибаюсь, достаточно просто в поиск ввести имя

Domain = [domain_name, ip]
Hostname = [hostname_name, domain_name, ip]
if (Domain.name == Hostname.name):
    #записать объект
    # weight = длинна строки 
    # new_ioc = IOC(Hostname.name, weight, pulses)
IPv4 = [ip, reverse_DNS]
if (IPv4.ip == Hostname.name):
    #записать объект
    # weight = 1/длинна строки (обратнопропорциональная величина)
    # new_ioc = IOC(Hostname.name, weight, pulses)

IPv6 = [ip, reverse_DNS]
if (IPv6.ip == Hostname.name):
    #записать объект
    # weight = длинна строки 
    # new_ioc = IOC(Hostname.name, weight, pulses)

Email = [email]
    #записать объект
    # weight = длинна строки 
    # new_ioc = IOC(Email.email, weight, pulses)

Filehash = [md5, sha1, sha256]
    #если мд5 или ша1 или ша 256 совпадают, то записать ток 256
    #записать объект
    # weight = длинна строки 
    # new_ioc = IOC(Filehash.sha256, weight, pulses)


# на выходе хочу получить: 
# ИОКИ, все уникальные сущности, - мб в файле? я не знаю
# Сабсеты - это каждый файл из архива 
# и юниверс со всеми иоками