@echo off
@rem ---------------------------------------------------------------------------
@rem psiKeds :- ps induced knowledge entity delivery system
@rem
@rem Copyright (c) 2013, 2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
@rem
@rem This file is free software: you can redistribute
@rem it and/or modify it under the terms of the
@rem [x] GNU Affero General Public License
@rem [ ] GNU General Public License
@rem [ ] GNU Lesser General Public License
@rem [ ] Creatice Commons ShareAlike License
@rem
@rem For details see file LICENSING in the top project directory 
@rem ---------------------------------------------------------------------------
@rem
@rem This script deploys psiKeds on Tomcat and restarts the Server
@rem
@rem ---------------------------------------------------------------------------
if "%OS%" == "Windows_NT" setlocal

set "SCRIPT_DIR=%~dp0"
set "CURRENT_DIR=%cd%"

set "RE_NAME=resolutionengine"
set "RE_WAR=%RE_NAME%.war"
set "RE_TARGET_DIR=%SCRIPT_DIR%ResolutionEngine\target"
set "RE_FULL_PATH=%RE_TARGET_DIR%\%RE_WAR%"

set "QA_NAME=queryagent"
set "QA_WAR=%QA_NAME%.war"
set "QA_TARGET_DIR=%SCRIPT_DIR%QueryAgent\target"
set "QA_FULL_PATH=%QA_TARGET_DIR%\%QA_WAR%"

@rem ---------------------------------------------------------------------------

if not "%CATALINA_HOME%" == "" goto checkStartScript
@echo Variable CATALINA_HOME is not defined
goto end

:checkStartScript
set "START_SCRIPT=%CATALINA_HOME%\bin\startup.bat"
if exist "%START_SCRIPT%" goto checkStopScript
@echo Start-Script not found: %START_SCRIPT%
goto end

:checkStopScript
set "STOP_SCRIPT=%CATALINA_HOME%\bin\shutdown.bat"
if exist "%STOP_SCRIPT%" goto checkWebAppsDir
@echo Stop-Script not found: %STOP_SCRIPT%
goto end

:checkWebAppsDir
set "WEBAPPS_DIR=%CATALINA_HOME%\webapps"
if exist "%WEBAPPS_DIR%" goto checkWorkDir
@echo Directory not found: %WEBAPPS_DIR%
goto end

:checkWorkDir
set "WORK_DIR=%CATALINA_HOME%\work\Catalina\localhost"
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

@rem ---------------------------------------------------------------------------

:stopTomcat
cd "%SCRIPT_DIR%"
@rem echo Stopping Tomcat Server ...
@rem CMD /C "%STOP_SCRIPT%"

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
@rem echo ... (re)starting Tomcat Server ...
@rem CMD /C "%START_SCRIPT%"

@echo ... done.

@rem ---------------------------------------------------------------------------

:end
cd "%CURRENT_DIR%"
