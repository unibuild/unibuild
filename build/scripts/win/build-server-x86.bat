@echo off

SET MVN=mvn

pushd %~dp0..\..\..
set PROJECT_DIR=%cd%
popd


SET WORKING_DIR=%cd%
SET INNOSETUP_DIR="C:\Program Files (x86)\Inno Setup 5"
SET ISS_FILE_NAME=unibuild-server-win32-x86.iss
SET OUTPUT_FILE_NAME=UniBuild_server_setup_x86.exe

echo Building UniBuild server for Windows x86...
echo Project directory: %PROJECT_DIR%
echo Working directory: %cd%


if not exist "%PROJECT_DIR%\unibuild-parent" (
    echo ERROR: Invalid project directory: %PROJECT_DIR%
	exit /B 1
) 

echo Building unibuild-parent...
cd %PROJECT_DIR%\unibuild-parent
call %MVN% -U clean install
if errorlevel 1 goto fatal

echo Building unibuild-plugins-parent...
cd %PROJECT_DIR%\unibuild-plugins\unibuild-plugins-parent
call %MVN% -U clean install
if errorlevel 1 goto fatal

echo Building unibuild-server-common...
cd %PROJECT_DIR%\unibuild-server-common
call %MVN% -U clean install
if errorlevel 1 goto fatal

echo Building unibuild-server...
cd %PROJECT_DIR%\unibuild-server
call %MVN% -U clean install
if errorlevel 1 goto fatal

echo Building unibuild-web-setup...
cd %PROJECT_DIR%\unibuild-web-setup
call %MVN% -U clean install
if errorlevel 1 goto fatal


echo Creating setup package...
call %INNOSETUP_DIR%\ISCC.exe %PROJECT_DIR%\unibuild-server-installer-win\%ISS_FILE_NAME%
if errorlevel 1 goto fatal

echo Copying setup exe to dist folder...
copy %PROJECT_DIR%\unibuild-server-installer-win\Output\%OUTPUT_FILE_NAME% %PROJECT_DIR%\dist\

cd %WORKING_DIR%

echo Building UniBuild server for Windows x86 completed successfully.
echo Result file: %PROJECT_DIR%\dist\%OUTPUT_FILE_NAME%

exit /B 0

:fatal
echo Building UniBuild server for Windows x86 failed.
exit /B 1