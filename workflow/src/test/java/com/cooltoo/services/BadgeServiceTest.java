package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.services.BadgeService;
import com.cooltoo.beans.BadgeBean;
import com.cooltoo.constants.BadgeGrade;
import com.cooltoo.constants.SocialAbilityType;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by yzzhao on 2/24/16.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/badge_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/speak_type_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/occupation_skill_data.xml")
})
public class BadgeServiceTest extends AbstractCooltooTest {

    @Autowired
    private BadgeService badgeService;

    @Test
    public void testCountByAbilityType(){
        long count = badgeService.countByAbilityType("ALL");
        Assert.assertEquals(8, count);

        count = badgeService.countByAbilityType(SocialAbilityType.COMMUNITY.name());
        Assert.assertEquals(6, count);

        count = badgeService.countByAbilityType(SocialAbilityType.SKILL.name());
        Assert.assertEquals(2, count);
    }

    @Test
    public void testGetBadgeByAbilityIdAndType() {
        int abilityId = 1;
        String abilityType = SocialAbilityType.COMMUNITY.name();
        List<BadgeBean> badges = badgeService.getBadgeByAbilityIdAndType(abilityId, abilityType);
        Assert.assertEquals(3, badges.size());

        abilityId = 1;
        abilityType = SocialAbilityType.SKILL.name();
        badges = badgeService.getBadgeByAbilityIdAndType(abilityId, abilityType);
        Assert.assertEquals(2, badges.size());
    }

    @Test
    public void testGetBadgeByAbilityType(){
        int pageIndex = 0;
        int pageSize  = 4;
        String abilityType = SocialAbilityType.COMMUNITY.name();
        List<BadgeBean> badges = badgeService.getBadgeByAbilityType(abilityType, pageIndex, pageSize);
        Assert.assertEquals(4, badges.size());

        pageIndex = 1;
        badges = badgeService.getBadgeByAbilityType(abilityType, pageIndex, pageSize);
        Assert.assertEquals(2, badges.size());
    }

    @Test
    public void testDeleteBadgeByIds(){
        String ids = badgeService.deleteBadgeByIds("1,2,3,4,5,6,7");
        Assert.assertNotNull(ids);
        List<BadgeBean> badges = badgeService.getBadgeByAbilityType("ALL", 0, 10);
        Assert.assertEquals(1, badges.size());
        Assert.assertEquals(8, badges.get(0).getId());
    }

    @Test
    public void testDeleteByAbilityIdAndType(){
        int    abilityId   = 1;
        String abilityType = SocialAbilityType.SKILL.name();
        List<Integer> ids = badgeService.deleteByAbilityIdAndType(abilityId, abilityType);
        Assert.assertNotNull(ids);
        Assert.assertEquals(2, ids.size());

        long count = badgeService.countByAbilityType(abilityType);
        Assert.assertEquals(0, count);
    }

    @Test
    public void testAddBadge(){
        ByteArrayInputStream image = new ByteArrayInputStream("fdsafdsafds".getBytes());
        BadgeBean badge = badgeService.addBadge("name222", 12, "LEVEL1", 1000, "SKILL", "aaaaa", image);
        Assert.assertNotNull(badge);
        Assert.assertTrue(badge.getId()>0);
        Assert.assertEquals("name222", badge.getName());
        Assert.assertEquals(12, badge.getPoint());
        Assert.assertEquals(BadgeGrade.LEVEL1, badge.getGrade());
        Assert.assertEquals(1000, badge.getAbilityId());
        Assert.assertEquals(SocialAbilityType.SKILL, badge.getAbilityType());
        Assert.assertTrue(badge.getImageId() > 0);
        Assert.assertNotNull(badge.getImageUrl());
    }

    @Test
    public void testUpdateBadget(){
        ByteArrayInputStream image = new ByteArrayInputStream("fdsafdsafds".getBytes());
        BadgeBean badge = badgeService.updateBadge(1, "name222", 123, "LEVEL2", 1000, "SKILL", "aaaaa", image);
        Assert.assertNotNull(badge);
        Assert.assertEquals(1, badge.getId());
        Assert.assertEquals("name222", badge.getName());
        Assert.assertEquals(123, badge.getPoint());
        Assert.assertEquals(BadgeGrade.LEVEL2, badge.getGrade());
        Assert.assertEquals(1000, badge.getAbilityId());
        Assert.assertEquals(SocialAbilityType.SKILL, badge.getAbilityType());
        Assert.assertNotEquals(1, badge.getImageId());
        Assert.assertNotNull(badge.getImageUrl());
    }
}
