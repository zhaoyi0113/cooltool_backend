package com.cooltoo.services;

import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.RegisterFrom;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.HospitalDepartmentRepository;
import com.cooltoo.repository.HospitalRepository;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by hp on 2016/8/10.
 */
@Service("CommonNurseService")
public class CommonNurseService {

    private static final Logger logger = LoggerFactory.getLogger(CommonNurseService.class);

    private static final Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));

    @Autowired private NurseRepository nurseRepository;
    @Autowired private HospitalRepository hospitalRepository;
    @Autowired private HospitalDepartmentRepository departmentRepository;

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

    public NurseEntity getNurseByMobileAndPassword(String mobile, String password) {
        logger.info("get nurse by mobile={} and password", mobile);
        if (VerifyUtil.isStringEmpty(mobile) || VerifyUtil.isStringEmpty(password) || !VerifyUtil.isIds(mobile)) {
            return null;
        }
        NurseEntity nurses = nurseRepository.findByMobileAndPassword(mobile, password);
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
    public long countByAuthorityAndFuzzyName(UserAuthority authority, String fuzzyName, YesNoEnum canAnswerNursingQuestion, YesNoEnum canSeeAllOrder, Integer hospitalId, Integer departmentId, RegisterFrom registerFrom) {
        logger.info("get nurse count by authority={} fuzzyName={} canAnswerNursingQuestion={} hospital={} department={}",
                authority, fuzzyName, canAnswerNursingQuestion, hospitalId, departmentId);

        if (VerifyUtil.isStringEmpty(fuzzyName)) {
            fuzzyName = null;
        }
        else {
            fuzzyName = VerifyUtil.reconstructSQLContentLike(fuzzyName);
        }
        long count;
        if (null==authority && null==fuzzyName && null==canAnswerNursingQuestion && null==hospitalId && null==departmentId && null==registerFrom && null==canSeeAllOrder) {
            count = nurseRepository.count();
        }
        else {
            count = nurseRepository.countByAuthority(authority, fuzzyName, canAnswerNursingQuestion, hospitalId, departmentId, registerFrom, canSeeAllOrder);
        }
        logger.info("count is {}", count);
        return count;
    }

    public Iterable<NurseEntity> getNurseByAuthorityAndFuzzyName(UserAuthority authority, String fuzzyName, YesNoEnum canAnswerNursingQuestion, YesNoEnum canSeeAllOrder, Integer hospitalId, Integer departmentId, RegisterFrom registerFrom, int pageIndex, int number) {
        logger.info("get nurse by authority={} fuzzyName={} canAnswerNursingQuestion={} hospital={} department={} at page {} with number {}",
                authority, fuzzyName, canAnswerNursingQuestion, hospitalId, departmentId, pageIndex, number);
        PageRequest page = new PageRequest(pageIndex, number, sort);
        Page<NurseEntity> resultSet = null;

        if (VerifyUtil.isStringEmpty(fuzzyName)) {
            fuzzyName = null;
        }
        else {
            fuzzyName = VerifyUtil.reconstructSQLContentLike(fuzzyName);
        }
        if (null==authority && null==fuzzyName && null==canAnswerNursingQuestion && null==hospitalId && null==departmentId && null==registerFrom && null==canSeeAllOrder) {
            resultSet = nurseRepository.findAll(page);
        }
        else {
            resultSet = nurseRepository.findByAuthority(authority, fuzzyName, canAnswerNursingQuestion, hospitalId, departmentId, registerFrom, canSeeAllOrder, page);
        }
        return resultSet;
    }

    public Iterable<NurseEntity> getNurseByAuthorityAndFuzzyName(UserAuthority authority, String fuzzyName, YesNoEnum canAnswerNursingQuestion, YesNoEnum canSeeAllOrder, Integer hospitalId, Integer departmentId, RegisterFrom registerFrom) {
        logger.info("get nurse by authority={} fuzzyName={} canAnswerNursingQuestion={} hospital={} department={}",
                authority, fuzzyName, canAnswerNursingQuestion, hospitalId, departmentId);
        List<NurseEntity> resultSet = null;

        if (VerifyUtil.isStringEmpty(fuzzyName)) {
            fuzzyName = null;
        }
        else {
            fuzzyName = VerifyUtil.reconstructSQLContentLike(fuzzyName);
        }
        if (null==authority && null==fuzzyName && null==canAnswerNursingQuestion && null==hospitalId && null==departmentId && null==registerFrom && null==canSeeAllOrder) {
            resultSet = nurseRepository.findAll(sort);
        }
        else {
            resultSet = nurseRepository.findByAuthority(authority, fuzzyName, canAnswerNursingQuestion, hospitalId, departmentId, registerFrom, canSeeAllOrder, sort);
        }
        return resultSet;
    }

    public Iterable<NurseEntity> getNurseByQueryString(UserAuthority authority, YesNoEnum canAnswerNursingQuestion, String fuzzyName, String hospitalName, String departmentName, int pageIndex, int number) {
        logger.info("get nurse by authority={} canAnswerNursingQuestion={} fuzzyName={} hospitalName={} departmentName={} at page {} with number {}",
                authority, canAnswerNursingQuestion, fuzzyName, hospitalName, departmentName, pageIndex, number);
        PageRequest page = new PageRequest(pageIndex, number, sort);
        Page<NurseEntity> resultSet = null;

        fuzzyName = (VerifyUtil.isStringEmpty(fuzzyName)) ? null : VerifyUtil.reconstructSQLContentLike(fuzzyName);
        hospitalName = (VerifyUtil.isStringEmpty(hospitalName)) ? null : VerifyUtil.reconstructSQLContentLike(hospitalName);
        departmentName = (VerifyUtil.isStringEmpty(departmentName)) ? null : VerifyUtil.reconstructSQLContentLike(departmentName);

        // get hospital Ids
        List<HospitalEntity> hospitals = hospitalRepository.findByNameLike(hospitalName, 1);
        List<Integer> hospitalIds = new ArrayList<>();
        for (HospitalEntity tmp : hospitals) {
            hospitalIds.add(tmp.getId());
        }
        hospitalIds.add(Integer.MIN_VALUE);
        hospitals.clear();

        // get hospital Ids
        List<HospitalDepartmentEntity> departments = departmentRepository.findByNameLike(departmentName);
        List<Integer> departmentIds = new ArrayList<>();
        for (HospitalDepartmentEntity tmp : departments) {
            departmentIds.add(tmp.getId());
        }
        departmentIds.add(Integer.MIN_VALUE);
        departments.clear();

        resultSet = nurseRepository.findByQueryString(authority, canAnswerNursingQuestion, fuzzyName, hospitalIds, departmentIds, page);

        return resultSet;
    }

    //========================================================================
    //                        adding
    //========================================================================
    @Transactional
    public NurseEntity registerNurse(String name, int age, GenderType gender, String mobile, String password, String identification, String realName, String shortNote, RegisterFrom from) {
        if (null==from) {
            logger.info("register from is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(mobile)){
            logger.info("mobile is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        mobile = mobile.trim();
        if(!nurseRepository.findByMobile(mobile).isEmpty()){
            throw new BadRequestException(ErrorCode.NURSE_ALREADY_EXISTED);
        }

        gender = null==gender ? GenderType.SECRET : gender;

        NurseEntity entity = new NurseEntity();
        entity.setName(name);
        entity.setAge(age);
        entity.setGender(gender);
        entity.setMobile(mobile.trim());
        entity.setPassword(password);
        entity.setRealName(realName);
        entity.setIdentification(identification);
        entity.setShortNote(shortNote);
        entity.setAuthority(UserAuthority.AGREE_ALL);
        entity.setRegisterFrom(from);
        logger.info("add new nurse={}", entity);
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

    @Transactional
    public List<NurseEntity> updateAuthority(String strNurseIds, UserAuthority authority) {
        logger.info("update nurse {} 's authority={}", strNurseIds, authority);
        // check parameters
        if (null==authority) {
            logger.error("authority is not valid");
            return new ArrayList<>();
        }
        if (!VerifyUtil.isIds(strNurseIds)) {
            logger.error("nurse ids is not valid");
            return new ArrayList<>();
        }

        // get ids
        List<Long> nurseIds = VerifyUtil.parseLongIds(strNurseIds);

        // get nurses
        List<NurseEntity> nurses = nurseRepository.findByIdIn(nurseIds);
        if (null==nurses) {
            return new ArrayList<>();
        }
        for (NurseEntity nurse : nurses) {
            nurse.setAuthority(authority);
        }
        nurses = nurseRepository.save(nurses);
        logger.info("update count is {}", nurses.size());
        return nurses;
    }

    @Transactional
    public long updateHeadPhoto(NurseEntity nurse, long fileId){
        logger.info("set head photo for nurse "+nurse+", fileId="+fileId);
        if (null!=nurse && fileId>0 && fileId!=nurse.getProfilePhotoId()) {
            nurse.setProfilePhotoId(fileId);
            nurseRepository.save(nurse);
        }
        return fileId;
    }

    @Transactional
    public long updateBackgroundImage(NurseEntity nurse, long fileId){
        logger.info("set background image for nurse "+nurse+", fileId="+fileId);
        if (null!=nurse && fileId>0 && fileId!=nurse.getBackgroundImageId()) {
            nurse.setBackgroundImageId(fileId);
            nurseRepository.save(nurse);
        }
        return fileId;
    }

    //========================================================================
    //                        deleting
    //========================================================================
    @Transactional
    public NurseEntity deleteNurse(long nurseId) {
        logger.info("delete nurse by nurseId={}", nurseId);
        NurseEntity nurse = nurseRepository.findOne(nurseId);
        if (null==nurse) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        nurseRepository.delete(nurseId);
        logger.info("delete nurse={}", nurse);
        return nurse;
    }

}
