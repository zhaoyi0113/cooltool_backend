package com.cooltoo.services;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by hp on 2016/8/10.
 */
@Service("CommonNurseService")
public class CommonNurseService {

    private static final Logger logger = LoggerFactory.getLogger(CommonNurseService.class);

    private static final Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));

    @Autowired private NurseRepository nurseRepository;

    //========================================================================
    //                        getting
    //========================================================================
    public boolean existNurse(long nurseId) {
        boolean existed = nurseRepository.exists(nurseId);
        logger.info("nurse is existed={}", existed);
        return existed;
    }

    public List<NurseEntity> getNurseByMobile(String mobile) {
        logger.info("get nurse by mobile={}", mobile);
        List<NurseEntity> nurses = nurseRepository.findByMobile(mobile);
        if (VerifyUtil.isListEmpty(nurses)) {
            return new ArrayList<>();
        }
        logger.info("count is ={}", nurses.size());
        return nurses;
    }

    public NurseEntity getNurseById(long nurseId) {
        logger.info("get nurse by nurseId={}", nurseId);
        NurseEntity entity = nurseRepository.findOne(nurseId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        return entity;
    }

    public List<NurseEntity> getNurseByIds(List<Long> userIds) {
        logger.info("get nurse by ids={}", userIds);
        if (VerifyUtil.isListEmpty(userIds)) {
            return new ArrayList<>();
        }
        List<NurseEntity> entities = nurseRepository.findByIdIn(userIds);
        if (VerifyUtil.isListEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities;
    }

    public List<Long> getNurseIdsByName(String fuzzyQueryName, int pageIndex, int sizePerPage) {
        logger.info("get nurseIds by fuzzily name={}", fuzzyQueryName);
        if (VerifyUtil.isStringEmpty(fuzzyQueryName)) {
            logger.info("the name for fuzzily querying is empty");
            return new ArrayList<>();
        }
        fuzzyQueryName = VerifyUtil.reconstructSQLContentLike(fuzzyQueryName);
        PageRequest pageReq = new PageRequest(pageIndex, sizePerPage);
        List<Long> nurseIds = nurseRepository.findIdsByFuzzyName(fuzzyQueryName, pageReq);
        if (VerifyUtil.isListEmpty(nurseIds)) {
            logger.info("find result set is empty");
            return new ArrayList<>();
        }
        logger.info("find result set size is {}", nurseIds.size());
        return nurseIds;
    }

    public List<Long> getNurseIdsByAuthority(UserAuthority authority) {
        logger.info("get nurseIds by authority={}", authority);
        List<Long> nurseIds = nurseRepository.findIdsByAuthority(authority);
        logger.info("count is {}", nurseIds.size());
        return nurseIds;
    }

    //==============================================================
    //             get used by administrator
    //==============================================================
    public long countByAuthorityAndFuzzyName(UserAuthority authority, String fuzzyName, YesNoEnum canAnswerNursingQuestion) {
        logger.info("get nurse count by authority={} fuzzyName={} canAnswerNursingQuestion",
                authority, fuzzyName, canAnswerNursingQuestion);

        if (VerifyUtil.isStringEmpty(fuzzyName)) {
            fuzzyName = null;
        }
        else {
            fuzzyName = VerifyUtil.reconstructSQLContentLike(fuzzyName);
        }
        long count;
        if (null==authority && null==fuzzyName && null==canAnswerNursingQuestion) {
            count = nurseRepository.count();
        }
        else {
            count = nurseRepository.countByAuthority(authority, fuzzyName, canAnswerNursingQuestion);
        }
        logger.info("count is {}", count);
        return count;
    }

    public Iterable<NurseEntity> getByAuthorityAndFuzzyName(UserAuthority authority, String fuzzyName, YesNoEnum canAnswerNursingQuestion, int pageIndex, int number) {
        logger.info("get nurse by authority={} fuzzyName={} at page {} with number {}", authority, fuzzyName, pageIndex, number);
        PageRequest page = new PageRequest(pageIndex, number, sort);
        Page<NurseEntity> resultSet = null;

        if (VerifyUtil.isStringEmpty(fuzzyName)) {
            fuzzyName = null;
        }
        else {
            fuzzyName = VerifyUtil.reconstructSQLContentLike(fuzzyName);
        }
        if (null==authority && null==fuzzyName && null==canAnswerNursingQuestion) {
            resultSet = nurseRepository.findAll(page);
        }
        else {
            resultSet = nurseRepository.findByAuthority(authority, fuzzyName, canAnswerNursingQuestion, page);
        }
        return resultSet;
    }

    //========================================================================
    //                        adding
    //========================================================================
    @Transactional
    public NurseEntity registerNurse(String name, int age, GenderType gender, String mobile, String password, String identification, String realName, String shortNote) {
        gender = null==gender ? GenderType.SECRET : gender;

        NurseEntity entity = new NurseEntity();
        entity.setName(name);
        entity.setAge(age);
        entity.setGender(gender);
        entity.setMobile(mobile);
        entity.setPassword(password);
        entity.setRealName(realName);
        entity.setIdentification(identification);
        entity.setShortNote(shortNote);
        entity.setAuthority(UserAuthority.AGREE_ALL);
        logger.info("add new nurse={}", entity);
        if (VerifyUtil.isStringEmpty(mobile)){
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        mobile = mobile.trim();
        if(!nurseRepository.findByMobile(mobile).isEmpty()){
            throw new BadRequestException(ErrorCode.NURSE_ALREADY_EXISTED);
        }
        entity = nurseRepository.save(entity);
        return entity;
    }


    //========================================================================
    //                        updating
    //========================================================================
    @Transactional
    public NurseEntity updateBasicInfo(long nurseId, String name, int age, GenderType gender, String mobile, String password, String identification, String realName, String shortNote, UserAuthority authority) {
        logger.info("update nurse basic info by id={} name={} age={} gender={} mobile={} password={} identification={} realName={} shortNote={}, authority={}",
                nurseId, name, age, gender, mobile, password, identification, realName, shortNote, authority);
        NurseEntity nurse = nurseRepository.findOne(nurseId);
        if (null==nurse) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        boolean change = false;
        if (!VerifyUtil.isStringEmpty(name) && !name.trim().equals(nurse.getName())) {
            nurse.setName(name.trim());
            change = true;
        }
        if (!VerifyUtil.isStringEmpty(mobile) && !mobile.trim().equals(nurse.getMobile())) {
            nurse.setMobile(mobile.trim());
            change = true;
        }
        if (!VerifyUtil.isStringEmpty(password) && !password.trim().equals(nurse.getPassword())) {
            nurse.setPassword(password.trim());
            change = true;
        }
        if (!VerifyUtil.isStringEmpty(identification) && !identification.trim().equals(nurse.getIdentification())) {
            nurse.setIdentification(identification.trim());
            change = true;
        }
        if (!VerifyUtil.isStringEmpty(realName) && !realName.trim().equals(nurse.getRealName())) {
            nurse.setRealName(realName.trim());
            change = true;
        }
        if (!VerifyUtil.isStringEmpty(shortNote) && !shortNote.trim().equals(nurse.getShortNote())) {
            nurse.setShortNote(shortNote.trim());
            change = true;
        }
        if (null!=authority && !authority.equals(nurse.getAuthority())) {
            nurse.setAuthority(authority);
            change = true;
        }
        if (null!=gender && !gender.equals(nurse.getGender())) {
            nurse.setGender(gender);
            change = true;
        }
        if (age>=0 && age!=nurse.getAge()) {
            nurse.setAge(age);
            change = true;
        }
        if (change) {
            nurse = nurseRepository.save(nurse);
        }
        return nurse;
    }
}
