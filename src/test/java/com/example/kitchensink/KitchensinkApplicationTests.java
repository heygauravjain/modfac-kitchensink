package com.example.kitchensink;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.example.kitchensink.config.TestApplicationConfig;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestApplicationConfig.class)
class KitchenSinkApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
    }
}
