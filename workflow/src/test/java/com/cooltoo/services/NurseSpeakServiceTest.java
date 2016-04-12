package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.*;
import com.cooltoo.backend.services.NurseSpeakService;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.exception.BadRequestException;
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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yzzhao on 3/15/16.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/speak_type_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_thumbs_up_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_comment_service_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/file_storage_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml"),
})
public class NurseSpeakServiceTest extends AbstractCooltooTest{

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakServiceTest.class.getName());

    @Autowired
    private NurseSpeakService speakService;

    @Test
    public void testGetNurseSpeak(){
        List<NurseSpeakBean> nurseSpeak = speakService.getNurseSpeak(1, 0, 3);
        Assert.assertEquals(3, nurseSpeak.size());
        Assert.assertEquals(1, nurseSpeak.get(0).getId());

        nurseSpeak = speakService.getNurseSpeak(1, 1, 3);
        Assert.assertEquals(3, nurseSpeak.size());
        Assert.assertEquals(4, nurseSpeak.get(0).getId());

        nurseSpeak = speakService.getNurseSpeak(1, 2, 3);
        Assert.assertEquals(3, nurseSpeak.size());
        Assert.assertEquals(7, nurseSpeak.get(0).getId());

        nurseSpeak = speakService.getNurseSpeak(1, 3, 3);
        Assert.assertEquals(2, nurseSpeak.size());
        Assert.assertEquals(10, nurseSpeak.get(0).getId());

    }

    @Test
    public void testAddNurseSpeak() {
        String content = "add speak content";
        String fileName = "test.txt";
        String fileContent = "file content";
        InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());
        NurseSpeakBean bean = speakService.addSmug(2, content, fileName, inputStream);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(2, bean.getUserId());
        Assert.assertEquals(content, bean.getContent());
        Assert.assertNotNull(bean.getTime());
        Assert.assertNotNull(bean.getImageUrl());
        logger.info(bean.toString());

        bean = speakService.addCathart(2, content, fileName, inputStream);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(2, bean.getUserId());
        Assert.assertEquals(content, bean.getContent());
        Assert.assertNotNull(bean.getTime());
        Assert.assertNotNull(bean.getImageUrl());
        logger.info(bean.toString());

        bean = speakService.addAskQuestion(2, content, fileName, inputStream);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(2, bean.getUserId());
        Assert.assertEquals(content, bean.getContent());
        Assert.assertNotNull(bean.getTime());
        Assert.assertNotNull(bean.getImageUrl());
        logger.info(bean.toString());
    }

    @Test
    public void testGetNurseSpeakById() {
        NurseSpeakBean bean = speakService.getNurseSpeak(3);
        Assert.assertEquals(3, bean.getId());
        Assert.assertEquals(1, bean.getUserId());
        Assert.assertEquals("hello 3 (@_@)!", bean.getContent());
        Assert.assertNotNull(bean.getTime());
        Assert.assertNotNull(bean.getImageUrl());
        Assert.assertEquals(3, bean.getComments().size());
        logger.info(bean.toString());
    }

    @Test
    public void testGetNurseSpeakCount(){
        long count = speakService.countByUserId(1);
        Assert.assertEquals(11, count);
    }

    @Test
    public void testGetNurseSpeakCountByUserIds() {
        Map<Long, Long> id2Count = speakService.countByUserIds("1,2,3");
        Assert.assertEquals(3, id2Count.size());
    }

    @Test
    public void testAddNurseSpeakComment() {
        String comment = "Test ping lun";
        Date time = new Date();
        NurseSpeakCommentBean commentBean = speakService.addSpeakComment(1, 1, 0, comment);
        Assert.assertTrue(commentBean.getId()>0);
        Assert.assertEquals(1, commentBean.getNurseSpeakId());
        Assert.assertEquals(1, commentBean.getCommentMakerId());
        Assert.assertEquals(0, commentBean.getCommentReceiverId());
        Assert.assertEquals(comment, commentBean.getComment());
        Assert.assertTrue(commentBean.getTime().getTime() >= time.getTime());
    }

    @Test
    public void testAddNurseSpeakThumbsUp() {
        NurseSpeakThumbsUpBean thumbsUp = null;
        Exception throwable = null;
        speakService.setNurseSpeakThumbsUp(5, 3);
        thumbsUp = speakService.getNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(5,3);
        Assert.assertNull(thumbsUp);

        throwable = null;
        try {
            speakService.setNurseSpeakThumbsUp(5, 1);
        }
        catch (Exception ex) {
            throwable = ex;
        }
        Assert.assertNotNull(throwable);
        Assert.assertTrue(throwable instanceof BadRequestException);

        Date time = new Date();
        NurseSpeakThumbsUpBean thumbsUpBean = speakService.setNurseSpeakThumbsUp(7, 3);
        Assert.assertEquals(7, thumbsUpBean.getNurseSpeakId());
        Assert.assertEquals(3, thumbsUpBean.getThumbsUpUserId());
        Assert.assertTrue(thumbsUpBean.getId()>0);
        Assert.assertTrue(thumbsUpBean.getTime().getTime() >= time.getTime());
    }

    @Test
    public void testGetNurseSpeakByType() {
        long userId = 1L;
        String speakType = SpeakType.SMUG.name();
        int index = 0;
        int number = 4;

        List<NurseSpeakBean> speaks = speakService.getSpeakByUserIdAndType(userId, speakType, index, number);
        Assert.assertEquals(4, speaks.size());

        number = 10;
        speaks = speakService.getSpeakByUserIdAndType(userId, speakType, index, number);
        Assert.assertEquals(10, speaks.size());

        for (NurseSpeakBean speak : speaks) {
            logger.info(speak.toString());
        }

        speakType = SpeakType.CATHART.name();
        speaks = speakService.getSpeakByUserIdAndType(userId, speakType, index, number);
        Assert.assertEquals(0, speaks.size());
    }

    @Test
    public void testCountNurseSpeakByType() {
        long userId = 1L;
        String speakType = SpeakType.SMUG.name();
        long count = speakService.countBySpeakByType(userId, speakType);
        Assert.assertEquals(11, count);

        speakType = SpeakType.ASK_QUESTION.name();
        count = speakService.countBySpeakByType(userId, speakType);
        Assert.assertEquals(0, count);
    }

}
