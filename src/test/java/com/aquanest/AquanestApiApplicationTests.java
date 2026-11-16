//package com.aquanest;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class AquanestApiApplicationTests {
//
//	@Test
//	void contextLoads() {
//	}
//
//}





package com.aquanest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AquanestApiApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring Boot application context starts.
    }
}
