package com.cooltoo.nurse360.features;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

/**
 * Created by zhaolisong on 16/9/28.
 */
public enum Nurse360Features implements Feature{

    @Label("SMS Code Verification")
    SMS_CODE,

    @Label("Send APNS")
    APNS;

    public boolean isActive(){
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
