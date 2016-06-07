package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.beans.WorkFileTypeBean;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by zhaolisong on 16/3/29.
 */
@Transactional
public class WorkFileTypeServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(WorkFileTypeServiceTest.class.getName());

    @Autowired private WorkFileTypeService workFileTypeService;
    @Autowired private OfficialFileStorageService officialStorage;

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
        List<WorkFileTypeBean> workfileTypes = workFileTypeService.getAllWorkFileType();
        WorkFileTypeBean       workFileType  = workfileTypes.get(0);

        String               imageData    = "fdafdafdsafdsafdsafdsafdsafdsa";
        String      name         = "aaaaaa";
        int         factor       = 50;
        int         maxFileCount = 29;
        int         minFileCount = 10;

        WorkFileTypeBean newType = workFileTypeService.updateWorkfileType(workFileType.getId(), name, factor, maxFileCount, minFileCount, null, null);

        Assert.assertEquals(newType.getId(), workFileType.getId());
        Assert.assertNotEquals(newType.getName(), workFileType.getName());
        Assert.assertEquals(newType.getName(), name);
        Assert.assertNotEquals(newType.getFactor(), workFileType.getFactor());
        Assert.assertEquals(newType.getFactor(), factor);
        Assert.assertNotEquals(newType.getMinFileCount(), workFileType.getMinFileCount());
        Assert.assertEquals(newType.getMinFileCount(), minFileCount);
        Assert.assertNotEquals(newType.getMaxFileCount(), workFileType.getMaxFileCount());
        Assert.assertEquals(newType.getMaxFileCount(), maxFileCount);
    }
}
