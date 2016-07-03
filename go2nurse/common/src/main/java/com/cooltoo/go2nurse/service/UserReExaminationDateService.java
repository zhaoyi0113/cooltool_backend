package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.UserReExaminationDateBean;
import com.cooltoo.go2nurse.converter.UserReExaminationDateBeanConverter;
import com.cooltoo.go2nurse.entities.UserReExaminationDateEntity;
import com.cooltoo.go2nurse.repository.UserReExaminationDateRepository;
import com.cooltoo.go2nurse.repository.UserRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by hp on 2016/7/3.
 */
@Service("UserReExaminationDateService")
public class UserReExaminationDateService {

    private static final Logger logger = LoggerFactory.getLogger(UserReExaminationDateService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "userId"),
            new Sort.Order(Sort.Direction.DESC, "hospitalizedGroupId"),
            new Sort.Order(Sort.Direction.ASC, "reExaminationDate")
            );

    @Autowired private UserReExaminationDateRepository repository;
    @Autowired private UserReExaminationDateBeanConverter beanConverter;

    @Autowired private UserRepository userRepository;
    @Autowired private UserDiagnosticPointRelationService userDiagnosticRelationService;

    //===============================================================
    //                           getting for user
    //===============================================================
    public List<UserReExaminationDateBean> getUserHospitalizedGroupReExamination(long userId, long hospitalizedGroupId) {
        logger.info("get user={} hospitalized_group={} 's re-examination date",
                userId, hospitalizedGroupId);
        List<CommonStatus> enabled = Arrays.asList(new CommonStatus[]{CommonStatus.ENABLED});
        List<UserReExaminationDateEntity> reExaminationDates = repository.findByUserIdAndHospitalizedGroupIdAndStatusIn(
                userId, hospitalizedGroupId, enabled, sort);
        List<UserReExaminationDateBean> beans = entitiesToBeans(reExaminationDates);
        logger.info("count is {}", beans.size());
        return beans;
    }

    /** @return hospitalized group id to re-examination date*/
    public Map<Long, List<UserReExaminationDateBean>> getUserReExamination(long userId) {
        logger.info("get user={} 's re-examination date", userId);
        List<CommonStatus> enabled = Arrays.asList(new CommonStatus[]{CommonStatus.ENABLED});
        List<UserReExaminationDateEntity> reExaminationDates = repository.findByUserIdAndStatusIn(userId, enabled, sort);
        List<UserReExaminationDateBean> beans = entitiesToBeans(reExaminationDates);

        List<Long> groupIds = new ArrayList<>();
        List<List<UserReExaminationDateBean>> groupReExaminationDates = new ArrayList<>();
        for (UserReExaminationDateBean bean : beans) {
            if (!groupIds.contains(bean.getHospitalizedGroupId())) {
                groupIds.add(bean.getHospitalizedGroupId());
                groupReExaminationDates.add(new ArrayList<>());
            }
            int indexOfGroup = groupIds.indexOf(bean.getHospitalizedGroupId());
            List<UserReExaminationDateBean> tmpBeans = groupReExaminationDates.get(indexOfGroup);
            tmpBeans.add(bean);
        }

        Map<Long, List<UserReExaminationDateBean>> returnValue = new HashMap<>();
        for (int i=0; i<groupIds.size(); i++) {
            Long groupId = groupIds.get(i);
            List<UserReExaminationDateBean> tmpBeans = groupReExaminationDates.get(i);
            returnValue.put(groupId, tmpBeans);
        }

        logger.info("count is {}", returnValue.size());
        return returnValue;
    }

    //===============================================================
    //                           getting for admin user
    //===============================================================

    private List<UserReExaminationDateBean> entitiesToBeans(Iterable<UserReExaminationDateEntity> entities) {
        List<UserReExaminationDateBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        for (UserReExaminationDateEntity entity : entities) {
            UserReExaminationDateBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    //===============================================================
    //                           add
    //===============================================================
    @Transactional
    public UserReExaminationDateBean addReExamination(long userId, long groupId, Date reExaminationDate) {
        logger.info("add user={} re-examination date hospitalizedGroupId={} reExaminationDate={}",
                userId, groupId, reExaminationDate);
        if (!userRepository.exists(userId)) {
            logger.error("user not exists");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<Long> userDiagnosticGroupIds = userDiagnosticRelationService.getUserAllGroupIds(userId);
        if (!userDiagnosticGroupIds.contains(groupId)) {
            logger.error("user hospitalized group id  not exists");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (null==reExaminationDate) {
            logger.error("user re-examination date is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        UserReExaminationDateEntity entity = new UserReExaminationDateEntity();
        entity.setUserId(userId);
        entity.setHospitalizedGroupId(groupId);
        entity.setReExaminationDate(reExaminationDate);
        entity.setIgnore(CommonStatus.DISABLED);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());

        UserReExaminationDateBean bean = beanConverter.convert(entity);
        logger.info("user re-examination date is {}", bean);
        return bean;
    }

    //===============================================================
    //                           update
    //===============================================================
    @Transactional
    public UserReExaminationDateBean updateReExamination(long reExaminationId, Date reExaminationDate, CommonStatus ignore, CommonStatus status) {
        logger.info("update re-examinationId={} with reExaminationDate={} and status={}",
                reExaminationId, reExaminationDate, status);
        UserReExaminationDateEntity entity = repository.findOne(reExaminationId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        logger.info("update the re-examination record={}", entity);

        boolean changed = false;
        if (null!=reExaminationDate) {
            entity.setReExaminationDate(reExaminationDate);
            changed = true;
        }
        if (null!=status) {
            entity.setStatus(status);
            changed = true;
        }
        if (null!=ignore) {
            entity.setIgnore(ignore);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }

        UserReExaminationDateBean bean = beanConverter.convert(entity);
        logger.info("user re-examination date is {}", bean);
        return bean;
    }

    //===============================================================
    //                           delete
    //===============================================================
    @Transactional
    public UserReExaminationDateBean deleteByReExaminationId(long reExaminationId) {
        logger.info("delete by user re-examination date id={}", reExaminationId);
        UserReExaminationDateEntity entity = repository.findOne(reExaminationId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        UserReExaminationDateBean bean = beanConverter.convert(entity);
        repository.delete(entity);
        logger.info("delete re-examination date is {}", bean);
        return bean;
    }

    @Transactional
    public List<UserReExaminationDateBean> deleteByUserAndHospitalizedGroup(long userId, long hospitalizedGroupId) {
        logger.info("delete by user re-examination date by userId={} hospitalizedGroupId={]", userId, hospitalizedGroupId);
        List<CommonStatus> statuses = CommonStatus.getAll();
        List<UserReExaminationDateEntity> entities = repository.findByUserIdAndHospitalizedGroupIdAndStatusIn(
                userId, hospitalizedGroupId, statuses, sort);
        if (VerifyUtil.isListEmpty(entities)) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        List<UserReExaminationDateBean> beans = new ArrayList<>();
        for (UserReExaminationDateEntity entity : entities) {
            UserReExaminationDateBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        repository.delete(entities);
        logger.info("delete re-examination date is {}", beans);
        return beans;
    }
}
