package com.cooltoo.services;

import com.cooltoo.Application;
import com.cooltoo.beans.BadgeBean;
import com.cooltoo.serivces.BadgeService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yzzhao on 2/24/16.
 */
@Transactional
public class BadgeServiceTest extends AbstractCooltooTest{

    @Autowired
    private BadgeService badgeService;

    @Test
    public void testCreateBadge(){
        BadgeBean badgeBean = new BadgeBean();
        badgeBean.setPoint(100);
        badgeBean.setName("aaa");
        int id = badgeService.createNewBadge(badgeBean);
        List<BadgeBean> allBadge = badgeService.getAllBadge();
        Assert.assertTrue(allBadge.size()>0);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/badge_data.xml")
    public void testGetBadget(){
        List<BadgeBean> allBadge = badgeService.getAllBadge();
        Assert.assertEquals(5, allBadge.size());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/badge_data.xml")
    public void testDeleteBadget(){
        BadgeBean badgeBean = badgeService.deleteBadge(1000);
        Assert.assertNotNull(badgeBean);
        Assert.assertEquals(1000, badgeBean.getId());
    }
}
