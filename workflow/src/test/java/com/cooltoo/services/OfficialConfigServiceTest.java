package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.beans.OfficialConfigBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.cooltoo.util.VerifyUtil;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
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
 * Created by zhaolisong on 16/5/9.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/official_config_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/file_storage_data.xml"),
})
public class OfficialConfigServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(OfficialConfigService.class.getName());

    @Autowired
    private OfficialConfigService configService;
    @Autowired
    private OfficialFileStorageService officialStorage;

    @Test
    public void testGetKeys() {
        List<String> configKeys = configService.getKeys();
        Assert.assertEquals(1, configKeys.size());
    }

    @Test
    public void testCountConfigByStatus() {
        String[] status = {"aa9", "ALL", "DISABLED", "ENABLED", ""};
        long count = configService.countConfig(status[0]);
        Assert.assertEquals(0, count);
        count = configService.countConfig(status[1]);
        Assert.assertEquals(5, count);
        count = configService.countConfig(status[2]);
        Assert.assertEquals(2, count);
        count = configService.countConfig(status[3]);
        Assert.assertEquals(3, count);
        count = configService.countConfig(status[4]);
        Assert.assertEquals(0, count);
    }

    @Test
    public void testGetConfigByName() {
        String configName = "config02";
        OfficialConfigBean configBean = configService.getConfig(configName);
        Assert.assertNotNull(configBean);
        Assert.assertEquals(configName, configBean.getName());
        Assert.assertEquals("2", configBean.getValue());
        Assert.assertEquals(2, configBean.getId());
        Assert.assertEquals(2L, configBean.getImageId());
    }

    @Test
    public void testGetConfigByStatus() {
        String[] status = {"aa9", "ALL", "DISABLED", "ENABLED", ""};
        List<OfficialConfigBean> configs = configService.getConfig(status[0], 0, 2);
        Assert.assertEquals(0, configs.size());

        configs = configService.getConfig(status[1], 0, 2);
        Assert.assertEquals(2, configs.size());
        configs = configService.getConfig(status[1], 2, 2);
        Assert.assertEquals(1, configs.size());

        configs = configService.getConfig(status[2], 0, 2);
        Assert.assertEquals(2, configs.size());
        configs = configService.getConfig(status[2], 1, 2);
        Assert.assertEquals(0, configs.size());

        configs = configService.getConfig(status[3], 0, 2);
        Assert.assertEquals(2, configs.size());
        configs = configService.getConfig(status[3], 1, 2);
        Assert.assertEquals(1, configs.size());

        configs = configService.getConfig(status[4], 0, 2);
        Assert.assertEquals(0, configs.size());
    }

    @Test
    public void testDeleteByIds() {
        String ids = "1,2,3,4";
        configService.deleteByIds(ids);
        List<OfficialConfigBean> configs = configService.getConfig("ALL", 0, 2);
        Assert.assertEquals(1, configs.size());
        Assert.assertEquals(5, configs.get(0).getId());
        Assert.assertEquals("config05", configs.get(0).getName());
        Assert.assertEquals("5", configs.get(0).getValue());
    }

    @Test
    public void testUpdateConfig() {
        int id = 1;
        String name = "config05";
        String value = name;
        String imageName = name;
        InputStream image =  new ByteArrayInputStream(imageName.getBytes());

        OfficialConfigBean configBean = configService.updateConfig(id, name, value, CommonStatus.DISABLED.name(), imageName, image);
        Assert.assertNotEquals(name, configBean.getName());
        Assert.assertEquals(value, configBean.getValue());
        Assert.assertEquals(CommonStatus.DISABLED, configBean.getStatus());
        Assert.assertTrue(1!=configBean.getImageId());
        Assert.assertTrue(officialStorage.fileExist(configBean.getImageId()));
        officialStorage.deleteFile(configBean.getImageId());
        Assert.assertFalse(officialStorage.fileExist(configBean.getImageId()));

        name = "hhahahaha";
        configBean = configService.updateConfig(id, name, null, null, null, null);
        Assert.assertEquals(name, configBean.getName());
    }

    @Test
    public void testAddConfig() {
        String name = OfficialConfigService.OFFICIAL_SPEAK_PROFILE;
        String value = name;
        String imageName = name;
        InputStream image =  new ByteArrayInputStream(imageName.getBytes());
        Throwable throwable = null;
        try {
            configService.addConfig(name, value, CommonStatus.DISABLED.name(), imageName, image);
        }
        catch (Exception ex) {
            throwable = ex;
        }
        Assert.assertNotNull(throwable);

        name = "test config";
        value = name;
        imageName = name;
        image =  new ByteArrayInputStream(imageName.getBytes());
        OfficialConfigBean configBean = configService.addConfig(name, value, CommonStatus.DISABLED.name(), imageName, image);
        Assert.assertNotNull(configBean);
        Assert.assertEquals(name, configBean.getName());
        Assert.assertEquals(value, configBean.getValue());
        Assert.assertEquals(CommonStatus.DISABLED, configBean.getStatus());
        Assert.assertTrue(configBean.getImageId()>0);
        Assert.assertTrue(!VerifyUtil.isStringEmpty(configBean.getImageUrl()));
        Assert.assertTrue(officialStorage.fileExist(configBean.getImageId()));

        officialStorage.deleteFile(configBean.getImageId());
        Assert.assertFalse(officialStorage.fileExist(configBean.getImageId()));
    }


}
