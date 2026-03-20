package com.gym.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class UserAccount {

    @Id
    private UUID id;
    private String username;
    private String email;
    private String passwordHash;
    private Role role;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    @Version
    private Long version;
}
