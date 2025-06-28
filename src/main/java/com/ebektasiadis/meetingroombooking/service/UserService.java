package com.ebektasiadis.meetingroombooking.service;

import com.ebektasiadis.meetingroombooking.dto.UserRequest;
import com.ebektasiadis.meetingroombooking.dto.UserResponse;
import com.ebektasiadis.meetingroombooking.exception.user.UserEmailExistsException;
import com.ebektasiadis.meetingroombooking.exception.user.UserNotFoundException;

public interface UserService {
    public Iterable<UserResponse> getAllUsers();

    public UserResponse getUserById(Long id) throws UserNotFoundException;

    public UserResponse createUser(UserRequest userRequest) throws UserEmailExistsException;

    public UserResponse updateUser(Long id, UserRequest userRequest) throws UserNotFoundException, UserEmailExistsException;

    public void deleteUser(Long id);
}
