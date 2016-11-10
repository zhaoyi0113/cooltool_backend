package com.cooltoo.go2nurse.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/9.
 */
public enum RequestMethod {
      GET
    , POST
    , PUT
    , DELETE
    , HEAD
    , OPTIONS
    ;

    public static RequestMethod parseString(String type) {
        RequestMethod ret = null;
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

    public static RequestMethod parseInt(int type) {
        RequestMethod ret = null;
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

    public static List<RequestMethod> getAll() {
        List<RequestMethod> all = new ArrayList<>();
        all.add(RequestMethod.GET);
        all.add(RequestMethod.POST);
        all.add(RequestMethod.PUT);
        all.add(RequestMethod.DELETE);
        all.add(RequestMethod.HEAD);
        all.add(RequestMethod.OPTIONS);
        return all;
    }
}
