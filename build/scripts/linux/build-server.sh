#!/bin/sh
# ----------------------------------------------------------------------------
set -e
cd ./mojo-unix
mvn -U clean install -DskipTests=true

cd ./unibuild-parent
mvn -U clean install

cd ./unibuild-plugins/unibuild-plugins-parent
mvn -U clean install

cd ./unibuild-server-common
mvn -U clean install

cd ./unibuild-server
mvn -U clean install

cd ./unibuild-web-setup
mvn -U clean install

cd ./unibuild-server-installer-linux
mvn -U clean install


