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


TOMCAT_START=/etc/init.d/tomcat7

# ---------------------------------------------------------------------------
# Do not modify below this line ...
# ---------------------------------------------------------------------------

if [ "$CATALINA_BASE" == ""  ]; then
  echo -n "Variable CATALINA_BASE undefined: using value ";
  CATALINA_BASE=/var/lib/tomcat7
  echo "<$CATALINA_BASE>";
fi

if [ ! -d "$CATALINA_BASE" ]; then
  echo "$CATALINA_BASE not found: stopping the deployment";
  exit;
fi

WEBAPPS_DIR=${CATALINA_BASE}/webapps
if [ ! -d "$WEBAPPS_DIR" ]; then
  echo "$WEBAPPS_DIR not found: stopping the deployment";
  exit;
fi

echo "shutting down the tomcat-server"
sudo $TOMCAT_START stop

echo "deleting the existing application directories"
sudo rm -rf $WEBAPPS_DIR/${RE_NAME}
sudo rm -rf $WEBAPPS_DIR/${QA_NAME}

echo "copying the new war files into $WEBAPPS_DIR"
cp $QA_FULL_PATH $RE_FULL_PATH $WEBAPPS_DIR

echo "restarting the tomcate"
sudo $TOMCAT_START start

echo "Attention! Do not forget to replace the content"
echo "of your psikeds config directory by the content"
echo "of the ${SCRIPT_DIR}/config"
echo "in case of any irritation"

exit

