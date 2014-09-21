psiKeds XText knowledgebase editor:- 
------------------------------------------------------

*Copyright (c) 2014 Karsten Reincke, Deutsche Telekom AG*

README
------
The ps induced knowledge entity delivery system shall enable knowledge
engineers to represent the domain specific knowledge in a knowledge base by
following a domain independent approach; and it shall enable knowledge users
to request the ps induced knowledge entity delivery system for inferred
knowledge entities as the descriptions of the domain objects.

The resolution engine of psikeds reads xml knowledgebases. These files must 
respect the psikeds/KnowledgeBase/src/main/resources/psikeds.xsd. One can 
validate such a knowledgbase by using the (libxml based) command
xmllint --noout --schema psikeds.xsd default.knowledgebase.xml

So, generally spoken, knowledge base editors are independent units
which are not derivated works of the resolution engine (which is licensed
under the AGPL)

This directory contains all material to install / generate / and test
the Xtext based psikeds editor (which thereforte is licensed under the
Eclipse public license 1.0)

LICENSING
---------
see file [000-LICENSING](000-LICENSING) in the top project directory

ORGANIZATION
------------
see file [000-CODEX](000-CODEX) in the top project directory


DOCUMENTATION AND FURTHER INFORMATION
-------------------------------------
see documents and guides in subfolder [doc](doc/)

/* the use of the psikeds Xtext Knowledge Base Editor still must be documented */
