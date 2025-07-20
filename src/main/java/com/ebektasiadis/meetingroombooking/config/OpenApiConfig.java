package com.ebektasiadis.meetingroombooking.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Meeting Room Booking API",
                description = "Create and manage users, meetings rooms and their bookings through this API.",
                version = "1.0",
                contact = @Contact(
                        name = "Efstratios Bektasiadis",
                        email = "ebektasiadis@gmail.com",
                        url = "https://ebektasiadis.dev"
                )
        )
)
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi v1() {
        return GroupedOpenApi
                .builder()
                .group("v1")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}
