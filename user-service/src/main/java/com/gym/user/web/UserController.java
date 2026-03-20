package com.gym.user.web;

import com.gym.user.service.UserManagementService;
import com.gym.user.web.dto.CreateUserRequest;
import com.gym.user.web.dto.UpdateUserStatusRequest;
import com.gym.user.web.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserManagementService userManagementService;

    public UserController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @PostMapping
    public Mono<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return userManagementService.createUser(request);
    }

    @GetMapping
    public Flux<UserResponse> listUsers() {
        return userManagementService.listUsers();
    }

    @PatchMapping("/{userId}/status")
    public Mono<UserResponse> updateStatus(@PathVariable("userId") UUID userId,
                                           @Valid @RequestBody UpdateUserStatusRequest request) {
        return userManagementService.updateStatus(userId, request);
    }
}
