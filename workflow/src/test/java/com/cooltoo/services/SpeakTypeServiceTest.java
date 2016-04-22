package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.SpeakTypeBean;
import com.cooltoo.backend.services.SpeakTypeService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhaolisong on 16/3/29.
 */
@Transactional
public class SpeakTypeServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(SpeakTypeServiceTest.class.getName());

    @Autowired
    private SpeakTypeService speakTypeService;

    @Test
    @DatabaseSetup("classpath:/com/cooltoo/services/speak_type_data.xml")
    public void testGetAllSpeakType() {
        List<SpeakTypeBean> speakTypes = speakTypeService.getAllSpeakType();
        Assert.assertEquals(4, speakTypes.size());
        logger.info(speakTypes.get(0).toString());
        logger.info(speakTypes.get(1).toString());
        logger.info(speakTypes.get(2).toString());
        logger.info(speakTypes.get(3).toString());
    }

    @Test
    @DatabaseSetup("classpath:/com/cooltoo/services/speak_type_data.xml")
    public void testUpdateSpeakType() {
        List<SpeakTypeBean> speakTypes = speakTypeService.getAllSpeakType();
        SpeakTypeBean       speakType  = speakTypes.get(0);

        String               imageData    = "fdafdafdsafdsafdsafdsafdsafdsa";
        ByteArrayInputStream imageStream  = new ByteArrayInputStream(imageData.getBytes());
        InputStream image        = imageStream;
        InputStream disableImage = imageStream;
        String      name         = "aaaaaa";
        int         factor       = 50;

        SpeakTypeBean newType = speakTypeService.updateSpeakType(speakType.getId(), name, factor, image, disableImage);

        Assert.assertEquals(newType.getId(), speakType.getId());
        Assert.assertNotEquals(newType.getName(), speakType.getName());
        Assert.assertEquals(newType.getName(), name);
        Assert.assertNotEquals(newType.getFactor(), speakType.getFactor());
        Assert.assertEquals(newType.getFactor(), factor);
        Assert.assertNotEquals(newType.getImageId(), speakType.getImageId());
        Assert.assertNotEquals(newType.getDisableImageId(), speakType.getDisableImageId());
    }
}
