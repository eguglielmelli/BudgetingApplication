package com.eguglielmelli.controllers;
import com.eguglielmelli.exceptions.UserNotFoundException;
import com.eguglielmelli.models.Account;
import com.eguglielmelli.models.Category;
import com.eguglielmelli.models.User;
import com.eguglielmelli.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User newUser = userService.createUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
    @PutMapping("/{userId}/password")
    public ResponseEntity<?> updatePassword(@PathVariable Long userId,
                                            @RequestParam String oldPassword,
                                            @RequestParam String newPassword) {
        try {
            userService.changePassword(userId, oldPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long userId) {
        userService.deleteAccount(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        String token = userService.authenticateUser(user);
        if (token != null) {
            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("token", token);
            return ResponseEntity.ok(tokenMap);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
    // GET endpoint to retrieve all accounts for a user
    @GetMapping("/{userId}/accounts")
    public ResponseEntity<List<Account>> getAllAccounts(@PathVariable Long userId) {
        List<Account> accounts = userService.getAllAccountsForUser(userId);
        return ResponseEntity.ok(accounts);
    }

    // GET endpoint to retrieve all categories for a user
    @GetMapping("/{userId}/categories")
    public ResponseEntity<List<Category>> getAllCategories(@PathVariable Long userId) {
        List<Category> categories = userService.getAllCategoriesForUser(userId);
        return ResponseEntity.ok(categories);
    }



}
