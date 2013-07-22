psiKeds :- ps induced knowledge entity delivery system
------------------------------------------------------

*Copyright (c) 2013-2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG*

CONFIG
------
The components of psiKeds (i.e. Resolution Engine and Query Agent) require some
basic configuration that must be provided within an external configuration
directory.

The location of the psiKeds-configuration-directory can be specified by using
<<<<<<< HEAD
the Java-VM-parameter *"-Dorg.psikeds.config.dir"* or is otherwise by default
*"${user.home}/psikeds/"*
=======
the Java-VM-parameter *-Dorg.psikeds.config.dir* or is otherwise by default
*${user.home}/psikeds/*
>>>>>>> ADD: formatting and linking

This configuration directory must at least contain *queryagent.properties*
(for running the Query Agent) and *resolutionengine.properties* (for running
the Resolution Engine). These properties-files must define all the properties
required by the spring-configuration of each application.

Additionally the configuration-directory can optionally contain any other
spring-application-context-file that will then overwrite the corresponding
one packaged within the WAR-file.

All spring configuration files can be found here:
<<<<<<< HEAD
- [ResolutionEngine/src/main/webapp/WEB-INF/config](../ResolutionEngine/src/main/webapp/WEB-INF/config/)
- [QueryAgent/src/main/webapp/WEB-INF/spring-config](../QueryAgent/src/main/webapp/WEB-INF/spring-config/)

This directory is a simple example of a psiKeds-configuration-directory.
It is used for building and testing the psiKeds Project and also contains
a [spring configuration file](knowledgebase-datasource-context.xml) showing
how to replace the default [Knowledge-Base](../KnowledgeBase/src/main/resources/kb.xml)
with a custom one.

For getting started you can simply copy the contents of this directory to
the directory *"${user.home}/psikeds/"*
=======
- [ResolutionEngine\src\main\webapp\WEB-INF\config](ResolutionEngine\src\main\webapp\WEB-INF\config)
- [QueryAgent\src\main\webapp\WEB-INF\spring-config](QueryAgent\src\main\webapp\WEB-INF\spring-config)

This directory is a simple example of a psiKeds-configuration-directory.
For getting started you can simply copy the contents of this directory to
the directory *${user.home}/psikeds/*
>>>>>>> ADD: formatting and linking
