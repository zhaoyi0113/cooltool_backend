package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.beans.NurseQualificationBean;
import com.cooltoo.backend.beans.SocialAbilitiesBean;
import com.cooltoo.backend.converter.NurseBeanConverter;
import com.cooltoo.backend.converter.NurseEntityConverter;
import com.cooltoo.backend.entities.HospitalEntity;
import com.cooltoo.backend.leancloud.LeanCloudService;
import com.cooltoo.backend.repository.HospitalRepository;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.StorageService;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lg380357 on 2016/3/2.
 */
@Service("NurseService")
public class NurseService {

    private static final Logger logger = LoggerFactory.getLogger(NurseService.class.getName());

    @Autowired
    private NurseRepository repository;
    @Autowired
    private NurseBeanConverter beanConverter;
    @Autowired
    private NurseEntityConverter entityConverter;
    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;
    @Autowired
    private NurseFriendsService friendsService;
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private NurseSpeakService speakService;
    @Autowired
    private NurseSocialAbilitiesService abilitiesService;
    @Autowired
    private LeanCloudService leanCloudService;
    @Autowired
    private NurseQualificationService qualificationService;
    @Autowired
    private NurseHospitalRelationService hospitalRelationService;


    //==================================================================
    //         add
    //==================================================================

    @Transactional
    public long registerNurse(String name, int age,
                              int gender, String mobile, String password, String smsCode) {
        logger.info("register new nurse "+mobile+", "+smsCode);
        leanCloudService.verifySmsCode(smsCode, mobile);
        NurseBean bean = new NurseBean();
        bean.setName(name);
        bean.setAge(age);
        bean.setGender(GenderType.parseInt(gender));
        bean.setMobile(mobile);
        bean.setPassword(password);
        return registerNurse(bean);
    }

