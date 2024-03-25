# Common Near Cache uService
    This libaray provides abstraction and generic implementation for near-cache.


#####  Following are modules that are present in this repo
- Abstraction layer `core-nearcache` that would be used as a soft dependency in the repos that need cache service
- A generic implementation `common-nearcache` module that would then be used by different cache implementation
- An abstract cache implementation `common-nearcache-spring` module using `Spring Cache` is available as a module
    - Using spring cache gives the flexibility to implement different cache storages such as Caffeine, Redis
    - Spring cache is `JSR-107` (JCache) compliant
    - By default, Caffeine is the chosen cache storage
    - Consumers of this module do have a choice to override by excluding the `com.github.ben-manes.caffeine: caffeine` and include appropriate dependency
- Master data cache domain objects and mappers `tenant-nearcache-domain` and `node-nearcache-domain` modules for tenant and node services
- Master data cache common service implementation `tenant-nearcache-userservice` and `node-nearcache-userservice` modules for tenant and node services
- Master data cache impl modules with spring implementation for above interfaces
    - `tenant-nearcache-spring-userservice` and `node-nearcache-spring--userservice` modules for tenant and node services
    -
    - these modules extend `common-nearcache-spring` in the context of tenant/node master data keys and values replacing generics
    -
#### Following are the bunch of things a consuming repository should be doing
- Include `core-nearcache` library dependency in the controller layer that gives the cache service interface
- Include master data cache domain libraries `tenant-nearcache-domain` and `node-nearcache-domain` modules that would give access to respective cache key and values
- A new module that would depend on


## Promising Engine Master Data Service

Description :-  This microservice is unified version of all the individual microservices. Individual microservice like pe-item, pe-node etc own a single master data entity and their related operations. Before the unified master data service, all individual microservices had their own deployment and JVM running on k8s nonprod cluster.

1) Intellij setup

  - Required Plugins - Lombok, SonarLint(optional), Cucumber for Java and Gherkin(for automation)

  - Add Java 21 version

    File -> Settings -> Gradle -> Add Java version 21 in Gradle JVM

    File -> Project Structure -> Add Java version 21 in SDK Column

2) Docker-compose file setup to configure all the dependent services ex.postgres,kafka,redis

  - NOTE :- Please don't run this docker-compose file if you have already installed and configured the dependent services in your local,these steps are only for those who are working on this service for first time and haven't installed it.If you have services already running and configured manually outside of Docker Compose, and you then decide to run a docker-compose.yml file that defines the same services, it could potentially interfere with the existing environment

  - Navigate to the directory containing your docker-compose.yml file(src->main->resources)

  - Run the "docker-compose up" command

        sudo docker-compose up -d

  - To stop the services defined in the docker-compose.yml file, you can use the following command:

         sudo docker-compose down

  - To check the status of the container managed by docker

          sudo docker-compose ps
          sudo docker-compose ps -a

  - To check the logs of a container

          sudo docker logs <container PID/container name>



3) Steps to run the application

   Run MasterDataNodeApplication.java from intellij

4) Steps to build and punblish the node service jar file in local

  1) In build.gradle of changed repositories, add mavenLocal() above every mavenCentral() when taking dependencies from local.

  2) Build the project

     Build -> Build Project(Ctrl+f9)

  3) gradle clean build publisMavenLocal

     Gradle -> pe-node-uservice -> Tasks -> publishing -> publishToMavenLocal()


5) Steps to connect to postgres DB locally .

  -  Install and run postgres service locally

  -  In application-default.yml, change postgres endpoint to localhost:5432


            Path -> spring.datasource
            datasource: 

                  platform: postgres
                  
                  url: jdbc:postgresql://localhost:5432/postgres
                  
                  username: postgres
                  
                  password: postgres 

6) Required Tools

  - Install postgres 14 and pgAdmin 4 for the postgresSql database.

    Connecting to dev data in pgAdmin 4 (Nextuple VPN is required)
  - Link to confluence page for setting up pgadmin : https://nextuple.atlassian.net/wiki/spaces/NEXTUPLEEN/pages/2465759449/Commonly+used+tools

  - Install Apache kafka in local
