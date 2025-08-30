package com.ebektasiadis.meetingroombooking.config;

import com.ebektasiadis.meetingroombooking.documentation.DocumentedExceptions;
import com.ebektasiadis.meetingroombooking.exception.common.ResponseProblemDetail;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final RequestMappingHandlerMapping handlerMapping;

    public OpenApiConfig(@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    private Object getDefaultValue(Class<?> type) {
        if (type == String.class || type == Character.class) return "string";
        if (type == int.class || type == Integer.class
                || type == short.class || type == Short.class
                || type == byte.class || type == Byte.class
                || type == long.class || type == Long.class
                || type == BigInteger.class) return 0;
        if (type == float.class || type == Float.class
                || type == double.class || type == Double.class
                || type == BigDecimal.class) return 0.0;
        if (type == boolean.class || type == Boolean.class) return false;
        if (type.isEnum()) return type.getEnumConstants()[0];
        if (type.isArray() || List.class.isAssignableFrom(type)) return List.of();
        if (type == UUID.class) return "00000000-0000-0000-0000-000000000000";
        if (type == LocalDate.class) return LocalDate.now().toString();
        if (type == LocalTime.class) return LocalTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
        if (type == LocalDateTime.class)
            return LocalDateTime.now().atOffset(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        if (type == OffsetDateTime.class)
            return OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).toString();
        if (type == ZonedDateTime.class)
            return ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).toString();
        if (type == Instant.class) return Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();

        Map<String, Object> fieldDefaults = new LinkedHashMap<>();
        Arrays.stream(type.getDeclaredFields()).forEach(field -> {
            field.setAccessible(true);
            fieldDefaults.put(field.getName(), getDefaultValue(field.getType()));
        });
        return fieldDefaults;
    }

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        Schema<?> problemDetailSchema = new ObjectSchema()
                .addProperty("type", new StringSchema()
                        .format("uri"))
                .addProperty("title", new StringSchema())
                .addProperty("status", new IntegerSchema())
                .addProperty("detail", new StringSchema())
                .addProperty("instance", new StringSchema()
                        .format("uri"))
                .addProperty("extensions", new ObjectSchema())
                .name("ProblemDetail");

        Example verificationExample = new Example().summary("Validation Error").value(Map.of(
                "type", "/problems/validation-error",
                "title", "Some fields are missing or are invalid.",
                "extensions", Map.of("fields", new Object())
        ));

        return GroupedOpenApi
                .builder()
                .group("v1")
                .pathsToMatch("/api/v1/**")
                .addOpenApiCustomizer(openApi -> {
                    Map<String, HandlerMethod> pathToHandlerMethod = handlerMapping.getHandlerMethods().entrySet().stream()
                            .flatMap(entry -> {
                                HandlerMethod handlerMethod = entry.getValue();
                                PathPatternsRequestCondition pathCondition = entry.getKey().getPathPatternsCondition();
                                RequestMethodsRequestCondition methodsCondition = entry.getKey().getMethodsCondition();

                                if (pathCondition == null) {
                                    return Stream.empty();
                                }

                                return pathCondition.getPatterns().stream()
                                        .flatMap(path -> methodsCondition.getMethods().stream()
                                                .map(method -> Map.entry(method.name() + ":" + path.toString(), handlerMethod))
                                        );
                            })
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                    openApi.schema(problemDetailSchema.getName(), problemDetailSchema);

                    openApi.getPaths().forEach((path, pathItem) -> {
                        pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                            String key = httpMethod.name() + ":" + path;
                            HandlerMethod handlerMethod = pathToHandlerMethod.get(key);

                            if (handlerMethod == null) {
                                return;
                            }

                            boolean isValidAnnotationExist = Arrays.stream(handlerMethod.getMethodParameters()).anyMatch(parameter -> parameter.getParameterAnnotation(Valid.class) != null);

                            Map<HttpStatus, ArrayList<Example>> examples = new HashMap<>();
                            if (isValidAnnotationExist) {
                                examples.put(HttpStatus.BAD_REQUEST, new ArrayList<>());
                                examples.get(HttpStatus.BAD_REQUEST).add(verificationExample);
                            }

                            DocumentedExceptions documentedExceptions = handlerMethod.getMethod().getAnnotation(DocumentedExceptions.class);

                            if (documentedExceptions == null) {
                                return;
                            }

                            Arrays.stream(documentedExceptions.value()).forEach(exception -> {
                                ResponseProblemDetail responseProblemDetail = exception.getAnnotation(ResponseProblemDetail.class);
                                if (responseProblemDetail == null) {
                                    return;
                                }

                                if (!examples.containsKey(responseProblemDetail.status())) {
                                    examples.put(responseProblemDetail.status(), new ArrayList<>());
                                }

                                Map<String, Object> extensions = new HashMap<>();
                                Arrays.stream(responseProblemDetail.extensions()).forEach(extension -> {
                                    extensions.put(extension.name(), this.getDefaultValue(extension.type()));
                                });

                                examples.get(responseProblemDetail.status()).add(
                                        new Example().summary(responseProblemDetail.title()).value(Map.of(
                                                "type", String.format("/problems/%s", responseProblemDetail.type()),
                                                "title", responseProblemDetail.title(),
                                                "status", responseProblemDetail.status().value(),
                                                "extensions", extensions
                                        ))
                                );
                            });

                            examples.forEach((httpStatus, _examples) -> {
                                MediaType mediaType = new MediaType().schema(problemDetailSchema);
                                AtomicInteger exampleId = new AtomicInteger();

                                _examples.forEach(example -> {
                                    mediaType.addExamples(String.format("example_%d", exampleId.getAndIncrement()), example);
                                });

                                operation.getResponses().addApiResponse(String.valueOf(httpStatus.value()), new ApiResponse()
                                        .description(httpStatus.getReasonPhrase())
                                        .content(new Content().addMediaType("application/problem+json", mediaType)));
                            });
                        });
                    });

                }).build();
    }
}
