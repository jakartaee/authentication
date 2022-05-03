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


4.1 Setting up an Evironment for the Maven Surefire based TCK Against a Compatible Implementation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The current JUnit based set of tests runs under a Maven surefire runner and needs a profile configured for your compatible
implementation to test. The top level tck/pom.xml includes example profiles including one for GlassFish. To configuration a
profile for your compatible implementation, start with the GlassFish glassfish-ci-managed profile, and modify the dependencies
and configuration to support your implementation and Arqulillian container implementation. The Arqulillian container implementation
starts up your container and deploys the test wars into it.


4.2 Setting up an Environment for the Legacy Jakarta Authentication TCK Against a Compatible Implementation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

==== 4.2.1 Download the Legacy TCK Contents

From the tck/old-tck-runner module, execute the following command:
`mvn generate-resources`

This will unpack the legacy TCK at tck/old-tck-runner/target/authentication-tck.

By default the legacy TCK is retrieved from 

```
https://download.eclipse.org/jakartaee/authentication/3.0/jakarta-authentication-tck-3.0.0.zip
```

An alternative location can be specified using the `tck.authentication.url` maven property.

[[GBFWN]][[TCAUT00012]][[to-configure-your-environment-for-the-jaspic-tck]]

==== 4.2.2 Configure Your Environment for the Legacy Jakarta Authentication TCK

Read the "Setup and Configuration" section of the
old-tck-runner/target/authentication-tck/docs/pdf-usersguide/Jakarta-Authentication-TCK-Users-Guide.pdf
Where that guide refers to the {TechnologyShortName} TCK, use the old-tck-runner/target/authentication-tck
contents.

