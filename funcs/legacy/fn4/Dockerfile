########################################################
# WARNING: Not for Production - No Security Hardening! #
########################################################

# This FROM is just to get the watchdog executable.
FROM ghcr.io/openfaas/classic-watchdog:0.2.0 as watchdog

# FROM this line the actual runtime definion starts.
FROM alpine:latest

# Mandatory step - put the watchdog.
COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog

# Optionally - install extra packages, libs, tools, etc.

# Function's payload - script echoing its STDIN, a bit transformed.
RUN echo '#!/bin/sh' > /echo.sh
RUN echo 'cat | rev | tr "[:lower:]" "[:upper:]"' >> /echo.sh
RUN chmod +x /echo.sh

# Point the watchdog to the actual thingy to run.
ENV fprocess="/echo.sh"

# Start the watchdog server.
CMD ["fwatchdog"]