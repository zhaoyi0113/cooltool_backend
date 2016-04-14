package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.*;
import com.cooltoo.backend.services.ImagesInSpeakService;
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
import java.util.ArrayList;
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
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/images_in_speak_data.xml")
})
public class NurseSpeakServiceTest extends AbstractCooltooTest{

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakServiceTest.class.getName());

    @Autowired
    private NurseSpeakService speakService;
    @Autowired
    private ImagesInSpeakService imagesService;

    @Test
    public void testGetNurseSpeakOnPage(){
        List<NurseSpeakBean> nurseSpeak = speakService.getNurseSpeak(1L, 0, 3);
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
    public void testGetSpeakByType() {
        List<NurseSpeakBean> nurseSpeak = speakService.getSpeakByType("smug", 0, 10);
        Assert.assertNotNull(nurseSpeak);
        Assert.assertEquals(10, nurseSpeak.size());

        nurseSpeak = speakService.getSpeakByType("smug", 0, 20);
        Assert.assertNotNull(nurseSpeak);
        Assert.assertEquals(11, nurseSpeak.size());
    }

    @Test
    public void testGetSpeakByUserIdAndType() {
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
    public void testGetNurseSpeak() {
        NurseSpeakBean bean = speakService.getNurseSpeak(3);
        Assert.assertEquals(3, bean.getId());
        Assert.assertEquals(1, bean.getUserId());
        Assert.assertEquals("hello 3 (@_@)!", bean.getContent());
        Assert.assertNotNull(bean.getTime());
        Assert.assertEquals(3, bean.getComments().size());
        logger.info(bean.toString());
    }

    @Test
    public void testAddSpeak() {
        String content = "add speak content";
        String fileName = "test.txt";
        String fileContent = "file content";
        InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());
        NurseSpeakBean bean = speakService.addSmug(2, content, fileName, inputStream);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(2, bean.getUserId());
        Assert.assertEquals(content, bean.getContent());
        Assert.assertNotNull(bean.getTime());
        logger.info(bean.toString());

        bean = speakService.addCathart(2, content, fileName, inputStream);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(2, bean.getUserId());
        Assert.assertEquals(content, bean.getContent());
        Assert.assertNotNull(bean.getTime());
        logger.info(bean.toString());

        bean = speakService.addAskQuestion(2, content, fileName, inputStream);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(2, bean.getUserId());
        Assert.assertEquals(content, bean.getContent());
        Assert.assertNotNull(bean.getTime());
        logger.info(bean.toString());
    }

    @Test
    public void testAddImage() {
        long speakId = 15;
        long userId  = 3;
        NurseSpeakBean speak = speakService.getNurseSpeak(speakId);
        Assert.assertEquals(2, speak.getImages().size());
        for (int i=15; i > 0; i--) {
            speakService.addImage(userId, speakId, "test", new ByteArrayInputStream("test".getBytes()));
        }
        speak = speakService.getNurseSpeak(speakId);
        Assert.assertEquals(9, speak.getImages().size());

        speakId = 14;
        speak = speakService.getNurseSpeak(speakId);
        Assert.assertEquals(1, speak.getImages().size());
        for (int i=15; i > 0; i--) {
            speakService.addImage(userId, speakId, "test", new ByteArrayInputStream("test".getBytes()));
        }
        speak = speakService.getNurseSpeak(speakId);
        Assert.assertEquals(1, speak.getImages().size());

        speakId = 1;
        userId  = 1;
        speak = speakService.getNurseSpeak(speakId);
        Assert.assertEquals(1, speak.getImages().size());
        for (int i=15; i > 0; i--) {
            speakService.addImage(userId, speakId, "test", new ByteArrayInputStream("test".getBytes()));
        }
        speak = speakService.getNurseSpeak(speakId);
        Assert.assertEquals(1, speak.getImages().size());
    }

    @Test
    public void testDeleteByIds() {
        String speakIds = "1,2,3,4,5,6";
        long   makerId  = 1;
        List<NurseSpeakBean> speaks = speakService.deleteByIds(makerId, speakIds);
        Assert.assertEquals(6, speaks.size());

        List<Long> lSpeakIds = new ArrayList<>();
        lSpeakIds.add(1L); lSpeakIds.add(2L);
        lSpeakIds.add(3L); lSpeakIds.add(4L);
        lSpeakIds.add(5L); lSpeakIds.add(6L);
        Map<Long, List<ImagesInSpeakBean>> images = imagesService.getImagesInSpeak(lSpeakIds);
        Assert.assertEquals(0, images.size());

        speaks = speakService.getNurseSpeak(makerId, 0, 11);
        Assert.assertEquals(5, speaks.size());
    }

    @Test
    public void testAddSpeakComment() {
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
    public void testDeleteSpeakComment() {
        long   speakId = 1;
        long   userId  = 1;
        String commentIds = "1,2";
        NurseSpeakBean speak = speakService.getNurseSpeak(speakId);
        Assert.assertEquals(2, speak.getCommentsCount());

        speakService.deleteSpeakComment(userId, commentIds);

        speak = speakService.getNurseSpeak(speakId);
        Assert.assertEquals(0, speak.getCommentsCount());
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
    public void testCountByUserId() {
        long userId = 1L;
        long count = speakService.countByUserId(userId);
        Assert.assertEquals(11, count);

        userId = 2L;
        count = speakService.countByUserId(userId);
        Assert.assertEquals(2, count);

        userId = 3L;
        count = speakService.countByUserId(userId);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testCountByUserIds() {
        String     strUserIds = "2,3";
        List<Long> userIds    = new ArrayList<>();
        userIds.add(2L);
        userIds.add(3L);

        Map<Long, Long> count1 = speakService.countByUserIds(strUserIds);
        Map<Long, Long> count2 = speakService.countByUserIds(userIds);

        Assert.assertNotNull(count1);
        Assert.assertNotNull(count2);
        Assert.assertEquals(2L, (count1.get(userIds.get(0))).longValue());
        Assert.assertEquals(2L, (count1.get(userIds.get(1))).longValue());
        Assert.assertEquals(2L, (count2.get(userIds.get(0))).longValue());
        Assert.assertEquals(2L, (count2.get(userIds.get(1))).longValue());
    }

    @Test
    public void testCountBySpeakType() {
        long userId = 1L;
        String speakType = SpeakType.SMUG.name();
        long count = speakService.countBySpeakType(userId, speakType);
        Assert.assertEquals(11, count);

        speakType = SpeakType.ASK_QUESTION.name();
        count = speakService.countBySpeakType(userId, speakType);
        Assert.assertEquals(0, count);
    }

}
