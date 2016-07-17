package com.cooltoo.go2nurse.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/6/12.
 */
@Component
public class Go2NurseUtility {

    private static final Logger logger = LoggerFactory.getLogger(Go2NurseUtility.class);

    @Value("${go2nurse.nginx.prefix}")
    private String httpPrefix;

    public String getHttpPrefix() {
        return httpPrefix;
    }

    public <T> T parseJsonList(String content, Class clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeFactory typeFactory = mapper.getTypeFactory();
            T returnValue =  mapper.readValue(content, typeFactory.constructCollectionType(List.class, clazz));
            if (null==returnValue) {
                return (T) new ArrayList<T>();
            }
            return returnValue;
        }
        catch (Exception ex) {
            logger.error("parse the json list error, json={}, class={}", content, clazz);
            return (T) new ArrayList<T>();
        }
    }

    public <T> T parseJsonBean(String content, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(content, clazz);
        }
        catch (Exception ex) {
            logger.error("parse the json list error, json={}, class={}", content, clazz);
            return null;
        }
    }

    public String toJsonString(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        }
        catch (Exception ex) {
            return null;
        }
    }
}
