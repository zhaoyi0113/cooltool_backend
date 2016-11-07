package com.cooltoo.nurse360.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/9/28.
 */
@Component
public class Nurse360Utility {

    private static final Logger logger = LoggerFactory.getLogger(Nurse360Utility.class);

    @Value("${nurse360.nginx.prefix}")
    private String httpPrefix;

    @Value("${nurse360.nginx.prefix.for.nursego}")
    private String httpPrefixForNurseGo;
    @Value("${storage.user.path}")
    private String nursegoUserPath;
    @Value("${storage.official.path}")
    private String nursegoOfficialPath;

    public String getHttpPrefix() {
        return httpPrefix;
    }

    public String getHttpPrefixForNurseGo() {
        return httpPrefixForNurseGo;
    }

    public String getHttpPrefixUserPathForNurseGo() {
        return httpPrefixForNurseGo+nursegoUserPath;
    }

    public String getHttpPrefixOfficialPathForNurseGo() {
        return httpPrefixForNurseGo+nursegoOfficialPath;
    }
}
