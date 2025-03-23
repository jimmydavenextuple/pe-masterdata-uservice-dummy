# Test Container Implementation Guide
Testcontainers enables the use of Docker images for external dependencies to create isolated and reproducible testing environments. 
It helps in spin up the real instances of databases, message brokers, or other services needed for testing within a controlled environment.

This is particularly useful for testing REST APIs and Kafka consumers, as it invokes them from outside the application, unlike method-level tests, helping to prevent issues related to the Spring Boot context.

## System requirements (Local & CI/CD)
- **Docker**: Testcontainers requires Docker to be installed on the machine. As it uses Docker to spin up the containers for the external dependencies.
    It is recommended to install docker v28. [Link for installation](https://docs.docker.com/engine/install/ubuntu/)

## Local setup
- **Add Azure Key Vault properties**: Add the following properties to the `application-proxy.yml` file to get the latest files.
    ```properties
    azure.keyvault:
      client-id: <Azure Client ID>
      client-key: <Azure Client Key>
      enabled: true
      tenant-id: <Azure Tenant ID>
      uri: <Azure Key Vault URI>
      secret-keys: ""
    ```
  
## File and Folder Conventions
Please follow the below conventions while creating the test files and folders.
- **To implement proxy tests for a new use case**:
  - Create a new test class in the `proxy` folder with the name `<<TestName>>ProxyTest.java` by extending the `AbstractProxyTest` class. (Refer to the `NodeProxyTest.java` file for reference)
  - Create a new folder in the `input` folder in the resources folder with the name `<<TestName>>` and add the input request file of the test case.
  - Create a new folder in the `expected` folder in the resources folder with the name `<<TestName>>` and add the expected response file of the test case.
```dockerignore
/test folder of the application
    ├── java
    │   └── com
    │       └── nextuple
    │           ├── AbstractSpringBootTest.java (Abstract class to initialize the test container)
    │           ├── masterdata
    │           │   ├── AbstractProxyTest.java (Abstract class to initialize any proxy specific test container & common methods)
    │           │   ├── AbstractPerfTest.java (Abstract class to initialize any perf specific test container & common methods)
    │           │   ├── proxy (Folder to keep all the proxy test classes)
    │           │   │   └── NodeProxyTest.java (Test class to test the Node proxy. Can be referred to as a template)
    │           │   └── utils
    │           │       └── SpringBootTestUtil.java (Templatization of util methods)
    │           └── README.md
    └── resources
        ├── db_setup.sql
        ├── expected (Folder to keep all the expected response files)
        │   └── node
        │       └── create-node-response.json
        └── input (Folder to keep all the input request files)
            └── node
                └── create-node.json
```

## Limitation
- **Tenant**: The test cases should be written for the `NEXTUPLE_GR` tenant only.
- **Keycloak**: The test is dependent on the Keycloak availability. The Keycloak should be up and running to run the test cases.

## Database setup
- **Database setup**: If the test case requires a database setup, add the SQL queries to the `db_setup.sql` file in the resources folder.

## How to write tests
- The test cases can be written as JUnit test cases itself.
- It is recommended to use the utility methods provided in the `SpringBootTestUtil` class for common operations.
- The test cases should be written for the `NEXTUPLE_GR` tenant only.
- It is recommended to use the order annotation to run the test cases in a specific order & effectively use the earlier test case data in the later test cases.

## How to run the tests
- This can be run as a JUnit test case itself.

## Possible util functions
Following functions can be accessed at `util` variable in the test cases.
1. **Parse content from resources to desired object (or) type reference**
2. **Parse string to desired object (or) type reference**
3. **Read the file content**
4. **REST API call & output extraction**: This function also attaches the access token to the request & asserts the responseCode.
5. **Poll the assertion**: This function pools the `Supplier` function until the `Consumer` function is satisfied.
6. **Intialize the Kafka consumer**: This function also subscribes to a list of topics.
6. **Poll the messages from Kafka**: This function also polls a topic until a specific count of messages is received.
7. **Refresh the access token**: This function is not needed to be called explicitly. It is refreshed automatically when the token is expired.

Please free to add any common utility methods (or) overloaded methods to the `SpringBootTestUtil` class that can be used across the test cases.

## FAQ
- **How to handle the failure of the test case in the bitbucket pipeline?**
    - The test reports will be available in the artifacts. The artifacts can be downloaded to check the system logs and the failure reason.
- **How to handle if the service (or) database methods are not available at the test case level?**
    - Add the `testImplementation` dependency of the module in the `build.gradle` file to use the method for polling the assertions.
        Example:
        ```gradle
        testImplementation(project(":pe-node-uservice:node-persistence-domain"))
        ```

## Future possibilities
- **If we are looking to perform an integration test of PE products, we can spin up the master data in pe-core as a container and test the performance & integration.**
- **If we are looking to run SQL queries on the database explicitly in any test case, we can use the JDBC template to run the queries.**
- **If we are looking to mock a method at a global level with a specific mapping (or) any other logic, we can leverage the Aspect Oriented Programming (AOP) to create mocks for any external call.**
- **If we are seeing any failures due to external dependency is not up, we can use wait strategies to wait for the specific condition to be met by the external container.**
