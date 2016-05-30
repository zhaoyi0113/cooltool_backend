package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseRelationshipBean;
import com.cooltoo.backend.converter.NurseRelationshipBeanConverter;
import com.cooltoo.backend.entities.NurseRelationshipEntity;
import com.cooltoo.backend.repository.NurseRelationshipRepository;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.RelationshipType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2016/5/30.
 */
@Service("NurseRelationshipService")
public class NurseRelationshipService {

    private static final Logger logger = LoggerFactory.getLogger(NurseRelationshipService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "relationType"),
            new Sort.Order(Sort.Direction.ASC, "relativeUserId")
    );

    @Autowired private NurseRelationshipRepository repository;
    @Autowired private NurseRelationshipBeanConverter beanConverter;
    @Autowired private NurseService nurseService;

    //================================================================
    //                       get --- admin use
    //================================================================
    public long countCondition(long userId, long relativeUserId, String strRelationType, String strStatus) {
        logger.info("count by userId={} relativeUserId={} relationType={} status={}",
                userId, relativeUserId, strRelationType, strStatus);
        RelationshipType relationType = RelationshipType.parseString(strRelationType);
        CommonStatus status = CommonStatus.parseString(strStatus);
        long count = repository.countByConditions(userId, relativeUserId, relationType, status);
        logger.info("count is {}", count);
        return count;
    }

    public List<NurseRelationshipBean> getRelation(long userId, long relativeUserId,
                                                   String strRelationType, String strStatus,
                                                   int pageIndex, int sizePerPage
    ) {
        logger.info("get relation by userId={} relativeUserId={} relationType={} status={}, at page={} {}/page",
                userId, relativeUserId, strRelationType, strStatus, pageIndex, sizePerPage);
        RelationshipType relationType = RelationshipType.parseString(strRelationType);
        CommonStatus status = CommonStatus.parseString(strStatus);
        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<NurseRelationshipEntity> resultSet = repository.findByConditions(userId, relativeUserId, relationType, status, page);
        return null;
    }

    //================================================================
    //               get -- user use
    //================================================================
//    public

    //================================================================
    //                       add
    //================================================================
    public NurseRelationshipBean addRelation(long userId, long relativeUserId, String strRelation) {
        logger.info("user {} add relationship {} to user {}", userId, strRelation, relativeUserId);
        RelationshipType relationType = RelationshipType.parseString(strRelation);
        if (null==relationType) {
            logger.error("relation type is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (!nurseService.existNurse(userId)) {
            logger.error("user not exist");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (!nurseService.existNurse(relativeUserId)) {
            logger.error("relative user not exist");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (userId==relativeUserId) {
            logger.error("user can not set relation to self");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        NurseRelationshipEntity relationship = repository.findByUserIdAndRelativeUserIdAndRelationType(
                userId, relativeUserId, relationType
        );
        if (null!=relationship) {
            logger.info("the relationship already exist, {}", relationship);
            relationship.setStatus(CommonStatus.ENABLED);
        }
        else {
            relationship = new NurseRelationshipEntity();
            relationship.setUserId(userId);
            relationship.setRelativeUserId(relativeUserId);
            relationship.setRelationType(relationType);
            relationship.setTime(new Date());
            relationship.setStatus(CommonStatus.ENABLED);
        }
        repository.save(relationship);

        return beanConverter.convert(relationship);
    }

    //================================================================
    //                       update
    //================================================================
    public NurseRelationshipBean updateRelation(long userId, long relativeUserId, RelationshipType relationType, CommonStatus status) {
        logger.info("user {} update relationship {} to user {} to status={}", userId, relationType, relativeUserId, status);
        if (relationType==null) {
            logger.error("relationship type is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        NurseRelationshipEntity relationship = repository.findByUserIdAndRelativeUserIdAndRelationType(
                userId, relativeUserId, relationType
        );
        if (null!=relationship) {
            boolean changed = false;
            if (null!=status && !status.equals(relationship.getStatus())) {
                relationship.setStatus(status);
                changed = true;
            }
            if (changed) {
                repository.save(relationship);
            }
            return beanConverter.convert(relationship);
        }
        return null;
    }

    public NurseRelationshipBean updateRelation(long userId, long relativeUserId, String strRelation, String strStatus) {
        RelationshipType relationType = RelationshipType.parseString(strRelation);
        CommonStatus status = CommonStatus.parseString(strStatus);
        return updateRelation(userId, relativeUserId, relationType, status);
    }
}
