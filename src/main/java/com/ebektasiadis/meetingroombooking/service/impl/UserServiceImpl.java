package com.ebektasiadis.meetingroombooking.service.impl;

import com.ebektasiadis.meetingroombooking.dto.UserRequest;
import com.ebektasiadis.meetingroombooking.dto.UserResponse;
import com.ebektasiadis.meetingroombooking.exception.user.UserEmailExistsException;
import com.ebektasiadis.meetingroombooking.exception.user.UserNotFoundException;
import com.ebektasiadis.meetingroombooking.exception.user.UserUsernameExistsException;
import com.ebektasiadis.meetingroombooking.mapper.UserMapper;
import com.ebektasiadis.meetingroombooking.model.User;
import com.ebektasiadis.meetingroombooking.repository.UserRepository;
import com.ebektasiadis.meetingroombooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    final private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Iterable<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) throws UserEmailExistsException {
        User user = UserMapper.toEntity(userRequest);

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserEmailExistsException(user.getEmail());
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserUsernameExistsException(user.getUsername());
        }

        user = userRepository.save(user);
        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) throws UserNotFoundException, UserEmailExistsException {
        User user = UserMapper.toEntity(userRequest);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!existingUser.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new UserEmailExistsException(user.getEmail());
            }
        }

        if (!existingUser.getUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                throw new UserUsernameExistsException(user.getUsername());
            }
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());

        existingUser = userRepository.save(existingUser);

        return UserMapper.toResponse(existingUser);
    }

    @Override
    public void deleteUser(Long id) throws UserNotFoundException {
        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(id);
    }
}
