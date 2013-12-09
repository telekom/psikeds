psiKeds :- ps induced knowledge entity delivery system
------------------------------------------------------

*Copyright (c) 2013-2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG*

DEPLOYING ON TOMCAT AND UBUNTU
------------------------------

For deploying the war-files of the psiKeds-resolulion-engine and the
psiKeds-query-agent on the applications server Tomcat in an Ubuntu 12.04
do the following steps:

- install the packages tomcat7-user, tomcat7, libapache2-mod-jk, tomcat7-admin

- generate any psikeds-configuration-directory being readable by the user tomcat7
  (for example /home/reincke/psikeds)

- edit the file /etc/default/tomcat7 and
  - expand the JAVA_OPTS definition by a specification to the generated 
    psikeds-configuration directory following the pattern 
    -Dorg.psikeds.config.dir=$PATH_TO_PSIKEDS_CONFIG-DIR
  - replace the existing parameter-Xmx128m by the three parameters
     -Xms512m -Xmx2048m -XX:MaxPermSize=128M
  (example:
  JAVA_OPTS="-Djava.awt.headless=true -Xms512m -Xmx2048m -XX:MaxPermSize=128M -XX:+UseConcMarkSweepGC -Dorg.psikeds.config.dir=/home/reincke/psikeds"
  )
  
- shut down the running tomcat instance by using the command
  bash> sudo /etc/init.d/tomcat7 stop

- add your deployer account to the group tomcat7

- copy the complete content of the config direcgtory
  delivered by the project into your own recently
  generated config directory (e.g /home/reincke/psikeds)

- in the psikeds git clone working directory call 
  bash> mvn clean install 

- [Note: tomcat needs CATALINA_HOME, CATALINA_BASE, and
   JAVA_HOME as environment variables. In UBUNTU 12.04,
  they are defined by the tomcat starting script in
  /etc/init.d/tomcat7 as CATALINA_HOME=/usr/share/tomcat7
  and CATALINA_BASE=/var/lib/tomcat7]
  
- copy the war files QueryAgent/target/queryagent.war and
  ResolutionEngine/target/resolutionengine.war to $CATALINA_BASE/webapps
  which should be resolvable as /var/lib/tomcat7/webapps
  (In case of problems delete all existing psikeds war-files and directories)
   
- restart the tomcat instance by using the command
  bash> sudo /etc/init.d/tomcat7 start

- call http://localhost:8080/ in your browser on the install machine.
  You should now see the tomcat-IT-Works-site

- call http://localhost:8080/resolutionengine in your browser on the
  install machine. You should now see the resolution engine
  overview site

- call http://localhost:8080/queryagent in your browser on the
  install machine. You should now see the default query frontend
  connected to the default knowledge base. Try to explore the
  knowledge base. You should be able to request for an answer
  to the standard query. And you should get a meaningful
  answer.

- Bingo.
