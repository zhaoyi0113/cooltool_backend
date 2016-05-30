package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.services.NurseRelationshipService;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.RelationshipType;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
}
