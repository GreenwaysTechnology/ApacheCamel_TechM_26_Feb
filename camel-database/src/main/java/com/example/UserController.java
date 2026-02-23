package com.example;


import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository    userRepository;
    private final FileLogRepository fileLogRepository;

    public UserController(UserRepository userRepository,
                          FileLogRepository fileLogRepository) {
        this.userRepository    = userRepository;
        this.fileLogRepository = fileLogRepository;
    }

    // ── Users ─────────────────────────────────────────────────────────────────
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    @DeleteMapping("/users")
    public String clearUsers() {
        long count = userRepository.count();
        userRepository.deleteAll();
        return "Deleted " + count + " user(s). Ready for re-test.";
    }

    // ── Audit Logs ────────────────────────────────────────────────────────────
    @GetMapping("/logs")
    public List<CamelFileLog> getLogs() {
        return fileLogRepository.findAll();
    }
}