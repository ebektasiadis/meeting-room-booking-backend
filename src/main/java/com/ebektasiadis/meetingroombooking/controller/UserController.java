package com.ebektasiadis.meetingroombooking.controller;

import com.ebektasiadis.meetingroombooking.dto.UserRequest;
import com.ebektasiadis.meetingroombooking.dto.UserResponse;
import com.ebektasiadis.meetingroombooking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Users API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get all users", description = "Returns a list of users")
    @GetMapping
    public ResponseEntity<Iterable<UserResponse>> getAllUsers() {
        Iterable<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by id", description = "Returns a single user")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Creates a new user", description = "Returns the created user")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse userCreated = userService.createUser(userRequest);

        URI resourceLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userCreated.id())
                .toUri();

        return ResponseEntity.created(resourceLocation).body(userCreated);
    }

    @Operation(summary = "Updates an existing user", description = "Returns the updated user")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        UserResponse userUpdated = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(userUpdated);
    }

    @Operation(summary = "Deletes an existing user")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
