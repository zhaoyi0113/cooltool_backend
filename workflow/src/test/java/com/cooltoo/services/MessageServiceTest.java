package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.MessageBean;
import com.cooltoo.backend.services.MessageService;
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
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/images_in_speak_data.xml")
})
public class MessageServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceTest.class.getName());

    @Autowired
    private MessageService messageSvr;

    @Test
    public void testGetMessages() {
        long userId = 1L;
        int page = 0;
        int size = 10;
        List<MessageBean> message = messageSvr.getMessages(userId, page, size);
        Assert.assertEquals(10, message.size());

        page = 1;
        message = messageSvr.getMessages(userId, page, size);
        Assert.assertEquals(5, message.size());

        size = 5;
        page = 0;
        List<MessageBean> message1 = messageSvr.getMessages(userId, page, size);
        Assert.assertEquals(5, message.size());
        Assert.assertTrue(message1.get(0).getTime().compareTo(message1.get(1).getTime()) >= 0);
        Assert.assertTrue(message1.get(1).getTime().compareTo(message1.get(2).getTime()) >= 0);
        Assert.assertTrue(message1.get(2).getTime().compareTo(message1.get(3).getTime()) >= 0);
        Assert.assertTrue(message1.get(3).getTime().compareTo(message1.get(4).getTime()) >= 0);
        page = 1;
        List<MessageBean> message2 = messageSvr.getMessages(userId, page, size);
        Assert.assertEquals(5, message.size());
        Assert.assertTrue(message1.get(4).getTime().compareTo(message2.get(0).getTime()) >= 0);
        Assert.assertTrue(message2.get(0).getTime().compareTo(message2.get(1).getTime()) >= 0);
        Assert.assertTrue(message2.get(1).getTime().compareTo(message2.get(2).getTime()) >= 0);
        Assert.assertTrue(message2.get(2).getTime().compareTo(message2.get(3).getTime()) >= 0);
        Assert.assertTrue(message2.get(3).getTime().compareTo(message2.get(4).getTime()) >= 0);
        page = 2;
        List<MessageBean> message3 = messageSvr.getMessages(userId, page, size);
        Assert.assertEquals(5, message.size());
        Assert.assertTrue(message2.get(4).getTime().compareTo(message3.get(0).getTime()) >= 0);
        Assert.assertTrue(message3.get(0).getTime().compareTo(message3.get(1).getTime()) >= 0);
        Assert.assertTrue(message3.get(1).getTime().compareTo(message3.get(2).getTime()) >= 0);
        Assert.assertTrue(message3.get(2).getTime().compareTo(message3.get(3).getTime()) >= 0);
        Assert.assertTrue(message3.get(3).getTime().compareTo(message3.get(4).getTime()) >= 0);
    }
}
