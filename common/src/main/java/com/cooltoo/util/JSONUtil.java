package com.cooltoo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/7.
 */
public class JSONUtil {

    private static final Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    public static JSONUtil newInstance() {
        return new JSONUtil();
    }

    private JSONUtil(){}

    public <T> T parseJsonMap(String content, Class keyType, Class valueType) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeFactory typeFactory = mapper.getTypeFactory();
            T returnValue =  mapper.readValue(content, typeFactory.constructMapType(HashMap.class, keyType, valueType));
            if (null==returnValue) {
                return (T) new HashMap<T, T>();
            }
            return returnValue;
        }
        catch (Exception ex) {
            logger.warn("parse the json map error, json={}, keyClass={} valueClass={} throwable={}", content, keyType, valueType, ex.getMessage());
            return (T) new HashMap<T, T>();
        }
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
            logger.warn("parse the json list error, json={}, class={} throwable={}", content, clazz, ex.getMessage());
            return (T) new ArrayList<T>();
        }
    }

    public <T> T parseJsonBean(String content, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(content, clazz);
        }
        catch (Exception ex) {
            logger.error("parse the json list error, json={}, class={} throwable={}", content, clazz, ex.getMessage());
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
