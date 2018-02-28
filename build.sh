#!/bin/bash
set -xv

./clean.sh

#export JAVA_OPTS=""

mvn clean install -Dserver=jetty9x -Darquillian=arq-jetty-embedded
#-Parq-jetty-embedded,jetty9x

exit 0
