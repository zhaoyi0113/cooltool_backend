package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.ImagesInSpeakBean;
import com.cooltoo.backend.services.ImagesInSpeakService;
import com.cooltoo.services.file.UserFileStorageService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 16/4/14.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/images_in_speak_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_data.xml")
})
public class ImagesInSpeakServiceTest extends AbstractCooltooTest{

    @Autowired
    private ImagesInSpeakService service;
    @Autowired
    private UserFileStorageService userStorage;

    @Test
    public void testCountBySpeakId() {
        long speakId = 1;
        long count = service.countImagesInSpeak(speakId);
        Assert.assertEquals(1, count);

        speakId = 15;
        count = service.countImagesInSpeak(speakId);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testGetImagesInSpeakBySpeakId() {
        long speakId = 1;
        List<ImagesInSpeakBean> images = service.getImagesInSpeak(speakId);
        Assert.assertEquals(1, images.size());

        speakId = 15;
        images = service.getImagesInSpeak(speakId);
        Assert.assertEquals(2, images.size());
    }

    @Test
    public void testGetImagesInSpeakBySpeakIds() {
        List<Long> speakIds = new ArrayList<>();
        speakIds.add(1L);
        speakIds.add(15L);
        Map<Long, List<ImagesInSpeakBean>> images = service.getImagesInSpeak(speakIds);
        Assert.assertEquals(2, images.size());
    }

    @Test
    public void testDeleteById() {
        long imagesInSpeakId = 1;
        long speakId = 1;
        ImagesInSpeakBean images = service.deleteById(imagesInSpeakId);
        Assert.assertNotNull(images);

        long count = service.countImagesInSpeak(speakId);
        Assert.assertEquals(0, count);
    }

    @Test
    public void testDeleteBySpeakIds() {
        long speakId1 = 1;
        long speakId2 = 2;
        long speakId3 = 3;
        long count1   = service.countImagesInSpeak(speakId1);
        long count2   = service.countImagesInSpeak(speakId2);
        long count3   = service.countImagesInSpeak(speakId3);
        Assert.assertEquals(1, count1);
        Assert.assertEquals(1, count2);
        Assert.assertEquals(1, count3);

        List<Long> speakIds = new ArrayList<>();
        speakIds.add(speakId1);
        speakIds.add(speakId2);
        speakIds.add(speakId3);
        service.deleteBySpeakIds(speakIds);

        count1   = service.countImagesInSpeak(speakId1);
        count2   = service.countImagesInSpeak(speakId2);
        count3   = service.countImagesInSpeak(speakId3);
        Assert.assertEquals(0, count1);
        Assert.assertEquals(0, count2);
        Assert.assertEquals(0, count3);
    }

    @Test
    public void testAddImage() {
        long        speakId     = 15;
        String      imageName   = "te";
        InputStream image       = new ByteArrayInputStream(imageName.getBytes());
        ImagesInSpeakBean  bean = service.addImage(speakId, imageName, image);
        Assert.assertNotNull(bean);
        Assert.assertNotNull(bean.getImageUrl());
        Assert.assertTrue(bean.getImageId()>0);
        Assert.assertEquals(speakId, bean.getSpeakId());
        Assert.assertTrue(userStorage.fileExist(bean.getImageId()));
        userStorage.deleteFile(bean.getImageId());
        Assert.assertFalse(userStorage.fileExist(bean.getImageUrl()));
    }
}
