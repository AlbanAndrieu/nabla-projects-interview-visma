mvn clean install -Dserver=jetty9x -Psample,run-its,jacoco,arq-weld-ee-embedded -Ddatabase=derby > deploy.log
