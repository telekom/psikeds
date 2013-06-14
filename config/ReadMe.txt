The contents of this directory is a sample of the psiKeds-config-directory.

It must contain at least queryagent.properties or resolutionengine.properties and
can additionally contain any other spring-application-context-files that will then
overwrite the one packaged within the WAR-file.

The location of the psiKeds-config-directory can be specified using the JVM-parameter
-Dorg.psikeds.config.dir or is ${user.home}/psikeds by default.
