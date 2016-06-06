package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseSpeakTopicSubscriberBean;
import com.cooltoo.backend.services.NurseSpeakTopicSubscriberService;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserType;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hp on 2016/6/5.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_topic_relation_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_topic_subscriber_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_topic_data.xml")
})
public class NurseSpeakTopicSubscriberServiceTest extends AbstractCooltooTest {

    @Autowired private NurseSpeakTopicSubscriberService subscriberService;

    @Test
    public void testCountSubscriberInTopic() {
        long topicId = 1L;
        String status = CommonStatus.ENABLED.name();
        long count = subscriberService.countSubscriberInTopic(topicId, status);
        Assert.assertEquals(2, count);

        topicId = 6;
        count = subscriberService.countSubscriberInTopic(topicId, status);
        Assert.assertEquals(1, count);
    }

    @Test
    public void testGetSubscriberInTopic() {
        int page = 0;
        int size = 2;
        long topicId = 1L;
        String status = CommonStatus.ENABLED.name();
        List<NurseSpeakTopicSubscriberBean> subscribers = subscriberService.getSubscriberInTopic(topicId, status, page, size);
        Assert.assertEquals(2, subscribers.size());
        Assert.assertEquals(11, subscribers.get(0).getUserId());
        Assert.assertEquals(17, subscribers.get(1).getUserId());
    }

    @Test
    public void testCountTopicInUser() {
        long userId = 16;
        String userType = UserType.NURSE.name();
        String status = CommonStatus.ENABLED.name();
        long count = subscriberService.countSubscriberByUser(userId, userType, status);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testGetSubscriberByUser() {
        int page = 0;
        int size = 2;
        long userId = 16L;
        String userType = UserType.NURSE.name();
        String status = CommonStatus.ENABLED.name();
        List<NurseSpeakTopicSubscriberBean> subscribers = subscriberService.getSubscriberByUser(userId, userType, status, page, size);
        Assert.assertEquals(2, subscribers.size());
        Assert.assertEquals(6, subscribers.get(0).getId());
        Assert.assertEquals(8, subscribers.get(1).getId());
    }

    @Test
    public void testSetTopicSubscriber() {
        long topicId = 2;
        long userId = 17;
        String userType = UserType.NURSE.name();
        String status = CommonStatus.ENABLED.name();

        long count = subscriberService.countSubscriberInTopic(topicId, status);
        subscriberService.setTopicSubscriber(topicId, userId, userType);
        long count2 = subscriberService.countSubscriberInTopic(topicId, status);
        Assert.assertEquals(count+1, count2);
    }

    @Test
    public void testUpdateStatusByUser() {
        long userId = 16L;
        String userType = UserType.NURSE.name();
        String orgStatus = CommonStatus.ENABLED.name();
        String status = CommonStatus.DISABLED.name();
        long count = subscriberService.countSubscriberByUser(userId, userType, status);
        Assert.assertEquals(0, count);
        long effectedNumber = subscriberService.updateStatusByUser(userId, userType, orgStatus, status);
        count = subscriberService.countSubscriberByUser(userId, userType, status);
        Assert.assertEquals(count, effectedNumber);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testUpdateStatusByTopic() {
        long topicId = 1L;
        String orgStatus = CommonStatus.ENABLED.name();
        String status = CommonStatus.DISABLED.name();
        long count = subscriberService.countSubscriberInTopic(topicId, status);
        Assert.assertEquals(0, count);
        long effectedNumber = subscriberService.updateStatusInTopic(topicId, orgStatus, status);
        count = subscriberService.countSubscriberInTopic(topicId, status);
        Assert.assertEquals(count, effectedNumber);
        Assert.assertEquals(2, count);
    }
}
