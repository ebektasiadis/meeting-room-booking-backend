package com.ebektasiadis.meetingroombooking.testutil;

import com.ebektasiadis.meetingroombooking.model.User;

public class UserTestBuilder {
    private Long id;
    private String username;
    private String email;

    private UserTestBuilder() {
        this.id = 1L;
        this.username = "default_username";
        this.email = "default@email.com";
    }

    private UserTestBuilder(UserTestBuilder userTestBuilder) {
        this.id = userTestBuilder.id;
        this.username = userTestBuilder.username;
        this.email = userTestBuilder.email;
    }

    public static UserTestBuilder aUser() {
        return new UserTestBuilder();
    }

    public UserTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserTestBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserTestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public User build() {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }

    public UserTestBuilder but() {
        return new UserTestBuilder(this);
    }
}