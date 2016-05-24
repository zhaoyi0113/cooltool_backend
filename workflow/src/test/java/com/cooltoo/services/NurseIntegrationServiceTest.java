package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.*;
import com.cooltoo.backend.converter.social_ability.CommentAbilityTypeConverter;
import com.cooltoo.backend.converter.social_ability.SkillAbilityTypeConverter;
import com.cooltoo.backend.converter.social_ability.SpeakAbilityTypeConverter;
import com.cooltoo.backend.converter.social_ability.ThumbsUpAbilityTypeConverter;
import com.cooltoo.backend.services.NurseIntegrationService;
import com.cooltoo.backend.services.NurseSpeakService;
import com.cooltoo.beans.SpecificSocialAbility;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.constants.UserType;
import com.cooltoo.util.NumberUtil;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by yzzhao on 5/21/16.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/occupation_skill_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/speak_type_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_thumbs_up_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_comment_service_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_integration_test_data.xml")
})
public class NurseIntegrationServiceTest extends AbstractCooltooTest {

    @Autowired private NurseIntegrationService      integrationService;
    @Autowired private SpeakAbilityTypeConverter    speakAbilityTypeConverter;
    @Autowired private CommentAbilityTypeConverter  commentAbilityTypeConverter;
    @Autowired private ThumbsUpAbilityTypeConverter thumbsUpAbilityTypeConverter;
    @Autowired private SkillAbilityTypeConverter    skillAbilityTypeConverter;
    @Autowired private NurseSpeakService            speakService;

    @Test
    public void testPublishSmugSpeak(){
        int point = integrationService.getNurseCommunityIntegration(1, SpeakType.SMUG);
        NurseSpeakBean nurseSpeakBean = speakService.addSmug(1, "test", "file", null);

        integrationService.addNurseSpeakIntegration(0, SpeakType.SMUG, nurseSpeakBean);
        int newPoint = integrationService.getNurseCommunityIntegration(1, SpeakType.SMUG);
        SpecificSocialAbility smugPoint = integrationService.getSpeakTypePoint(SpeakType.SMUG);
        Assert.assertEquals(point+smugPoint.getFactor(), newPoint);

        speakService.deleteByIds(1, ""+nurseSpeakBean.getId());
        newPoint = integrationService.getNurseCommunityIntegration(1, SpeakType.SMUG);
        Assert.assertEquals(point+smugPoint.getFactor(), newPoint);
    }

    @Test
    public void testPublishComplainSpeak(){
        SpeakType speakType = SpeakType.CATHART;
        int point = integrationService.getNurseCommunityIntegration(1, speakType);
        NurseSpeakBean nurseSpeakBean = speakService.addCathart(1, "test","", "file", null);

        integrationService.addNurseSpeakIntegration(0, speakType, nurseSpeakBean);
        int newPoint = integrationService.getNurseCommunityIntegration(1, speakType);
        SpecificSocialAbility complainPoint = integrationService.getSpeakTypePoint(speakType);
        Assert.assertEquals(point+complainPoint.getFactor(), newPoint);

        speakService.deleteByIds(1, ""+nurseSpeakBean.getId());
        newPoint = integrationService.getNurseCommunityIntegration(1, speakType);
        Assert.assertEquals(point+complainPoint.getFactor(), newPoint);
    }


    @Test
    public void testPublishAskQuestionSpeak(){
        SpeakType speakType = SpeakType.ASK_QUESTION;
        int point = integrationService.getNurseCommunityIntegration(1, speakType);
        NurseSpeakBean nurseSpeakBean = speakService.addAskQuestion(1, "test", "file", null);

        integrationService.addNurseSpeakIntegration(0, speakType, nurseSpeakBean);
        int newPoint = integrationService.getNurseCommunityIntegration(1, speakType);
        SpecificSocialAbility askPoint = integrationService.getSpeakTypePoint(speakType);
        Assert.assertEquals(point+askPoint.getFactor(), newPoint);

        speakService.deleteByIds(1, ""+nurseSpeakBean.getId());
        newPoint = integrationService.getNurseCommunityIntegration(1, speakType);
        Assert.assertEquals(point+askPoint.getFactor(), newPoint);
    }

