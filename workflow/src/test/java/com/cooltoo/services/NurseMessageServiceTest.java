package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseMessageBean;
import com.cooltoo.backend.services.NurseMessageService;
import com.cooltoo.constants.MessageType;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.constants.SuggestionStatus;
import com.cooltoo.constants.UserType;
import com.cooltoo.util.NumberUtil;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zhaolisong on 16/5/18.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/speak_type_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_thumbs_up_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_comment_service_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/file_storage_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/images_in_speak_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_message_data.xml")
})
public class NurseMessageServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(NurseMessageServiceTest.class.getName());

    @Autowired
    private NurseMessageService messageSvr;

    @Test
    public void testCountMessageByStatus() {
        long userId = 1L;
        UserType userType = UserType.NURSE;
        String statuses = "unread,read,deleted";
        long count = messageSvr.countMessageByStatus(userType, userId, statuses);
        Assert.assertEquals(15, count);
        statuses="unread,read";
        count = messageSvr.countMessageByStatus(userType, userId, statuses);
        Assert.assertEquals(14, count);
        statuses="read";
        count = messageSvr.countMessageByStatus(userType, userId, statuses);
        Assert.assertEquals(5, count);
    }

    @Test
    public void testSetMessageStatus(){
        long userId = 1L;
        UserType userType = UserType.NURSE;
        String statuses = "deleted";
        long countD = messageSvr.countMessageByStatus(userType, userId, statuses);
        statuses="unread";
        long countU = messageSvr.countMessageByStatus(userType, userId, statuses);
        statuses="read";
        long countR = messageSvr.countMessageByStatus(userType, userId, statuses);

        messageSvr.setMessageStatus(1, "read");
        statuses="read";
        long count = messageSvr.countMessageByStatus(userType, userId, statuses);
        Assert.assertEquals(countR+1, count);
        statuses="unread";
        count = messageSvr.countMessageByStatus(userType, userId, statuses);
        Assert.assertEquals(countU-1, count);

        messageSvr.setMessageStatus(1, "deleted");
        statuses="deleted";
        count = messageSvr.countMessageByStatus(userType, userId, statuses);
        Assert.assertEquals(countD+1, count);
        statuses="unread";
        count = messageSvr.countMessageByStatus(userType, userId, statuses);
        Assert.assertEquals(countU-1, count);
    }

    @Test
    public void testGetMessage() {
        long userId = 1L;
        UserType userType = UserType.NURSE;
        String statuses = "deleted";
        List<NurseMessageBean> messages = messageSvr.getMessages(userType, userId, statuses);
        Assert.assertEquals(1, messages.size());

        // thumbs up
        // <nurse_message id="7" user_type="0" user_id="1" reason_id="7" ability_id="2" ability_type="3" status="2" time_created="2016-03-18 9:11:32" />
        Assert.assertEquals(7L, messages.get(0).getId());
        Assert.assertEquals(NumberUtil.getTime("2016-03-18 9:11:32", NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS), messages.get(0).getTime().getTime());
        Assert.assertEquals(SuggestionStatus.DELETED,  messages.get(0).getStatus());
        Assert.assertEquals(SocialAbilityType.COMMUNITY, messages.get(0).getAbilityType());
        Assert.assertEquals(1, messages.get(0).getAbilityId());
        Assert.assertEquals(UserType.NURSE, messages.get(0).getUserType());
        Assert.assertEquals(5L, messages.get(0).getUserId());
        Assert.assertEquals(4L, messages.get(0).getReasonId());
        Assert.assertEquals("hello 4 (*-*)!", messages.get(0).getContent());
        Assert.assertEquals(MessageType.ThumbsUp, messages.get(0).getType());
        Assert.assertNotNull(messages.get(0).getAbilityName());

        statuses = "read,unread";
        messages = messageSvr.getMessages(userType, userId, statuses);
        Assert.assertEquals(14, messages.size());
        Assert.assertEquals(9L, messages.get(12).getId());
        Assert.assertEquals(8L, messages.get(13).getId());
        // comment
        // <nurse_message id="9"  user_type="0" user_id="1" reason_id="2" ability_id="1" ability_type="3" status="1" time_created="2016-02-02 00:00:00" />
        Assert.assertEquals(9L, messages.get(12).getId());
        Assert.assertEquals(NumberUtil.getTime("2016-02-02 00:00:00", NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS), messages.get(12).getTime().getTime());
        Assert.assertEquals(SuggestionStatus.READ,  messages.get(12).getStatus());
        Assert.assertEquals(SocialAbilityType.COMMUNITY, messages.get(12).getAbilityType());
        Assert.assertEquals(1, messages.get(12).getAbilityId());
        Assert.assertEquals(UserType.NURSE, messages.get(12).getUserType());
        Assert.assertEquals(1L, messages.get(12).getUserId());
        Assert.assertEquals(1L, messages.get(12).getReasonId());
        Assert.assertEquals("chou mei 2", messages.get(12).getContent());
        Assert.assertEquals(MessageType.Comment, messages.get(12).getType());
        Assert.assertNull(messages.get(12).getAbilityName());
    }

    @Test
    public void testGetMessagePerPage() {
        long userId = 1L;
        UserType userType = UserType.NURSE;
        String statuses = "deleted";
        List<NurseMessageBean> messages = messageSvr.getMessages(userType, userId, statuses, 0, 3);
        Assert.assertEquals(1, messages.size());

        // thumbs up
        // <nurse_message id="7" user_type="0" user_id="1" reason_id="7" ability_id="2" ability_type="3" status="2" time_created="2016-03-18 9:11:32" />
        Assert.assertEquals(7L, messages.get(0).getId());
        Assert.assertEquals(NumberUtil.getTime("2016-03-18 9:11:32", NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS), messages.get(0).getTime().getTime());
        Assert.assertEquals(SuggestionStatus.DELETED,  messages.get(0).getStatus());
        Assert.assertEquals(SocialAbilityType.COMMUNITY, messages.get(0).getAbilityType());
        Assert.assertEquals(1, messages.get(0).getAbilityId());
        Assert.assertEquals(UserType.NURSE, messages.get(0).getUserType());
        Assert.assertEquals(5L, messages.get(0).getUserId());
        Assert.assertEquals(4L, messages.get(0).getReasonId());
        Assert.assertEquals("hello 4 (*-*)!", messages.get(0).getContent());
        Assert.assertEquals(MessageType.ThumbsUp, messages.get(0).getType());
        Assert.assertNotNull(messages.get(0).getAbilityName());

        statuses = "read,unread";
        messages = messageSvr.getMessages(userType, userId, statuses, 4, 3);
        Assert.assertEquals(2, messages.size());
        Assert.assertEquals(9L, messages.get(0).getId());
        Assert.assertEquals(8L, messages.get(1).getId());
        // comment
        // <nurse_message id="9"  user_type="0" user_id="1" reason_id="2" ability_id="1" ability_type="3" status="1" time_created="2016-02-02 00:00:00" />
        Assert.assertEquals(9L, messages.get(0).getId());
        Assert.assertEquals(NumberUtil.getTime("2016-02-02 00:00:00", NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS), messages.get(0).getTime().getTime());
        Assert.assertEquals(SuggestionStatus.READ,  messages.get(0).getStatus());
        Assert.assertEquals(SocialAbilityType.COMMUNITY, messages.get(0).getAbilityType());
        Assert.assertEquals(1, messages.get(0).getAbilityId());
        Assert.assertEquals(UserType.NURSE, messages.get(0).getUserType());
        Assert.assertEquals(1L, messages.get(0).getUserId());
        Assert.assertEquals(1L, messages.get(0).getReasonId());
        Assert.assertEquals("chou mei 2", messages.get(0).getContent());
        Assert.assertEquals(MessageType.Comment, messages.get(0).getType());
        Assert.assertNull(messages.get(0).getAbilityName());
    }
}
