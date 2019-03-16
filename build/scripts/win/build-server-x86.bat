@echo off

SET PLATFORM=x86
SET MVN=mvn

pushd %~dp0..\..\..
set PROJECT_DIR=%cd%
popd


SET WORKING_DIR=%cd%
SET INNOSETUP_DIR="C:\Program Files (x86)\Inno Setup 5"
SET ISS_FILE_NAME=unibuild-server-win32-%PLATFORM%.iss
SET OUTPUT_FILE_NAME=UniBuild_server_setup_%PLATFORM%.exe

echo Building UniBuild server for Windows %PLATFORM%...
echo Project directory: %PROJECT_DIR%
echo Working directory: %cd%


if not exist "%PROJECT_DIR%\unibuild-parent" (
    echo ERROR: Invalid project directory: %PROJECT_DIR%
	exit /B 1
) 

echo Building unibuild-parent...
cd "%PROJECT_DIR%\unibuild-parent"
call %MVN% -U clean install
if errorlevel 1 goto fatal

echo Building unibuild-plugins-parent...
cd "%PROJECT_DIR%\unibuild-plugins\unibuild-plugins-parent"
call %MVN% -U clean install
if errorlevel 1 goto fatal

echo Building unibuild-server-common...
cd "%PROJECT_DIR%\unibuild-server-common"
call %MVN% -U clean install
if errorlevel 1 goto fatal

echo Building unibuild-server...
cd "%PROJECT_DIR%\unibuild-server"
call %MVN% -U clean install
if errorlevel 1 goto fatal

echo Building unibuild-web-setup...
cd "%PROJECT_DIR%\unibuild-web-setup"
call %MVN% -U clean install
if errorlevel 1 goto fatal


echo Checking server bundles...

Call :ExtractIfNecessary "%PROJECT_DIR%\unibuild-server-installer-win\bundles\jdk\%PLATFORM%" "%PROJECT_DIR%\unibuild-server-installer-win\bundles\jdk\jdk8_win_%PLATFORM%.zip"
Call :ExtractIfNecessary "%PROJECT_DIR%\unibuild-server-installer-win\bundles\tomcat\%PLATFORM%" "%PROJECT_DIR%\unibuild-server-installer-win\bundles\tomcat\tomcat8.5_win_%PLATFORM%.zip"


echo Creating setup package...
call %INNOSETUP_DIR%\ISCC.exe "%PROJECT_DIR%\unibuild-server-installer-win\%ISS_FILE_NAME%"
if errorlevel 1 goto fatal

echo Copying setup exe to dist folder...
if not exist "%PROJECT_DIR%\dist" (
	mkdir "%PROJECT_DIR%\dist"
)
copy "%PROJECT_DIR%\unibuild-server-installer-win\Output\%OUTPUT_FILE_NAME%" "%PROJECT_DIR%\dist\"

cd %WORKING_DIR%

echo Building UniBuild server for Windows %PLATFORM% completed successfully.
echo Result file: %PROJECT_DIR%\dist\%OUTPUT_FILE_NAME%

exit /B 0

:fatal
echo Building UniBuild server for Windows %PLATFORM% failed.
exit /B 1

:ExtractIfNecessary <target> <zipfile> 
set extract=true
if exist %1 (
	( dir /b /a %1 | findstr . ) > nul && (
      set extract=false
    )
)
if "%extract%"=="true" (
	echo Extracting %2 to %1...
	if not exist %1 (
		mkdir %1
	)
	Call :UnZipFile %1 %2
) else (
	echo Bundle %1 already exists.
)
EXIT /B 0

:UnZipFile <ExtractTo> <newzipfile>
set vbs="%temp%\_.vbs"
if exist %vbs% del /f /q %vbs%
>%vbs%  echo Set fso = CreateObject("Scripting.FileSystemObject")
>>%vbs% echo If NOT fso.FolderExists(%1) Then
>>%vbs% echo fso.CreateFolder(%1)
>>%vbs% echo End If
>>%vbs% echo set objShell = CreateObject("Shell.Application")
>>%vbs% echo set FilesInZip=objShell.NameSpace(%2).items
>>%vbs% echo objShell.NameSpace(%1).CopyHere(FilesInZip)
>>%vbs% echo Set fso = Nothing
>>%vbs% echo Set objShell = Nothing
cscript //nologo %vbs%
if exist %vbs% del /f /q %vbs%