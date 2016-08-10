package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseFriendsBean;
import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.beans.NurseSpeakTopicBean;
import com.cooltoo.backend.services.NurseSpeakTopicService;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.constants.UserType;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/6/5.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_topic_relation_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_topic_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml")
})
public class NurseSpeakTopicServiceTest extends AbstractCooltooTest {

    @Autowired private NurseSpeakTopicService topicService;

    @Test
    public void testGetTopicByTitle() {
        String title = "";
        NurseSpeakTopicBean topic = topicService.getTopicByTile(title);
        Assert.assertNull(topic);

        title = "topic";
        topic = topicService.getTopicByTile(title);
        Assert.assertNull(topic);

        title = "topic 002";
        topic = topicService.getTopicByTile(title);
        Assert.assertNotNull(topic);
        Assert.assertEquals(2, topic.getId());
    }

    @Test
    public void testCountTopic() {
        String title = "";
        String status = CommonStatus.ENABLED.name();
        long count = topicService.countTopic(title, status);
        Assert.assertEquals(3, count);

        status = CommonStatus.DISABLED.name();
        count = topicService.countTopic(title, status);
        Assert.assertEquals(2, count);

        status = CommonStatus.DELETED.name();
        count = topicService.countTopic(title, status);
        Assert.assertEquals(1, count);

        title = "00";
        status = CommonStatus.ENABLED.name();
        count = topicService.countTopic(title, status);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testGetTopic() {
        String title = "";
        String status = CommonStatus.ENABLED.name();
        List<NurseSpeakTopicBean> topics = topicService.getTopic(title, status, 0, 2);
        Assert.assertEquals(2, topics.size());
        Assert.assertEquals(3, topics.get(0).getId());
        Assert.assertEquals(2, topics.get(1).getId());

        status = CommonStatus.DISABLED.name();
        topics = topicService.getTopic(title, status, 0, 2);
        Assert.assertEquals(2, topics.size());
        Assert.assertEquals(5, topics.get(0).getId());
        Assert.assertEquals(4, topics.get(1).getId());

        status = CommonStatus.DELETED.name();
        topics = topicService.getTopic(title, status, 0, 2);
        Assert.assertEquals(1, topics.size());

        title = "00";
        status = CommonStatus.ENABLED.name();
        topics = topicService.getTopic(title, status, 0, 3);
        Assert.assertEquals(2, topics.size());
        Assert.assertEquals(2, topics.get(0).getId());
        Assert.assertEquals(1, topics.get(1).getId());
    }

    @Test
    public void testCountUsersInTopic() {
        long topicId = 1L;
        String userAuthority = UserAuthority.AGREE_ALL.name();
        long count = topicService.countUsersInTopic(topicId, userAuthority);
        Assert.assertEquals(3, count);

        topicId = 2L;
        count = topicService.countUsersInTopic(topicId, userAuthority);
        Assert.assertEquals(1, count);

        userAuthority = UserAuthority.DENY_ALL.name();
        count = topicService.countUsersInTopic(topicId, userAuthority);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testGetUsersInTopic() {
        long topicId = 1L;
        String userAuthority = UserAuthority.AGREE_ALL.name();
        List<NurseFriendsBean> nurses = topicService.getUsersInTopic(topicId, 0, userAuthority, 0, 2);
        Assert.assertEquals(2, nurses.size());

        topicId = 2L;
        nurses = topicService.getUsersInTopic(topicId, 0, userAuthority, 0, 2);
        Assert.assertEquals(1, nurses.size());
        Assert.assertEquals(11L, nurses.get(0).getFriendId());

        userAuthority = UserAuthority.DENY_ALL.name();
        nurses = topicService.getUsersInTopic(topicId, 0, userAuthority, 0, 2);
        Assert.assertEquals(2, nurses.size());
        Assert.assertEquals(12L, nurses.get(0).getFriendId());
        Assert.assertEquals(15L, nurses.get(1).getFriendId());
    }

    @Test
    public void testCountSpeaksInTopic() {
        long topicId = 1L;
        String speakStatus = CommonStatus.ENABLED.name();
        long count = topicService.countSpeaksInTopic(topicId, speakStatus);
        Assert.assertEquals(4, count);

        topicId = 4L;
        count = topicService.countSpeaksInTopic(topicId, speakStatus);
        Assert.assertEquals(0, count);

        speakStatus = CommonStatus.DISABLED.name();
        count = topicService.countSpeaksInTopic(topicId, speakStatus);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testGetSpeaksInTopic() {
        long topicId = 1L;
        String speakStatus = CommonStatus.ENABLED.name();
        List<NurseSpeakBean> speaks = topicService.getSpeaksInTopic(topicId, speakStatus, 0, 4);
        Assert.assertEquals(4, speaks.size());
        Assert.assertEquals(34L, speaks.get(0).getId());
        Assert.assertEquals(31L, speaks.get(1).getId());
        Assert.assertEquals(27L, speaks.get(2).getId());
        Assert.assertEquals(21L, speaks.get(3).getId());

        topicId = 4L;
        speaks = topicService.getSpeaksInTopic(topicId, speakStatus, 0, 2);
        Assert.assertEquals(0, speaks.size());

        speakStatus = CommonStatus.DISABLED.name();
        speaks = topicService.getSpeaksInTopic(topicId, speakStatus, 0, 2);
        Assert.assertEquals(2, speaks.size());
        Assert.assertEquals(30L, speaks.get(0).getId());
        Assert.assertEquals(24L, speaks.get(1).getId());
    }

    @Test
    public void testGetTopicsBySpeakIds() {
        List<Long> speaksId = Arrays.asList(new Long[]{26L, 27L, 28L, 29L, 30L});
        String topicStatus = CommonStatus.ENABLED.name();
        Map<Long, List<NurseSpeakTopicBean>> speaksId2Topics = topicService.getTopicsBySpeakIds(speaksId, topicStatus);
        Assert.assertEquals(5, speaksId2Topics.size());
        Assert.assertEquals(0, speaksId2Topics.get(26L).size());
        Assert.assertEquals(1, speaksId2Topics.get(27L).size());
        Assert.assertEquals(1L, speaksId2Topics.get(27L).get(0).getId());
        Assert.assertEquals(1, speaksId2Topics.get(28L).size());
        Assert.assertEquals(2L, speaksId2Topics.get(28L).get(0).getId());
        Assert.assertEquals(1, speaksId2Topics.get(29L).size());
        Assert.assertEquals(3L, speaksId2Topics.get(29L).get(0).getId());
        Assert.assertEquals(0, speaksId2Topics.get(30L).size());
    }

    @Test
    public void testAddSpeakTopicsBySpeakContent() {
        String title1 = "topic 03";
        String title2 = "槽点测试";
        String speakContent = "#topic 03#test#槽点测试#安安宝贝###发布吧";
        String topicStatus = CommonStatus.ENABLED.name();
        long userId = -1;
        String userType = "";
        long speakId = 36;

        List<Long> speakIds = Arrays.asList(new Long[]{speakId});
        Map<Long, List<NurseSpeakTopicBean>> speakId2Topic = topicService.getTopicsBySpeakIds(speakIds, topicStatus);
        Assert.assertEquals(0, speakId2Topic.size());

        List<NurseSpeakTopicBean> topics = topicService.addSpeakTopicsBySpeakContent(userId, userType, speakId, speakContent);
        Assert.assertEquals(2, topics.size());
        Assert.assertEquals(3L, topics.get(0).getId());
        Assert.assertTrue(topics.get(1).getId()>0);
        Assert.assertEquals(title1, topics.get(0).getTitle());
        Assert.assertEquals(title2, topics.get(1).getTitle());

        speakIds = Arrays.asList(new Long[]{speakId});
        speakId2Topic = topicService.getTopicsBySpeakIds(speakIds, topicStatus);
        Assert.assertEquals(1, speakId2Topic.size());
        Assert.assertEquals(title1, topics.get(0).getTitle());
        Assert.assertEquals(title2, topics.get(1).getTitle());
    }

    @Test
    public void testUpdateTopic() {
        long topicId = 1L;
        String title = "topic 001";
        String newLabel = "lbl test";
        long userId = -1;
        String userType = "";
        String taxonomy = "taxonomy test";
        String description = "desc test";
        int province = 3;
        String status = CommonStatus.ENABLED.name();
        String newStatus = CommonStatus.DELETED.name();
        long clickNumberIncrement = 30;
        long clickNumber = 10;

        List<NurseSpeakTopicBean> topicsBean = topicService.getTopic(title, status, 0, 10);
        Assert.assertEquals(1, topicsBean.size());
        NurseSpeakTopicBean topicBean = topicsBean.get(0);
        Assert.assertEquals(clickNumber, topicBean.getClickNumber());

        topicService.updateTopic(topicId, userId, userType, newLabel, taxonomy, description, province, newStatus, clickNumberIncrement);

        topicsBean = topicService.getTopic(title, newStatus, 0, 10);
        Assert.assertEquals(1, topicsBean.size());
        NurseSpeakTopicBean newTopicBean = topicsBean.get(0);

        Assert.assertEquals(topicBean.getId(), newTopicBean.getId());
        Assert.assertEquals(userId, newTopicBean.getCreatorId());
        Assert.assertEquals(UserType.NURSE, newTopicBean.getCreatorType());
        Assert.assertEquals(description, newTopicBean.getDescription());
        Assert.assertEquals(taxonomy, newTopicBean.getTaxonomy());
        Assert.assertEquals(newLabel, newTopicBean.getLabel());
        Assert.assertEquals(province, newTopicBean.getProvince());
        Assert.assertEquals(topicBean.getClickNumber()+clickNumberIncrement, newTopicBean.getClickNumber());
        Assert.assertEquals(clickNumber+clickNumberIncrement, newTopicBean.getClickNumber());
    }
}
