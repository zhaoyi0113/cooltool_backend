package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.WorkFileTypeBean;
import com.cooltoo.backend.services.WorkFileTypeService;
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
public class WorkFileTypeServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(WorkFileTypeServiceTest.class.getName());

    @Autowired
    private WorkFileTypeService workFileTypeService;

    @Test
    @DatabaseSetup("classpath:/com/cooltoo/services/work_file_type_data.xml")
    public void testGetAllWorkFileType() {
        List<WorkFileTypeBean> workfileTypes = workFileTypeService.getAllWorkFileType();
        Assert.assertEquals(4, workfileTypes.size());
        logger.info(workfileTypes.get(0).toString());
        logger.info(workfileTypes.get(1).toString());
        logger.info(workfileTypes.get(2).toString());
        logger.info(workfileTypes.get(3).toString());
    }

    @Test
    @DatabaseSetup("classpath:/com/cooltoo/services/work_file_type_data.xml")
    public void testUpdateWorkFileType() {
        List<WorkFileTypeBean> speakTypes = workFileTypeService.getAllWorkFileType();
        WorkFileTypeBean       speakType  = speakTypes.get(0);

        String               imageData    = "fdafdafdsafdsafdsafdsafdsafdsa";
        ByteArrayInputStream imageStream  = new ByteArrayInputStream(imageData.getBytes());
        InputStream image        = imageStream;
        InputStream disableImage = imageStream;
        String      name         = "aaaaaa";
        int         factor       = 50;
        int         maxFileCount = 29;
        int         minFileCount = 10;

        WorkFileTypeBean newType = workFileTypeService.updateWorkfileType(speakType.getId(), name, factor, maxFileCount, minFileCount, image, disableImage);

        Assert.assertEquals(newType.getId(), speakType.getId());
        Assert.assertNotEquals(newType.getName(), speakType.getName());
        Assert.assertEquals(newType.getName(), name);
        Assert.assertNotEquals(newType.getFactor(), speakType.getFactor());
        Assert.assertEquals(newType.getFactor(), factor);
        Assert.assertNotEquals(newType.getMinFileCount(), speakType.getMinFileCount());
        Assert.assertEquals(newType.getMinFileCount(), minFileCount);
        Assert.assertNotEquals(newType.getMaxFileCount(), speakType.getMaxFileCount());
        Assert.assertEquals(newType.getMaxFileCount(), maxFileCount);
        Assert.assertNotEquals(newType.getImageId(), speakType.getImageId());
        Assert.assertNotEquals(newType.getDisableImageId(), speakType.getDisableImageId());
    }
}
