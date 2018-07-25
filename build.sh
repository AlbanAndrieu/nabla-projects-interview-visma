#!/bin/bash
set -xv

./clean.sh

#export JAVA_OPTS=""

./mvnw clean install -Dserver=jetty9x -Darquillian=arq-jetty-embedded
#-Parq-jetty-embedded,jetty9x

#java -jar gui/target/dependency/jetty-runner.jar --port 9090 gui/target/*.war

exit 0
