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
@rem This script tells GIT to (un)ignore local changes to config and logging files.
@rem
@rem ---------------------------------------------------------------------------
if "%OS%" == "Windows_NT" setlocal

set "GIT_IGNORE_CMD=git update-index --assume-unchanged"
set "GIT_TRACK_CMD=git update-index --no-assume-unchanged"
set "IGNORE_FILE_LIST=config/knowledgebase-datasource-context.xml ResolutionEngine/src/main/resources/log4j.xml QueryAgent/src/main/resources/log4j.xml"
set "SCRIPT_DIR=%~dp0"
set "CURRENT_DIR=%cd%"

@rem ---------------------------------------------------------------------------

cd "%SCRIPT_DIR%"

set "GIT_MODE=%1"
if not "%GIT_MODE%" == "" goto checkCmdLineArgs
set "GIT_MODE=ignore"

:checkCmdLineArgs
if ""%GIT_MODE%"" == ""ignore"" goto ignoreCmd
if ""%GIT_MODE%"" == ""track"" goto trackCmd

@echo Unknown Cmd-Line-Argument: %GIT_MODE%
@echo Possible Options: ignore or track
goto end

@rem ---------------------------------------------------------------------------

:ignoreCmd
@echo Ignoring Files: %IGNORE_FILE_LIST%
%GIT_IGNORE_CMD% %IGNORE_FILE_LIST%
goto end

@rem ---------------------------------------------------------------------------

:trackCmd
@echo Tracking Files: %IGNORE_FILE_LIST%
%GIT_TRACK_CMD% %IGNORE_FILE_LIST%
goto end

@rem ---------------------------------------------------------------------------

:end
cd "%CURRENT_DIR%"
