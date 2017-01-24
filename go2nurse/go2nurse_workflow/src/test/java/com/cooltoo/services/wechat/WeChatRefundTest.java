package com.cooltoo.services.wechat;

import com.cooltoo.go2nurse.openapp.WeChatPayService;

/**
 * Created by zhaolisong on 23/01/2017.
 */
public class WeChatRefundTest {

    public static void main(String[] args) {
        WeChatPayService weChatPayService = new WeChatPayService();
        weChatPayService.refundByWeChat(
                "wxbeec5531b4ae001a",
                "1252899001",
                "WEB",
                "4001032001201611058807648016",
                "mRFMo4gUKKF0Jb5BFbE3CF6OJ1DHy1H",
                "ReFundTradeNoTest00000000000000000001",
                1,
                1,
                "CNY",
                null,
                "/Users/zhaolisong/Downloads/cert-3-1252899001/apiclient_cert.p12");
/*

        {
                "transaction_id":"4001032001201611058807648016",
                "nonce_str":"CgcHoJvxHPnGPUZrC5XdpGcJA6OjeeA",
                "bank_type":"CFT","openid":"oFmjasjc8rv0E8Gq63IHGD5fP49A",
                "fee_type":"CNY",
                "mch_id":"1252899001",
                "cash_fee":"1",
                "device_info":"WEB",
                "out_trade_no":"mRFMo4gUKKF0Jb5BFbE3CF6OJ1DHy1H",
                "appid":"wxbeec5531b4ae001a",
                "total_fee":"1",
                "trade_type":"JSAPI",
                "result_code":"SUCCESS",
                "time_end":"20161105123237",
                "is_subscribe":"Y",
                "return_code":"SUCCESS"
        }
*/
    }
}
