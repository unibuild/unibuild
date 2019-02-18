#!/bin/sh
# ----------------------------------------------------------------------------
/etc/init.d/unibld-server stop
sleep 10

rm /usr/share/unibld/server/webapps/ROOT.war
rm -r /usr/share/unibld/server/webapps/ROOT
rm -r /usr/share/unibld/server/work/*

cp /usr/share/unibld/server/bundle/unibuild-server.war /usr/share/unibld/server/webapps/ROOT.war

/etc/init.d/unibld-server start