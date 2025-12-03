package com.cosmocats.marketplace.domain.service;

import com.cosmocats.marketplace.domain.exception.FeatureNotAvailableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CosmoCatServiceTest {

    @Autowired
    private CosmoCatService cosmoCatService;

    @Test
    @DisplayName("Should execute method successfully when COSMO_CATS feature is enabled (true)")
    void getCosmoCats_WhenFeatureIsEnabled_ShouldExecuteSuccessfully() {

        List<String> cats = assertDoesNotThrow(
                () -> cosmoCatService.getCosmoCats(),
                "Aspect should not have blocked this method!"
        );

        assertNotNull(cats);
        assertEquals(3, cats.size());
        assertTrue(cats.contains("Captain Meow")); // Asserting against specific data
    }

    @Test
    @DisplayName("Should throw FeatureNotAvailableException when KITTY_PRODUCTS feature is disabled (false)")
    void getSpecialKittyProducts_WhenFeatureIsDisabled_ShouldThrowException() {

        FeatureNotAvailableException exception = assertThrows(
                FeatureNotAvailableException.class,
                () -> cosmoCatService.getSpecialKittyProducts(),
                "Aspect should have intercepted this call and thrown an exception."
        );

        String expectedMessage = "Function 'KITTY_PRODUCTS' disabled";
        assertEquals(expectedMessage, exception.getMessage());
    }
}