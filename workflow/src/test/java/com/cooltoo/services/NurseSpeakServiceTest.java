package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.services.NurseSpeakService;
import com.cooltoo.constants.SpeakType;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
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
        NurseSpeakBean bean = speakService.getNurseSpeak(1, 3);
        Assert.assertEquals(3, bean.getId());
        Assert.assertEquals(1, bean.getUserId());
        Assert.assertEquals("hello", bean.getContent());
        Assert.assertNotNull(bean.getTime());
        Assert.assertNotNull(bean.getImageUrl());
        System.out.println(bean);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_data.xml")
    public void testGetNurseSpeakCount(){
        long count = speakService.getNurseSpeakCount(1);
        Assert.assertEquals(11, count);
    }
}
