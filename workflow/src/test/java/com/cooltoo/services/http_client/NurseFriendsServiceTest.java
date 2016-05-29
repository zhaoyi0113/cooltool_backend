package com.cooltoo.services.http_client;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 2016/5/29.
 */
public class NurseFriendsServiceTest {
    private String url = "http://localhost:8080/nursego";
    private String charset = "utf-8";
    private String token ="1";
    private UtilityHttpClient httpClientUtil = null;

    public NurseFriendsServiceTest(){
        httpClientUtil = new UtilityHttpClient();
    }

    public void test(){
        String addFriendship = url + "/nurse/friends/add/2";
        Map<String, String> parameters = new HashMap<>();
        httpClientUtil.doPost(addFriendship, token, parameters, charset);
    }

    public static void main(String[] args){
        final NurseFriendsServiceTest main = new NurseFriendsServiceTest();
        for (int i = 0; i < 10; i ++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    main.test();
                }
            }).start();
        }
    }
}
