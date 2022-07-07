///////////////////////////////////////////////////////////////////////
NOTE TO WRITERS:
These CLI examples can be customized as necessary.
///////////////////////////////////////////////////////////////////////

To run all tests against GlassFish, use following command: +
[source,oac_no_warn]
----
mvn -Pglassfish-ci-managed clean test surefire-report:report
----
This will produce a target/site/surefire-report.html summary. Replace the glassfish-ci-managed profile name with the
name of any other configured compatible implementation to run the tests against that implementation.

When doing testing with staged dependencies, you may need to add the -Pstaging profile:
----
mvn -Pstaging -Pglassfish-ci-managed clean test surefire-report:report
----

When running tests against Jakarta EE Compatible Implementation you need to specify the `-Pplatform` profile:
This will set the `KEYWORDS` parameter accordingly for choosing the tests to be run for Platform mode (Full profile in default).
----
mvn -Pplatform clean test surefire-report:report 
----

When running tests against Jakarta EE Web Profile Compatible Implementation you need to specify the `-Pplatform,web` profiles:
This will set the `KEYWORDS` parameter accordingly for choosing the tests to be run for Platform mode in web profile.
Also the glassfish web profile bundle will be chosen to run the test against.
----
mvn -Pplatform,web clean test surefire-report:report 
----
