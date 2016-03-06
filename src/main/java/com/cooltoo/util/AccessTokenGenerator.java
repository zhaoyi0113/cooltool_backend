package com.cooltoo.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by yzzhao on 1/10/16.
 */
@Component
public class AccessTokenGenerator {

    public static final String DEFAULT_ENCODING = "UTF-8";

    public String generateAccessToken(String id, String saltedPassword) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.write(id.getBytes(DEFAULT_ENCODING));
            dataOutputStream.write(saltedPassword.getBytes(DEFAULT_ENCODING));
            dataOutputStream.write(Calendar.getInstance().getTime().toString().getBytes());
            dataOutputStream.flush();
            return DigestUtils.md2Hex(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
