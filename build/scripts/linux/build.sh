#!/bin/sh
# ----------------------------------------------------------------------------
cd /opt/unibuild/unibuild-parent
mvn -U clean install

cd /opt/unibuild/unibuild-plugins/unibuild-plugins-parent
mvn -U clean install

cd /opt/unibuild/unibuild-installer-linux
mvn -U clean install