    @Test
    public void testPublishSpeakCommentAndAnswer() {
        // 评论
        long speakId = 14;
        long maker = 4;
        long receiver = 5;
        String comment = "ping lun";
        int point = integrationService.getNurseCommentIntegration(maker);
        NurseSpeakCommentBean speakComment = speakService.addSpeakComment(speakId, maker, receiver, comment);

        integrationService.addNurseCommentIntegration(speakComment);
        int newPoint = integrationService.getNurseCommentIntegration(maker);
        SpecificSocialAbility commentPoint = commentAbilityTypeConverter.getItem(CommentAbilityTypeConverter.COMMENT);
        Assert.assertEquals(point+commentPoint.getFactor(), newPoint);

        receiver = 3;
        speakComment = speakService.addSpeakComment(speakId, maker, receiver, comment);

        integrationService.addNurseCommentIntegration(speakComment);
        newPoint = integrationService.getNurseCommentIntegration(maker);
        commentPoint = commentAbilityTypeConverter.getItem(CommentAbilityTypeConverter.COMMENT);
        Assert.assertEquals(point+commentPoint.getFactor(), newPoint);

        // 答题
        speakId = 15;
        maker = 4;
        receiver = 5;
        comment = "da ti";
        point = integrationService.getNurseAnswerIntegration(maker);
        speakComment = speakService.addSpeakComment(speakId, maker, receiver, comment);

        integrationService.addNurseCommentIntegration(speakComment);
        newPoint = integrationService.getNurseAnswerIntegration(maker);
        commentPoint = commentAbilityTypeConverter.getItem(CommentAbilityTypeConverter.ANSWER);
        Assert.assertEquals(point+commentPoint.getFactor(), newPoint);

        receiver = 3;
        speakComment = speakService.addSpeakComment(speakId, maker, receiver, comment);

        integrationService.addNurseCommentIntegration(speakComment);
        newPoint = integrationService.getNurseAnswerIntegration(maker);
        commentPoint = commentAbilityTypeConverter.getItem(CommentAbilityTypeConverter.ANSWER);
        Assert.assertEquals(point+commentPoint.getFactor(), newPoint);
    }

    @Test
    public void testPublishSpeakThumbsUp() {
        SpecificSocialAbility thumbsUpPoint     = thumbsUpAbilityTypeConverter.getItem(ThumbsUpAbilityTypeConverter.THUMBS_UP_OTHERS);
        SpecificSocialAbility beenThumbsUpPoint = thumbsUpAbilityTypeConverter.getItem(ThumbsUpAbilityTypeConverter.BEEN_THUMBS_UP);
        // 点赞/被赞
        long speakId = 14;
        long commentMaker = 4;
        long speakMaker = 3;
        int pointThumbsUp     = integrationService.getNurseThumbsUpIntegration(commentMaker);
        int pointBeenThumbsUp = integrationService.getNurseBeenThumbsUpIntegration(speakMaker);
        // add thumbs up by NurseSpeakAOPService
        NurseSpeakThumbsUpBean speakThumbsUp = speakService.setNurseSpeakThumbsUp(speakId, commentMaker);

        integrationService.addNurseThumbsUpIntegration(speakThumbsUp);
        int newPointThumbsUp     = integrationService.getNurseThumbsUpIntegration(speakThumbsUp.getThumbsUpUserId());
        int newPointBeenThumbsUp = integrationService.getNurseBeenThumbsUpIntegration(speakThumbsUp.getUserIdBeenThumbsUp());
        Assert.assertEquals(pointThumbsUp + thumbsUpPoint.getFactor(), newPointThumbsUp);
        Assert.assertEquals(pointBeenThumbsUp + thumbsUpPoint.getFactor(), newPointBeenThumbsUp);


        speakThumbsUp = speakService.setNurseSpeakThumbsUp(speakId, commentMaker);

        integrationService.addNurseThumbsUpIntegration(speakThumbsUp);
        newPointThumbsUp     = integrationService.getNurseThumbsUpIntegration(speakThumbsUp.getThumbsUpUserId());
        newPointBeenThumbsUp = integrationService.getNurseBeenThumbsUpIntegration(speakThumbsUp.getUserIdBeenThumbsUp());
        Assert.assertEquals(pointThumbsUp + thumbsUpPoint.getFactor(), newPointThumbsUp);
        Assert.assertEquals(pointBeenThumbsUp + thumbsUpPoint.getFactor(), newPointBeenThumbsUp);
    }

