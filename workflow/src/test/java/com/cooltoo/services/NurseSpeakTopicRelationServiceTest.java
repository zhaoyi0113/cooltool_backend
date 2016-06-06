package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseSpeakTopicRelationBean;
import com.cooltoo.backend.services.NurseSpeakTopicRelationService;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserAuthority;
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
public class NurseSpeakTopicRelationServiceTest extends AbstractCooltooTest {

    @Autowired private NurseSpeakTopicRelationService topicRelationService;

    @Test
    public void testCountUserTakePartIn() {
        long topicId = 1L;
        String userAuthority = UserAuthority.AGREE_ALL.name();
        long count = topicRelationService.countUserTakePartIn(topicId, userAuthority);
        Assert.assertEquals(3, count);
    }

    @Test
    public void testGetUserTakePartIn() {
        long topicId = 1L;
        String userAuthority = UserAuthority.AGREE_ALL.name();
        List<Long> usersId = topicRelationService.getUserTakePartIn(topicId, userAuthority, 0, 2);
        Assert.assertEquals(2, usersId.size());
        Assert.assertEquals(Long.valueOf(11L), usersId.get(0));
        Assert.assertEquals(Long.valueOf(17L), usersId.get(1));

        usersId = topicRelationService.getUserTakePartIn(topicId, userAuthority, 1, 2);
        Assert.assertEquals(1, usersId.size());
        Assert.assertEquals(Long.valueOf(14L), usersId.get(0));
    }


    @Test
    public void testCountSpeaksInTopic() {
        long topicId = 1L;
        String speakStatus = CommonStatus.ENABLED.name();
        long count = topicRelationService.countSpeaksInTopic(topicId, speakStatus);
        Assert.assertEquals(4, count);

        topicId = 4L;
        speakStatus = CommonStatus.DISABLED.name();
        count = topicRelationService.countSpeaksInTopic(topicId, speakStatus);
        Assert.assertEquals(2, count);

        topicId = 6L;
        speakStatus = CommonStatus.DELETED.name();
        count = topicRelationService.countSpeaksInTopic(topicId, speakStatus);
        Assert.assertEquals(1, count);
    }

    @Test
    public void testGetSpeaksIdInTopic() {
        long topicId = 1L;
        String speakStatus = CommonStatus.ENABLED.name();
        int page = 0;
        int size = 2;
        List<Long> speaksId = topicRelationService.getSpeaksIdInTopic(topicId, speakStatus, page, size);
        Assert.assertEquals(2, speaksId.size());
        Assert.assertEquals(Long.valueOf(34L), speaksId.get(0));
        Assert.assertEquals(Long.valueOf(31L), speaksId.get(1));

        topicId = 4L;
        speakStatus = CommonStatus.DISABLED.name();
        speaksId = topicRelationService.getSpeaksIdInTopic(topicId, speakStatus, page, size);
        Assert.assertEquals(2, speaksId.size());
        Assert.assertEquals(Long.valueOf(30L), speaksId.get(0));
        Assert.assertEquals(Long.valueOf(24L), speaksId.get(1));

        topicId = 6L;
        speakStatus = CommonStatus.DELETED.name();
        speaksId = topicRelationService.getSpeaksIdInTopic(topicId, speakStatus, page, size);
        Assert.assertEquals(1, speaksId.size());
        Assert.assertEquals(Long.valueOf(26L), speaksId.get(0));
    }

    @Test
    public void testGetTopicIdsBySpeakId() {
        long speakId = 35L;
        String status = CommonStatus.ENABLED.name();
        List<Long> topicsId = topicRelationService.getTopicIdsBySpeakId(speakId, status);
        Assert.assertEquals(2, topicsId.size());
        Assert.assertEquals(Long.valueOf(3L), topicsId.get(0));
        Assert.assertEquals(Long.valueOf(2L), topicsId.get(1));

        speakId = 34L;
        topicsId = topicRelationService.getTopicIdsBySpeakId(speakId, status);
        Assert.assertEquals(1, topicsId.size());
        Assert.assertEquals(Long.valueOf(1L), topicsId.get(0));
    }

