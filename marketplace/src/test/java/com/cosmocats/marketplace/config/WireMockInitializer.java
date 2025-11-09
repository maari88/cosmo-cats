package com.cosmocats.marketplace.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Map;

public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().dynamicPort());
        wireMockServer.start();

        context.addApplicationListener(event -> {
            if (event instanceof ContextClosedEvent) {
                wireMockServer.stop();
            }
        });

        String wiremockBaseUrl = wireMockServer.baseUrl();

        System.setProperty("WIREMOCK_BASE_URL", wiremockBaseUrl);

        context.getEnvironment()
                .getSystemProperties()
                .putAll(Map.of("WIREMOCK_BASE_URL", wiremockBaseUrl));
    }
}
