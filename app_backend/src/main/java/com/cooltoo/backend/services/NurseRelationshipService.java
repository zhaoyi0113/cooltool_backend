package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.beans.NurseRelationshipBean;
import com.cooltoo.backend.converter.NurseRelationshipBeanConverter;
import com.cooltoo.backend.entities.NurseRelationshipEntity;
import com.cooltoo.backend.repository.NurseRelationshipRepository;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.RelationshipType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by hp on 2016/5/30.
 */
@Service("NurseRelationshipService")
public class NurseRelationshipService {

    private static final Logger logger = LoggerFactory.getLogger(NurseRelationshipService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "userId"),
            new Sort.Order(Sort.Direction.ASC, "relationType"),
            new Sort.Order(Sort.Direction.ASC, "relativeUserId"),
            new Sort.Order(Sort.Direction.ASC, "time")
    );

    private static final Map<Long, List<Long>> userId2BlockSpeakUserIds = new HashMap<Long, List<Long>>();

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
        List<NurseRelationshipBean> beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    //================================================================
    //               get -- user use
    //================================================================
    public List<Long> getUserBlockSpeakUserIds(long userId) {
        List<Long> blockSpeakUserIds = userId2BlockSpeakUserIds.get(userId);
        if (null==blockSpeakUserIds) {
            List<Long> blockUserIds = repository.findRelativeUserIdByCondition(userId, RelationshipType.BLOCK_ALL_SPEAK, CommonStatus.ENABLED);
            if (!VerifyUtil.isListEmpty(blockUserIds)) {
                blockSpeakUserIds = blockUserIds;
            }
            else {
                blockSpeakUserIds = new ArrayList<>();
            }
            userId2BlockSpeakUserIds.put(userId, blockSpeakUserIds);
        }
        List<Long> tmp = new ArrayList<>();
        for (Long blockSpeakUserId : blockSpeakUserIds) {
            tmp.add(blockSpeakUserId);
        }
        return tmp;
    }

    public List<Long> getRelativeUserId(long userId, String strRelationType, String strStatus) {
        logger.info("user {} get relative user ids by relationType={} status={}", userId, strRelationType, strStatus);
        RelationshipType relationType = RelationshipType.parseString(strRelationType);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Long> relativeUserIds = repository.findRelativeUserIdByCondition(userId, relationType, status);
        if (null==relativeUserIds) {
            relativeUserIds = new ArrayList<>();
        }
        logger.info("count is {}", relativeUserIds.size());
        return relativeUserIds;
    }

    public List<NurseRelationshipBean> getRelation(boolean withInfo, long userId, long relativeUserId, String strRelationType, String strStatus) {
        logger.info("get relation by userId={} relativeUserId={} relationType={} status={}, withOtherInfo={}",
                userId, relativeUserId, strRelationType, strStatus, withInfo, withInfo);
        RelationshipType relationType = RelationshipType.parseString(strRelationType);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<NurseRelationshipEntity> resultSet = repository.findByConditions(userId, relativeUserId, relationType, status, sort);
        List<NurseRelationshipBean> beans = entitiesToBeans(resultSet);
        if (withInfo) {
            fillOtherProperties(beans);
        }
        logger.info("count is {}", beans.size());
        return beans;
    }

    private List<NurseRelationshipBean> entitiesToBeans(Iterable<NurseRelationshipEntity> entities) {
        List<NurseRelationshipBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        NurseRelationshipBean bean;
        for (NurseRelationshipEntity entity : entities) {
            bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<NurseRelationshipBean> beans) {
        List<Long> userIds = new ArrayList<>();
        for (NurseRelationshipBean relation : beans) {
            long userId = relation.getUserId();
            if (!userIds.contains(userId)) {
                userIds.add(userId);
            }
            userId = relation.getRelativeUserId();
            if (!userIds.contains(userId)) {
                userIds.add(userId);
            }
        }

        List<NurseBean> nurses = nurseService.getNurseWithoutOtherInfo(userIds);
        Map<Long, NurseBean> userId2Bean = new HashMap<>();
        for (NurseBean nurse : nurses) {
            userId2Bean.put(nurse.getId(), nurse);
        }

        NurseBean nurse;
        for (NurseRelationshipBean relation : beans) {
            nurse = userId2Bean.get(relation.getUserId());
            relation.setUser(nurse);
            nurse = userId2Bean.get(relation.getRelativeUserId());
            relation.setRelativeUser(nurse);
        }
    }

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

        modifyBlockSpeakUserIds(relationship);

        return beanConverter.convert(relationship);
    }

    //================================================================
    //                       update
    //================================================================
    public  NurseRelationshipBean updateRelationStatus(long relationId, String strStatus) {
        logger.info("update status by relationId={} to status={}", relationId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null==status) {
            logger.warn("status is invalid");
            return null;
        }
        if (!repository.exists(relationId)) {
            logger.warn("record not exist");
            return null;
        }
        NurseRelationshipEntity relationship = repository.findOne(relationId);
        boolean changed = false;
        if (!status.equals(relationship.getStatus())) {
            relationship.setStatus(status);
            changed = true;
        }

        modifyBlockSpeakUserIds(relationship);

        if (changed) {
            repository.save(relationship);
        }
        return beanConverter.convert(relationship);
    }

    public NurseRelationshipBean updateRelationStatus(long userId, long relativeUserId, RelationshipType relationType, CommonStatus status) {
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

            modifyBlockSpeakUserIds(relationship);

            return beanConverter.convert(relationship);
        }
        return null;
    }

    public NurseRelationshipBean updateRelationStatus(long userId, long relativeUserId, String strRelation, String strStatus) {
        RelationshipType relationType = RelationshipType.parseString(strRelation);
        CommonStatus status = CommonStatus.parseString(strStatus);
        return updateRelationStatus(userId, relativeUserId, relationType, status);
    }

    private void modifyBlockSpeakUserIds(NurseRelationshipEntity relationship) {
        if (null==relationship) {
            return;
        }

        long userId = relationship.getUserId();
        long relativeUserId = relationship.getRelativeUserId();
        RelationshipType relationType = relationship.getRelationType();
        CommonStatus status = relationship.getStatus();

        if (RelationshipType.BLOCK_ALL_SPEAK.equals(relationType)) {
            List<Long> blockSpeakUserIds = userId2BlockSpeakUserIds.get(userId);
            if (null==blockSpeakUserIds) {
                blockSpeakUserIds = new ArrayList<>();
                userId2BlockSpeakUserIds.put(userId, blockSpeakUserIds);
            }
            if (CommonStatus.ENABLED.equals(status)) {
                if (!blockSpeakUserIds.contains(relativeUserId)) {
                    blockSpeakUserIds.add(relativeUserId);
                }
            }
            else {
                if (blockSpeakUserIds.contains(relativeUserId)) {
                    blockSpeakUserIds.remove(relativeUserId);
                }
            }
        }
    }
}
