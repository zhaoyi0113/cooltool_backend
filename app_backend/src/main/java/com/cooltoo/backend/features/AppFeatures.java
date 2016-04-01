package com.cooltoo.backend.features;

import org.springframework.context.annotation.Bean;
import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;
import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.spi.FeatureProvider;

/**
 * Created by yzzhao on 3/30/16.
 */
public enum AppFeatures implements Feature {


    @Label("SMS Code Verification")
    SMS_CODE;


    public boolean isSMSCodeVerificationEnabled(){
        return FeatureContext.getFeatureManager().isActive(this);
    }
}


