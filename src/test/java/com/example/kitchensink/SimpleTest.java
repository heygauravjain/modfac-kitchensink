package com.example.kitchensink;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.example.kitchensink.config.TestApplicationConfig;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestApplicationConfig.class)
class SimpleTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
    }

    @Test
    void basicTest() {
        // Simple test to ensure tests are running
        assert true;
    }
} 