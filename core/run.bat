cls

call setenv.bat

call mvn clean compile test exec:java -Dserver=jetty9x

pause

