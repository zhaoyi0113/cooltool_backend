package com.cooltoo.leancloud;

import com.cooltoo.features.AppFeatures;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.HttpClient;

import java.io.IOException;

/**
 * Created by yzzhao on 3/19/16.
 */
@Service("LeanCloudService")
public class LeanCloudService {

    private static final Logger logger = LoggerFactory.getLogger(LeanCloudService.class);

    @Value("${leancloud_url}")
    private String leanCloudUrl;

    @Value("${leancloud_version}")
    private String version;

    @Value("${leancloud_verify_sms_code}")
    private String verifySmsPath;

    @Value("${leancloud_id}")
    private String appId;

    @Value("${leancloud_key}")
    private String appKey;


    public void verifySmsCode(String code, String mobile){
        if(!AppFeatures.SMS_CODE.isActive()){
            logger.info("sms code verify is disabled ");
            return;
        }
        logger.info("sms code verify is enabled");
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(getVerifySmsUrl()+"/"+code+"?"+mobile);
        post.setHeader("X-LC-Id", appId);
        post.setHeader("X-LC-Key", appKey);
        try {
//            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
//            parameters.add(new BasicNameValuePair(""));
//            httppost.setEntity(new UrlEncodedFormEntity(postParameters));
//            //Just for debugging
//            if (!mobile.isEmpty()) {
//                return;
//            }

            HttpResponse response = httpClient.execute(post);
            String body = EntityUtils.toString(response.getEntity(), "UTF-8");
            logger.info("get verify sms response "+body);
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                logger.info("verify success.");
                return;
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        throw new BadRequestException(ErrorCode.SMS_VERIFY_FAILED);
    }

    private String getVerifySmsUrl(){
        return leanCloudUrl+"/"+version+"/"+verifySmsPath;
    }

}
