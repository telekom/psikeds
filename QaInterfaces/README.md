psiKeds :- ps induced knowledge entity delivery system
------------------------------------------------------

*Copyright (c) 2013-2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG*

This Module contains the Interface of the psiKeds Query Agent, i.e.
the generic [Service-Definition](src/main/java/org/psikeds/queryagent/interfaces/presenter/services/),
[REST-Services](src/main/java/org/psikeds/queryagent/interfaces/presenter/rest/),
[SOAP-Services](src/main/java/org/psikeds/queryagent/interfaces/presenter/soap/)
and the corresponding [Objects exchanged between Client/Browser and Server/Presenter](src/main/java/org/psikeds/queryagent/interfaces/presenter/pojos/).

The compilation result is a JAR-File that will be imported by the [Query Agent](../QueryAgent/).

Please note that currenty the Presentation Layer of the Query Agent is
basically a Proxy for the Resolution Engine. Therefore the data structures
of the Interface are at the moment very similar (*but not identical!*) to
those of [ReInterfaces](../ReInterfaces/). However this will change in future releases.
