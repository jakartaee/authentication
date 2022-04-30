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
