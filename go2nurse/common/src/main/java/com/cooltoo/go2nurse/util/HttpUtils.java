package com.cooltoo.go2nurse.util;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yzzhao on 8/17/16.
 */
public final class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    public static Map<String, String> getRequest(String url){
        try {
            HttpEntity entity = getHttpResponseEntity(url);
            String body = EntityUtils.toString(entity, "UTF-8").trim();
            Gson gson = new Gson();
            Map map = gson.fromJson(body, Map.class);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new HashMap<>();
    }

    public static <T> T getHttpRequest(String url, Class<T> returnType){
        try {
            HttpEntity entity = getHttpResponseEntity(url);
            String body = EntityUtils.toString(entity, "UTF-8").trim();
            logger.info("get http response "+body);
            Gson gson = new Gson();
            T ret = gson.fromJson(body, returnType);
            return ret;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static HttpEntity getHttpResponseEntity(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(httpGet);
        return response.getEntity();
    }
}
