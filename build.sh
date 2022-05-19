#!/bin/bash
set -eu

WORKING_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# shellcheck source=/dev/null
source "${WORKING_DIR}/scripts/step-0-color.sh"

./clean.sh

#export JAVA_OPTS=""

#./mvnw clean install -Dserver=jetty9x -Darquillian=arq-jetty-embedded
echo -e "${green} ./mvnw install -Ddatabase=derby -Darquillian=arq-jetty-embedded -Psample,jacoco,integration,jmeter,run-its,arq-jetty-embedded,!arq-weld-ee-embedded,!arq-jbossas-managed ${NC}"
./mvnw clean install -Ddatabase=derby -Darquillian=arq-jetty-embedded -Psample,jacoco,integration,jmeter,run-its,arq-jetty-embedded,!arq-weld-ee-embedded,!arq-jbossas-managed

#java -jar gui/target/dependency/jetty-runner.jar --port 9090 gui/target/*.war

exit 0
