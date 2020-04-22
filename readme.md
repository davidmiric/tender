CONTENTS OF THIS FILE
---------------------

 * Introduction
 * Decisions
 * How to run the Application
 * Rest Api Documentation
 * Improvements for the future
 
 INTRODUCTION
 ------------
 
 The Tender application is homework assingment from Omero which is part of the interview process. 
 It is meant to be built in one working day. I have wrote it with some assumptions made along the way
 to make whole work simpler. The task is to create back end application for tender management including creation of tenders,
 submitting/accepting offers and fetching data.
 
  * For a full description of the task, please have a look at pdf in this repository:
    Homework Assignment.pdf
    
DECISIONS
------------
 
 * Database. For development and demo purposes embedded in-memory H2 database is used, as well as for integration tests. 
 This made development more convenient and faster then working with Postgres database for example. 
 SQL over NoSql approach is chosen. Schema is well known and probably won't change in the future. ACID compliance is required for tender application. Large amount of data or lot of types of data are not expected in this case.
 Configurations are in application.properties files and there are scripts to initialize data for these two databases. 
 
    * Demo database configuration: 
        * Configure it in `src/main/resources/application.properties` and 
        * define init data in `src/main/resources/data-demo.sql`
    
    * Integrations test database configuration: 
       * Configure it in `src/test/resources/application-integrationtest.properties` and 
       * define init data in `src/test/resources/data-integrationtests.sql`

 * User Authentication. No user authentication is needed or done according to requirements. Issuers and Bidders are two separate and independent entities and identifications is done by sending their ids within requests.
 
 * Tests. Integration tests are written for happy-case scenarios and to generate SpringRestDocs documentation. eeded or done according to requirements. Issuers and Bidders are two separate and independent entities and identifications is done by sending their ids within requests.
 Unit tests are written for services, not for repositories.
 
 
 
 HOW TO RUN THE APPLICATION
 ------------
  
  * Download repository as zip and unzip it, or clone it to directory
 
  * Position yourself to this folder, open terminal and run command: ./mwn spring-boot:run
  
  * Server is running on http://localhost:8080
  
  
 REST API DOCUMENTATION
 ------------
    
   * Complete REST API description could be found in ApiDescription.html but that is not automatically updated.
    
    
   * To generate and see the last version of this documentation you need to:
        * run terminal in root directory of the project and run commands:
        `./mvnw test` and then `./mvnw package`
        * New API description file will be available here `target/generated-docs/ApiDescription.html`.
    
   * Server is running on http://localhost:8080
  
 FUTURE IMPROVEMENTS
 ------------
   * Project can be improved and here are some of the areas it might be improved in.
     * Database could be changed to other SQL database as PostgreSQL.
     * Add user Authentication and Authorisation. So that bidders can only submit offers and issuers only create tenders.
     * Write repository unit test for specific methods.
     * Add log statements.
     * Returning pageable data in endpoints that currently return all results.
     * etc.