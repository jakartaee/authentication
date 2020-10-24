# Jakarta Authentication

This repository contains the code for Jakarta Authentication.

[Online JavaDoc](https://javadoc.io/doc/jakarta.security.auth.message/jakarta.security.auth.message-api/)

Building
--------

Jakarta Authentication can be built by executing the following from the project root:

``mvn clean package``

The API jar can then be found in /api/target.

Making Changes
--------------

To make changes, fork this repository, make your changes, and submit a pull request.

About Jakarta Authentication
-------------

Jakarta Authentication defines a general low-level SPI for authentication mechanisms, which are controllers that interact with a caller and a container's environment to obtain the caller's credentials, validate these, and pass an authenticated identity (such as name and groups) to the container. 
        
Jakarta Authentication consists of several profiles, with each profile telling how a specific container
(such as Jakarta Servlet) can integrate with- and adapt to this SPI.
aa
