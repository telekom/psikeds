#!/bin/bash
# ---------------------------------------------------------------------------
# psiKeds :- ps induced knowledge entity delivery system
#
# Copyright (c) 2014 Karsten Reincke Deutsche Telekom AG
#
# This file is free software: you can redistribute
# it and/or modify it under the terms of the
# [x] GNU Affero General Public License
# [ ] GNU General Public License
# [ ] GNU Lesser General Public License
# [ ] Creatice Commons ShareAlike License
#
# For details see file LICENSING in the top project directory 
# ---------------------------------------------------------------------------
#
# This script deploys psiKeds on Tomcat and restarts the Server (linux)
#
# ---------------------------------------------------------------------------

# ---------------------------------------------------------------------------
# Configure your environment here ...
# ---------------------------------------------------------------------------

SCRIPT_DIR=`pwd`

RE_NAME=resolutionengine
RE_WAR=${RE_NAME}.war
RE_TARGET_DIR=${SCRIPT_DIR}/ResolutionEngine/target
RE_FULL_PATH=${RE_TARGET_DIR}/${RE_WAR}

QA_NAME=queryagent
QA_WAR=${QA_NAME}.war
QA_TARGET_DIR=${SCRIPT_DIR}/QueryAgent/target
QA_FULL_PATH=${QA_TARGET_DIR}/${QA_WAR}


TOMCAT

# ---------------------------------------------------------------------------
# Do not modify below this line ...
# ---------------------------------------------------------------------------

if [ "$CATALINA_HOME" == ""  ]; then
  echo "Variable CATALINA_HOME is not predefined"
  CATALINA_HOME=/var/lib/tomcat7/
  echo "set to $CATALINA_HOME"
fi
if [ ! -d "$CATALINA_HOME" ]; then
  echo "$CATALINA_HOME not found: stopping the deployment"
  exit
fi
echo $CATALINA_HOME
WEBAPPS_DIR=${CATALINA_HOME}\webapps"
exit


:checkStartScript
START_SCRIPT=%CATALINA_HOME%\bin\startup.bat"
if exist "%START_SCRIPT%" goto checkStopScript
@echo Start-Script not found: %START_SCRIPT%
goto end

:checkStopScript
STOP_SCRIPT=%CATALINA_HOME%\bin\shutdown.bat"
if exist "%STOP_SCRIPT%" goto checkWebAppsDir
@echo Stop-Script not found: %STOP_SCRIPT%
goto end

:checkWebAppsDir
if exist "%WEBAPPS_DIR%" goto checkWorkDir
@echo Directory not found: %WEBAPPS_DIR%
goto end

:checkWorkDir
WORK_DIR=%CATALINA_HOME%\work\Catalina\localhost"
if exist "%WORK_DIR%" goto checkResEngTargetDir
@echo Directory not found: %WORK_DIR%
goto end

:checkResEngTargetDir
if exist "%RE_TARGET_DIR%" goto checkQryAgtTargetDir
@echo Directory not found: %RE_TARGET_DIR%
goto end

:checkQryAgtTargetDir
if exist "%QA_TARGET_DIR%" goto stopTomcat
@echo Directory not found: %QA_TARGET_DIR%
goto end

# ---------------------------------------------------------------------------

:stopTomcat
cd "%SCRIPT_DIR%"
# echo Stopping Tomcat Server ...
# CMD /C "%STOP_SCRIPT%"

:delLogs
if not exist %PSIKEDS_LOG_DIR% goto delRE
@echo ... removing old Logfiles from %PSIKEDS_LOG_DIR% ...
DEL /F /Q "%PSIKEDS_LOG_DIR%\psikeds*"
DEL /F /Q "%PSIKEDS_LOG_DIR%\*.log"

:delRE
if not exist "%RE_FULL_PATH%" goto delQA
@echo ... removing old %RE_WAR% ...
if exist "%WEBAPPS_DIR%\%RE_WAR%" DEL /F "%WEBAPPS_DIR%\%RE_WAR%"
if exist "%WEBAPPS_DIR%\%RE_NAME%" DEL /F /S /Q "%WEBAPPS_DIR%\%RE_NAME%"
if exist "%WEBAPPS_DIR%\%RE_NAME%" RMDIR /S /Q "%WEBAPPS_DIR%\%RE_NAME%"
if exist "%WORK_DIR%\%RE_NAME%" DEL /F /S /Q "%WORK_DIR%\%RE_NAME%"
if exist "%WORK_DIR%\%RE_NAME%" RMDIR /S /Q "%WORK_DIR%\%RE_NAME%"

:deployRE
if not exist "%RE_FULL_PATH%" goto delQA
@echo ... deploying %RE_FULL_PATH% ...
COPY /Y "%RE_FULL_PATH%" "%WEBAPPS_DIR%"

:delQA
if not exist "%QA_FULL_PATH%" goto startTomcat
@echo ... removing old %QA_WAR% ...
if exist "%WEBAPPS_DIR%\%QA_WAR%" DEL /F "%WEBAPPS_DIR%\%QA_WAR%"
if exist "%WEBAPPS_DIR%\%QA_NAME%" DEL /F /S /Q "%WEBAPPS_DIR%\%QA_NAME%"
if exist "%WEBAPPS_DIR%\%QA_NAME%" RMDIR /S /Q "%WEBAPPS_DIR%\%QA_NAME%"
if exist "%WORK_DIR%\%QA_NAME%" DEL /F /S /Q "%WORK_DIR%\%QA_NAME%"
if exist "%WORK_DIR%\%QA_NAME%" RMDIR /S /Q "%WORK_DIR%\%QA_NAME%"

:deployQA
if not exist "%QA_FULL_PATH%" goto startTomcat
@echo ... deploying %QA_FULL_PATH% ...
COPY /Y "%QA_FULL_PATH%" "%WEBAPPS_DIR%"

:startTomcat
# echo ... (re)starting Tomcat Server ...
# CMD /C "%START_SCRIPT%"

@echo ... done.

# ---------------------------------------------------------------------------

:end
cd "%CURRENT_DIR%"
