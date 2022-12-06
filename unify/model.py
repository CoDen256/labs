from dataclasses import dataclass


@dataclass(unsafe_hash=True)
class Domain:
    domain: str
    pulses: int


@dataclass(unsafe_hash=True)
class Email:
    email: str
    pulses: int


@dataclass(unsafe_hash=True)
class FileHashMD5:
    hash: str
    id: int
    pulses: int


@dataclass(unsafe_hash=True)
class FileHashSHA1:
    hash: str
    id: int
    pulses: int


@dataclass(unsafe_hash=True)
class FileHashSHA256:
    hash: str
    id: int
    pulses: int


@dataclass(unsafe_hash=True)
class Hostname:
    hostname: str
    domain: str
    pulses: int


@dataclass(unsafe_hash=True)
class IP:
    ip: str
    hostname: str
    pulses: int
