KB-META:
id w7LampDemoPurePsPvSemLeakFixed
name "w7/kbsr L[A|T][M|P][PH|PY] purely ps/pv based / semantic leak fixed"
release "0.1.1"
(C) "2014, Karsten Reincke, Deutsche Telekom AG, Darmstadt"

licensing 
 "proprietary license: All rights are reserved. Feel free to contact:
  k.reincke@telekom.de"
created 2014-04-06
modified 2014-04-16 
engineer "Karsten Reincke"
description 
"Re-implementation of the first w7/kbsr LAMP demo database purely based
 on the elements which constitute the structure of an entity: purposes
 and variants"
 
"This version demonstrates the fix of the semantic leak by using NOT events"

PSIKEDS-SENSORS:

PSIKEDS-CONCEPTS:

PSIKEDS-PURPOSES: 

purpose deliverStaticWebPages {
  root true
  label "deliver: Static Web Pages"
  description 
  "[something to] deliver HTML pages to the requesting browser whereas these
  pages are readily prepared and stored on the file system."
}

purpose deliverDynamicWebPages {
  root true
  label "deliver: Dynamic Web Pages"
  description 
  "[something to] deliver dynamically computed HTML pages to the requesting 
   browser and to compose theses pages in the moment when they are requested." 
}

purpose deliverPersistentDynamicWebPages {
  root true
  label "deliver: Persistent Dynamic Web Pages"
  description
  "[something to] deliver dynamically computed HTML pages to the requesting 
   browser and to compose theses pages in the moment when they are requested
   and to computed them on the base of persistently stored database data." 
}

purpose deliverPersistentJavaApplicationsOutput {
  root true
  label "deliver: Output of Persistently Operating Java Applications"
  description
  "[something to] deliver the output of Java applications to a requesting 
   client whereas these Java applications shall be able to use persistently 
   stored database data for dynamically computing its output." 
}

purpose deliverSimpleJavaApplicationsOutput {
  root true
  label "deliver: Output of Simply Operating Java Applications"
  description
  "[something to] deliver the output of Java applications to a requesting 
   client whereas these Java applications shall not be able to incorporate
   persistently stored database data for dynamically computing the output." 
}

purpose deliverPages { root false label "deliver: Pages" }
purpose execScripts { label "execute: Scripts" }
purpose linkToDataStore { label "link to: Data store " }
purpose storeData { label "store : Data" }
purpose execEeBeans { label "execute : EE Beans" }
purpose execJavaApps { label "execute : Java Applications" }

PSIKEDS-VARIANTS:

variant WeSeScriSoSt {
  label "Apache-Scripting-Software-Stack"
  description 
  "An Apache based web server software stack enriched by particular scripting 
   modules for delivering dynamically composed web pages."
}

variant WeSeDbScriSoSt {
  label "Apache-Database-Scripting-Software-Stack"
  description 
  "An Apache based web server software stack being able to use database data
   and being enriched by particular scripting modules for delivering 
   dynamically composed web pages"
}

variant JaEjbDbSoSt {
  label "Java-Ejb-Database-Software-Stack"
  description 
  "A java application server software stack being able to use database data
   and to operate on java beans for offering web services etc."
}

variant JaWapDbSoSt {
  label "Java-Web-App-Database-Software-Stack"
  description 
  "A java application server software stacke being able to compute its output 
   on the base of database data."
}

variant Apache { 
  label "Apache"
  description "The famous Apache Webserver"
}

variant ModPhp { label "Apache-PHP-Module"}
variant ModPython { label "Apache-Python-Module"}

variant MysqlPhpConnector { label "Mysql-PHP-Connector"}
variant MysqlPythonConnector { label "Mysql-Python-Connector"}
variant MysqlJavaConnector { label "Mysql-JDBC-Connector"}

variant PostgresPhpConnector { label "Postgres-PHP-Connector"}
variant PostgresPythonConnector { label "Postgres-Python-Connector"}
variant PostgresJavaConnector { label "Postgres-JDBC-Connector"}

variant Postgres { label "Postgres"}
variant Mysql { label "Mysql"}

variant Jboss { label "JBossr"}
variant Tomcat { label "Tomcat"}


PSIKEDS-IS-FULFILLED-BY-STATEMENTS:

purpose.system deliverStaticWebPages isFulfilledBy { Apache }

purpose.system deliverDynamicWebPages isFulfilledBy { 
  WeSeScriSoSt 
  WeSeDbScriSoSt
  }

purpose.system deliverPersistentDynamicWebPages isFulfilledBy {
  JaEjbDbSoSt
  JaWapDbSoSt
}

purpose.system deliverSimpleJavaApplicationsOutput isFulfilledBy {
  Tomcat
}

purpose.system deliverPages isFulfilledBy { Apache }

purpose.system execScripts isFulfilledBy { ModPhp ModPython }

