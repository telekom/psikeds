KB-META:
01: id w7LampDemoPurePsPv
02: name "w7/kbsr L[A|T][M|P][PH|PY] purely ps/pv based demo database"
04: release "0.1.1"
05: (C) "2014, Karsten Reincke, Deutsche Telekom AG, Darmstadt"

06: licensing 
 "proprietary license: All rights are reserved. Feel free to contact:
  k.reincke@telekom.de"
07: created 2014-04-06
08: modified 2014-04-22 
10: engineer "Karsten Reincke"
11: description
"Re-implementation of the first w7/kbsr LAMP demo database purely based
 on the elements which constitute the structure of an entity: purposes
 and variants"
 
"This version still reproduces the known semantic leak"

PSIKEDS-SENSORS:

PSIKEDS-CONCEPTS:

PSIKEDS-PURPOSES: 

purpose deliverStaticWebPages (
  root true
  label "deliver: Static Web Pages"
  description 
  "[something to] deliver HTML pages to the requesting browser whereas these
  pages are readily prepared and stored on the file system."
)

purpose deliverDynamicWebPages (
  root true
  label "deliver: Dynamic Web Pages"
  description 
  "[something to] deliver dynamically computed HTML pages to the requesting 
   browser and to compose theses pages in the moment when they are requested." 
)

purpose deliverPersistentDynamicWebPages (
  root true
  label "deliver: Persistent Dynamic Web Pages"
  description
  "[something to] deliver dynamically computed HTML pages to the requesting 
   browser and to compose theses pages in the moment when they are requested
   and to computed them on the base of persistently stored database data." 
)

purpose deliverPersistentJavaApplicationsOutput (
  root true
  label "deliver: Output of Persistently Operating Java Applications"
  description
  "[something to] deliver the output of Java applications to a requesting 
   client whereas these Java applications shall be able to use persistently 
   stored database data for dynamically computing its output." 
)

purpose deliverSimpleJavaApplicationsOutput (
  root true
  label "deliver: Output of Simply Operating Java Applications"
  description
  "[something to] deliver the output of Java applications to a requesting 
   client whereas these Java applications shall not be able to incorporate
   persistently stored database data for dynamically computing the output." 
)

purpose deliverPages ( root false label "deliver: Pages" )
purpose execScripts ( label "execute: Scripts" )
purpose linkToDataStore ( label "link to: Data store " )
purpose storeData ( label "store : Data" )
purpose execEeBeans ( label "execute : EE Beans" )
purpose execJavaApps ( label "execute : Java Applications" )

PSIKEDS-VARIANTS:

variant WeSeScriSoSt (
  label "Apache-Scripting-Software-Stack"
  description 
  "An Apache based web server software stack enriched by particular scripting 
   modules for delivering dynamically composed web pages."
)

variant WeSeDbScriSoSt (
  label "Apache-Database-Scripting-Software-Stack"
  description 
  "An Apache based web server software stack being able to use database data
   and being enriched by particular scripting modules for delivering 
   dynamically composed web pages"
)

variant JaEjbDbSoSt (
  label "Java-Ejb-Database-Software-Stack"
  description 
  "A java application server software stack being able to use database data
   and to operate on java beans for offering web services etc."
)

variant JaWapDbSoSt (
  label "Java-Web-App-Database-Software-Stack"
  description 
  "A java application server software stacke being able to compute its output 
   on the base of database data."
)

variant Apache ( 
  label "Apache"
  description "The famous Apache Webserver"
)

variant ModPhp ( label "Apache-PHP-Module")
variant ModPython ( label "Apache-Python-Module")

variant MysqlPhpConnector ( label "Mysql-PHP-Connector")
variant MysqlPythonConnector ( label "Mysql-Python-Connector")
variant MysqlJavaConnector ( label "Mysql-JDBC-Connector")

variant PostgresPhpConnector ( label "Postgres-PHP-Connector")
variant PostgresPythonConnector ( label "Postgres-Python-Connector")
variant PostgresJavaConnector ( label "Postgres-JDBC-Connector")

variant Postgres ( label "Postgres")
variant Mysql ( label "Mysql")

variant Jboss ( label "JBoss")
variant Tomcat ( label "Tomcat")


PSIKEDS-IS-FULFILLED-BY-STATEMENTS:

purpose.system deliverStaticWebPages (
  isFulfilledBy { *pv> Apache } 
)

purpose.system deliverDynamicWebPages ( 
  isFulfilledBy {  *pv> WeSeScriSoSt | *pv> WeSeDbScriSoSt }
)

purpose.system deliverPersistentDynamicWebPages ( 
  isFulfilledBy {  *pv> JaEjbDbSoSt | *pv>  JaWapDbSoSt }
)

purpose.system deliverSimpleJavaApplicationsOutput ( 
  isFulfilledBy { *pv> Tomcat }
)

purpose.system deliverPages ( 
  isFulfilledBy { *pv> Apache }
)

purpose.system execScripts ( 
  isFulfilledBy { *pv>  ModPhp | *pv> ModPython }
)

