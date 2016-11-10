package com.cooltoo.go2nurse.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/9.
 */
public enum HttpRequestType {
      GET
    , POST
    , PUT
    , DELETE
    , HEAD
    , OPTIONS
    ;

    public static HttpRequestType parseString(String type) {
        HttpRequestType ret = null;
        if (GET.name().equalsIgnoreCase(type)) {
            ret = GET;
        }
        else if (POST.name().equalsIgnoreCase(type)) {
            ret = POST;
        }
        else if (PUT.name().equalsIgnoreCase(type)) {
            ret = PUT;
        }
        else if (DELETE.name().equalsIgnoreCase(type)) {
            ret = DELETE;
        }
        else if (HEAD.name().equalsIgnoreCase(type)) {
            ret = HEAD;
        }
        else if (OPTIONS.name().equalsIgnoreCase(type)) {
            ret = OPTIONS;
        }
        return ret;
    }

    public static HttpRequestType parseInt(int type) {
        HttpRequestType ret = null;
        if (GET.ordinal() == type) {
            ret = GET;
        }
        else if (POST.ordinal() == type) {
            ret = POST;
        }
        else if (PUT.ordinal() == type) {
            ret = PUT;
        }
        else if (DELETE.ordinal() == type) {
            ret = DELETE;
        }
        else if (HEAD.ordinal() == type) {
            ret = HEAD;
        }
        else if (OPTIONS.ordinal() == type) {
            ret = OPTIONS;
        }
        return ret;
    }

    public static List<HttpRequestType> getAll() {
        List<HttpRequestType> all = new ArrayList<>();
        all.add(HttpRequestType.GET);
        all.add(HttpRequestType.POST);
        all.add(HttpRequestType.PUT);
        all.add(HttpRequestType.DELETE);
        all.add(HttpRequestType.HEAD);
        all.add(HttpRequestType.OPTIONS);
        return all;
    }
}
