package com.cooltoo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.togglz.core.Feature;
import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.mem.InMemoryStateRepository;
import org.togglz.core.spi.FeatureProvider;
import org.togglz.core.user.UserProvider;
import org.togglz.spring.boot.autoconfigure.TogglzProperties;

/**
 * Created by yzzhao on 3/30/16.
 */
@Configuration
@ConditionalOnMissingBean(FeatureProvider.class)
@ConditionalOnProperty(name = "togglz.feature-enums")
public class AppTogglzConfig implements TogglzConfig{

    @Bean
    public FeatureProvider featureProvider() {
        return new EnumBasedFeatureProvider(AppFeatures.class);
    }

    @Override
    public Class<? extends Feature> getFeatureClass() {
        return AppFeatures.class;
    }

    @Override
    public StateRepository getStateRepository() {
        return null;
    }

    @Override
    public UserProvider getUserProvider() {
        return null;
    }
}
