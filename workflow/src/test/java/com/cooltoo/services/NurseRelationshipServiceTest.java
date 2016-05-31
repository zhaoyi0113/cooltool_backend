package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseRelationshipBean;
import com.cooltoo.backend.services.NurseRelationshipService;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.RelationshipType;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hp on 2016/5/30.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_relationship_data.xml")
})
public class NurseRelationshipServiceTest extends AbstractCooltooTest {

    @Autowired private NurseRelationshipService relationshipService;

    @Test
    public void testCount() {
        long userId = 0;
        long relativeUserId = 0;
        String relationType = "";
        String status = "";
        long count = relationshipService.countCondition(userId, relativeUserId, relationType, status);
        Assert.assertEquals(5, count);

        userId = 1;
        count = relationshipService.countCondition(userId, relativeUserId, relationType, status);
        Assert.assertEquals(3, count);

        userId = 0;
        relativeUserId = 3;
        count = relationshipService.countCondition(userId, relativeUserId, relationType, status);
        Assert.assertEquals(2, count);

        relativeUserId = 0;
        relationType = RelationshipType.BLOCK_ALL_SPEAK.name();
        count = relationshipService.countCondition(userId, relativeUserId, relationType, status);
        Assert.assertEquals(3, count);

        relationType = "";
        status = CommonStatus.ENABLED.name();
        count = relationshipService.countCondition(userId, relativeUserId, relationType, status);
        Assert.assertEquals(3, count);

        userId = 1;
        count = relationshipService.countCondition(userId, relativeUserId, relationType, status);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testGetNurseRelationship() {
        long userId = 0;
        long relativeUserId = 0;
        String relationType = "";
        String status = "";
        int pageIndex = 0;
        int sizePerPage = 4;
        List<NurseRelationshipBean> relation = null;

        relation = relationshipService.getRelation(userId, relativeUserId, relationType, status, pageIndex, sizePerPage);
        Assert.assertEquals(4, relation.size());
        Assert.assertEquals(1, relation.get(0).getId());
        Assert.assertEquals(2, relation.get(1).getId());
        Assert.assertEquals(3, relation.get(2).getId());
        Assert.assertEquals(5, relation.get(3).getId());
        Assert.assertNotNull(relation.get(0).getUser());
        Assert.assertNotNull(relation.get(1).getUser());
        Assert.assertNotNull(relation.get(2).getUser());
        Assert.assertNotNull(relation.get(3).getUser());
        Assert.assertNotNull(relation.get(0).getRelativeUser());
        Assert.assertNotNull(relation.get(1).getRelativeUser());
        Assert.assertNotNull(relation.get(2).getRelativeUser());
        Assert.assertNotNull(relation.get(3).getRelativeUser());

        pageIndex = 1;
        relation = relationshipService.getRelation(userId, relativeUserId, relationType, status, pageIndex, sizePerPage);
        Assert.assertEquals(1, relation.size());
        Assert.assertEquals(4, relation.get(0).getId());
        Assert.assertNotNull(relation.get(0).getUser());
        Assert.assertNotNull(relation.get(0).getRelativeUser());

        userId = 1;
        relation = relationshipService.getRelation(false, userId, relativeUserId, relationType, status);
        Assert.assertEquals(3, relation.size());
        Assert.assertEquals(1, relation.get(0).getId());
        Assert.assertEquals(2, relation.get(1).getId());
        Assert.assertEquals(3, relation.get(2).getId());
    }

    @Test
    public void testGetRelativeUserIds() {
        long userId = 1;
        String relationType = RelationshipType.BLOCK_ALL_SPEAK.name();
        String status = CommonStatus.ENABLED.name();
        List<Long> relativeUserIds = relationshipService.getRelativeUserId(userId, relationType, status);
        Assert.assertEquals(2, relativeUserIds.size());
        Assert.assertTrue(relativeUserIds.contains(4L));
        Assert.assertTrue(relativeUserIds.contains(5L));

        relationType = RelationshipType.OTHER.name();
        status = CommonStatus.DISABLED.name();
        relativeUserIds = relationshipService.getRelativeUserId(userId, relationType, status);
        Assert.assertEquals(1, relativeUserIds.size());
        Assert.assertTrue(relativeUserIds.contains(4L));
    }

    @Test
    public void testSetRelation() {
        long userId = 1;
        long relativeUserId = 6;
        String relationType = RelationshipType.BLOCK_ALL_SPEAK.name();

        List<NurseRelationshipBean> relationships = relationshipService.getRelation(false, userId, relativeUserId, relationType, "");
        Assert.assertEquals(0, relationships.size());

        NurseRelationshipBean bean = relationshipService.setRelation(userId, relativeUserId, relationType);
        Assert.assertNotNull(bean);
        Assert.assertEquals(userId, bean.getUserId());
        Assert.assertEquals(relativeUserId, bean.getRelativeUserId());
        Assert.assertEquals(RelationshipType.BLOCK_ALL_SPEAK, bean.getRelationType());
        Assert.assertEquals(CommonStatus.ENABLED, bean.getStatus());

        relationships = relationshipService.getRelation(false, userId, relativeUserId, relationType, "");
        Assert.assertEquals(1, relationships.size());

        bean = relationshipService.setRelation(userId, relativeUserId, relationType);
        Assert.assertNotNull(bean);
        Assert.assertEquals(userId, bean.getUserId());
        Assert.assertEquals(relativeUserId, bean.getRelativeUserId());
        Assert.assertEquals(RelationshipType.BLOCK_ALL_SPEAK, bean.getRelationType());
        Assert.assertEquals(CommonStatus.DISABLED, bean.getStatus());
    }

    @Test
    public void testUpdateRelationStatus() {
        long relationId = 3;
        long userId = 1;
        long relativeUserId = 5;
        String relationType = RelationshipType.BLOCK_ALL_SPEAK.name();

        List<NurseRelationshipBean> relationships = relationshipService.getRelation(false, userId, relativeUserId, relationType, "");
        Assert.assertEquals(1, relationships.size());
        Assert.assertEquals(relationId, relationships.get(0).getId());
        Assert.assertEquals(userId, relationships.get(0).getUserId());
        Assert.assertEquals(relativeUserId, relationships.get(0).getRelativeUserId());
        Assert.assertEquals(RelationshipType.BLOCK_ALL_SPEAK, relationships.get(0).getRelationType());
        Assert.assertEquals(CommonStatus.ENABLED, relationships.get(0).getStatus());

        String status = CommonStatus.DISABLED.name();
        NurseRelationshipBean bean = relationshipService.updateRelationStatus(relationId, status);
        Assert.assertNotNull(bean);
        Assert.assertEquals(relationId, bean.getId());
        Assert.assertEquals(userId, bean.getUserId());
        Assert.assertEquals(relativeUserId, bean.getRelativeUserId());
        Assert.assertEquals(RelationshipType.BLOCK_ALL_SPEAK, bean.getRelationType());
        Assert.assertEquals(CommonStatus.DISABLED, bean.getStatus());

        status = CommonStatus.DELETED.name();
        bean = relationshipService.updateRelationStatus(userId, relativeUserId, relationType, status);
        Assert.assertNotNull(bean);
        Assert.assertEquals(relationId, bean.getId());
        Assert.assertEquals(userId, bean.getUserId());
        Assert.assertEquals(relativeUserId, bean.getRelativeUserId());
        Assert.assertEquals(RelationshipType.BLOCK_ALL_SPEAK, bean.getRelationType());
        Assert.assertEquals(CommonStatus.DELETED, bean.getStatus());
    }
}
