package com.nextuple.common.config;

import com.nextuple.common.properties.ApplicationProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.servers.ServerVariable;
import io.swagger.v3.oas.models.servers.ServerVariables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.Pair;

/** Configuration related to OpenAPI documentation should be placed here */
@Configuration
@RequiredArgsConstructor
public class OpenAPIConfiguration {

  private final ApplicationProperties applicationProperties;

  @Bean
  public Info serviceInformation() {
    return new Info()
        .title(applicationProperties.getTitle())
        .description(applicationProperties.getDescription())
        .version(applicationProperties.getVersion())
        .contact(
            new Contact()
                .email(applicationProperties.getContactEmail())
                .name(applicationProperties.getContactName())
                .url(applicationProperties.getContactWebsite()))
        .extensions(
            applicationProperties.getProperties().entrySet().stream()
                .map(e -> Pair.of("x-" + e.getKey().toLowerCase(Locale.ROOT), e.getValue()))
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
  }

  @Bean
  public OpenAPI openAPIConfig(Info serviceInformation) {
    // Global security
    Components components = new Components();
    components.addSecuritySchemes(
        "api_key",
        new SecurityScheme()
            .type(SecurityScheme.Type.APIKEY)
            .description(
                "To access any of the APIs unless mentioned otherwise will need API Key input for "
                    + "authentication purpose. To get your API Key, please get in touch with Nextuple team.")
            .in(SecurityScheme.In.HEADER)
            .name("X-API-Key"));
    components.addSecuritySchemes(
        "bearer-jwt",
        new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description(
                "To access many of the APIs in role-based access manner, please use this mechanism. Please contact "
                    + "Nextuple team on how to generate the required token for this mechanism.")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization"));

    // Global servers
    ServerVariable customerServerVariable = new ServerVariable();
    customerServerVariable.setDescription("Customer specific ID shared by Nextuple team");
    customerServerVariable.setDefault("internal");
    ServerVariable basePathServerVariable = new ServerVariable();
    basePathServerVariable.setDescription("Module specific base will be shared by Nextuple team");
    basePathServerVariable.setDefault("module");
    List<Server> servers = new ArrayList<>();
    servers.add(
        new Server()
            .url("https://{customer}-test.nextuple.com/{module}")
            .description("Customer specific test (sandbox) endpoint")
            .variables(
                new ServerVariables()
                    .addServerVariable("customer", customerServerVariable)
                    .addServerVariable("module", basePathServerVariable)));
    servers.add(
        new Server()
            .url("https://{customer}.nextuple.com/{module}")
            .description("Customer specific Production endpoint")
            .variables(
                new ServerVariables()
                    .addServerVariable("customer", customerServerVariable)
                    .addServerVariable("module", basePathServerVariable)));
    return new OpenAPI()
        .info(serviceInformation)
        .components(components)
        .servers(servers)
        .security(
            Arrays.asList(
                new SecurityRequirement().addList("api_key"),
                new SecurityRequirement().addList("bearer-jwt")));
  }
}