    @Test
    public void testAddTopicRelation() {
        long speakId = 40L;
        long userId = 17L;
        String status = CommonStatus.ENABLED.name();
        List<Long> topicsId = Arrays.asList(new Long[]{1L, 2L, 3L, 4L});
        List<NurseSpeakTopicRelationBean> relationBeans = topicRelationService.addTopicRelation(topicsId, speakId, userId);
        Assert.assertEquals(4, relationBeans.size());

        topicsId = topicRelationService.getTopicIdsBySpeakId(speakId, status);
        Assert.assertEquals(3, topicsId.size());
        Assert.assertEquals(Long.valueOf(3L), topicsId.get(0));
        Assert.assertEquals(Long.valueOf(2L), topicsId.get(1));
        Assert.assertEquals(Long.valueOf(1L), topicsId.get(2));
    }

    @Test
    public void testGetTopicIdsBySpeakIds() {
        List<Long> speaksId = Arrays.asList(new Long[]{21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L, 29L, 30L});
        String topicStatus = CommonStatus.ENABLED.name();
        Map<Long, List<Long>> speaksId2topicsId = topicRelationService.getTopicIdsBySpeakIds(speaksId, topicStatus);
        Assert.assertEquals(10, speaksId2topicsId.size());
        Assert.assertEquals(1, speaksId2topicsId.get(21L).size());
        Assert.assertEquals(Long.valueOf(1), speaksId2topicsId.get(21L).get(0));
        Assert.assertEquals(1, speaksId2topicsId.get(22L).size());
        Assert.assertEquals(Long.valueOf(2), speaksId2topicsId.get(22L).get(0));
        Assert.assertEquals(1, speaksId2topicsId.get(23L).size());
        Assert.assertEquals(Long.valueOf(3), speaksId2topicsId.get(23L).get(0));
        Assert.assertEquals(0, speaksId2topicsId.get(24L).size());
        Assert.assertEquals(0, speaksId2topicsId.get(25L).size());
        Assert.assertEquals(0, speaksId2topicsId.get(26L).size());
        Assert.assertEquals(1, speaksId2topicsId.get(27L).size());
        Assert.assertEquals(Long.valueOf(1), speaksId2topicsId.get(27L).get(0));
        Assert.assertEquals(1, speaksId2topicsId.get(28L).size());
        Assert.assertEquals(Long.valueOf(2), speaksId2topicsId.get(28L).get(0));
        Assert.assertEquals(1, speaksId2topicsId.get(29L).size());
        Assert.assertEquals(Long.valueOf(3), speaksId2topicsId.get(29L).get(0));
        Assert.assertEquals(0, speaksId2topicsId.get(30L).size());
    }
//
//    @Test
//    public void testUpdateTopicRelationByTopicId() {
//        int page = 0;
//        int size = 10;
//        long topicId = 4L;
//        List<CommonStatus> originalStatus = Arrays.asList(new CommonStatus[]{CommonStatus.DISABLED, CommonStatus.ENABLED});
//        CommonStatus status = CommonStatus.DELETED;
//        CommonStatus speakStatus = CommonStatus.ENABLED;
//        int effectedNumber = topicRelationService.updateTopicRelationByTopicId(topicId, originalStatus, status);
//        Assert.assertEquals(2, effectedNumber);
//        List<Long> speaksId = topicRelationService.getSpeaksIdInTopic(topicId, status.name(), speakStatus.name(), page, size);
//        Assert.assertEquals(2, speaksId.size());
//        Assert.assertEquals(Long.valueOf(30L), speaksId.get(0));
//        Assert.assertEquals(Long.valueOf(24L), speaksId.get(1));
//    }
//
//    @Test
//    public void testUpdateTopicRelationBySpeakId() {
//        long speakId = 35L;
//        List<CommonStatus> originalStatus = Arrays.asList(new CommonStatus[]{CommonStatus.DISABLED, CommonStatus.ENABLED});
//        CommonStatus status = CommonStatus.DELETED;
//        int effectedNumber = topicRelationService.updateTopicRelationBySpeakId(speakId, originalStatus, status);
//        Assert.assertEquals(2, effectedNumber);
//        List<Long> topicsId = topicRelationService.getTopicIdsBySpeakId(speakId, status.name());
//        Assert.assertEquals(2, topicsId.size());
//        Assert.assertEquals(Long.valueOf(3L), topicsId.get(0));
//        Assert.assertEquals(Long.valueOf(2L), topicsId.get(1));
//    }
}
