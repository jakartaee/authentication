///////////////////////////////////////////////////////////////////////
NOTE TO WRITERS:
The following sections should be customized for the technology.
This text was originally from the Jakarta RESTful Web Services TCK.  Most references
to Jakarta RESTful Web Services have been parameterized to serve as a simple starting
point for customization.  There are still many details that will
need to be changed or removed.  The major sections 4.1, 4.2, and
4.3 should be preserved.  If their titles are changed, the links
at the top of config.adoc will need to be changed as well as well
as toc.adoc.
///////////////////////////////////////////////////////////////////////

[[GBFVU]][[configuring-your-environment-to-run-the-tck-against-the-reference-implementation]]

[NOTE]
=======================================================================

In these instructions, variables in angle brackets need to be expanded
for each platform. For example, `<JAVA_HOME>` becomes `$JAVA_HOME` on
Solaris/Linux and `%JAVA_HOME%` on Windows. In addition, the forward
slashes (`/`) used in all of the examples need to be replaced with
backslashes (`\`) for Windows. Finally, be sure to use the appropriate
separator for your operating system when specifying multiple path
entries (`;` on Windows, `:` on UNIX/Linux).

On Windows, you must escape any backslashes with an extra backslash in
path separators used in any of the following properties, or use forward
slashes as a path separator instead.


=======================================================================

4.1 Download the Jakarta Authentication TCK
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The Jakarta Authentication TCK is retrieved from 

```
https://download.eclipse.org/jakartaee/authentication/3.1/jakarta-authentication-tck-3.1.0.zip
```

4.2 Setting up an Evironment for the Maven Surefire based TCK Against a Compatible Implementation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The current JUnit based set of tests runs under a Maven surefire runner and needs a profile configured for your compatible
implementation to test. The top level tck/pom.xml includes example profiles including one for GlassFish. To configuration a
profile for your compatible implementation, start with the GlassFish glassfish-ci-managed profile, and modify the dependencies
and configuration to support your implementation and Arqulillian container implementation. The Arqulillian container implementation
starts up your container and deploys the test wars into it.


4.3 Setting up an Evironment Against a Jakarta EE {JakartaEEVersion} CI for SPI TCK tests
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The SPI tests (in [tck]/spi which are part of the latest Jakarta Authentication TCK need additional configuration.

These SPI tests dive a little deeper into the assertions of the Jakarta Authentication SPI, and directly test for a number of 
technical expectations. In contrast, most other tests in the Jakarta Authentication TCK look more at observable system
behavior and are more akin to Integration Tests (IT).

The common sub-module ([tck]/spi/module) of the SPI module primarily contains a special AuthConfigFactory (the TSSV) that is installed
using a ServletContainerInitializer. This AuthConfigFactory traces a lot of operations
on it to a logging file (saved to location set by system property "log.file.location").
This logging file is read by the (client side) tests, which verify whether specific entries are present or absent in said log file.

Both the CI (e.g. Eclipse GlassFish) and the client environment running the tests (junit) needs to have this "log.file.location" specified and
pointing to the same location.

Among the SPI tests is a test for the "auth context ID", which can be obtained in a standard way, but contains an implementation specific 
value. In order to test that this value is correct, the expected value for a CI needs to be configured using the client environment
system property "logical.hostname.servlet".

A Jakarta Authentication implementation provides an "AuthConfigFactory" implementation, which can be obtained in a standard way, but
obviously is of a implementation specific type. In order to test various assumptions about this "vendor AuthConfigFactory", the exact type
needs to be provided to the tests via the system property "vendor.authconfig.factory". Both the CI (e.g. GlassFish) and the client 
environment running the tests (junit) needs to have this "vendor.authconfig.factory" specified and set to the same value.

The `tck/pom.xml` file contains a full example for Eclipse GlassFish in the "glassfish-ci-managed" profile in the surefire configuration
section.

