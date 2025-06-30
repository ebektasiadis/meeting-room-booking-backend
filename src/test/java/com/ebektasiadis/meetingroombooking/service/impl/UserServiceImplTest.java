package com.ebektasiadis.meetingroombooking.service.impl;

import com.ebektasiadis.meetingroombooking.dto.UserRequest;
import com.ebektasiadis.meetingroombooking.dto.UserResponse;
import com.ebektasiadis.meetingroombooking.exception.user.UserEmailExistsException;
import com.ebektasiadis.meetingroombooking.exception.user.UserNotFoundException;
import com.ebektasiadis.meetingroombooking.exception.user.UserUsernameExistsException;
import com.ebektasiadis.meetingroombooking.model.User;
import com.ebektasiadis.meetingroombooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
public class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    Long nonExistingUserId;

    User userJohnDoe;
    User userMarySmith;

    @BeforeEach
    void setUp() {
        nonExistingUserId = 999L;

        userJohnDoe = new User();
        userJohnDoe.setId(1L);
        userJohnDoe.setUsername("john_doe");
        userJohnDoe.setEmail("john@doe.com");

        userMarySmith = new User();
        userMarySmith.setId(2L);
        userMarySmith.setUsername("mary_smith");
        userMarySmith.setEmail("mary@smith.com");
    }


    @Nested
    @DisplayName("deleteUser method")
    class DeleteUser {

        @Test
        @DisplayName("should not throw if id exists")
        void deleteUser_existingUser_doesNotThrowException() {
            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));

            assertThatNoException().isThrownBy(() -> userService.deleteUser(userJohnDoe.getId()));

            verify(userRepository, times(1)).findById(userJohnDoe.getId());
            verify(userRepository, times(1)).deleteById(userJohnDoe.getId());
        }

        @Test
        @DisplayName("should throw UserNotFoundException if id does not exist")
        void deleteUser_nonExistingUser_throwsUserNotFoundException() {
            when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteUser(nonExistingUserId))
                    .isInstanceOf(UserNotFoundException.class)
                    .extracting("userId")
                    .isEqualTo(nonExistingUserId);

            verify(userRepository, times(1)).findById(nonExistingUserId);
            verify(userRepository, times(0)).deleteById(userJohnDoe.getId());
        }
    }

    @Nested
    @DisplayName("updateUser method")
    class UpdateUser {

        @Test
        @DisplayName("should throw UserNotFoundException if id does not exist")
        void updateUser_nonExistingUser_throwsUserNotFoundException() {
            when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(nonExistingUserId, any(UserRequest.class)))
                    .isInstanceOf(UserNotFoundException.class)
                    .extracting("userId")
                    .isEqualTo(nonExistingUserId);

            verify(userRepository, times(1)).findById(nonExistingUserId);
        }

        @Test
        @DisplayName("should throw UserUsernameExistsException if new username is used by another user")
        void updateUser_newUsernameIsUsedByAnotherUser_throwsUserUsernameExistsException() {
            UserRequest userRequest = new UserRequest(userMarySmith.getUsername(), userJohnDoe.getEmail());

            when(userRepository.findByUsername(userMarySmith.getUsername())).thenReturn(Optional.of(userMarySmith));
            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));

            assertThatThrownBy(() -> userService.updateUser(userJohnDoe.getId(), userRequest))
                    .isInstanceOf(UserUsernameExistsException.class)
                    .extracting("userUsername")
                    .isEqualTo(userMarySmith.getUsername());

            verify(userRepository, times(1)).findById(userJohnDoe.getId());
            verify(userRepository, times(0)).findByEmail(userRequest.email());
            verify(userRepository, times(1)).findByUsername(userRequest.username());
            verify(userRepository, times(0)).save(any(User.class));
        }

        @Test
        @DisplayName("should throw UserEmailExistsException if new email is used by another user")
        void updateUser_newEmailIsUsedByAnotherUser_throwsUserEmailExistsException() {
            UserRequest userRequest = new UserRequest(userJohnDoe.getUsername(), userMarySmith.getEmail());

            when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.of(userMarySmith));
            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));

            assertThatThrownBy(() -> userService.updateUser(userJohnDoe.getId(), userRequest))
                    .isInstanceOf(UserEmailExistsException.class)
                    .extracting("userEmail")
                    .isEqualTo(userMarySmith.getEmail());

            verify(userRepository, times(1)).findById(userJohnDoe.getId());
            verify(userRepository, times(1)).findByEmail(userRequest.email());
            verify(userRepository, times(0)).findByUsername(userRequest.username());
            verify(userRepository, times(0)).save(any(User.class));
        }

        @Test
        @DisplayName("should return the updated user")
        void updateUser_existingUser_returnsUpdatedUser() {
            UserRequest userRequest = new UserRequest("samuelSerif", "samuel@serif.com");
            User updatedUser = new User();
            updatedUser.setId(userJohnDoe.getId());
            updatedUser.setUsername(userRequest.username());
            updatedUser.setEmail(userRequest.email());

            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));
            when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.empty());
            when(userRepository.findByUsername(userRequest.username())).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);

            UserResponse userResponse = userService.updateUser(userJohnDoe.getId(), userRequest);

            assertThat(userResponse).isNotNull();
            assertThat(userResponse.id()).isEqualTo(userJohnDoe.getId());
            assertThat(userResponse.username()).isEqualTo(userRequest.username());
            assertThat(userResponse.email()).isEqualTo(userRequest.email());

            verify(userRepository, times(1)).findById(userJohnDoe.getId());
            verify(userRepository, times(1)).findByUsername(userRequest.username());
            verify(userRepository, times(1)).findByEmail(userRequest.email());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("should return the updated user when username remains the same")
        void updateUser_existingUserSameUsername_returnsUpdatedUser() {
            UserRequest userRequest = new UserRequest(userJohnDoe.getUsername(), "samuel@serif.com");
            User updatedUser = new User();
            updatedUser.setId(userJohnDoe.getId());
            updatedUser.setUsername(userRequest.username());
            updatedUser.setEmail(userRequest.email());

            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));
            when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);

            UserResponse userResponse = userService.updateUser(userJohnDoe.getId(), userRequest);

            assertThat(userResponse).isNotNull();
            assertThat(userResponse.id()).isEqualTo(userJohnDoe.getId());
            assertThat(userResponse.username()).isEqualTo(userRequest.username());
            assertThat(userResponse.email()).isEqualTo(userRequest.email());

            verify(userRepository, times(1)).findById(userJohnDoe.getId());
            verify(userRepository, times(0)).findByUsername(userRequest.username());
            verify(userRepository, times(1)).findByEmail(userRequest.email());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("should return the updated user when email remains the same")
        void updateUser_existingUserSameEmail_returnsUpdatedUser() {
            UserRequest userRequest = new UserRequest("samuelSerif", userJohnDoe.getEmail());
            User updatedUser = new User();
            updatedUser.setId(userJohnDoe.getId());
            updatedUser.setUsername(userRequest.username());
            updatedUser.setEmail(userRequest.email());

            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));
            when(userRepository.findByUsername(userRequest.username())).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);

            UserResponse userResponse = userService.updateUser(userJohnDoe.getId(), userRequest);

            assertThat(userResponse).isNotNull();
            assertThat(userResponse.id()).isEqualTo(userJohnDoe.getId());
            assertThat(userResponse.username()).isEqualTo(userRequest.username());
            assertThat(userResponse.email()).isEqualTo(userRequest.email());

            verify(userRepository, times(1)).findById(userJohnDoe.getId());
            verify(userRepository, times(1)).findByUsername(userRequest.username());
            verify(userRepository, times(0)).findByEmail(userRequest.email());
            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("createUser method")
    class CreateUser {

        @Test
        @DisplayName("should return the created user")
        void createUser_nonExistingUser_returnsUser() {
            UserRequest userRequest = new UserRequest(userJohnDoe.getUsername(), userJohnDoe.getEmail());

            when(userRepository.save(any(User.class))).thenReturn(userJohnDoe);
            when(userRepository.findByUsername(userRequest.username())).thenReturn(Optional.empty());
            when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.empty());

            UserResponse userResponse = userService.createUser(userRequest);

            assertThat(userResponse).isNotNull();
            assertThat(userResponse.id()).isEqualTo(userJohnDoe.getId());
            assertThat(userResponse.username()).isEqualTo(userJohnDoe.getUsername());
            assertThat(userResponse.email()).isEqualTo(userJohnDoe.getEmail());

            verify(userRepository, times(1)).save(any(User.class));
            verify(userRepository, times(1)).findByUsername(userRequest.username());
            verify(userRepository, times(1)).findByEmail(userRequest.email());
        }

        @Test
        @DisplayName("should throw UserUsernameExistsException when username is used by another user")
        void createUser_existingUsername_throwsUserUsernameExistsException() {
            UserRequest userRequest = new UserRequest(userJohnDoe.getUsername(), userJohnDoe.getEmail());

            when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.empty());
            when(userRepository.findByUsername(userRequest.username())).thenReturn(Optional.of(userJohnDoe));

            assertThatThrownBy(() -> userService.createUser(userRequest))
                    .isInstanceOf(UserUsernameExistsException.class)
                    .extracting("userUsername")
                    .isEqualTo(userRequest.username());

            verify(userRepository, times(0)).save(any(User.class));
            verify(userRepository, times(1)).findByUsername(userRequest.username());
            verify(userRepository, times(1)).findByEmail(userRequest.email());
        }

        @Test
        @DisplayName("should throw UserEmailExistsException when email is used by another user")
        void createUser_existingEmail_throwsUserEmailExistsException() {
            UserRequest userRequest = new UserRequest(userJohnDoe.getUsername(), userJohnDoe.getEmail());

            when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.of(userJohnDoe));

            assertThatThrownBy(() -> userService.createUser(userRequest))
                    .isInstanceOf(UserEmailExistsException.class)
                    .extracting("userEmail")
                    .isEqualTo(userRequest.email());

            verify(userRepository, times(0)).save(any(User.class));
            verify(userRepository, times(0)).findByUsername(userRequest.username());
            verify(userRepository, times(1)).findByEmail(userRequest.email());
        }
    }

    @Nested
    @DisplayName("getAllUsers method")
    class GetAllUsers {
        @Test
        @DisplayName("should return all users")
        void getAllUsers_existingUsers_returnsAllUsers() {
            List<User> users = Arrays.asList(userJohnDoe, userMarySmith);

            when(userRepository.findAll()).thenReturn(users);

            List<UserResponse> userResponses = userService.getAllUsers();

            assertThat(userResponses).isNotNull();
            assertThat(userResponses).hasSize(users.size());
            assertThat(userResponses)
                    .extracting("id", "username")
                    .contains(tuple(userJohnDoe.getId(), userJohnDoe.getUsername()))
                    .contains(tuple(userMarySmith.getId(), userMarySmith.getUsername()));

            verify(userRepository, times((1))).findAll();
        }

        @Test
        @DisplayName("should return empty list if no users exist")
        void getAllUsers_nonExistingUsers_returnsEmptyList() {
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            List<UserResponse> userResponses = userService.getAllUsers();

            assertThat(userResponses).isNotNull();
            assertThat(userResponses).isEmpty();

            verify(userRepository, times((1))).findAll();
        }
    }

    @Nested
    @DisplayName("getUserById method")
    class GetUserById {
        @Test
        @DisplayName("should return the user if user id exists")
        void getUserById_existingUser_returnsUser() {
            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));

            UserResponse userResponse = userService.getUserById(userJohnDoe.getId());

            assertThat(userResponse).isNotNull();
            assertThat(userResponse.id()).isEqualTo(userJohnDoe.getId());
            assertThat(userResponse.username()).isEqualTo(userJohnDoe.getUsername());
            assertThat(userResponse.email()).isEqualTo(userJohnDoe.getEmail());

            verify(userRepository, times((1))).findById(userJohnDoe.getId());
        }

        @Test
        @DisplayName("should throw UserNotFoundException if user id does not exist")
        void getUserById_nonExistingUser_throwsUserNotFoundException() {
            when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(nonExistingUserId))
                    .isInstanceOf(UserNotFoundException.class)
                    .extracting("userId")
                    .isEqualTo(nonExistingUserId);

            verify(userRepository, times((1))).findById(nonExistingUserId);
        }
    }
}