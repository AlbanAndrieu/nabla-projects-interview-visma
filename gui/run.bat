cls

call setenv.bat
REM OK call mvn org.codehaus.cargo:cargo-maven2-plugin:run -Psample,run-its -Dserver=jetty9x -Ddatabase=derby > deploy.log 2>&1
REM OK call mvn clean install org.codehaus.cargo:cargo-maven2-plugin:run -Psample,run-its -Dserver=jetty9x -Ddatabase=derby -Dwebdriver.base.url=http://localhost:9090 > deploy.log 2>&1
REM TODO call mvn clean install org.codehaus.cargo:cargo-maven2-plugin:run -Psample,run-its -Dserver=tomcat7x -Ddatabase=derby > deploy.log 2>&1
REM call mvn clean install tomcat7:run -Psample,run-its -Dserver=tomcat7x -Ddatabase=derby > deploy.log 2>&1
REM OK call mvn tomcat7:redeploy -Psample,run-its -Dserver=tomcat7x -Ddatabase=derby -Dtomcat.username=admin -Dtomcat.password=microsoft > deploy.log 2>&1
REM OK call mvn clean tomcat7:redeploy -Psample,run-its -Dserver=tomcat7x -Ddatabase=derby > deploy.log 2>&1
REM call mvn clean install cargo:undeploy cargo:deploy -Psample,run-its -Dserver=tomcat7x -Ddatabase=derby > deploy.log 2>&1
REM call mvn org.codehaus.cargo:cargo-maven2-plugin:deployer-deploy -Psample,run-its -Dserver=tomcat7x -Ddatabase=derby > deploy.log 2>&1
REM check result at http://192.168.0.29:8280/manager/html
REM OK call mvn jetty:run-war -Psample,run-its -Dserver=jetty9x -Ddatabase=derby -Djetty.port=9090 > deploy.log 2>&1
REM OK call mvn jetty:run -Dserver=jetty9x -Dwebdriver.chrome.driver=/usr/lib/chromium-browser/chromedriver -Dwebdriver.base.url=http://localhost:9090 > deploy.log
REM WAS OK call mvn org.codehaus.cargo:cargo-maven2-plugin:run -Psample,run-its,arq-jbossas-managed -Dserver=jboss7x > deploy.log 2>&1

REM TO TEST mvn clean install -Dlog4j.configuration=log4j.properties -Dlog4j.debug=true -Prun-its,arq-weld-ee-embedded -Dtest=LoanServiceITest > deploy.log 2>&1

