psiKeds :- ps induced knowledge entity delivery system
------------------------------------------------------

*Copyright (c) 2013-2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG*

This Module contains the Interface of the psiKeds Resolution Engine, i.e.
the generic [Service-Definition](src/main/java/org/psikeds/resolutionengine/interfaces/services/),
[REST-Services](src/main/java/org/psikeds/resolutionengine/interfaces/rest/),
[SOAP-Services](src/main/java/org/psikeds/resolutionengine/interfaces/soap/)
and the corresponding [Objects exchanged between Client and Server](src/main/java/org/psikeds/resolutionengine/interfaces/pojos/).

The compilation result is a JAR-File that will be imported by both
[Client](../QueryAgent/)
and [Server](../ResolutionEngine/).
