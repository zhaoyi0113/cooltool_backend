package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.beans.SpeakTypeBean;
import com.cooltoo.backend.services.NurseIntegrationService;
import com.cooltoo.backend.services.NurseSpeakService;
import com.cooltoo.constants.SpeakType;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzzhao on 5/21/16.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_integration_test_data.xml")
})
public class NurseIntegrationServiceTest extends AbstractCooltooTest {

    @Autowired
    private NurseIntegrationService integrationService;

    @Autowired
    private NurseSpeakService speakService;

    @Test
    public void testPublishSmugSpeak(){
        int point = integrationService.getNurseCommunityIntegration(1, SpeakType.SMUG);

        InputStream inputStream = new ByteArrayInputStream("test".getBytes());
        NurseSpeakBean nurseSpeakBean = speakService.addSmug(1, "test", "file", inputStream);

        integrationService.nurseSpeakIntegration(0, SpeakType.SMUG, nurseSpeakBean);
        int newPoint = integrationService.getNurseCommunityIntegration(1, SpeakType.SMUG);
        SpeakTypeBean smugPoint = integrationService.getSpeakTypePoint(SpeakType.SMUG);
        Assert.assertEquals(point+smugPoint.getFactor(), newPoint);

        speakService.deleteByIds(1, ""+nurseSpeakBean.getId());
        newPoint = integrationService.getNurseCommunityIntegration(1, SpeakType.SMUG);
        Assert.assertEquals(point, newPoint);
    }

    @Test
    public void testPublishComplainSpeak(){
        SpeakType speakType = SpeakType.CATHART;
        int point = integrationService.getNurseCommunityIntegration(1, speakType);

        InputStream inputStream = new ByteArrayInputStream("test".getBytes());
        NurseSpeakBean nurseSpeakBean = speakService.addCathart(1, "test","", "file", inputStream);

        integrationService.nurseSpeakIntegration(0, speakType, nurseSpeakBean);
        int newPoint = integrationService.getNurseCommunityIntegration(1, speakType);
        SpeakTypeBean smugPoint = integrationService.getSpeakTypePoint(speakType);
        Assert.assertEquals(point+smugPoint.getFactor(), newPoint);

        speakService.deleteByIds(1, ""+nurseSpeakBean.getId());
        newPoint = integrationService.getNurseCommunityIntegration(1, speakType);
        Assert.assertEquals(point, newPoint);
    }


    @Test
    public void testPublishAskQuestionSpeak(){
        SpeakType speakType = SpeakType.ASK_QUESTION;
        int point = integrationService.getNurseCommunityIntegration(1, speakType);

        InputStream inputStream = new ByteArrayInputStream("test".getBytes());
        NurseSpeakBean nurseSpeakBean = speakService.addAskQuestion(1, "test", "file", inputStream);

        integrationService.nurseSpeakIntegration(0, speakType, nurseSpeakBean);
        int newPoint = integrationService.getNurseCommunityIntegration(1, speakType);
        SpeakTypeBean smugPoint = integrationService.getSpeakTypePoint(speakType);
        Assert.assertEquals(point+smugPoint.getFactor(), newPoint);

        speakService.deleteByIds(1, ""+nurseSpeakBean.getId());
        newPoint = integrationService.getNurseCommunityIntegration(1, speakType);
        Assert.assertEquals(point, newPoint);
    }
}
