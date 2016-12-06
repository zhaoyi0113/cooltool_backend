package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.UserCurrentVisitBean;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.converter.UserCurrentVisitBeanConverter;
import com.cooltoo.go2nurse.entities.UserCurrentVisitEntity;
import com.cooltoo.go2nurse.repository.UserCurrentVisitRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Created by zhaolisong on 2016/12/6.
 */
@Service("UserCurrentVisitService")
public class UserCurrentVisitService {

    private static final Logger logger = LoggerFactory.getLogger(UserCurrentVisitService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private UserCurrentVisitRepository repository;
    @Autowired private UserCurrentVisitBeanConverter beanConverter;
    @Autowired private UserService userService;



    //=================================================================
    //                 getter for nurse/patient
    //=================================================================
    public boolean existsCurrentVisit(long userId) {
        return repository.countByUserId(userId)>0;
    }

    public UserCurrentVisitBean getCurrentVisit(long userId) {
        logger.info("get user={} current visit", userId);
        List<UserCurrentVisitEntity> entities = repository.findByUserId(userId, sort);
        if (VerifyUtil.isListEmpty(entities)) {
            logger.error("There is no current visit");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        UserCurrentVisitEntity entity = entities.get(0);
        UserCurrentVisitBean bean = beanConverter.convert(entity);
        logger.warn("current visit =", bean);
        return bean;
    }


    //===============================================================
    //                 setter
    //===============================================================
    @Transactional
    public UserCurrentVisitBean setUserCurrentVisit(long userId, DiagnosticEnumeration diagnosticPoint) {
        logger.info("add user={} current visit of diagnostic_point={}", userId, diagnosticPoint);
        if (!userService.existUser(userId)) {
            logger.error("user not exist");
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }

        UserCurrentVisitEntity entity = null;
        List<UserCurrentVisitEntity> existed = repository.findByUserId(userId, sort);
        if (VerifyUtil.isListEmpty(existed)) {
            entity = new UserCurrentVisitEntity();
            entity.setUserId(userId);
            entity.setTime(new Date());
            entity.setStatus(CommonStatus.ENABLED);
        }
        else {
            entity = existed.get(0);
        }

        boolean changed = false;
        if (null!=diagnosticPoint) {
            entity.setDiagnosticPoint(diagnosticPoint.ordinal());
            changed = true;
        }
        if (changed) {
            entity = repository.save(entity);
        }

        // delete redundant record
        existed = repository.findByUserId(userId, sort);
        if (!VerifyUtil.isListEmpty(existed)) {
            for (int i=0; i < existed.size(); i ++) {
                UserCurrentVisitEntity tmp = existed.get(i);
                if (null==tmp) { continue; }
                if (tmp.getId() == entity.getId()) {
                    existed.remove(i);
                    break;
                }
            }
            if (!VerifyUtil.isListEmpty(existed)) {
                repository.delete(existed);
            }
        }

        UserCurrentVisitBean bean = beanConverter.convert(entity);
        logger.info("add relations is {}", bean);
        return bean;
    }
}
