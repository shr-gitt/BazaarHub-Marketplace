package com.bazaarhub.backend.config;

import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.enums.AdminAccessLevel;
import com.bazaarhub.backend.feature.user.enums.Gender;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import com.bazaarhub.backend.shared.enums.Role;
import com.bazaarhub.backend.shared.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeedData implements ApplicationRunner {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args){
        if(!userRepository.existsByEmailAndUserStatusNot("super.admin@bazaarhub.com", UserStatus.DELETED)){
            userRepository.save(
                    new User(
                            "Super",
                            "Admin",
                            "super.admin@bazaarhub.com",
                            "9800000012",
                            Gender.OTHER,
                            passwordEncoder.encode("Admin@1234"),
                            Role.ADMIN,
                            AdminAccessLevel.SUPER,
                            UserStatus.ACTIVE
                    )
            );
        }
    }
}
