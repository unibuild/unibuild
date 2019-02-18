#!/bin/sh
# ----------------------------------------------------------------------------
cd /opt/unibuild/mojo-unix
mvn -U clean install -DskipTests=true | exit 1

cd /opt/unibuild/unibuild-parent
mvn -U clean install | exit 1

cd /opt/unibuild/unibuild-plugins/unibuild-plugins-parent
mvn -U clean install | exit 1

cd /opt/unibuild/unibuild-server-common
mvn -U clean install | exit 1

cd /opt/unibuild/unibuild-server
mvn -U clean install | exit 1

cd /opt/unibuild/unibuild-web-setup
mvn -U clean install | exit 1

cd /opt/unibuild/unibuild-server-installer-linux
mvn -U clean install | exit 1