    @Transactional
    public long registerNurse(NurseBean bean) {
        NurseEntity entity = entityConverter.convert(bean);
        if (VerifyUtil.isStringEmpty(entity.getMobile()) || VerifyUtil.isStringEmpty(entity.getPassword())){
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if(!repository.findByMobile(bean.getMobile()).isEmpty()){
            throw new BadRequestException(ErrorCode.NURSE_ALREADY_EXISTED);
        }
        entity = repository.save(entity);
        return entity.getId();
    }

    //==================================================================
    //         get
    //==================================================================

    public NurseBean getNurse(String mobile) {
        List<NurseEntity> nurses = repository.findByMobile(mobile);
        if (null!=nurses && !nurses.isEmpty() && nurses.size()==1) {
            NurseEntity nurseE = nurses.get(0);
            return beanConverter.convert(nurseE);
        }
        logger.error("Get nurse by mobile is error, result is {}.", nurses);
        throw new BadRequestException(ErrorCode.DATA_ERROR);
    }

    public NurseBean getNurse(long userId) {
        NurseEntity entity = repository.findOne(userId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
        NurseBean nurse = beanConverter.convert(entity);
        int friendsCount = friendsService.getFriendsCount(userId);
        nurse.setProperty(NurseBean.FRIENDS_COUNT, friendsCount);
        List<HospitalEntity> nurseHospitals = hospitalRepository.getNurseHospitals(userId);
        if(!nurseHospitals.isEmpty()){
            nurse.setHospital(nurseHospitals.get(0).getName());
        }
        try {
            NurseHospitalRelationBean relation = hospitalRelationService.getRelationByNurseId(userId);
            if (null != relation) {
                nurse.setProperty(NurseBean.HOSPITAL_DEPARTMENT, relation);
            }
        }
        catch (Exception ex) {
        }
        // add speak count
        long speakCount = speakService.countByUserId(userId);
        nurse.setProperty(NurseBean.SPEAK_COUNT, speakCount);
        // add skill nominated count
        List<SocialAbilitiesBean> norminated = this.abilitiesService.getUserAllTypeAbilities(userId);
        nurse.setProperty(NurseBean.ABILITY_COUNT, null==norminated ? 0 : norminated.size());
        // get nurse's qualification
        List<NurseQualificationBean> qualifications = qualificationService.getAllNurseQualifications(userId);
        if (null!=qualifications && !qualifications.isEmpty()) {
            nurse.setProperty(NurseBean.QUALIFICATION, qualifications.get(0));
        }
        return nurse;
    }

    //==============================================================
    //             get used by administrator
    //==============================================================

    public long countByAuthority(String strAuthority) {
        logger.info("get nurse count by authority={}", strAuthority);
        if ("ALL".equalsIgnoreCase(strAuthority)) {
            return repository.count();
        }
        UserAuthority authority = UserAuthority.parseString(strAuthority);
        if (null==authority) {
            return 0;
        }
        return repository.countByAuthority(authority);
    }

    public List<NurseBean> getAllByAuthority(String strAuthority, int pageIndex, int number) {
        logger.info("get nurse by authority={} at page {} with number {}", strAuthority, pageIndex, number);
        PageRequest       page      = new PageRequest(pageIndex, number, Sort.Direction.DESC, "id");

        // get nuser by authority
        Page<NurseEntity> resultSet = null;
        if ("ALL".equalsIgnoreCase(strAuthority)) {
            resultSet = repository.findAll(page);
        }
        else {
            UserAuthority authority = UserAuthority.parseString(strAuthority);
            if (null == authority) {
                return new ArrayList<>();
            }
            resultSet = repository.findByAuthority(authority, page);
        }

        // parse to bean
        List<NurseBean> beanList = new ArrayList<NurseBean>();
        for (NurseEntity entity : resultSet) {
            NurseBean bean = beanConverter.convert(entity);
            beanList.add(bean);
        }

        // fill other information
        fillOtherProperties(beanList);
        return beanList;
    }

    private void fillOtherProperties(List<NurseBean> nurses) {
        if (null==nurses || nurses.isEmpty()) {
            return;
        }

        List<Long> userIds = new ArrayList<>();
        for (NurseBean bean :nurses) {
            userIds.add(bean.getId());
        }

        try {
            Map<Long, NurseHospitalRelationBean> hospitals  = hospitalRelationService.getRelationMapByNurseIds(userIds);
            Map<Long, Long>                      speakCount = speakService.countByUserIds(userIds);
            for (NurseBean bean : nurses) {
                NurseHospitalRelationBean hospital = hospitals.get(bean.getId());
                Long speakNum = speakCount.get(bean.getId());
                speakNum = null == speakNum ? 0L : speakNum;
                bean.setProperty(NurseBean.SPEAK_COUNT, speakNum);
                if (null != hospital) {
                    bean.setProperty(NurseBean.HOSPITAL_DEPARTMENT, hospital);
                }
            }
        }
        catch (Exception ex) {
            logger.warn("fill nurse other information throw exception {}", ex);
        }
    }

    //==================================================================
    //         delete
    //==================================================================

    @Transactional
    public NurseBean deleteNurse(long userId) {
        NurseEntity entity = repository.findOne(userId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
        repository.delete(userId);
        return beanConverter.convert(entity);
    }

    //==================================================================
    //         update
    //==================================================================

    @Transactional
    public NurseBean updateNurse(long userId, String name, int age, int gender) {
        NurseEntity entity = repository.findOne(userId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }

        boolean changed = false;
        if (name != null) {
            entity.setName(name);
            changed = true;
        }
        if (entity.getAge()!=age && age>0) {
            entity.setAge(age);
            changed = true;
        }
        if(gender >= 0) {
            entity.setGender(GenderType.parseInt(gender));
        }

        if (changed) {
            entity = repository.save(entity);
        }
        return beanConverter.convert(entity);
    }

    @Transactional
    public String updateHeadPhoto(long userId, String fileName, InputStream inputStream){
        logger.info("add head photo for user "+userId+", fileName="+fileName);
        NurseEntity nurse = repository.findOne(userId);
        long fileId = 0;
        try {
            fileId = storageService.saveFile(nurse.getProfilePhotoId(), fileName, inputStream);
            nurse.setProfilePhotoId(fileId);
        }
        catch (BadRequestException ex) {
            logger.info("Delete file has exception throwing " + ex);
            if (ex.getErrorCode().equals(ErrorCode.FILE_DELETE_FAILED)) {
                throw ex;
            }
        }
        repository.save(nurse);
        return storageService.getFilePath(fileId);
    }

    @Transactional
    public String updateBackgroundImage(long userId, String fileName, InputStream inputStream){
        NurseEntity nurse = repository.findOne(userId);
        long fileId = 0;
        try {
            fileId = storageService.saveFile(nurse.getBackgroundImageId(), fileName, inputStream);
            nurse.setBackgroundImageId(fileId);
        }
        catch (BadRequestException ex) {
            logger.info("Delete file has exception throwing " + ex);
            if (ex.getErrorCode().equals(ErrorCode.FILE_DELETE_FAILED)) {
                throw ex;
            }
        }
        repository.save(nurse);
        return storageService.getFilePath(fileId);
    }

    @Transactional
    public NurseBean updateRealNameAndIdentification(long userId, String realName, String identification) {
        boolean changed = false;
        NurseEntity nurse = repository.findOne(userId);
        if (null==nurse) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
        logger.info("nurse real name is : " + realName);
        logger.info("nurse identification is : " + identification);
        if (!VerifyUtil.isStringEmpty(realName)) {
            nurse.setRealName(realName);
            changed = true;
        }
        if (NumberUtil.isIdentificationValid(identification)) {
            nurse.setIdentification(identification);
            changed = true;
        }
        if (changed) {
            nurse = repository.save(nurse);
        }
        return beanConverter.convert(nurse);
    }

    @Transactional
    public NurseBean updateShortNote(long userId, String shortNote) {
        NurseEntity nurse = repository.findOne(userId);
        if (null==nurse) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
        logger.info("nurse short note is : " + shortNote);
        if (VerifyUtil.isStringEmpty(shortNote)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        nurse.setShortNote(shortNote);
        nurse = repository.save(nurse);
        return beanConverter.convert(nurse);
    }

    @Transactional
    public NurseBean updateMobilePassword(long userId, String smsCode, String mobile, String newMobile, String password, String newPassword) {
        logger.info("modify the password and mobile : [smsCode"+smsCode+", mobile=" +mobile+ ", newMobile="+newMobile+", password="+password+", newPassword="+newPassword+"]");

        NurseBean nurse = null;
        if (repository.exists(userId)) {
            nurse = getNurse(userId);
        }
        if (null==nurse) {
            nurse = getNurse(mobile);
            userId = nurse.getId();
        }
        // if not modify the mobile
        if (!VerifyUtil.isStringEmpty(mobile)) {
            if (!mobile.equals(nurse.getMobile())) {
                logger.error("the mobile is not equals to the user setting");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }
        if (VerifyUtil.isStringEmpty(newMobile)) {
            newMobile = nurse.getMobile();
        }
        /* check the verify code */
        //leanCloudService.verifySmsCode(smsCode, newMobile);

        // check password
        if (!VerifyUtil.isStringEmpty(password)) {
            if (!password.equals(nurse.getPassword())) {
                throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
            }
            if (VerifyUtil.isStringEmpty(newPassword)) {
                throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
            }
            else if (password.equals(newPassword)) {
                logger.info("the new password is same with old password.");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }
        else if (!VerifyUtil.isStringEmpty(mobile) && !VerifyUtil.isStringEmpty(newPassword)) {
            //newPassword = newPassword;
        }
        else {
            newPassword = null;
        }
        NurseBean bean = new NurseBean();
        bean.setId(userId);
        bean.setMobile(newMobile);
        bean.setPassword(newPassword);
        return updateMobilePassword(bean);
    }

    @Transactional
    public NurseBean updateMobilePassword(NurseBean bean) {
        logger.info("modify the password and mobile : [newMobile="+bean.getMobile()+", newPassword="+bean.getPassword()+"]");
        // get nurse
        NurseEntity nurseEntity = repository.findOne(bean.getId());
        if (null==nurseEntity) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }

        boolean changed = false;
        String valueChanged = bean.getMobile();
        // check mobile
        if (!VerifyUtil.isStringEmpty(valueChanged)) {
            if (!valueChanged.equals(nurseEntity.getMobile())) {
                nurseEntity.setMobile(valueChanged);
                changed = true;
            }
        }
        valueChanged = bean.getPassword();
        if (!VerifyUtil.isStringEmpty(valueChanged)) {
            if (!valueChanged.equals(nurseEntity.getPassword())) {
                nurseEntity.setPassword(valueChanged);
                changed = true;
            }
        }
        if (changed) {
            nurseEntity = repository.save(nurseEntity);
        }
        return beanConverter.convert(nurseEntity);
    }

    @Transactional
    public NurseBean updateAuthority(long userId, String strAuthority) {
        logger.info("update nurse {} authority to {}", userId, strAuthority);
        List<NurseBean> results = updateAuthority(""+userId, strAuthority);
        if (null==results || results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    @Transactional
    public List<NurseBean> updateAuthority(String strNurseIds, String strAuthority) {
        logger.info("update nurse {} 's authority property to {}", strNurseIds, strAuthority);

        // check parameters
        UserAuthority authority = UserAuthority.parseString(strAuthority);
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
        List<NurseEntity> nurses = repository.findByIdIn(nurseIds);
        if (null==nurses) {
            return new ArrayList<>();
        }
        for (NurseEntity nurse : nurses) {
            nurse.setAuthority(authority);
        }
        Iterable        resultSet = repository.save(nurses);
        List<NurseBean> results   = new ArrayList<NurseBean>();
        for (Object obj : resultSet) {
            if (obj instanceof NurseEntity) {
                NurseBean bean = beanConverter.convert(((NurseEntity)obj));
                results.add(bean);
            }
        }
        return results;
    }
}
