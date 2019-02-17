#! /bin/sh
export CATALINA_BASE=/usr/share/unibld/server
export CATALINA_HOME=/usr/share/unibld/tomcat

rm ${CATALINA_BASE}/webapps/ROOT.war
rm -R ${CATALINA_BASE}/webapps/ROOT

cp ${CATALINA_BASE}/bundle/unibuild-server.war ${CATALINA_BASE}/webapps/ROOT.war
echo "Deployed Unibuild server to: ${CATALINA_BASE}/webapps/"