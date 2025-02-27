# Promising Engine Master Data Service

## Overview
The **Promising Engine Master Data Service** is a unified microservice that consolidates individual master data microservices (e.g., `pe-item`, `pe-node`). Previously, each service was deployed separately with its own JVM on a Kubernetes non-prod cluster. This unified approach simplifies deployment and management.

---

## 🚀 Quick Setup Guide

### IntelliJ Setup

**Required Plugins:**
- Lombok
- SonarLint *(optional)*
- Cucumber for Java & Gherkin *(for automation)*

**Configure Java 21:**
- **Gradle JVM:** `File -> Settings -> Gradle -> Add Java 21`
- **Project SDK:** `File -> Project Structure -> Add Java 21`

---

### Required Tools
- **PostgreSQL 14 & pgAdmin 4**
- [Nextuple VPN Connectivity Guide](https://nextuple.atlassian.net/wiki/x/BYBQlg)
- **Apache Kafka** *(Install locally)*

---

### Setting Up Dependencies with Docker-Compose [Optional<sup>*</sup> ]

**⚠️ Important:** 
- <sup>*</sup> If postgres and other required tools are installed locally and running, skip this step.
- If you have already installed and configured dependencies manually (Postgres, Kafka, Redis), skip this step. Running `docker-compose` may interfere with your existing setup.

**Steps:**
1. Navigate to the `docker-compose.yml` directory: `src/main/resources`
2. Start services:
   ```sh
   sudo docker-compose up -d
   ```
3. Stop services:
   ```sh
   sudo docker-compose down
   ```
4. Check running containers:
   ```sh
   sudo docker-compose ps
   sudo docker-compose ps -a
   ```
5. View container logs:
   ```sh
   sudo docker logs <container_id/container_name>
   ```

---

### Building & Publishing the Node / Master Data Service Jar Locally [Optional]

1. Modify `build.gradle` to prioritize local dependencies:
   ```gradle
   repositories {
       mavenLocal() // Add this above mavenCentral()
   }
   ```
2. Build the project:  
   **Shortcut:** `Ctrl + F9`  
   **Menu:** `Build -> Build Project`
3. Publish to local Maven repository:
   ```sh
   gradle clean build publishToMavenLocal
   ```
   **or via IntelliJ Gradle Tab:**
    - Navigate to `pe-node-uservice -> Tasks -> publishing -> publishToMavenLocal()`

---

### Disabling the kafka logs [Optional]

- Update `application-default.yml`:
   ```yml
   logging:
    level:
      org.apache.kafka: OFF
   ```

---

### Connecting to Postgres Locally

- Update `application-default.yml` to use localhost address of postgres:
   ```yml
   spring:
     datasource:
       platform: postgres
       url: jdbc:postgresql://localhost:5432/postgres
       username: postgres
       password: postgres
   ```

---

### Adding Azure Key Vault and Keycloak Configuration

- **⚠️ Important:** Get `<client-id>`, `<client-key>`, and `<tenant-id>` from the DevOps / PE team.
  - Update `application-default.yml`:
     ```yml
     azure.keyvault:
         client-id: <client-id>
         client-key: <client-key>
         enabled: true
         tenant-id: <tenant-id>
         uri: https://nextuple-dev.vault.azure.net/
         secret-keys: "COMMON-MONGODB-CREDENTIAL,COMMON-KAFKA-CONFIG,MASTER-DATA-KEYCLOAK-CLIENT-ID"
     ```
      ```yml
      spring:
        security:
          oauth2:
            client:
              registration:
                my-client:
                  client-id: applicationclient-pe-masterdata-uservice
                  client-secret: \${MASTER-DATA-KEYCLOAK-CLIENT-ID}
                  authorization-grant-type: client_credentials
                  scope: applicationclient-pe-masterdata-scope
              provider:
                my-client:
                  token-uri: https://keycloak-nonprod.nextuple.com/realms/{realm-name}/protocol/openid-connect/token
      ```

    >   **NOTE:**  `{realm-name}` can be changed to the respective realm name. For example, in dev, it is `noms-dev`
  
      ```yml
      neo-platform:
        auth-injector:
          token-strategy: generate
          filter:
            enabled: true
          client-uri-mappings:
            my-client: /**
        auth-validator:
          application-name: pe-masterdata
          header:
            tenant-id: x-tenant-id
          issuer-uri: https://keycloak-nonprod.nextuple.com/realms/{realm-name}
          authz-config:
            config-path: "http://plt-authz-config-uservice:8080/api/configurations/groups/application-group/scope-id/pe-masterdata"
            refresh-interval-mins: 30
            role:
              json-paths: \$.resource_access.tenantclient-nextuple.roles,\$.resource_access.tenantclient-kohls.roles,\$.resource_access.tenantclient-xyzinc.roles,\$.resource_access.tenantclient-abcinc.roles,\$.resource_access.tenantclient-neednow.roles,\$.resource_access.tenantclient-ctppt.roles,\$.resource_access.applicationclient-pe-masterdata-uservice.roles
    
      ```

    >   **NOTE:**  `config-path: "http://plt-authz-config-uservice:8080/api/configurations/groups/application-group/scope-id/pe-masterdata"` is the path to the `plt-authz-config-uservice`. This service is used to get the roles and permissions for the application. This can be changed based on the environment. For example, in dev, it is `"https://internal-dev.nextuple.com/services/api/configurations/groups/application-group/scope-id/pe-masterdata"`

    > **NOTE:** `token-strategy: generate` can be changed to `reuse` if running master data in standalone mode and it requires making external API calls..
---

### Running the Application
- Open **IntelliJ** and run `PeMasterDataUserviceApplication.java`

---