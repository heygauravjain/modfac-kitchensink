package com.example.kitchensink.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.repository.MemberRepository;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                if (memberRepository.count() == 0) {
                    MemberDocument admin = new MemberDocument();
                    admin.setName("admin");
                    admin.setEmail("admin@admin.com");
                    admin.setPhoneNumber("1234567890");
                    admin.setRole("ROLE_ADMIN");
                    admin.setPassword(passwordEncoder.encode("admin123")); // Encrypt password
                    memberRepository.save(admin);

                    log.info("Inserted default admin member.");

                    MemberDocument user = new MemberDocument();
                    user.setName("user");
                    user.setEmail("user@user.com");
                    user.setPhoneNumber("0987654321");  
                    user.setRole("ROLE_USER");
                    user.setPassword(passwordEncoder.encode("user123")); // Encrypt password
                    memberRepository.save(user);
                    log.info("Inserted default user member.");
                } else {
                    log.info("Members already exist! Skipping default insert.");
                }
            } catch (Exception e) {
                log.error("Error initializing default data: " + e.getMessage());
                throw e;
            }
        };
    }
}