package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.UserReExaminationBean;
import com.cooltoo.go2nurse.converter.UserReExaminationBeanConverter;
import com.cooltoo.go2nurse.entities.UserReExaminationEntity;
import com.cooltoo.go2nurse.repository.UserReExaminationRepository;
import com.cooltoo.go2nurse.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2016/7/3.
 */
@Service("UserReExaminationService")
public class UserReExaminationService {

    private static final Logger logger = LoggerFactory.getLogger(UserReExaminationService.class);

    @Autowired private UserReExaminationRepository repository;
    @Autowired private UserReExaminationBeanConverter beanConverter;

    @Autowired private UserRepository userRepository;
    @Autowired private UserDiagnosticPointRelationService userDiagnosticRelationService;

    //===============================================================
    //                           getting
    //===============================================================
//    public List<UserReExaminationBean> getUserReExamination(long userId) {
//
//    }

    //===============================================================
    //                           add
    //===============================================================
    @Transactional
    public UserReExaminationBean addReExamination(long userId, long groupId, Date reExaminationDate) {
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

        UserReExaminationEntity entity = new UserReExaminationEntity();
        entity.setUserId(userId);
        entity.setHospitalizedGroupId(groupId);
        entity.setReExaminationDate(reExaminationDate);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());

        UserReExaminationBean bean = beanConverter.convert(entity);
        logger.info("user re-examination date is {}", bean);
        return bean;
    }

    //===============================================================
    //                           update
    //===============================================================
    @Transactional
    public UserReExaminationBean updateReExamination(long reExaminationId, Date reExaminationDate, CommonStatus status) {
        logger.info("update re-examinationId={} with reExaminationDate={} and status={}",
                reExaminationId, reExaminationDate, status);
        UserReExaminationEntity entity = repository.findOne(reExaminationId);
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

        if (changed) {
            entity = repository.save(entity);
        }

        UserReExaminationBean bean = beanConverter.convert(entity);
        logger.info("user re-examination date is {}", bean);
        return bean;
    }

    //===============================================================
    //                           delete
    //===============================================================

}
