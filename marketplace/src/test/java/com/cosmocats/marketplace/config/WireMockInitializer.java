package com.cosmocats.marketplace.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final WireMockServer wireMockServer =
            new WireMockServer(new WireMockConfiguration().dynamicPort());

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        if (!wireMockServer.isRunning()) {
            wireMockServer.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (wireMockServer.isRunning()) {
                    wireMockServer.stop();
                }
            }));
        }
        String wiremockBaseUrl = wireMockServer.baseUrl();

        System.setProperty("WIREMOCK_BASE_URL", wiremockBaseUrl);

        context.getEnvironment()
                .getSystemProperties()
                .putAll(Map.of("WIREMOCK_BASE_URL", wiremockBaseUrl));
    }
}