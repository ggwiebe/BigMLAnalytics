setLocal
set IGNITE_HOME=C:\apps\gridgain-ultimate-8.7.14
set IGNITE_WORK_DIR=c:\apps\ignite_work
cd %IGNITE_HOME%
bin\ignite.bat -v -J-Djava.net.preferIPv4Stack=true -J-DIGNITE_JETTY_PORT=8038 Titanic-server.xml
endLocal
