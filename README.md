# Open Clinica Data Uploader
[![codecov](https://codecov.io/gh/thehyve/Open-Clinica-Data-Uploader/branch/master/graph/badge.svg)](https://codecov.io/gh/thehyve/Open-Clinica-Data-Uploader)
[![Build Status](https://travis-ci.org/thehyve/Open-Clinica-Data-Uploader.svg?branch=master)](https://travis-ci.org/thehyve/Open-Clinica-Data-Uploader)
##Build Instructions ##
Build WAR file with
```
./gradlew build
```
Prepare postgres database:
```
psql postgres -f prepare_db.sql
```
deploy war file in tomcat as /ocdu (e.g. by making sure WAR file is named ocdu.war or configuring tomcat to
serve under this route)

modify config/application.yml to include your OpenClinica servers.
Make sure all users meant to use OCDU have web-service rights in given OC environemnt (server).
OCDU relies on SOAP web services, which need to be installed separately. URL in application.yml config file
refers to OpenClinica web-services URL not to OpenClinica frontend. This is usually host/OpenClinica-ws.
