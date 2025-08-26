package com.example;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    // Simple in-memory data - no database needed
    private List<Map<String, String>> users = Arrays.asList(
        Map.of("id", "1", "name", "John Doe", "email", "john@example.com"),
        Map.of("id", "2", "name", "Jane Smith", "email", "jane@example.com"),
        Map.of("id", "3", "name", "Bob Johnson", "email", "bob@example.com")
    );

    @GetMapping("/health")
    public String health() {
        return "Application is running!";
    }

    @GetMapping("/users")
    public List<Map<String, String>> getUsers() {
        return users;
    }

    @GetMapping("/users/{id}")
    public Map<String, String> getUser(@PathVariable String id) {
        return users.stream()
                .filter(user -> user.get("id").equals(id))
                .findFirst()
                .orElse(Map.of("error", "User not found"));
    }

    @PostMapping("/users")
    public Map<String, String> createUser(@RequestBody Map<String, String> newUser) {
        String newId = String.valueOf(users.size() + 1);
        newUser.put("id", newId);
        // In real app, you'd add to database. Here we just return the user.
        return newUser;
    }
}
