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
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.StorageService;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        bean.setGender(gender);
        bean.setMobile(mobile);
        bean.setPassword(password);
        return registerNurse(bean);
    }

    @Transactional
    public long registerNurse(NurseBean bean) {
        NurseEntity entity = entityConverter.convert(bean);
        if (null==entity.getMobile() || entity.getMobile().isEmpty() || entity.getPassword() == null || entity.getPassword().isEmpty()){
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
    public NurseBean updateNurse(long id, String name, int age, int gender, String mobile) {
        NurseEntity entity = repository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
        if (null==name || "".equals(name)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        boolean changed = false;
        if (!entity.getName().equals(name)) {
            entity.setName(name);
            changed = true;
        }
        if (entity.getAge()!=age && age>0) {
            entity.setAge(age);
            changed = true;
        }
        if (entity.getGender()!=gender && gender>0) {
            entity.setGender(gender);
            changed = true;
        }
        // TODO need to add algorithm to verify the mobil
        if (NumberUtil.isMobileValid(mobile) && !mobile.equals(entity.getMobile())) {
            entity.setMobile(mobile);
            changed = true;
        }
        if (changed) {
            entity = repository.save(entity);
        }
        return beanConverter.convert(entity);
    }

    @Transactional
    public void addHeadPhoto(long id, String fileName, InputStream inputStream){
        logger.info("add head photo for user "+id+", fileName="+fileName);
        NurseEntity nurse = repository.findOne(id);
        try {
            long fileId = storageService.saveFile(nurse.getProfilePhotoId(), fileName, inputStream);
            nurse.setProfilePhotoId(fileId);
        }
        catch (BadRequestException ex) {
            logger.info("Delete file has exception throwing " + ex);
            if (ex.getErrorCode().equals(ErrorCode.FILE_DELETE_FAILED)) {
                throw ex;
            }
        }
        repository.save(nurse);
    }

    @Transactional
    public void addBackgroundImage(long id, String fileName, InputStream inputStream){
        NurseEntity nurse = repository.findOne(id);
        try {
            long fileId = storageService.saveFile(nurse.getBackgroundImageId(), fileName, inputStream);
            nurse.setBackgroundImageId(fileId);
        }
        catch (BadRequestException ex) {
            logger.info("Delete file has exception throwing " + ex);
            if (ex.getErrorCode().equals(ErrorCode.FILE_DELETE_FAILED)) {
                throw ex;
            }
        }
        repository.save(nurse);
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
}
