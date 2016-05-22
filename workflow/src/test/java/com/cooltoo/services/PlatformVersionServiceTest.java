package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.beans.PlatformVersionBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.PlatformType;
import com.cooltoo.exception.BadRequestException;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yzzhao on 5/22/16.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/platform_version_data.xml")
})
public class PlatformVersionServiceTest extends AbstractCooltooTest {

    @Autowired
    private PlatformVersionService versionService;

    @Test
    public void testAddPlatformVersion(){
        PlatformVersionBean bean = versionService.addPlatformVersion("IOS", "4.0", "",1 ,"aaa","bbb");
        Assert.assertNotNull(bean);
        Assert.assertEquals(CommonStatus.ENABLED, bean.getStatus());
        BadRequestException ex = null;
        try{
            versionService.addPlatformVersion("IOS", "4.0", "" ,0 ,"aaa","bbb");
        }catch(BadRequestException e){
            ex = e;
        }
        Assert.assertNotNull(ex);
        PlatformVersionBean newBean = versionService.getPlatformVersion(bean.getId());
        Assert.assertEquals(bean.getId(), newBean.getId());

        newBean = versionService.getPlatformLatestVersion(PlatformType.IOS);
        Assert.assertEquals("4.0", newBean.getVersion());
    }

    @Test
    public void testGetLatestVersion(){
        PlatformVersionBean latest = versionService.getPlatformLatestVersion(PlatformType.IOS);
        Assert.assertEquals("2.0", latest.getVersion());

    }

}
