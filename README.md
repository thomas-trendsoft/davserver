# davserver

This is my first real open source project. It is a try to build a java implementation of WebDAV (RFC4918) with additional support for CalDAV and CardDAV (RFC 4791) and the required WebDAV ACL (RFC 3744). 

It is an learning project to get a protocol implementation. The target is still to get a production ready WebDAV Protocol implementation. The API will be able to make simple implementations of file or database resources behind a WebDAV server interface. 

The second step with CalDAV and CardDAV should enable the server to be able to serve resources, calendars, contacts and todos. There will be no user interface or anything else, the project aims only to be the protocol implementation. 

## Status 

A base file server worked to create,edit,delete,share some office files. Authentication is for HTTP Basic Auth in small steps given.  
 
THE PROJECT IS WORK IN PROGRESS and no useful release is done yet.

For testing surpose i will try to get this framework running against the litmus project (https://github.com/tolsen/litmus). First steps are done the library passes the base tests.

The CalDAV extension will be tested later against the ccs-caldavtester (https://github.com/apple/ccs-caldavtester/)

## Test Installation

At the moment it is really simple to get the server run. You will also need the ical4dav package (https://github.com/thomas-trendsoft/ical4dav)  installed.

* change to davserver directory
* mkdir files
* mvn clean package
* java -jar target/davserver-0.2-jar-with-dependencies.jar

If auth is needed use "admin" with password "admin". Changes will be done later to get a real user management. The test repositories are:

* http://yourhost:8843/files (Simple File Repository)
* http://yourhost:8843/calendars/mytest/calendar (static CalDAV test calendar)
* http://yourhost:8843/simple (Simple Memory managed repository)


## Litmus Progress

* running 'http': done
* running `basic': done 
* running 'copymove': done (with warnings)
* running 'props': done (with warnings)
* running 'locks': 69 tests run: 67 passed, 2 failed. 97.1%
