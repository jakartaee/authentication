# Jakarta Authentication

This repository contains the code for Jakarta Authentication.

[Online JavaDoc](https://javadoc.io/doc/jakarta.security.auth.message/jakarta.security.auth.message-api/)

[TCK results summary](https://eclipse-ee4j.github.io/jaspic/certifications/authentication/1.1/TCK-Results)

Building
--------

Jakarta Authentication can be built by executing the following from the project root:

``mvn clean package``

The API jar can then be found in /app/target.

Making Changes
--------------

To make changes, fork this repository, make your changes, and submit a pull request.

About Jakarta Authentication
-------------

Jakarta Authentication defines a general low-level SPI for authentication mechanisms, which are controllers that interact with a caller and a container's environment to obtain the caller's credentials, validate these, and pass an authenticated identity (such as name and groups) to the container. 
        
Jakarta Authentication consists of several profiles, with each profile telling how a specific container
(such as Jakarta Servlet) can integrate with- and adapt to this SPI.
