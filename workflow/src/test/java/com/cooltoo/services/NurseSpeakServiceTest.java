package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.*;
import com.cooltoo.backend.services.ImagesInSpeakService;
import com.cooltoo.backend.services.NurseSpeakService;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.services.file.UserFileStorageService;
import com.cooltoo.util.VerifyUtil;
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
    @Autowired
    private UserFileStorageService userStorage;

    @Test
    public void testCountAndGetSpeakByUserIdContentLikeAndTime() {
        long count = speakService.countByContentAndTime(1, "@_@", "2016-01-01 00:00:00", "2016-01-20 00:00:00");
        Assert.assertEquals(2, count);
        List<NurseSpeakBean> speaks = speakService.getSpeakByContentLikeAndTime(1, "@_@", "2016-01-01 00:00:00", "2016-01-19 00:00:00", 0, 5);
        Assert.assertEquals(2, speaks.size());
        Assert.assertEquals(3L, speaks.get(0).getId());
        Assert.assertEquals(9L, speaks.get(1).getId());
    }

    @Test
    public void testCountAndGetSpeakByContentLikeAndTime() {
        long count = speakService.countByContentAndTime(0, "hello", "2016-01-01 00:0:00", "2016-01-09 00:00:00");
        Assert.assertEquals(4, count);
        List<NurseSpeakBean> speaks = speakService.getSpeakByContentLikeAndTime(0, "hello", "2016-01-01 00:00:00", "2016-01-09 00:00:00", 0, 5);
        Assert.assertEquals(4, speaks.size());
        Assert.assertEquals(12L, speaks.get(0).getId());
        Assert.assertEquals(13L, speaks.get(1).getId());
        Assert.assertEquals(14L, speaks.get(2).getId());
        Assert.assertEquals(15L, speaks.get(3).getId());
    }

    @Test
    public void testUpdateSpeak() {
        String strSpeakIds = "1,2,3,4,5,6,7,8,9";
        List<Long> speakIds = VerifyUtil.parseLongIds(strSpeakIds);
        long success = speakService.updateSpeakStatus(strSpeakIds, CommonStatus.DISABLED.name());
        Assert.assertTrue(success==9);
        List<NurseSpeakBean> speakBeans = speakService.getSpeakByContentLikeAndTime(0L, null, null, null, 0, 30);
        for (NurseSpeakBean speak : speakBeans) {
            if (speakIds.contains(speak.getId()))
                Assert.assertEquals(CommonStatus.DISABLED, speak.getStatus());
            else
                Assert.assertEquals(CommonStatus.ENABLED, speak.getStatus());
        }
    }

    @Test
    public void testCountSortSpeakBySpeakType() {
        List<Long> speakIds = VerifyUtil.parseLongIds("2,3,12,13,14,15,16,17");
        long count = speakService.countSortSpeakBySpeakType(speakIds, 1);
        Assert.assertEquals(2, count);
        count = speakService.countSortSpeakBySpeakType(speakIds, 2);
        Assert.assertEquals(2, count);
        count = speakService.countSortSpeakBySpeakType(speakIds, 3);
        Assert.assertEquals(2, count);
        count = speakService.countSortSpeakBySpeakType(speakIds, 4);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testGetSpeakOnPage(){
        long userId = 1L;
        int pageIdx = 0;
        int number  = 3;
        String type = SpeakType.allValues();

        List<NurseSpeakBean> nurseSpeak = speakService.getSpeak(true, userId, type, pageIdx, number);
        Assert.assertEquals(3, nurseSpeak.size());
        Assert.assertEquals(1, nurseSpeak.get(0).getId());

        pageIdx = 1;
        nurseSpeak = speakService.getSpeak(true, userId, type, pageIdx, number);
        Assert.assertEquals(3, nurseSpeak.size());
        Assert.assertEquals(4, nurseSpeak.get(0).getId());

        pageIdx = 2;
        nurseSpeak = speakService.getSpeak(true, userId, type, pageIdx, number);
        Assert.assertEquals(3, nurseSpeak.size());
        Assert.assertEquals(7, nurseSpeak.get(0).getId());

        pageIdx = 3;
        nurseSpeak = speakService.getSpeak(true, userId, type, pageIdx, number);
        Assert.assertEquals(2, nurseSpeak.size());
        Assert.assertEquals(10, nurseSpeak.get(0).getId());

    }

    @Test
    public void testGetSpeakDiffType() {
        long userId = -1L;
        int pageIdx = 0;
        int number  = 10;
        String type = "smug";

        List<NurseSpeakBean> nurseSpeak = speakService.getSpeak(false, userId, type, pageIdx, number);
        Assert.assertNotNull(nurseSpeak);
        Assert.assertEquals(10, nurseSpeak.size());

        number = 20;
        nurseSpeak = speakService.getSpeak(false, userId, type, pageIdx, number);
        Assert.assertNotNull(nurseSpeak);
        Assert.assertEquals(11, nurseSpeak.size());
    }

    @Test
    public void testGetSpeakByUserIdAndType() {
        long userId = 1L;
        String speakType = SpeakType.SMUG.name();
        int index = 0;
        int number = 4;

        List<NurseSpeakBean> speaks = speakService.getSpeak(true, userId, speakType, index, number);
        Assert.assertEquals(4, speaks.size());

        number = 10;
        speaks = speakService.getSpeak(true, userId, speakType, index, number);
        Assert.assertEquals(10, speaks.size());

        for (NurseSpeakBean speak : speaks) {
            logger.info(speak.toString());
        }

        speakType = SpeakType.CATHART.name();
        speaks = speakService.getSpeak(true, userId, speakType, index, number);
        Assert.assertEquals(0, speaks.size());
    }

    @Test
    public void testGetNurseSpeak() {
        List<NurseSpeakBean> beans = speakService.getNurseSpeak(-1, 3);
        Assert.assertEquals(1, beans.size());
        NurseSpeakBean bean = beans.get(0);
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
        Assert.assertTrue(userStorage.fileExist(bean.getImages().get(0).getImageId()));
        userStorage.deleteFile(bean.getImages().get(0).getImageId());
        Assert.assertFalse(userStorage.fileExist(bean.getImages().get(0).getImageUrl()));
        logger.info(bean.toString());

        bean = speakService.addCathart(2, content, "大笑", fileName, inputStream);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(2, bean.getUserId());
        Assert.assertEquals(content, bean.getContent());
        Assert.assertEquals("大笑", bean.getAnonymousName());
        Assert.assertNotNull(bean.getTime());
        Assert.assertTrue(userStorage.fileExist(bean.getImages().get(0).getImageId()));
        userStorage.deleteFile(bean.getImages().get(0).getImageId());
        Assert.assertFalse(userStorage.fileExist(bean.getImages().get(0).getImageUrl()));
        logger.info(bean.toString());

        bean = speakService.addAskQuestion(2, content, fileName, inputStream);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(2, bean.getUserId());
        Assert.assertEquals(content, bean.getContent());
        Assert.assertNotNull(bean.getTime());
        Assert.assertTrue(userStorage.fileExist(bean.getImages().get(0).getImageId()));
        userStorage.deleteFile(bean.getImages().get(0).getImageId());
        Assert.assertFalse(userStorage.fileExist(bean.getImages().get(0).getImageUrl()));
        logger.info(bean.toString());
    }

    @Test
    public void testAddImage() {
        long speakId = 15;
        long userId  = 3;
        List<ImagesInSpeakBean> imagesInSpeaks = new ArrayList<>();
        List<NurseSpeakBean> beans = speakService.getNurseSpeak(userId, speakId);
        Assert.assertEquals(1, beans.size());
        NurseSpeakBean speak = beans.get(0);
        Assert.assertEquals(2, speak.getImages().size());
        for (int i=15; i > 0; i--) {
            ImagesInSpeakBean image = speakService.addImage(userId, speakId, "test", new ByteArrayInputStream("test".getBytes()));
            imagesInSpeaks.add(image);
        }
        beans = speakService.getNurseSpeak(userId, speakId);
        Assert.assertEquals(1, beans.size());
        speak = beans.get(0);
        Assert.assertEquals(9, speak.getImages().size());

        speakId = 14;
        beans = speakService.getNurseSpeak(userId, speakId);
        Assert.assertEquals(1, beans.size());
        speak = beans.get(0);
        Assert.assertEquals(1, speak.getImages().size());
        for (int i=15; i > 0; i--) {
            ImagesInSpeakBean image = speakService.addImage(userId, speakId, "test", new ByteArrayInputStream("test".getBytes()));
            imagesInSpeaks.add(image);
        }
        beans = speakService.getNurseSpeak(userId, speakId);
        Assert.assertEquals(1, beans.size());
        speak = beans.get(0);
        Assert.assertEquals(1, speak.getImages().size());

        speakId = 1;
        userId  = 1;
        beans = speakService.getNurseSpeak(userId, speakId);
        Assert.assertEquals(1, beans.size());
        speak = beans.get(0);
        Assert.assertEquals(1, speak.getImages().size());
        for (int i=15; i > 0; i--) {
            ImagesInSpeakBean image = speakService.addImage(userId, speakId, "test", new ByteArrayInputStream("test".getBytes()));
            imagesInSpeaks.add(image);
        }
        beans = speakService.getNurseSpeak(userId, speakId);
        Assert.assertEquals(1, beans.size());
        speak = beans.get(0);
        Assert.assertEquals(1, speak.getImages().size());

        for (ImagesInSpeakBean image : imagesInSpeaks) {
            if (image.getImageId()>0) {
                Assert.assertTrue(userStorage.fileExist(image.getImageId()));
                userStorage.deleteFile(image.getImageId());
                Assert.assertFalse(userStorage.fileExist(image.getImageUrl()));
            }
        }
    }

    @Test
    public void testDeleteByIds() {
        String speakIds = "1,2,3,4,5,6";
        long   makerId  = 1;
        String type     = SpeakType.allValues();

        List<NurseSpeakBean> speaks = speakService.deleteByIds(makerId, speakIds);
        Assert.assertEquals(6, speaks.size());

        List<Long> lSpeakIds = VerifyUtil.parseLongIds(speakIds);
        Map<Long, List<ImagesInSpeakBean>> images = imagesService.getImagesInSpeak(lSpeakIds);
        Assert.assertEquals(0, images.size());

        speaks = speakService.getSpeak(true, makerId, type, 0, 11);
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
        List<NurseSpeakBean> beans = speakService.getNurseSpeak(userId, speakId);
        Assert.assertEquals(1, beans.size());
        NurseSpeakBean speak = beans.get(0);
        Assert.assertEquals(2, speak.getCommentsCount());

        speakService.deleteSpeakComment(userId, commentIds);

        beans = speakService.getNurseSpeak(userId, speakId);
        Assert.assertEquals(1, beans.size());
        speak = beans.get(0);
        Assert.assertEquals(0, speak.getCommentsCount());
    }

    @Test
    public void testAddNurseSpeakThumbsUp() {
        NurseSpeakThumbsUpBean thumbsUp = null;
        Exception throwable = null;
        speakService.setNurseSpeakThumbsUp(5, 3);
        thumbsUp = speakService.getNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(5,3);
        Assert.assertNull(thumbsUp);

// user can thumbs up self speaks
//        throwable = null;
//        try {
//            speakService.setNurseSpeakThumbsUp(5, 1);
//        }
//        catch (Exception ex) {
//            throwable = ex;
//        }
//        Assert.assertNotNull(throwable);
//        Assert.assertTrue(throwable instanceof BadRequestException);

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
        String type = SpeakType.allValues();

        long count = speakService.countSpeak(true, userId, type);
        Assert.assertEquals(11, count);

        userId = 2L;
        count = speakService.countSpeak(true, userId, type);
        Assert.assertEquals(2, count);

        userId = 3L;
        count = speakService.countSpeak(true, userId, type);
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
        String type = SpeakType.SMUG.name();

        long count = speakService.countSpeak(true, userId, type);
        Assert.assertEquals(11, count);

        type = SpeakType.ASK_QUESTION.name();
        count = speakService.countSpeak(true, userId, type);
        Assert.assertEquals(0, count);
    }

    @Test
    public void testCountOfficial() {
        long userId = -1L;
        String type = "official";

        long count = speakService.countSpeak(true, userId, type);
        Assert.assertEquals(5, count);

        List<NurseSpeakBean> speaks = speakService.getSpeak(true, userId, type, 1, 3);
        Assert.assertEquals(2, speaks.size());
    }
}
