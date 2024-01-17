package com.eguglielmelli.service;

import com.eguglielmelli.models.User;
import com.eguglielmelli.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        lenient().when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        ReflectionTestUtils.setField(userService, "jwtSecret", "testSecretKey");
    }

    @Test
    void whenCreateUser_thenUserCreated() {
        when(userRepository.findByEmailAndIsDeletedFalse(testUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.createUser(testUser);

        assertNotNull(createdUser);
        assertEquals("Test User", createdUser.getName());
        assertEquals("encodedPassword", createdUser.getPassword()); // Check if password is encoded
        verify(userRepository).save(any(User.class));
    }


    @Test
    void whenAuthenticateUser_thenGenerateJwtToken() {

        when(userRepository.findByEmailAndIsDeletedFalse(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        String jwtToken = userService.authenticateUser(testUser);

        assertNotNull(jwtToken);
    }

    @Test
    void whenChangePassword_thenPasswordUpdated() {
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", testUser.getPassword())).thenReturn(true);


        userService.changePassword(testUser.getUserId(), "oldPassword", "newPassword");

        verify(userRepository).save(testUser);
        verify(passwordEncoder).encode("newPassword");
    }

    @Test
    void whenDeleteAccount_thenAccountMarkedDeleted() {
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));

        userService.deleteAccount(testUser.getUserId());

        assertTrue(testUser.isDeleted());
        verify(userRepository).save(testUser);
    }
}