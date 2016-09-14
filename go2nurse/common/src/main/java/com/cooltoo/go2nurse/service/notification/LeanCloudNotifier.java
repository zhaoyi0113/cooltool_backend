package com.cooltoo.go2nurse.service.notification;

import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by hp on 2016/9/13.
 */
public class LeanCloudNotifier {

    @Value("${leancloud_msg_push_url}")
    private String leanCloudMsgPushUrl;
    @Value("${leancloud_id}")
    private String leanCloudId;
    @Value("${leancloud_key}")
    private String leanCloudKey;

    public void pushToUser(String token, String channel, String jsonData) {
        try {
            URL targetUrl = new URL(leanCloudMsgPushUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("X-LC-Id", leanCloudId);
            httpConnection.setRequestProperty("X-LC-Key", leanCloudKey);
            httpConnection.setRequestProperty("Content-Type", "application/json");

            LeanCloudMessageBean msg = new LeanCloudMessageBean();
            msg.setDeviceToken(token);
            msg.setChannels(new String[]{channel});
            msg.setData(jsonData);
            System.out.println(msg.getJson());

            OutputStream outputStream = httpConnection.getOutputStream();
            outputStream.write(msg.getJson().getBytes());
            outputStream.flush();

            if (httpConnection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "+ httpConnection.getResponseCode());
            }

            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader((httpConnection.getInputStream())));

            String output;
            System.out.println("Output from Server:\n");
            while ((output = responseBuffer.readLine()) != null) {
                System.out.println(output);
            }

            httpConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        LeanCloudNotifier notifier = new LeanCloudNotifier();
        notifier.leanCloudMsgPushUrl="https://leancloud.cn/1.1/push";
        notifier.leanCloudId="eibNA1jXA0ECjqCRikaIp0qS-gzGzoHsz";
        notifier.leanCloudKey="qEeYLPJaKYUf5buvzhCokDXl";
        notifier.pushToUser("b8f93bf3-8b7d-4515-88c0-f8db387d6e22", "public", "\"data\":{\"alert\":\"LeanCloud 向您问好！\"}");
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

        public void setDeviceToken(String deviceToken) {
            StringBuilder msg = new StringBuilder();
            msg.append("{");
            msg.append(doubleQuote).append("installationId").append(doubleQuote).append(":").append(doubleQuote).append(deviceToken).append(doubleQuote);
            msg.append("}");
            this.where = msg.toString();
            if (null==deviceToken || deviceToken.trim().length()==0) {
                this.where = null;
            }
        }

        public void setData(String jsonData) {
            this.jsonData = jsonData;
        }

        public String getJson() {
            StringBuilder msg = new StringBuilder();
            msg.append("{");
            if (null==channels) {
                msg.append(doubleQuote).append("channels").append(doubleQuote).append(":").append(channels).append(",");
            }
            if (null==where) {
                msg.append(doubleQuote).append("where").append(doubleQuote).append(":").append(where).append(",");
            }
            msg.append(doubleQuote).append("data").append(doubleQuote).append(":").append(jsonData);
            msg.append("}");
            return msg.toString();
        }
    }
}
