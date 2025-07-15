package com.ebektasiadis.meetingroombooking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI(Environment environment) {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Meeting Room Booking API")
                                .description("Create and manage users, meetings rooms and their bookings through this API.")
                                .version("1.0")
                                .contact(
                                        new Contact()
                                                .url("https://ebektasiadis.dev")
                                                .name("Efstratios Bektasiadis")
                                                .email("ebektasiadis@gmail.com")
                                )
                );
    }
}
