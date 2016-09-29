package com.cooltoo.go2nurse.service;

import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.google.common.io.CharStreams;
import com.google.gson.JsonSyntaxException;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Event;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 16/9/29.
 */
@Service("ChargeWebHookService")
public class ChargeWebHookService {

    private static final Logger logger = LoggerFactory.getLogger(ChargeWebHookService.class);

    @Autowired private ServiceOrderService orderService;
    @Autowired private Go2NurseUtility utility;

    public Object webHookBody(HttpServletRequest request) {
        logger.info("receive web hooks");
        if (null==request) {
            logger.warn("http servlet request is null");
            return null;
        }

        ServletInputStream inputStream;
        try { inputStream = request.getInputStream(); } catch (IOException io) { inputStream = null; }
        if (null==inputStream) {
            logger.warn("get http servlet request's input stream is null");
            return null;
        }

        String body;
        try { body = CharStreams.toString(new InputStreamReader(inputStream)); } catch (IOException io) { body = null; }
        if (null==body) {
            logger.warn("get http servlet request's body is null");
            return null;
        }

        logger.info("receive body={}", body);

        // ping++ charge
        body = body.trim();
        Event event;
        try { event = Event.GSON.fromJson(body, Event.class); } catch (JsonSyntaxException josnEx) { event = null; }
        if (null!=event) {
            logger.info("this is ping++ charge");
            Charge charge = (Charge)event.getData().getObject();
            orderService.orderChargeWebhooks(charge.getId(), event.getId(), body);
            return event;
        }

        // weixin charge
        Document document;
        SAXReader reader = new SAXReader();
        try { document = reader.read(new ByteArrayInputStream(body.getBytes())); }
        catch (DocumentException doc) { document = null; }
        if (null!=document) {
            logger.info("this is WeiXin charge, message={}", body);

            return weixinCharge(document);
        }

        return null;
    }

    private String weixinCharge(Document document) {
        Map<String,String> map = new HashMap<>();
        try {
            Element root = document.getRootElement();
            List<Element> elem = root.elements();
            for (Element e : elem) { map.put(e.getName(), e.getText()); }
        }
        catch (Exception ex) {
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[notification message format error]]></return_msg></xml>";
        }

        String returnCode = map.get("return_code");
        if (returnCode.equalsIgnoreCase("success")) {
            String outTradeNo = map.get("out_trade_no");
            orderService.orderChargeWebhooks(outTradeNo, outTradeNo, utility.toJsonString(map));
            //TODO -- wait for check the sign of WeiXin callback
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        }
        else {
            logger.info("payment fail for WeiXin, message={}");
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[notification message is 'FAIL']]></return_msg></xml>";
        }
    }
}
