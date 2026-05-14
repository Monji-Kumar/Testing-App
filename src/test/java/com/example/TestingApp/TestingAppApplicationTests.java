package com.example.TestingApp;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class TestingAppApplicationTests {

	@BeforeAll
    static void contextLoads() {
        log.info("Context is Loading");
	}

    @Test
    @DisplayName("Display Test 2")
    void test2() {
        log.info("Display Test 2");
    }

    @Test
    @DisplayName("Display Test 1")
    void test1() {
        log.info("Display Test 1");
    }

    @BeforeEach
    void beforeEach() {
        log.info("Before Each call");
    }

    @AfterEach
    void afterEach() {
        log.info("After Each call");
    }

    @AfterAll
    static void contextEnds(){
        log.info("Asoviva Ovari Da");
    }
}
