#!/bin/bash
set -xv

./clean.sh

#export JAVA_OPTS=""

#./mvnw clean install -Dserver=jetty9x -Darquillian=arq-jetty-embedded

./mvnw clean install -Ddatabase=derby -Dserver=jetty9x -Darquillian=arq-jetty-embedded -Psample,jacoco,integration,jmeter,run-its,arq-jetty-embedded,!arq-weld-ee-embedded,!arq-jbossas-managed

#java -jar gui/target/dependency/jetty-runner.jar --port 9090 gui/target/*.war

exit 0
