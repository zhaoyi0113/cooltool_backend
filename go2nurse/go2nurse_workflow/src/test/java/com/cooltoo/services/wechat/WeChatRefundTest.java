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
                null, "HsLNUOPYrgEe5cFLLNdVYJXiU02eZb6",
                "ReFundTradeNoTest00000000000000000001",
                1,
                1,
                "CNY",
                null);
    }
}
