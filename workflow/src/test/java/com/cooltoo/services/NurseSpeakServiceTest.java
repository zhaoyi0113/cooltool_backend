package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.beans.NurseSpeakThumbsUpBean;
import com.cooltoo.backend.services.NurseSpeakService;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.exception.BadRequestException;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by yzzhao on 3/15/16.
 */
@Transactional
public class NurseSpeakServiceTest extends AbstractCooltooTest{

    @Autowired
    private NurseSpeakService speakService;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_data.xml")
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
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_data.xml")
    public void testAddNurseSpeak() {
        String content = "add speak content";
        String fileName = "test.txt";
        String fileContent = "file content";
        InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());
        NurseSpeakBean bean = speakService.addNurseSpeak(2, content, "SMUG", fileName, inputStream);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(2, bean.getUserId());
        Assert.assertEquals(content, bean.getContent());
        Assert.assertNotNull(bean.getTime());
        Assert.assertNotNull(bean.getImageUrl());
        System.out.println(bean);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_data.xml")
    public void testGetNurseSpeakById() {
        NurseSpeakBean bean = speakService.getNurseSpeak(3);
        Assert.assertEquals(3, bean.getId());
        Assert.assertEquals(1, bean.getUserId());
        Assert.assertEquals("hello", bean.getContent());
        Assert.assertNotNull(bean.getTime());
        Assert.assertNotNull(bean.getImageUrl());
        Assert.assertEquals(3, bean.getComments().size());
        System.out.println(bean);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_data.xml")
    public void testGetNurseSpeakCount(){
        long count = speakService.getNurseSpeakCount(1);
        Assert.assertEquals(11, count);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_data.xml")
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
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_data.xml")
    public void testAddNurseSpeakThumbsUp() {
        Exception throwable = null;
        try {
            speakService.addNurseSpeakThumbsUp(5, 3);
        }
        catch (Exception ex) {
            throwable = ex;
        }
        Assert.assertNotNull(throwable);
        Assert.assertTrue(throwable instanceof BadRequestException);

        throwable = null;
        try {
            speakService.addNurseSpeakThumbsUp(5, 1);
        }
        catch (Exception ex) {
            throwable = ex;
        }
        Assert.assertNotNull(throwable);
        Assert.assertTrue(throwable instanceof BadRequestException);

        Date time = new Date();
        NurseSpeakThumbsUpBean thumbsUpBean = speakService.addNurseSpeakThumbsUp(7, 3);
        Assert.assertEquals(7, thumbsUpBean.getNurseSpeakId());
        Assert.assertEquals(3, thumbsUpBean.getThumbsUpUserId());
        Assert.assertTrue(thumbsUpBean.getId()>0);
        Assert.assertTrue(thumbsUpBean.getTime().getTime() >= time.getTime());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_data.xml")
    public void testDeleteNurseSpeakThumbsUp() {
        NurseSpeakThumbsUpBean thumbsUpBean = speakService.getNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(5, 3);
        Assert.assertNotNull(thumbsUpBean);
        Assert.assertEquals(5, thumbsUpBean.getNurseSpeakId());
        Assert.assertEquals(3, thumbsUpBean.getThumbsUpUserId());

        speakService.deleteNurseSpeakThumbsUp(5, 3);

        Exception throwable = null;
        try {
            speakService.getNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(5, 3);
        }
        catch (Exception ex) {
            throwable = ex;
        }
        Assert.assertNotNull(throwable);
        Assert.assertTrue(throwable instanceof BadRequestException);
    }
}
