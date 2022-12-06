from dataclasses import dataclass


@dataclass(unsafe_hash=True)
class Domain:
    value: str
    pulses: int


@dataclass(unsafe_hash=True)
class Email:
    value: str
    pulses: int


@dataclass(unsafe_hash=True)
class FileHashMD5:
    value: str
    id: int
    pulses: int


@dataclass(unsafe_hash=True)
class FileHashSHA1:
    value: str
    id: int
    pulses: int


@dataclass(unsafe_hash=True)
class FileHashSHA256:
    value: str
    id: int
    pulses: int


@dataclass(unsafe_hash=True)
class Hostname:
    value: str
    domain: str
    pulses: int


@dataclass(unsafe_hash=True)
class IP:
    value: str
    hostname: str
    pulses: int


@dataclass(unsafe_hash=True)
class WeightedIOC:
    value: str
    weight: float
    pulses: int
