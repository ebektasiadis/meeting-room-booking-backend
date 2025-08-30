package com.ebektasiadis.meetingroombooking.service;

import com.ebektasiadis.meetingroombooking.dto.UserRequest;
import com.ebektasiadis.meetingroombooking.dto.UserResponse;
import com.ebektasiadis.meetingroombooking.exception.user.UserEmailExistsException;
import com.ebektasiadis.meetingroombooking.exception.user.UserNotFoundException;
import com.ebektasiadis.meetingroombooking.exception.user.UserUsernameExistsException;

import java.util.List;

public interface UserService {
    public List<UserResponse> getAllUsers();

    public UserResponse getUserById(Long id) throws UserNotFoundException;

    public UserResponse createUser(UserRequest userRequest) throws UserEmailExistsException, UserUsernameExistsException;

    public UserResponse updateUser(Long id, UserRequest userRequest) throws UserNotFoundException, UserEmailExistsException, UserUsernameExistsException;

    public void deleteUser(Long id) throws UserNotFoundException;
}
