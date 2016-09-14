package com.cooltoo.go2nurse.service.notification;

import com.cooltoo.constants.DeviceType;
import com.cooltoo.go2nurse.beans.UserDeviceTokensBean;
import com.cooltoo.go2nurse.service.UserDeviceTokensService;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/9/13.
 */
@Component
public class LeanCloudNotifier {

    private static final Logger logger = LoggerFactory.getLogger(LeanCloudNotifier.class);

    @Value("${leancloud_msg_push_url}")
    private String leanCloudMsgPushUrl;
    @Value("${leancloud_id}")
    private String leanCloudId;
    @Value("${leancloud_key}")
    private String leanCloudKey;

    @Autowired private Go2NurseUtility utility;

    public void publishToDevice(String token, String[] channel, MessageBean customJson) {
        logger.info("token={} receive channel={} customJson={}", token, channel, customJson);
        pushToToken(DeviceType.Android.getDeviceType(), token, channel, customJson);
    }

    public void publishToDevices(List<String> tokens, String[] channel, MessageBean customJson) {
        logger.info("tokens={} receive channel={} customJson={}", tokens, channel, customJson);
        for (int i=0; i<tokens.size(); i++) {
            String tmp = tokens.get(i);
            pushToToken(DeviceType.Android.getDeviceType(), tmp, channel, customJson);
        }
    }

    private void pushToToken(String deviceType, String token, String[] channel, MessageBean customJson) {
        try {
            URL targetUrl = new URL(leanCloudMsgPushUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("X-LC-Id", leanCloudId);
            httpConnection.setRequestProperty("X-LC-Key", leanCloudKey);
            httpConnection.setRequestProperty("Content-Type", "application/json");

            LeanCloudMessageBean msg = new LeanCloudMessageBean();
            msg.setDeviceTypeAndToken(deviceType, token);
            msg.setChannels(channel);
            msg.setData(utility.toJsonString(customJson));

            OutputStream outputStream = httpConnection.getOutputStream();
            outputStream.write(msg.getJson().getBytes());
            outputStream.flush();

            logger.info("http code:{}", httpConnection.getResponseCode());
            String output;
            StringBuilder outputMessage = new StringBuilder();
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader((httpConnection.getInputStream())));
            while ((output = responseBuffer.readLine()) != null) {
                outputMessage.append(output);
            }
            logger.info("http message from server:{}", outputMessage.toString());

            httpConnection.disconnect();
        } catch (MalformedURLException e) {
            logger.info("leancloud push message error:{}", e.getMessage());
        } catch (IOException e) {
            logger.info("leancloud push message error:{}", e.getMessage());
        }

    }

    public static class LeanCloudMessageBean {
        String doubleQuote = "\"";
        private String channels;
        private String where;
        private String jsonData;

        public void setChannels(String[] channels) {
            StringBuilder msg = new StringBuilder();
            msg.append("[");
            int count = null==channels ? 0 : channels.length;
            for (int i=0; i<count; i++) {
                msg.append(doubleQuote).append(channels[i]).append(doubleQuote);
                if ((i+1)!=count) {
                    msg.append(",");
                }
            }
            msg.append("]");
            this.channels = msg.toString();
            if (null==channels || channels.length==0) {
                this.channels=null;
            }
        }

        public void setDeviceTypeAndToken(String deviceType, String deviceToken) {
            StringBuilder msg = new StringBuilder();
            msg.append("{");
            msg.append(doubleQuote).append("deviceType").append(doubleQuote).append(":").append(doubleQuote).append(deviceType).append(doubleQuote);
            msg.append(",");
            msg.append(doubleQuote).append("installationId").append(doubleQuote).append(":").append(doubleQuote).append(deviceToken).append(doubleQuote);
            msg.append("}");
            this.where = msg.toString();
            if (null==deviceToken || deviceToken.trim().length()==0) {
                this.where = null;
            }
            if (null==deviceType || deviceType.trim().length()==0) {
                this.where = null;
            }
        }

        public void setData(String jsonData) {
            this.jsonData = jsonData;
        }

        public String getJson() {
            StringBuilder msg = new StringBuilder();
            msg.append("{");
            if (null!=channels) {
                msg.append(doubleQuote).append("channels").append(doubleQuote).append(":").append(channels).append(",");
            }
            if (null!=where) {
                msg.append(doubleQuote).append("where").append(doubleQuote).append(":").append(where).append(",");
            }
            msg.append(doubleQuote).append("data").append(doubleQuote).append(":").append(jsonData);
            msg.append("}");
            return msg.toString();
        }
    }
}
