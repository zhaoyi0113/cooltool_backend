package com.cooltoo.services.wechat;

import com.cooltoo.go2nurse.openapp.WeChatPayService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Created by zhaolisong on 23/01/2017.
 */
public class WeChatRefundTest {

    public static void main(String[] args) throws FileNotFoundException {
        WeChatPayService weChatPayService = new WeChatPayService();

        File keyFile = new File("go2nurse/go2nurse_workflow/src/test/resources/api_client_cert_1252899001.p12");
        FileInputStream instream = new FileInputStream(keyFile);
        Map<String, String> returnMap = null;
        returnMap = weChatPayService.checkPaymentByWeChat(
                "wxbeec5531b4ae001a",
                "1252899001",
                "4001032001201611058807648016",
                "mRFMo4gUKKF0Jb5BFbE3CF6OJ1DHy1H");
        System.out.println(returnMap);
        System.out.println();
        returnMap = weChatPayService.refundByWeChat(
                "wxbeec5531b4ae001a",
                "1252899001",
                "JSAPI",
                "4001032001201611058807648016",
                "mRFMo4gUKKF0Jb5BFbE3CF6OJ1DHy1H",
                "ReFundTradeNoTest00000000000000000001",
                1,
                1,
                "CNY",
                null,
                instream);
        System.out.println(returnMap);
        System.out.println();
        returnMap = weChatPayService.checkRefundByWeChat("wxbeec5531b4ae001a",
                "1252899001",
                "JSAPI",
                "ReFundTradeNoTest00000000000000000001",
                null,
                null,
                null);
        System.out.println(returnMap);
        System.out.println();
        returnMap = weChatPayService.closeOrderByWeChat("wxbeec5531b4ae001a",
                "1252899001",
                "mRFMo4gUKKF0Jb5BFbE3CF6OJ1DHy1H");
        System.out.println(returnMap);
        System.out.println();
    }
}
