package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.beans.NurseQualificationBean;
import com.cooltoo.backend.converter.NurseBeanConverter;
import com.cooltoo.backend.converter.NurseEntityConverter;
import com.cooltoo.backend.entities.HospitalEntity;
import com.cooltoo.backend.leancloud.LeanCloudService;
import com.cooltoo.backend.repository.HospitalRepository;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.VetStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.StorageService;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
    private NurseSkillNorminationService norminationService;
    @Autowired
    private LeanCloudService leanCloudService;
    @Autowired
    private NurseQualificationService qualificationService;

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
        if(!repository.findNurseByMobile(bean.getMobile()).isEmpty()){
            throw new BadRequestException(ErrorCode.NURSE_ALREADY_EXISTED);
        }
        entity = repository.save(entity);
        return entity.getId();
    }


    public NurseBean getNurse(long id) {
        NurseEntity entity = repository.findOne(id);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        NurseBean nurse = beanConverter.convert(entity);
        int friendsCount = friendsService.getFriendsCount(id);
        nurse.setProperty(NurseBean.FRIENDS_COUNT, friendsCount);
        List<HospitalEntity> nurseHospitals = hospitalRepository.getNurseHospitals(id);
        if(!nurseHospitals.isEmpty()){
            nurse.setHospital(nurseHospitals.get(0).getName());
        }
        long speakCount = speakService.getNurseSpeakCount(id);
        nurse.setProperty(NurseBean.SPEAK_COUNT, speakCount);
        long norminated = this.norminationService.getUserAllSkillNorminatedCount(id);
        nurse.setProperty(NurseBean.NORMINATED_COUNT, norminated);
        return nurse;
    }

    public List<NurseBean> getAll() {
        Iterable<NurseEntity> all = repository.findAll();
        List<NurseBean> beanList = new ArrayList<NurseBean>();
        for (NurseEntity entity : all) {
            NurseBean bean = beanConverter.convert(entity);
            beanList.add(bean);
        }
        return beanList;
    }

    @Transactional
    public NurseBean deleteNurse(long id) {
        NurseEntity entity = repository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
        repository.delete(id);
        return beanConverter.convert(entity);
    }

    @Transactional
    public NurseBean updateNurse(long id, String name, int age, int gender) {
        NurseEntity entity = repository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }

        boolean changed = false;
        if (name != null && !entity.getName().equals(name)) {
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
    public String addHeadPhoto(long id, String fileName, InputStream inputStream){
        logger.info("add head photo for user "+id+", fileName="+fileName);
        NurseEntity nurse = repository.findOne(id);
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
    public String addBackgroundImage(long id, String fileName, InputStream inputStream){
        NurseEntity nurse = repository.findOne(id);
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
    public NurseBean setRealNameAndIdentification(long id, String realName, String identification) {
        NurseEntity nurse = repository.findOne(id);
        if (null==nurse) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
        logger.info("nurse real name is : " + realName);
        logger.info("nurse identification is : " + identification);
        if (VerifyUtil.isStringEmpty(realName)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (!NumberUtil.isIdentificationValid(identification)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        nurse.setRealName(realName);
        nurse.setIdentification(identification);
        nurse = repository.save(nurse);
        return beanConverter.convert(nurse);
    }

    @Transactional
    public NurseQualificationBean updateNurseWorkFile(long id, String fileName, InputStream workFile) {
        NurseQualificationBean bean = qualificationService.updateNurseWorkFile(id, null, fileName, workFile, VetStatus.WAITING);
        return bean;
    }

    @Transactional
    public NurseQualificationBean addNurseWorkFile(long id, String name, String workFileName, InputStream workFile) {
        NurseQualificationBean bean = qualificationService.addNurseWorkFile(id, name, workFileName, workFile);
        return bean;
    }

    @Transactional
    public NurseQualificationBean addNurseIdentification(long id, String name, String idFileName, InputStream idFile) {
        NurseQualificationBean bean = qualificationService.addNurseIdentificationFile(id, name, idFileName, idFile);
        return bean;
    }

    @Transactional
    public List<NurseQualificationBean> getAllNurseQualification(long id) {
        List<NurseQualificationBean> qualifications = qualificationService.getAllNurseQualifications(id);
        return qualifications;
    }

    @Transactional
    public NurseBean updateMobilePassword(long id, String smsCode, String newMobile, String password, String newPassword) {
        logger.info("modify the password and mobile : [smsCode"+smsCode+", newMobile="+newMobile+", password="+password+", newPassword="+newPassword+"]");
        leanCloudService.verifySmsCode(smsCode, newMobile);
        NurseBean bean = new NurseBean();
        bean.setId(id);
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
}
