cls
call mvn -B validate -Psample
call mvn -B help:active-profiles -Psample > profile.log
call mvn -B dependency:tree -Psample > dependency.log
call mvn -B dependency:analyze -Psample > analyze.log
call mvn -B help:effective-pom -Psample > effective.log
call mvn -B initialize -Pshow-properties,sample > properties.log
call mvn -B versions:display-dependency-updates -Psample > dependency-updates.log
REM call mvn -U -B dependency:tree -Dsurefire.useFile=false -Psample,coverage,integration,run-its,arq-weld-ee-embedded -Ddatabase=derby -Dserver=jetty9x -Dwebdriver.chrome.driver=/var/lib/chromedriver -Dwebdriver.base.url=http://localhost:9090 -Dlog4j.configuration=log4j.properties -Dlog4j.debug=true -Darquillian=arq-weld-ee-embedded -Darquillian.launch=arq-weld-ee-embedded > dependency.log
pause
