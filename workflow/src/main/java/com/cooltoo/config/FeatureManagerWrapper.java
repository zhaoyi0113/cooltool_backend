package com.cooltoo.config;

import org.togglz.core.manager.FeatureManager;

/**
 * Created by yzzhao on 3/30/16.
 */
public class FeatureManagerWrapper {

    private FeatureManager featureManager;
    private static final FeatureManagerWrapper instance = new FeatureManagerWrapper();

    private FeatureManagerWrapper() {
        // singleton
    }

    public static FeatureManagerWrapper getInstance() {
        return instance;
    }

    public void setFeatureManager(FeatureManager featureManager) {
        if(this.featureManager == null) {
            this.featureManager = featureManager;
        } else {
            throw new UnsupportedOperationException("featureManager is already set, cannot reset.");
        }
    }

    public FeatureManager getFeatureManager() {
        return featureManager;
    }
}
