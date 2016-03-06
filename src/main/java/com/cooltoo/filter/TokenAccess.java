package com.cooltoo.filter;

/**
 * Created by yzzhao on 3/6/16.
 */
public @interface TokenAccess {

    boolean requireAccessToken() default false;
}