purpose.system linkToDataStore isFulfilledBy {
  MysqlPhpConnector
  MysqlPythonConnector
  MysqlJavaConnector
  PostgresPhpConnector
  PostgresPythonConnector
  PostgresJavaConnector
}

purpose.system storeData isFulfilledBy { Mysql Postgres }

purpose.system execEeBeans isFulfilledBy { Jboss }

purpose.system execJavaApps isFulfilledBy { Tomcat }

PSIKEDS-IS-CONSTITUTED-BY-STATEMENTS:
purpose.variant WeSeScriSoSt isConstitutedBy {
  < 1 instance(s)-of deliverPages >
  < 1 instance(s)-of execScripts >
}

purpose.variant WeSeDbScriSoSt isConstitutedBy {
  < 1 instance(s)-of deliverPages >
  < 1 instance(s)-of execScripts >
  < 1 instance(s)-of linkToDataStore >
  < 1 instance(s)-of storeData >
}

purpose.variant JaEjbDbSoSt isConstitutedBy {
  < 1 instance(s)-of linkToDataStore >
  < 1 instance(s)-of storeData >
  < 1 instance(s)-of execEeBeans >
}

purpose.variant JaWapDbSoSt isConstitutedBy {
  < 1 instance(s)-of linkToDataStore >
  < 1 instance(s)-of storeData >
  < 1 instance(s)-of execJavaApps >
}

PSIKEDS-LOGICAL-INFERENCE-ELEMENTS:

event.var WeSeDbWithMysqlPhpConnex [
  label "DB based web server with mysql-php-connector" 
  context < WeSeDbScriSoSt linkToDataStore >
  fact *variant> MysqlPhpConnector
]

event.var WeSeDbWithMysqlPythonConnex [
  label "DB based web server with mysql-phython-connector" 
  context < WeSeDbScriSoSt linkToDataStore >
  fact *variant> MysqlPythonConnector
]

event.var WeSeDbWithPostgresPhpConnex [
  label "DB based web server with postgres-php-connector" 
  context < WeSeDbScriSoSt linkToDataStore >
  fact *variant> PostgresPhpConnector
]

event.var WeSeDbWithPostgresPythonConnex [
  label "DB based web server with postgres-phython-connector" 
  context < WeSeDbScriSoSt linkToDataStore >
  fact *variant> PostgresPythonConnector
]

event.var WeSeDbWithModPython [
 label  "DB based web server with mod-python"
 context < WeSeDbScriSoSt  execScripts >
 fact *variant> ModPython
]

event.var WeSeDbWithModPhp [
 label  "DB based web server with mod-php"
 context < WeSeDbScriSoSt  execScripts >
 fact *variant> ModPhp
]

event.var WeSeDbWithMysql [
 label  "DB based web server with mysql"
 context < WeSeDbScriSoSt  storeData >
 fact *variant> Mysql
]

event.var WeSeDbWithPostgres [
 label  "DB based web server with postgres"
 context < WeSeDbScriSoSt  storeData >
 fact *variant> Postgres
]

/* --- */

event.var JaEjbDbWithMysqlJavaConnector [
  label "Java Ejb Database Software Stack mit Mysql Java Connector"
  context < JaEjbDbSoSt linkToDataStore >
  fact *variant> MysqlJavaConnector
  ]

event.var JaEjbDbWithPostgresJavaConnector [
  label "Java Ejb Database Software Stack mit Postgres Java Connector"
  context < JaEjbDbSoSt linkToDataStore >
  fact *variant> PostgresJavaConnector
  ]

event.var JaEjbDbWithMysql [
  label "Java Ejb Database Software Stack mit Mysql DBMS"
  context < JaEjbDbSoSt storeData >
  fact *variant> Mysql
  ]

event.var JaEjbDbWithPostgres [
  label "Java Ejb Database Software Stack mit Postgres DBMS"
  context < JaEjbDbSoSt storeData >
  fact *variant> Postgres
  ]



event.var JaWapDbWithMysqlJavaConnector [
  label "Java Web App Database Software Stack mit Mysql Java Connector"
  context < JaWapDbSoSt linkToDataStore >
  fact *variant> MysqlJavaConnector
  ]

event.var JaWapDbWithPostgresJavaConnector [
  label "Java Web App Database Software Stack mit Postgres Java Connector"
  context < JaWapDbSoSt linkToDataStore >
  fact *variant> PostgresJavaConnector
  ]

event.var JaWapDbWithMysql [
  label "Java Web App Database Software Stack mit Mysql "
  context < JaWapDbSoSt storeData >
  fact *variant> Mysql
  ]

event.var JaWapDbWithPostgres [
  label "Java Web App Database Software Stack mit Postgres "
  context < JaWapDbSoSt storeData >
  fact *variant> Postgres
  ]
  
/*
 * These are these events used in rules to exclude
 * that the java-database-connectors are offered as solution in the
 * context of deriving a web server scripting with db software stack 
 * which by definition is not able to handle embedded java code (ejb)
 */ 