purpose.system linkToDataStore ( 
  isFulfilledBy {
    *pv> MysqlPhpConnector
  | *pv> MysqlPythonConnector
  | *pv> MysqlJavaConnector
  | *pv> PostgresPhpConnector
  | *pv> PostgresPythonConnector
  | *pv> PostgresJavaConnector
  }
)

purpose.system storeData ( 
  isFulfilledBy { *pv> Mysql | *pv> Postgres }
)

purpose.system execEeBeans ( 
  isFulfilledBy { *pv>  Jboss }
)

purpose.system execJavaApps ( 
  isFulfilledBy { *pv> Tomcat }
)

PSIKEDS-IS-CONSTITUTED-BY-STATEMENTS:

purpose.variant WeSeScriSoSt (
  isConstitutedBy {
     < 1 of *ps>  deliverPages >
   & < 1 of *ps>  execScripts > }
)

purpose.variant WeSeDbScriSoSt ( 
  isConstitutedBy {
    < 1 of *ps> deliverPages >
  & < 1 of *ps> execScripts >
  & < 1 of *ps> linkToDataStore >
  & < 1 of *ps> storeData > 
  }
)

purpose.variant JaEjbDbSoSt ( 
  isConstitutedBy {
    < 1 of *ps> linkToDataStore >
  & < 1 of *ps> storeData >
  & < 1 of *ps> execEeBeans > 
  }
)

purpose.variant JaWapDbSoSt ( 
  isConstitutedBy {
    < 1 of *ps> linkToDataStore >
  & < 1 of *ps> storeData >
  & < 1 of *ps> execJavaApps > }
)

PSIKEDS-LOGICAL-INFERENCE-ELEMENTS:

event.var WeSeDbWithMysqlPhpConnex (
  label "DB based web server with mysql-php-connector" 
  context [ *pv> WeSeDbScriSoSt *ps> linkToDataStore ]
  fact *pv> MysqlPhpConnector
)

event.var WeSeDbWithMysqlPythonConnex (
  label "DB based web server with mysql-phython-connector" 
  context [ *pv> WeSeDbScriSoSt *ps> linkToDataStore ]
  fact *pv> MysqlPythonConnector
)

event.var WeSeDbWithPostgresPhpConnex (
  label "DB based web server with postgres-php-connector" 
  context [ *pv> WeSeDbScriSoSt *ps> linkToDataStore ]
  fact *pv> PostgresPhpConnector
)

event.var WeSeDbWithPostgresPythonConnex (
  label "DB based web server with postgres-phython-connector" 
  context [ *pv> WeSeDbScriSoSt *ps> linkToDataStore ]
  fact *pv> PostgresPythonConnector
)

event.var WeSeDbWithModPython (
 label  "DB based web server with mod-python"
 context [ *pv> WeSeDbScriSoSt *ps> execScripts ]
 fact *pv> ModPython
)

event.var WeSeDbWithModPhp (
 label  "DB based web server with mod-php"
 context [ *pv> WeSeDbScriSoSt *ps> execScripts ]
 fact *pv> ModPhp
)

event.var WeSeDbWithMysql (
 label  "DB based web server with mysql"
 context [ *pv> WeSeDbScriSoSt *ps>  storeData ]
 fact *pv> Mysql
)

event.var WeSeDbWithPostgres (
 label  "DB based web server with postgres"
 context [ *pv> WeSeDbScriSoSt *ps> storeData ]
 fact *pv> Postgres
)

/* --- */

event.var JaEjbDbWithMysqlJavaConnector (
  label "Java Ejb Database Software Stack mit Mysql Java Connector"
  context [ *pv> JaEjbDbSoSt *ps> linkToDataStore ]
  fact *pv> MysqlJavaConnector
)

event.var JaEjbDbWithPostgresJavaConnector (
  label "Java Ejb Database Software Stack mit Postgres Java Connector"
  context [ *pv> JaEjbDbSoSt *ps> linkToDataStore ]
  fact *pv> PostgresJavaConnector
)

event.var JaEjbDbWithMysql (
  label "Java Ejb Database Software Stack mit Mysql DBMS"
  context [ *pv> JaEjbDbSoSt *ps>  storeData ]
  fact *pv> Mysql
)

event.var JaEjbDbWithPostgres (
  label "Java Ejb Database Software Stack mit Postgres DBMS"
  context [ *pv> JaEjbDbSoSt *ps>  storeData ]
  fact *pv> Postgres
  )



event.var JaWapDbWithMysqlJavaConnector (
  label "Java Web App Database Software Stack mit Mysql Java Connector"
  context [ *pv> JaWapDbSoSt *ps>  linkToDataStore ]
  fact *pv> MysqlJavaConnector
)

event.var JaWapDbWithPostgresJavaConnector (
  label "Java Web App Database Software Stack mit Postgres Java Connector"
  context [ *pv> JaWapDbSoSt *ps>  linkToDataStore ]
  fact *pv> PostgresJavaConnector
  )

