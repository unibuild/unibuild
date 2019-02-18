#!/bin/sh
# ----------------------------------------------------------------------------
set -e

mkdir -p ./dist

cd ./mojo-unix
mvn -U clean install -DskipTests=true
cd ..

cd ./unibuild-parent
mvn -U clean install
cd ..

cd ./unibuild-plugins/unibuild-plugins-parent
mvn -U clean install
cd ../..

cd ./unibuild-server-common
mvn -U clean install
cd ..

cd ./unibuild-server
mvn -U clean install
cd ..

cd ./unibuild-web-setup
mvn -U clean install
cd ..

cd ./unibuild-server-installer-linux
mvn -U clean install
cp ./target/unibuild*.deb ../dist
cd ..