event.enforcing WeSeDbScriSoStAsTrigger [
  label "The Context of the Web Server DB Scripting Software Stack"
  description "TRUE whenever you are deriving this stack"
  context=fact
  *variant> WeSeDbScriSoSt
]  
  
event.var WeSeDbScrSoStWithoutMysqlJavaConnex [
  label "TRUE IF MysqlJavaConnector not used as Db connector 
   in the context of aggregating a WeSeDbScriSoSt"
  context < WeSeDbScriSoSt linkToDataStore>
  NOT
  fact *variant> MysqlJavaConnector
]  
  
event.var WeSeDbScrSoStWithoutPostgresJavaConnex [
  label "TRUE IF PostgresJavaConnector not used as Db connector 
   in the context of aggregating a WeSeDbScriSoSt"
  context < WeSeDbScriSoSt linkToDataStore>
  NOT
  fact *variant> PostgresJavaConnector
]    
  
PSIKEDS-RELATIONAL-INFERENCE-ELEMENTS:

PSIKEDS-INFERENCE-CONDITIONS:

logic.rule IFmysqlPhpConnexTHENModPhp 
label "IF mysql-php-connector THEN mod-php" 
description "R-1.1"
means ({ WeSeDbWithMysqlPhpConnex } -> WeSeDbWithModPhp)

logic.rule IFmysqlPythonConnexTHENModPython 
label "IF mysql-python-connector THEN mod-python" 
description "R-1.2"
means ({ WeSeDbWithMysqlPythonConnex } -> WeSeDbWithModPython)

logic.rule IFpostgresPhpConnexTHENModPhp 
label "IF postgres-php-connector THEN mod-php" 
description "R-1.3"
means ({ WeSeDbWithPostgresPhpConnex } -> WeSeDbWithModPhp)

logic.rule IFpostgresPythonConnexTHENModPython 
label "IF postgres-python-connector THEN mod-python" 
description "R-1.4"
means ({ WeSeDbWithPostgresPythonConnex } -> WeSeDbWithModPython)

logic.rule IFmysqlPhpConnexTHENmysqlDb 
label "IF mysql-php-connector THEN mysql dbms" 
description "R-2.1"
means ({ WeSeDbWithMysqlPhpConnex } -> WeSeDbWithMysql)

logic.rule IFmysqlPythonConnexTHENmysqlDb 
label "IF mysql-python-connector THEN mysql dbms" 
description "R-2.2"
means ({ WeSeDbWithMysqlPythonConnex } -> WeSeDbWithMysql)

logic.rule IFpostgresPhpConnexTHENmysqlDb 
label "IF postgres-php-connector THEN postgres dbms" 
description "R-2.3"
means ({ WeSeDbWithPostgresPhpConnex } -> WeSeDbWithPostgres)

logic.rule IFpostgresPythonConnexTHENmysqlDb 
label "IF postgres-python-connector THEN postgres dbms" 
description "R-2.4"
means ({ WeSeDbWithPostgresPythonConnex } -> WeSeDbWithPostgres)

logic.rule IFJaEjbDbWithMysqlJavaConnectorTHENJaEjbDbWithMysql
label "Mysql Connector in context of JavaEjbDbSoST requires Mysql DBMS" 
description "R-3.1"
means ({ JaEjbDbWithMysqlJavaConnector } -> JaEjbDbWithMysql)

logic.rule IFJaEjbDbWithPostgresJavaConnectorTHENJaEjbDbWithPostgres
label "Postgress Connector in context of JavaEjbDbSoST requires Postgres DBMS" 
description "R-3.2"
means ({ JaEjbDbWithPostgresJavaConnector } -> JaEjbDbWithPostgres)

logic.rule IFJaWapDbWithPostgresJavaConnectorTHENJaWapDbWithPostgres
label "Mysql Connector in context of JavaWapDbSoST requires Mysql DBMS" 
description "R-3.3"
means ({ JaWapDbWithMysqlJavaConnector } -> JaWapDbWithMysql)

logic.rule IFJaWapDbWithPostgresJavaConnectorTHENJaWapDbWithPostgres
label "Postgress Connector in context of JavaWapDbSoST requires Postgres DBMS" 
description "R-3.4"
means ({ JaWapDbWithPostgresJavaConnector } -> JaWapDbWithPostgres)

/* Rules for fixing the semantic leak */
logic.enforcer WeSeScriSoStWithoutMysqlJavaConnex
label "Exclud the use of Mysql Java Connector in the context 
of aggregating a WebServer Scripting with DB Software Stack"
means ( WeSeDbScriSoStAsTrigger -> WeSeDbScrSoStWithoutMysqlJavaConnex)

logic.enforcer WeSeScriSoStWithoutPostgresJavaConnex
label "Exclud the use of Postgres Java Connector in the context 
of aggregating a WebServer Scripting with DB Software Stack"
means ( WeSeDbScriSoStAsTrigger -> WeSeDbScrSoStWithoutPostgresJavaConnex)


