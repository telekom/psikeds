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
@rem This script performs a full built of psiKeds
@rem
@rem ---------------------------------------------------------------------------
if "%OS%" == "Windows_NT" setlocal

set "SCRIPT_DIR=%~dp0"
set "CURRENT_DIR=%cd%"

cd "%SCRIPT_DIR%"
mvn clean install
cd "%CURRENT_DIR%"
