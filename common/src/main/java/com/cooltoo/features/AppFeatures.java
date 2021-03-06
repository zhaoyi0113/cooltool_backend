package com.cooltoo.features;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

/**
 * Created by yzzhao on 3/30/16.
 */
public enum AppFeatures implements Feature {


    @Label("SMS Code Verification")
    SMS_CODE,

    @Label("Send APNS")
    APNS;


    public boolean isActive(){
        return FeatureContext.getFeatureManager().isActive(this);
    }
}


