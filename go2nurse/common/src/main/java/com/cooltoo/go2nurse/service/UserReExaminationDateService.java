package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.UserDiagnosticPointRelationBean;
import com.cooltoo.go2nurse.beans.UserReExaminationDateBean;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.constants.ProcessStatus;
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
            new Sort.Order(Sort.Direction.DESC, "groupId"),
            new Sort.Order(Sort.Direction.ASC, "reExaminationDate")
            );

    @Autowired private UserReExaminationDateRepository repository;
    @Autowired private UserReExaminationDateBeanConverter beanConverter;

    @Autowired private UserRepository userRepository;
    @Autowired private UserDiagnosticPointRelationService userDiagnosticRelationService;

    //===============================================================
    //                           getting for user
    //===============================================================
    public List<UserReExaminationDateBean> getUserGroupReExamination(long userId, long groupId) {
        logger.info("get user={} group={} 's re-examination date",
                userId, groupId);
        List<CommonStatus> enabled = Arrays.asList(new CommonStatus[]{CommonStatus.ENABLED});
        List<UserReExaminationDateEntity> reExaminationDates = repository.findByUserIdAndGroupIdAndStatusIn(
                userId, groupId, enabled, sort);
        List<UserReExaminationDateBean> beans = entitiesToBeans(reExaminationDates);
        logger.info("count is {}", beans.size());
        return beans;
    }

    /** @return hospitalized group id to re-examination date*/
    public List<List<UserReExaminationDateBean>> getUserReExamination(long userId) {
        logger.info("get user={} 's re-examination date", userId);
        List<CommonStatus> enabled = Arrays.asList(new CommonStatus[]{CommonStatus.ENABLED});
        List<UserReExaminationDateEntity> reExaminationDates = repository.findByUserIdAndStatusIn(userId, enabled, sort);
        List<UserReExaminationDateBean> beans = entitiesToBeans(reExaminationDates);

        List<Long> groupIds = new ArrayList<>();
        List<List<UserReExaminationDateBean>> groupReExaminationDates = new ArrayList<>();
        for (UserReExaminationDateBean bean : beans) {
            if (!groupIds.contains(bean.getGroupId())) {
                groupIds.add(bean.getGroupId());
                groupReExaminationDates.add(new ArrayList<>());
            }
            int indexOfGroup = groupIds.indexOf(bean.getGroupId());
            List<UserReExaminationDateBean> tmpBeans = groupReExaminationDates.get(indexOfGroup);
            tmpBeans.add(bean);
        }

//        Map<Long, List<UserReExaminationDateBean>> returnValue = new HashMap<>();
//        for (int i=0; i<groupIds.size(); i++) {
//            Long groupId = groupIds.get(i);
//            List<UserReExaminationDateBean> tmpBeans = groupReExaminationDates.get(i);
//            returnValue.put(groupId, tmpBeans);
//        }

        logger.info("count is {}", groupReExaminationDates.size());
        return groupReExaminationDates;
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
    public UserReExaminationDateBean addReExamination(long userId, long groupId, long hospitalizedGroupId, Date reExaminationDate, YesNoEnum isStartDate, YesNoEnum hasOperation) {
        logger.info("user={} add re-examination date groupId={} hospitalizedGroupId={} reExaminationDate={}, isStartDate={}",
                userId, groupId, hospitalizedGroupId, reExaminationDate, isStartDate);
        if (!userRepository.exists(userId)) {
            logger.error("user not exists");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (hospitalizedGroupId>0) {
            List<Long> userDiagnosticGroupIds = userDiagnosticRelationService.getUserAllGroupIds(userId);
            if (!userDiagnosticGroupIds.contains(hospitalizedGroupId)) {
                logger.error("user hospitalized group id  not exists");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }
        if (null==reExaminationDate) {
            logger.error("user re-examination date is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        UserReExaminationDateEntity entity = new UserReExaminationDateEntity();
        entity.setUserId(userId);
        entity.setHasOperation(YesNoEnum.YES.equals(hasOperation) ? 1 : 0);
        entity.setIsStartDate(null==isStartDate ? YesNoEnum.NONE : isStartDate);
        entity.setGroupId(groupId);
        entity.setHospitalizedGroupId(hospitalizedGroupId);
        entity.setReExaminationDate(reExaminationDate);
        entity.setIgnore(CommonStatus.DISABLED);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = repository.save(entity);

        UserReExaminationDateBean bean = beanConverter.convert(entity);
        logger.info("user re-examination date is {}", bean);
        return bean;
    }

    @Transactional
    public List<UserReExaminationDateBean> addReExaminationByDiagnosticDates(
            long userId, List<UserDiagnosticPointRelationBean> userDiagnosticPointDates) {
        logger.info("user={} add re-examination date by user-diagnostic-point-relation={}",
                userId, userDiagnosticPointDates);

        if (VerifyUtil.isListEmpty(userDiagnosticPointDates)) {
            logger.error("there is no user diagnostic point relation dates");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (ProcessStatus.CANCELED.equals(userDiagnosticPointDates.get(0).getProcessStatus())) {
            logger.error("user's diagnostic point relation dates has been cancelled");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        // get operation or discharged_from_hospital date and hospitalized group id
        long hospitalizedGroupId = 0;
        Date startDate = null;
        YesNoEnum hasOperation = YesNoEnum.NO;
        for (UserDiagnosticPointRelationBean diagnostic : userDiagnosticPointDates) {
            if (0==hospitalizedGroupId) {
                hospitalizedGroupId = diagnostic.getGroupId();
            }
            if (DiagnosticEnumeration.OPERATION.equals(diagnostic.getDiagnostic())) {
                startDate = diagnostic.getDiagnosticTime();
                hasOperation = YesNoEnum.YES;
                break;
            }
            if (DiagnosticEnumeration.DISCHARGED_FROM_THE_HOSPITAL.equals(diagnostic.getDiagnostic())) {
                startDate = diagnostic.getDiagnosticTime();
            }
        }
        if (null==startDate) {
            logger.error("there is no operation-date or discharged-from-hospital-date");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        List<UserReExaminationDateBean> reExaminationDateBeans =
                addReExaminationByStartDate(userId, hospitalizedGroupId, startDate, hasOperation);
        return reExaminationDateBeans;
    }

    @Transactional
    public List<UserReExaminationDateBean> addReExaminationByStartDate(
            long userId, long hospitalizedGroupId, Date startDate, YesNoEnum hasOperation) {
        logger.info("user={} add re-examination date by hospitalizedGroupId={} startDate={} hasOperation={}",
                userId, hospitalizedGroupId, startDate, hasOperation);
        if (null==hasOperation) {
            logger.error("there is no has operation flag");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (null==startDate) {
            logger.error("there is no start date");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        // get hospitalized group id
        hospitalizedGroupId = hospitalizedGroupId<0 ? 0 : hospitalizedGroupId;

        // get re_examination dates and group id
        long reExamDateGroupId = System.currentTimeMillis();
        List<Date> reExamDates = getReExaminationDate(startDate, YesNoEnum.YES.equals(hasOperation));

        // add re_examination dates
        List<UserReExaminationDateBean> reExaminationDateBeans = new ArrayList<>();
        UserReExaminationDateBean bean;
        bean = addReExamination(userId, reExamDateGroupId, hospitalizedGroupId, startDate, YesNoEnum.YES, hasOperation);
        reExaminationDateBeans.add(bean);
        for (Date date : reExamDates) {
            bean = addReExamination(userId, reExamDateGroupId, hospitalizedGroupId, date, YesNoEnum.NO, hasOperation);
            reExaminationDateBeans.add(bean);
        }

        return reExaminationDateBeans;
    }


    private List<Date> getReExaminationDate(Date startDate, boolean hasOperation) {
        List<Date> reExamDate = new ArrayList<>();
        if (null==startDate) {
            return reExamDate;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        calendar.add(Calendar.MONTH, 3);
        if (hasOperation) {
            reExamDate.add(calendar.getTime());
        }

        calendar.add(Calendar.MONTH, 3);
        reExamDate.add(calendar.getTime());

        calendar.add(Calendar.MONTH, 6);
        reExamDate.add(calendar.getTime());

        return reExamDate;
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

    @Transactional
    public List<UserReExaminationDateBean> updateReExaminationStartDate(long userId, long groupId, Date reExaminationStartDate) {
        logger.info("user={} update re-examination date by groupId={} reExaminationStartDate={}",
                userId, groupId, reExaminationStartDate);
        List<UserReExaminationDateBean> beans = new ArrayList<>();
        if (null==reExaminationStartDate) {
            logger.warn("re-examination start date is empty");
            return beans;
        }

        List<CommonStatus> statuses = CommonStatus.getAll();
        List<UserReExaminationDateEntity> entities = repository.findByUserIdAndGroupIdAndStatusIn(userId, groupId, statuses, sort);
        if (VerifyUtil.isListEmpty(entities)) {
            logger.warn("there is no re-examination date to update");
            return beans;
        }

        long deltaWithStart = 0;
        for (UserReExaminationDateEntity tmp : entities) {
            if (YesNoEnum.YES.equals(tmp.getIsStartDate())) {
                deltaWithStart = reExaminationStartDate.getTime() - tmp.getReExaminationDate().getTime();
                break;
            }
        }
        if (deltaWithStart==0) {
            logger.warn("re-examination start date is the same");
            return beans;
        }

        for (UserReExaminationDateEntity tmp : entities) {
            Date reExamDate = tmp.getReExaminationDate();
            tmp.setReExaminationDate(new Date(reExamDate.getTime() + deltaWithStart));
        }

        entities = repository.save(entities);
        beans = entitiesToBeans(entities);
        return beans;
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
        List<UserReExaminationDateEntity> entities = repository.findByUserIdAndGroupIdAndStatusIn(
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
