package com.ebektasiadis.meetingroombooking.repository;

import com.ebektasiadis.meetingroombooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByEmail(String email);

    public Optional<User> findByUsername(String username);
}
