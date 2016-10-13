package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.beans.NurseQualificationFileBean;
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
 * Created by hp on 2016/4/5.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_qualification_file_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/file_storage_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/work_file_type_data.xml")
})
public class NurseQualificationFileServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(NurseQualificationFileServiceTest.class.getName());

    @Autowired
    private NurseQualificationFileService service;

    @Test
    public void testGetAll() {
        List<NurseQualificationFileBean> all = null;
        all = service.getAllFileByQualificationId(1, "");
        Assert.assertEquals(5, all.size());
        all = service.getAllFileByQualificationId(2, "");
        Assert.assertEquals(1, all.size());
        all = service.getAllFileByQualificationId(3, "");
        Assert.assertEquals(3, all.size());
    }
}
