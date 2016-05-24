package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.services.BadgeService;
import com.cooltoo.beans.BadgeBean;
import com.cooltoo.beans.SpecificSocialAbility;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by yzzhao on 2/24/16.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/badge_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/speak_type_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_department_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/occupation_skill_data.xml")
})
public class BadgeServiceTest extends AbstractCooltooTest {

    @Autowired
    private BadgeService badgeService;
    @Autowired
    private OfficialFileStorageService officialStorage;

    @Test
    public void testGetAllAbilityType() {
        Map<String, String> allAbility = badgeService.getAllAbilityType();
        Assert.assertEquals(5, allAbility.size());
        Assert.assertEquals("发言", allAbility.get("COMMUNITY"));
        Assert.assertEquals("技能", allAbility.get("SKILL"));
        Assert.assertEquals("职业", allAbility.get("OCCUPATION"));
        Assert.assertEquals("点赞", allAbility.get("THUMBS_UP"));
        Assert.assertEquals("评论", allAbility.get("COMMENT"));
    }

    @Test
    public void testGetItemsOfType() {
        List<SpecificSocialAbility> items = badgeService.getItemsOfType("COMMUNITY");
        Assert.assertEquals(4, items.size());
        for (SpecificSocialAbility ability : items) {
            Assert.assertTrue(ability.getAbilityId() == 1
                    || ability.getAbilityId() == 2
                    || ability.getAbilityId() == 3
                    || ability.getAbilityId() == 4);
            Assert.assertTrue("晒图".equals(ability.getAbilityName())
                    || "吐槽".equals(ability.getAbilityName())
                    || "提问".equals(ability.getAbilityName())
                    || "官方发言".equals(ability.getAbilityName()));
            Assert.assertEquals("COMMUNITY", ability.getAbilityType().name());
        }

        items = badgeService.getItemsOfType("SKILL");
        Assert.assertEquals(1, items.size());
        Assert.assertEquals(1000, items.get(0).getAbilityId());
        Assert.assertEquals("occ1", items.get(0).getAbilityName());
        Assert.assertEquals("SKILL", items.get(0).getAbilityType().name());
        items = badgeService.getItemsOfType("OCCUPATION");
        Assert.assertEquals(0, items.size());
        items = badgeService.getItemsOfType("THUMBS_UP");
        Assert.assertEquals(2, items.size());
        for (SpecificSocialAbility ability : items) {
            Assert.assertTrue(ability.getAbilityId() == 1 || ability.getAbilityId() == 2);
            Assert.assertTrue("被赞".equals(ability.getAbilityName()) || "点赞".equals(ability.getAbilityName()));
            Assert.assertEquals("THUMBS_UP", ability.getAbilityType().name());
        }
        items = badgeService.getItemsOfType("COMMENT");
        Assert.assertEquals(2, items.size());
        for (SpecificSocialAbility ability : items) {
            Assert.assertTrue(ability.getAbilityId() == 1 || ability.getAbilityId() == 2);
            Assert.assertTrue("评论".equals(ability.getAbilityName()) || "答题".equals(ability.getAbilityName()));
            Assert.assertEquals("COMMENT", ability.getAbilityType().name());
        }
    }

    @Test
    public void testGetBadgeByPointAndAbilityIdAndType(){
        BadgeBean badge = badgeService.getBadgeByPointAndAbilityIdAndType(90, 1, "COMMUNITY");
        Assert.assertNull(badge);
        badge = badgeService.getBadgeByPointAndAbilityIdAndType(100, 1, "COMMUNITY");
        Assert.assertEquals(1, badge.getId());
        Assert.assertEquals("铜 1", badge.getName());
        Assert.assertEquals(0, badge.getGrade());
        badge = badgeService.getBadgeByPointAndAbilityIdAndType(150, 1, "COMMUNITY");
        Assert.assertEquals(1, badge.getId());
        Assert.assertEquals("铜 1", badge.getName());
        Assert.assertEquals(0, badge.getGrade());
        badge = badgeService.getBadgeByPointAndAbilityIdAndType(200, 1, "COMMUNITY");
        Assert.assertEquals(2, badge.getId());
        Assert.assertEquals("银 1", badge.getName());
        Assert.assertEquals(1, badge.getGrade());
        badge = badgeService.getBadgeByPointAndAbilityIdAndType(250, 1, "COMMUNITY");
        Assert.assertEquals(2, badge.getId());
        Assert.assertEquals("银 1", badge.getName());
        Assert.assertEquals(1, badge.getGrade());
    }

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
        BadgeBean badge = badgeService.addBadge("name222", "description 222", 12, 1, 1000, "SKILL", "aaaaa", image);
        Assert.assertNotNull(badge);
        Assert.assertTrue(badge.getId()>0);
        Assert.assertEquals("name222", badge.getName());
        Assert.assertEquals("description 222", badge.getDescription());
        Assert.assertEquals(12, badge.getPoint());
        Assert.assertEquals(1, badge.getGrade());
        Assert.assertEquals(1000, badge.getAbilityId());
        Assert.assertEquals(SocialAbilityType.SKILL, badge.getAbilityType());
        Assert.assertTrue(badge.getImageId() > 0);
        Assert.assertNotNull(badge.getImageUrl());
        Assert.assertTrue(officialStorage.fileExist(badge.getImageId()));
        officialStorage.deleteFile(badge.getImageId());
        Assert.assertFalse(officialStorage.fileExist(badge.getImageUrl()));

    }

    @Test
    public void testUpdateBadget(){
        ByteArrayInputStream image = new ByteArrayInputStream("fdsafdsafds".getBytes());
        BadgeBean badge = badgeService.updateBadge(1, "name222", "description 222", 123, 2, 1000, "SKILL", "aaaaa", image);
        Assert.assertNotNull(badge);
        Assert.assertEquals(1, badge.getId());
        Assert.assertEquals("name222", badge.getName());
        Assert.assertEquals("description 222", badge.getDescription());
        Assert.assertEquals(123, badge.getPoint());
        Assert.assertEquals(2, badge.getGrade());
        Assert.assertEquals(1000, badge.getAbilityId());
        Assert.assertEquals(SocialAbilityType.SKILL, badge.getAbilityType());
        Assert.assertNotEquals(1, badge.getImageId());
        Assert.assertNotNull(badge.getImageUrl());
        Assert.assertTrue(officialStorage.fileExist(badge.getImageId()));
        officialStorage.deleteFile(badge.getImageId());
        Assert.assertFalse(officialStorage.fileExist(badge.getImageUrl()));
    }
}
