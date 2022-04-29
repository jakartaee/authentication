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

4.1 Configuring Your Environment to Run the TCK Against the Reference Implementation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

After configuring your environment as described in this section,
continue with the instructions in link:#GBFUY[Section 4.6, "Using the
JavaTest Harness Software."]


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

[[GBFWN]][[TCAUT00012]][[to-configure-your-environment-for-the-jaspic-tck]]

4.1.2 To Configure Your Environment for the Jakarta Authentication TCK
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This section describes how to configure your environment to run the
{TechnologyShortName} TCK tests.

Deploy the {TechnologyShortName} TCK tests in the manner that your implementation
requires, based on the type of profile.

If your implementation is Jakart EE based, set the `platform.mode`
property in the `ts.jte` file to `jakartaEE`.

If your implementation is not Jakarta EE based, set the `platform.mode`
property in the `ts.jte` file to `standalone`.


1.  Set the following environment variables in your shell environment:
  a.  `JAVA_HOME` to the directory in which Java SE 8 is installed
  b.  `TS_HOME` to the directory in which the {TechnologyShortName} TCK
  {TechnologyVersion} software is installed
  c.  `PATH` to include the following directories: `JAVA_HOME/bin`,
  +{TechnologyHomeEnv}/bin+, and `M2_HOME/bin`


[[GCLHU]][[configuring-your-environment-to-repackage-and-run-the-tck-against-the-vendor-implementation]]

4.2 Configuring Your Environment to Repackage and Run the TCK Against the Vendor Implementation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

After configuring your environment as described in this section,
continue with the instructions to run the TCK.


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

With the {TechnologyShortName} TCK, vendors can specify the level of {TechnologyShortName} support
with which they comply. For example, a vendor may be compliant with the
Servlet Profile, the SOAP Profile, or another (possibly unknown)
profile. If a vendor chooses not to pursue compliance with any profile,
they have an option of meeting something called baseline compliance.
This is the level of compliance that exists regardless of which profile
is being tested.

When a vendor is vying for compliance against no profile and is trying
to get baseline compliance certification only, they have to implement a
porting package (for example, a customvehicle) and pass the baseline
tests that are in the `TS_HOME/src/com/sun/ts/tests/jaspic/spi/baseline`
directory.

The sections that follow explain how to create a custom vehicle and how
to replace the default vehicle with a custom vehicle.

[[CBDCAIEE]][[TCAUT117]][[to-create-a-custom-vehicle]]

4.2.1 To Create a Custom Vehicle
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

A custom vehicle must be created and used when {TechnologyShortName} profile tests are
run in an environment that does not contain a Web server. If your {TechnologyShortName}
profile implementation includes a Web server, you do not need to
implement your own custom vehicle.

The custom vehicle exists, in stubbed out form, and must be implemented
in a way that provides a wrapper in which {TechnologyShortName} tests can execute. The
default `jaspicservlet` vehicle is an example of a vehicle that wraps
and executes tests in a Servlet container. The `jaspicservlet` vehicle
source can be used a reference to help you implement your own custom
vehicle. The `jaspicservlet` vehicle is in the
`src/com/sun/ts/tests/common/vehicle/jaspicservlet` directory.

1.  Use the stubbed-out `customvehicle` in the
`src/com/sun/ts/tests/common/vehicle/customvehicle` directory as your
starting point.
2.  Modify the `CustomVehicleRunner` class, using other vehicles as
references. +
The `bin/xml/ts.vehicles.xml` file includes a stubbed-out section for
the `customvehicle`, which you can modify to build you own
`customvehicle`.
3.  Build the `customvehicle` you created.
4.  Modify the `src/vehicle.properties` file so that it refers to
`customvehicle` instead of `jaspicservlet`. +
The `vehicle.properties` file is used during runtime to indicate in
which vehicle the tests should be executed.
5.  Remove or rename the `src/testsuite.jtd` file. +
This allows the test harness to identify tests to be run in your
`customvehicle`.

[[TCAUT118]][[sthref10]]


[[to-replace-the-default-vehicle-with-a-custom-vehicle]]
4.2.2 To Replace the Default Vehicle With a Custom Vehicle
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If your {TechnologyShortName} server does not have web support, you will need to create
your own vehicle. A vehicle is a wrapper that supports running tests in
different server-side containers, such as servlet, JSP, and so on. The
{TechnologyShortName} TCK provides a default vehicle, `jaspicservlet`, which supports
running the TCK tests in a {TechnologyShortName} runtime that has a Servlet container.
To support running tests in an environment other than a Servlet
container, you need to implement your own vehicle, effectively replacing
the default vehicle, `jaspicservlet`.

This TCK was designed so you could use `jaspicservlet` as a template for
creating your own vehicle. The `jaspicservlet` vehicle is used to
contain and execute your client-side tests in the connector runtime.

The `jaspicservlet` vehicle is located in the
`<TS_HOME>/src/com/sun/ts/tests/common/vehicle/jaspicservlet` directory.

To run the tests in a vehicle other than `jaspicservlet`, you need to
create a custom vehicle named `customvehicle`. See
link:#CBDCAIEE[Section 4.2.1, "To Create a Custom Vehicle,"] for more
information on this topic.



[[GHGDG]][[publishing-the-test-applications]]

4.3 Publishing the Test Applications
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Not needed for the {TechnologyShortName} TCK.
