package com.gym.user.bootstrap;

import com.gym.user.config.BootstrapAdminProperties;
import com.gym.user.domain.Role;
import com.gym.user.domain.UserAccount;
import com.gym.user.repository.UserAccountRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class AdminBootstrapper implements ApplicationRunner {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final BootstrapAdminProperties bootstrapAdminProperties;

    public AdminBootstrapper(UserAccountRepository userAccountRepository,
                             PasswordEncoder passwordEncoder,
                             BootstrapAdminProperties bootstrapAdminProperties) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.bootstrapAdminProperties = bootstrapAdminProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        userAccountRepository.existsByUsername(bootstrapAdminProperties.username())
                .filter(exists -> !exists)
                .flatMap(ignored -> {
                    Instant now = Instant.now();
                    UserAccount admin = UserAccount.builder()
                            .id(UUID.randomUUID())
                            .username(bootstrapAdminProperties.username())
                            .email(bootstrapAdminProperties.email())
                            .passwordHash(passwordEncoder.encode(bootstrapAdminProperties.password()))
                            .role(Role.ROLE_ADMIN)
                            .active(true)
                            .createdAt(now)
                            .updatedAt(now)
                            .build();
                    return userAccountRepository.save(admin);
                })
                .subscribe();
    }
}

