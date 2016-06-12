package com.cooltoo.go2nurse.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/12.
 */
@Component
public class Go2NurseUtility {

    @Value("${go2nurse.nginx.prefix}")
    private String httpPrefix;

    public String getHttpPrefix() {
        return httpPrefix;
    }
}