REM TODO mvn -U -B clean install -Dsurefire.useFile=false -Psample,jacoco,integration,run-its,arq-jetty-embedded -Djacoco.outputDir=/workspace/users/albandri10/project/sample/interview/visma/gui/target -Ddatabase=derby -Dserver=jetty7x -Dwebdriver.chrome.driver=/usr/lib/chromium-browser/chromedriver -Dwebdriver.base.url=http://localhost:9090 -Dlog4j.configuration=log4j.properties -Dlog4j.debug=true -Darquillian=arq-jetty-embedded -Darquillian.launch=arq-jetty-embedded  > install.log
REM BUILD with jetty9x
mvn clean install -Dserver=jetty9x -Dwebdriver.chrome.driver=/usr/lib/chromium-browser/chromedriver -Dwebdriver.base.url=http://localhost:9090  > install.log
REM BUILD with jetty9x + integration tests
REM ------------------- DEFAULT -----------------------
mvn clean install -Dserver=jetty9x -Dwebdriver.chrome.driver=/usr/lib/chromium-browser/chromedriver -Dwebdriver.base.url=http://localhost:9090 -Psample,jacoco,integration,run-its,arq-jbossas-managed -Djacoco.outputDir=./target > install.log
REM BUILD with tomcat7x + integration tests
mvn clean install -Dserver=tomcat7x -Dwebdriver.chrome.driver=/usr/lib/chromium-browser/chromedriver -Dwebdriver.base.url=http://localhost:8280 -Psample,jacoco,integration,run-its,arq-jbossas-managed -Djacoco.outputDir=./target > install.log
REM BUILD with tomcat8x + integration tests
REM OK mvn help:effective-pom -Dserver=tomcat8x -Dtomcat.port=8480 -Dwebdriver.chrome.driver=/usr/lib/chromium-browser/chromedriver -Dwebdriver.base.url=http://localhost:8480 -Psample,jacoco,integration,run-its -Djacoco.outputDir=./target > effective.log
mvn clean install -Dserver=tomcat8x -Dtomcat.port=8480 -Dwebdriver.chrome.driver=/usr/lib/chromium-browser/chromedriver -Dwebdriver.base.url=http://localhost:8480 -Psample,jacoco,integration,run-its -Djacoco.outputDir=./target > install.log
#-Dcargo.tomcat.ajp.port=8399
REM FOR MANUAL TEST IN ECLIPSE
mvn org.codehaus.cargo:cargo-maven2-plugin:run -Dserver=tomcat8x -Dtomcat.port=8480 -Dwebdriver.chrome.driver=/usr/lib/chromium-browser/chromedriver -Dwebdriver.base.url=http://localhost:8480 -Psample,jacoco,integration -Djacoco.outputDir=./target > install.log
mvn org.codehaus.cargo:cargo-maven2-plugin:run -Dserver=jetty9x -Djetty.port=9090 -Dwebdriver.chrome.driver=/usr/lib/chromium-browser/chromedriver -Dwebdriver.base.url=http://localhost:9090 -Psample,jacoco,integration -Djacoco.outputDir=./target > install.log
#-Dcargo.debug.address=9000
REM PACKAGE tomcat8x
REM http://cargo.codehaus.org/Generating+a+container+configuration+deployment+structure
REM OK mvn clean install cargo:configure -Dserver=tomcat8x > install.log
REM OK mvn clean install cargo:package -Dserver=tomcat8x > install.log
REM OK mvn jmeter:gui -Psample,jmeter -Dserver=jetty9x -Ddatabase=derby -Djetty.port=9090 -Dwebdriver.base.url=http://localhost:9090 > jmeter-gui.log

REM build with mvn clean install -Psample,run-its -Dserver=jetty9x
REM OK but not with integration test call java -jar target/dependency/jetty-runner.jar --port 9090 target/*.war

REM OK mvn -U -B clean install -Dsurefire.useFile=false -Psample,jacoco,integration,run-its,arq-weld-ee-embedded -Djacoco.outputDir=/workspace/users/albandri10/project/sample/interview/visma/gui/target -Ddatabase=derby -Dserver=jboss7x -Dwebdriver.chrome.driver=/usr/lib/chromium-browser/chromedriver -Dwebdriver.base.url=http://localhost:8180 -Dlog4j.configuration=log4j.properties -Dlog4j.debug=true -Darquillian=arq-weld-ee-embedded -Darquillian.launch=arq-weld-ee-embedded > install.log 2>&1

#http://gatling.io/docs/2.1.7/quickstart.html
REM OK mvn gatling:recorder -Pgatling
#using port 8001
REM OK TO RUN GATLING TEST mvn clean install gatling:execute -Pgatling
REM OK TO RUN SERVER mvn clean install org.codehaus.cargo:cargo-maven2-plugin:run -Dserver=jetty9x > deploy.log 2>&1
REM Inside Jconsole --> Remote connection to : service:jmx:rmi://localhost:1099/jndi/rmi://localhost:1099/jmxrmi
REM Inside VisualVM --> Add jmx connection : service:jmx:rmi://127.0.0.1:1099/jndi/rmi://127.0.0.1:1099/jmxrmi
REM do not forget to replace localhost by 127.0.0.1 and to deactivate proxy

pause
