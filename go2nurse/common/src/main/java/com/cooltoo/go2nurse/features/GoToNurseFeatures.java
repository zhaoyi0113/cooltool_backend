package com.cooltoo.go2nurse.features;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

/**
 * Created by yzzhao on 6/6/16.
 */
public enum GoToNurseFeatures implements Feature{

    @Label("SMS Code Verification")
    SMS_CODE;


    public boolean isActive(){
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