event.var JaWapDbWithMysql (
  label "Java Web App Database Software Stack mit Mysql "
  context [ *pv> JaWapDbSoSt *ps>  storeData ]
  fact *pv> Mysql
)

event.var JaWapDbWithPostgres (
  label "Java Web App Database Software Stack mit Postgres "
  context [ *pv> JaWapDbSoSt *ps> storeData ]
  fact *pv> Postgres
)
  
  
PSIKEDS-RELATIONAL-INFERENCE-ELEMENTS:

PSIKEDS-INFERENCE-CONDITIONS:

logic.rule IFmysqlPhpConnexTHENModPhp (
  label "IF mysql-php-connector THEN mod-php" 
  description "R-1.1"
  nexus *pv> WeSeDbScriSoSt
  induces [{ *ev> WeSeDbWithMysqlPhpConnex } 
          => *ev> WeSeDbWithModPhp ]
)

logic.rule IFmysqlPythonConnexTHENModPython (
  label "IF mysql-python-connector THEN mod-python" 
  description "R-1.2"
  nexus *pv> WeSeDbScriSoSt
  induces [{ *ev>  WeSeDbWithMysqlPythonConnex } 
          => *ev> WeSeDbWithModPython ]
)

logic.rule IFpostgresPhpConnexTHENModPhp  (
  label "IF postgres-php-connector THEN mod-php" 
  description "R-1.3"
  nexus *pv> WeSeDbScriSoSt  
  induces [{ *ev>  WeSeDbWithPostgresPhpConnex } 
          => *ev> WeSeDbWithModPhp ]
  )

logic.rule IFpostgresPythonConnexTHENModPython  (
  label "IF postgres-python-connector THEN mod-python" 
  description "R-1.4"
  nexus *pv> WeSeDbScriSoSt  
  induces [{ *ev>  WeSeDbWithPostgresPythonConnex } 
          => *ev> WeSeDbWithModPython ]
)

logic.rule IFmysqlPhpConnexTHENmysqlDb (
  label "IF mysql-php-connector THEN mysql dbms" 
  description "R-2.1"
  nexus *pv> WeSeDbScriSoSt
  induces [{ *ev>  WeSeDbWithMysqlPhpConnex } 
          => *ev> WeSeDbWithMysql]
)

logic.rule IFmysqlPythonConnexTHENmysqlDb  (
  label "IF mysql-python-connector THEN mysql dbms" 
  description "R-2.2"
  nexus *pv> WeSeDbScriSoSt
  induces [{ *ev> WeSeDbWithMysqlPythonConnex } 
          => *ev> WeSeDbWithMysql]
)

logic.rule IFpostgresPhpConnexTHENmysqlDb  (
  label "IF postgres-php-connector THEN postgres dbms" 
  description "R-2.3"
  nexus *pv> WeSeDbScriSoSt
  induces [{ *ev> WeSeDbWithPostgresPhpConnex } 
          => *ev> WeSeDbWithPostgres]
)

logic.rule IFpostgresPythonConnexTHENmysqlDb (
  label "IF postgres-python-connector THEN postgres dbms" 
  description "R-2.4"
  nexus *pv> WeSeDbScriSoSt
  induces [{ *ev> WeSeDbWithPostgresPythonConnex } 
          => *ev> WeSeDbWithPostgres ]
)

logic.rule IFJaEjbDbWithMysqlJavaConnectorTHENJaEjbDbWithMysql  (
  label "Mysql Connector in context of JavaEjbDbSoST requires Mysql DBMS" 
  description "R-3.1"
  nexus *pv> JaEjbDbSoSt
  induces [{ *ev> JaEjbDbWithMysqlJavaConnector } 
          => *ev> JaEjbDbWithMysql ]
)

logic.rule IFJaEjbDbWithPostgresJavaConnectorTHENJaEjbDbWithPostgres (
  label "Postgress Connector in context of JavaEjbDbSoST requires Postgres DBMS" 
  description "R-3.2"
  nexus *pv> JaEjbDbSoSt
  induces [{ *ev> JaEjbDbWithPostgresJavaConnector } 
          => *ev> JaEjbDbWithPostgres]
)


logic.rule IFJaWapDbWithMysqlJavaConnectorTHENJaWapDbWithMysql (
  label "Mysql Connector in context of JavaWapDbSoST requires Mysql DBMS" 
  description "R-3.3"
  nexus *pv> JaWapDbSoSt
  induces [{ *ev> JaWapDbWithMysqlJavaConnector } 
          => *ev> JaWapDbWithMysql]
)

/*  */
logic.rule IFJaWapDbWithPostgresJavaConnectorTHENJaWapDbWithPostgres (
  label "Postgress Connector in context of JavaWapDbSoST requires Postgres DBMS" 
  description "R-3.4" 
  nexus *pv> JaWapDbSoSt
  induces [{ *ev> JaWapDbWithPostgresJavaConnector } 
          => *ev> JaWapDbWithPostgres]
)

