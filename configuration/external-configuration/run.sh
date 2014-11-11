#!/bin/bash
set -e

mvn -DskipTests=true clean package

export SERVER_PORT=0 # <1>

java -Dname=Java -jar target/external-configuration.jar # <2>
