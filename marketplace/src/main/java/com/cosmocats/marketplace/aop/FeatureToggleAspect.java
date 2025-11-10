package com.cosmocats.marketplace.aop;

import com.cosmocats.marketplace.config.features.Feature;
import com.cosmocats.marketplace.config.features.RequiresFeature;
import com.cosmocats.marketplace.domain.exception.FeatureNotAvailableException;
import com.cosmocats.marketplace.domain.service.FeatureToggleService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FeatureToggleAspect {

    private final FeatureToggleService featureToggleService;

    public FeatureToggleAspect(FeatureToggleService featureToggleService) {
        this.featureToggleService = featureToggleService;
    }

    @Around("@annotation(requiresFeature)")
    public Object checkFeatureToggle(ProceedingJoinPoint joinPoint, RequiresFeature requiresFeature) throws Throwable {
        Feature feature = requiresFeature.value();

        boolean isEnabled = isFeatureEnabled(feature);

        if (isEnabled) {
            return joinPoint.proceed();
        } else {
            throw new FeatureNotAvailableException(feature.name());
        }
    }

    private boolean isFeatureEnabled(Feature feature) {
        return switch (feature) {
            case COSMO_CATS -> featureToggleService.isCosmoCatsEnabled();
            case KITTY_PRODUCTS -> featureToggleService.isKittyProductsEnabled();
        };
    }
}