    @Test
    public void testGetIntegrationSorted() {
        long userId = 1;
        List<NurseIntegrationBean> allIntegrations_UserID_1 = integrationService.getIntegrationSorted(userId, UserType.NURSE, CommonStatus.ENABLED);
        userId = 2;
        List<NurseIntegrationBean> allIntegrations_UserID_2 = integrationService.getIntegrationSorted(userId, UserType.NURSE, CommonStatus.ENABLED);
        userId = 3;
        List<NurseIntegrationBean> allIntegrations_UserID_3 = integrationService.getIntegrationSorted(userId, UserType.NURSE, CommonStatus.ENABLED);
        userId = 4;
        List<NurseIntegrationBean> allIntegrations_UserID_4 = integrationService.getIntegrationSorted(userId, UserType.NURSE, CommonStatus.ENABLED);
        userId = 5;
        List<NurseIntegrationBean> allIntegrations_UserID_5 = integrationService.getIntegrationSorted(userId, UserType.NURSE, CommonStatus.ENABLED);
        userId = 7;
        List<NurseIntegrationBean> allIntegrations_UserID_7 = integrationService.getIntegrationSorted(userId, UserType.NURSE, CommonStatus.ENABLED);

        userId = 1;
        SpecificSocialAbility ability = speakAbilityTypeConverter.getItem(SpeakType.SMUG);
        int integration = integrationService.getIntegration(allIntegrations_UserID_1, UserType.NURSE, userId, ability.getAbilityType(), ability.getAbilityId());
        Assert.assertEquals(320, integration);

        ability = skillAbilityTypeConverter.getItem(1000);
        integration = integrationService.getIntegration(allIntegrations_UserID_1, UserType.NURSE, userId, ability.getAbilityType(), ability.getAbilityId());
        Assert.assertEquals(30, integration);

        ability = commentAbilityTypeConverter.getItem(CommentAbilityTypeConverter.COMMENT);
        userId = 2;
        integration = integrationService.getIntegration(allIntegrations_UserID_2, UserType.NURSE, userId, ability.getAbilityType(), ability.getAbilityId());
        Assert.assertEquals(3, integration);
        userId = 3;
        integration = integrationService.getIntegration(allIntegrations_UserID_3, UserType.NURSE, userId, ability.getAbilityType(), ability.getAbilityId());
        Assert.assertEquals(4, integration);

        ability = thumbsUpAbilityTypeConverter.getItem(ThumbsUpAbilityTypeConverter.BEEN_THUMBS_UP);
        userId = 1;
        integration = integrationService.getIntegration(allIntegrations_UserID_1, UserType.NURSE, userId, ability.getAbilityType(), ability.getAbilityId());
        Assert.assertEquals(45, integration);

        ability = thumbsUpAbilityTypeConverter.getItem(ThumbsUpAbilityTypeConverter.THUMBS_UP_OTHERS);
        userId = 2;
        integration = integrationService.getIntegration(allIntegrations_UserID_2, UserType.NURSE, userId, ability.getAbilityType(), ability.getAbilityId());
        Assert.assertEquals(20, integration);
        userId = 3;
        integration = integrationService.getIntegration(allIntegrations_UserID_3, UserType.NURSE, userId, ability.getAbilityType(), ability.getAbilityId());
        Assert.assertEquals(20, integration);
        userId = 4;
        integration = integrationService.getIntegration(allIntegrations_UserID_4, UserType.NURSE, userId, ability.getAbilityType(), ability.getAbilityId());
        Assert.assertEquals(10, integration);
        userId = 5;
        integration = integrationService.getIntegration(allIntegrations_UserID_5, UserType.NURSE, userId, ability.getAbilityType(), ability.getAbilityId());
        Assert.assertEquals(10, integration);
        userId = 7;
        integration = integrationService.getIntegration(allIntegrations_UserID_7, UserType.NURSE, userId, ability.getAbilityType(), ability.getAbilityId());
        Assert.assertEquals(10, integration);
    }

    @Test
    public void testFirstTimeFetchPoint() {
        long userId = 1;
        List<NurseIntegrationBean> allIntegrations = integrationService.getIntegrationSorted(userId, UserType.NURSE, CommonStatus.ENABLED);

        SpecificSocialAbility ability = speakAbilityTypeConverter.getItem(SpeakType.SMUG);
        int integration = integrationService.getIntegration(allIntegrations, UserType.NURSE, userId, ability.getAbilityType(), ability.getAbilityId());
        Assert.assertEquals(320, integration);

        Date date = integrationService.firstTimeFetchPoint(allIntegrations, UserType.NURSE, userId, ability.getAbilityType(), ability.getAbilityId(), 300);
        Assert.assertEquals(NumberUtil.getTime("2016-05-01 08:00:00", NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS), date.getTime());
    }
}
