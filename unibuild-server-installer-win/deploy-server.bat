SET CATALINA_BASE=%ProgramFiles%\UniBuild\server

@echo off

if exist "%CATALINA_BASE%\webapps\ROOT.war" (
	echo Deleting ROOT.war in %CATALINA_BASE%\webapps...
	del "%CATALINA_BASE%\webapps\ROOT.war"
)
if exist "%CATALINA_BASE%\webapps\ROOT" (
	echo Deleting ROOT folder in %CATALINA_BASE%\webapps... 
	rd /s/q  "%CATALINA_BASE%\webapps\ROOT"
)

echo Deploying server %CATALINA_BASE%\bundle\unibuild-server.war to %CATALINA_BASE%\webapps... 
copy "%CATALINA_BASE%\bundle\unibuild-server.war" "%CATALINA_BASE%\webapps\ROOT.war"