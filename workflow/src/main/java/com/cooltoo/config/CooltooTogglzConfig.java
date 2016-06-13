package com.cooltoo.config;

import com.cooltoo.features.AppFeatures;
import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.user.UserProvider;

/**
 * Created by yzzhao on 6/8/16.
 */
//@Component
public class CooltooTogglzConfig implements TogglzConfig {

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
