package com.cosmocats.marketplace;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;

@SpringBootTest
public abstract class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("cosmocats")
            .withUsername("test")
            .withPassword("test")
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));

    static {
        try {
            postgres.start();
            System.out.println("✅ TESTCONTAINERS STARTED: " + postgres.getJdbcUrl());
        } catch (Exception e) {
            System.err.println("❌ TESTCONTAINERS FAILED TO START:");
            e.printStackTrace();
            throw e;
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.datasource.driverClassName", () -> "org.postgresql.Driver");

        registry.add("spring.liquibase.enabled", () -> "false");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }
}