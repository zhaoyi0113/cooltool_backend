package com.cooltoo.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yzzhao on 3/2/16.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface LoginAuthentication {
    boolean requireNurseLogin() default false;

    boolean requirePatientLogin() default false;
}
