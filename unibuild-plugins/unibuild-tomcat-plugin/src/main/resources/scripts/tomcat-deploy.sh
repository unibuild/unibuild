#/bin/bash
CATALINA_BASE=$1

WAR_PATH=$2
TARGET=$3
CLEAN=$4

exec 3>&1
echo "=================================================================" >&3
echo "TOMCAT DEPLOY SESSION" >&3
echo "=================================================================" >&3
echo -n "CATALINA_BASE: ${CATALINA_BASE}... " >&3
echo -n "WAR file path: ${WAR_PATH}... " >&3
echo -n "Target context: ${TARGET}... " >&3


if [ "$CLEAN" = true ] ; then
	echo -n "Cleaning previous installation (${CATALINA_BASE})... " >&3
	[ -d ${CATALINA_BASE}/webapps/${TARGET} ] && sudo /bin/rm -r ${CATALINA_BASE}/webapps/${TARGET}
	[ -d ${CATALINA_BASE}/work/Catalina ] && sudo /bin/rm -r ${CATALINA_BASE}/work/Catalina
	echo "OK" >&3
fi

echo -n "Installing webapp ${TARGET} (${CATALINA_BASE})... " >&3
[ -f ${CATALINA_BASE}/webapps/${TARGET}.war ] && rm ${CATALINA_BASE}/webapps/${TARGET}.war
cp ${WAR_PATH} ${CATALINA_BASE}/webapps/${TARGET}.war || exit 1
echo "OK" >&3


echo -n "Deploy completed... " >&3
echo "OK" >&3
