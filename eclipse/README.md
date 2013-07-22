psiKeds :- ps induced knowledge entity delivery system
------------------------------------------------------

*Copyright (c) 2013-2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG*

ECLIPSE INTEGRATION
-------------------
psiKeds uses Maven for building and does not require any special IDE. However,
if you would like to use Eclipse IDE for developing and building psiKeds, then
this is a short description of how to achieve that:

- 0. Install software packages for Eclipse, Git, Maven and JDK.

- 1. Checkout psiKeds from GitHub using git.

- 2. Run Eclipse IDE.

- 3. Start Import-Wizard of Eclipse:
     *Menu --> File --> Import --> Existing Maven Projects*

     If you do not have an option called "Existing Maven Projects", then you
	 need to install "Maven Integration or Eclipse" (M2E-Plugin) first:
     *Menu --> Help --> Install new Software*

- 4. Select Toplevel directory of psiKeds as RootDirectory for Import, e.g.
     *~/github/psikeds/*

- 5. Select psikeds-Project and all Sub-Projects/Modules

- 6. Left click on Finish
     ==> After a short period of time you should see each psiKeds-Component
         as a separate Eclipse-Project

- 7. Right click on the Root-Project "psikeds" and select "Run as" --> "Maven install"
     ==> A full build of all psiKeds-Components will be performed.

Troubleshooting
---------------
If Eclipse should display compiler-erros, please verify that in the settings
for project "KnowledgeBase" the Source-Folder "target/generated-sources/jaxb"
is defined. This directory contains JAXB-Classes generated from the KB-XSD.

NOTE 1
------
The usage of M2E is recomended while simply executing "mvn eclipse:eclipse"
is strongly discouraged.

While running this command also creates Eclipse-Project-Files, it does however
not support the M2E-Plugin and its capability to automatically derive the
Eclipse-Classpath from the Maven-POM. Therefore a static references (pointing to
your local Maven-Repository) is added to your .classpath for each dependency
existing at this moment.

Later these Classpath-Entries have to be updated manually whenever a Maven-
Dependency changes. Otherwise the Eclipse-Build will either break or (even
worse) work but not not correspond to the Maven-Build!

NOTE 2
------
For those, who are very familiar with Eclipse and who prefer to create/optimize
the Eclipse-Project-Files manually, there are Templates included in this
directory.

1. Copy the Files ".project", ".classpath" and the directory ".settings" with
   all its contents to each psiKeds-Component-Subdirectory that includes a
   Maven pom.xml

2. Edit each .project and specify the Project's Name from Maven-POM as Name
   for the Eclipse-Project.

3. Within Eclipse select "Import Existing Projects into Workspace".

DOCUMENTATION
-------------
For further details regarding Maven-Eclipse-Integration see:
- [http://maven.apache.org/eclipse-plugin.html](http://maven.apache.org/eclipse-plugin.html)
