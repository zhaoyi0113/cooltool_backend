package com.cooltoo.backend.services;

import com.cooltoo.beans.NurseQualificationBean;
import com.cooltoo.backend.beans.SocialAbilitiesBean;
import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseExtensionBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.RegisterFrom;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.converter.NurseBeanConverter;
import com.cooltoo.converter.NurseEntityConverter;
import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.leancloud.LeanCloudService;
import com.cooltoo.repository.NurseHospitalRelationRepository;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.services.CommonNurseHospitalRelationService;
import com.cooltoo.services.CommonNurseService;
import com.cooltoo.services.NurseExtensionService;
import com.cooltoo.services.NurseQualificationService;
import com.cooltoo.services.file.UserFileStorageService;
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
import java.util.Map;

/**
 * Created by lg380357 on 2016/3/2.
 */
@Service("NurseService")
public class NurseService {

    private static final Logger logger = LoggerFactory.getLogger(NurseService.class.getName());

    private static boolean isInitDenyNurseIds = false;
    private static final List<Long> denyNurseIds = new ArrayList<>();

    @Autowired private NurseRepository repository;
    @Autowired private CommonNurseService commonNurseService;
    @Autowired private NurseBeanConverter beanConverter;
    @Autowired private NurseEntityConverter entityConverter;
    @Autowired
    @Qualifier("UserFileStorageService")
    private UserFileStorageService userStorage;
    @Autowired private NurseFriendsService friendsService;
    @Autowired private NurseHospitalRelationRepository hospitalRelationRepository;
    @Autowired private NurseSpeakService speakService;
    @Autowired private NurseSocialAbilitiesService abilitiesService;
    @Autowired private LeanCloudService leanCloudService;
    @Autowired private NurseQualificationService qualificationService;
    @Autowired private CommonNurseHospitalRelationService hospitalRelationService;
    @Autowired private NurseExtensionService nurseExtensionService;


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

        GenderType genderType = GenderType.parseInt(gender);
        genderType = null==genderType ? GenderType.SECRET : genderType;
        bean.setGender(genderType);

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
        List<NurseEntity> nurses = commonNurseService.getNurseByMobile(bean.getMobile());
        if(!nurses.isEmpty()){
            throw new BadRequestException(ErrorCode.NURSE_ALREADY_EXISTED);
        }
        entity.setAuthority(UserAuthority.AGREE_ALL);
        entity.setRegisterFrom(RegisterFrom.COOLTOO);
        entity = commonNurseService.registerNurse(entity.getName(), entity.getAge(), entity.getGender(),
                entity.getMobile(), entity.getPassword(),
                entity.getIdentification(), entity.getRealName(), entity.getShortNote(), entity.getRegisterFrom());
        return entity.getId();
    }

    //==================================================================
    //         get
    //==================================================================
    public List<Long> getAllDenyNurseIds() {
        if (!isInitDenyNurseIds) {
            List<Long> denyIds = commonNurseService.getNurseIdsByAuthority(UserAuthority.DENY_ALL);
            if (!VerifyUtil.isListEmpty(denyIds)) {
                denyNurseIds.addAll(denyIds);
            }
            isInitDenyNurseIds = true;
        }
        List<Long> tmp = new ArrayList<>();
        for (Long denyUseId : denyNurseIds) {
            tmp.add(denyUseId);
        }
        return tmp;
    }

    public boolean existNurse(long nurseId) {
        return commonNurseService.existNurse(nurseId);
    }

    public NurseBean getNurse(String mobile) {
        List<NurseEntity> nurses = commonNurseService.getNurseByMobile(mobile);
        if (null!=nurses && !nurses.isEmpty() && nurses.size()==1) {
            NurseEntity nurseE = nurses.get(0);
            return beanConverter.convert(nurseE);
        }
        logger.error("Get nurse by mobile is error, result is {}.", nurses);
        throw new BadRequestException(ErrorCode.DATA_ERROR);
    }

    public NurseBean getNurseWithoutOtherInfo(long userId) {
        NurseEntity entity = commonNurseService.getNurseById(userId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        NurseBean nurse = beanConverter.convert(entity);
        List<Long> imageIds = new ArrayList<>();
        imageIds.add(nurse.getProfilePhotoId());
        imageIds.add(nurse.getBackgroundImageId());
        Map<Long, String> imgId2Path = userStorage.getFilePath(imageIds);
        nurse.setProfilePhotoUrl(imgId2Path.get(nurse.getProfilePhotoId()));
        nurse.setBackgroundImageUrl(imgId2Path.get(nurse.getBackgroundImageId()));
        return nurse;
    }

    public List<NurseBean> getNurseWithoutOtherInfo(List<Long> userIds) {
        logger.info("get nurse without other info by ids={}", userIds);
        List<NurseEntity> entities = commonNurseService.getNurseByIds(userIds);
        if (VerifyUtil.isListEmpty(entities)) {
            return new ArrayList<>();
        }
        List<NurseBean> beans = entities2Beans(entities);
        List<Long> imageIds = new ArrayList<>();
        for (NurseBean nurse : beans) {
            imageIds.add(nurse.getProfilePhotoId());
            imageIds.add(nurse.getBackgroundImageId());
        }
        Map<Long, String> imgId2Path = userStorage.getFilePath(imageIds);
        for (NurseBean nurse : beans) {
            String profile = imgId2Path.get(nurse.getProfilePhotoId());
            String background = imgId2Path.get(nurse.getBackgroundImageId());
            nurse.setProfilePhotoUrl(profile);
            nurse.setBackgroundImageUrl(background);
        }
        logger.info("count is {}", beans.size());
        return beans;
    }

    public NurseBean getNurse(long userId) {
        NurseEntity entity = commonNurseService.getNurseById(userId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        NurseBean nurse = beanConverter.convert(entity);
        List<Long> imageIds = new ArrayList<>();
        imageIds.add(nurse.getProfilePhotoId());
        imageIds.add(nurse.getBackgroundImageId());
        Map<Long, String> imgId2Path = userStorage.getFilePath(imageIds);
        nurse.setProfilePhotoUrl(imgId2Path.get(nurse.getProfilePhotoId()));
        nurse.setBackgroundImageUrl(imgId2Path.get(nurse.getBackgroundImageId()));

        long friendsCount = friendsService.countFriendship(userId);
        nurse.setProperty(NurseBean.FRIENDS_COUNT, friendsCount);
        List<HospitalEntity> nurseHospitals = hospitalRelationRepository.getNurseHospitals(userId);
        if(!nurseHospitals.isEmpty()){
            nurse.setHospital(nurseHospitals.get(0).getName());
        }
        try {
            NurseHospitalRelationBean relation = hospitalRelationService.getRelationByNurseId(userId, "");
            if (null != relation) {
                nurse.setProperty(NurseBean.HOSPITAL_DEPARTMENT, relation);
            }
        }
        catch (Exception ex) {}
        // add speak count
        String speakType = "SMUG,ASK_QUESTION,SHORT_VIDEO";
        long speakCount = speakService.countSpeak(true, userId, speakType);
        nurse.setProperty(NurseBean.SPEAK_COUNT, speakCount);
        // add skill nominated count
        List<SocialAbilitiesBean> norminated = this.abilitiesService.getUserAllTypeAbilities(userId);
        nurse.setProperty(NurseBean.ABILITY_COUNT, null==norminated ? 0 : norminated.size());
        // get nurse's qualification
        List<NurseQualificationBean> qualifications = qualificationService.getAllNurseQualifications(userId, "");
        if (null!=qualifications && !qualifications.isEmpty()) {
            nurse.setProperty(NurseBean.QUALIFICATION, qualifications.get(0));
        }
        // get nurse's extension
        NurseExtensionBean extensionInfo = nurseExtensionService.getExtensionByNurseId(userId);
        if (null!=extensionInfo) {
            nurse.setProperty(NurseBean.INFO_EXTENSION, extensionInfo);
        }
        return nurse;
    }

    public List<NurseBean> getNurse(List<Long> nurseIds) {
        logger.info("get nurse by ids={}", nurseIds);

        List<NurseBean> nurses = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(nurseIds)) {
            List<NurseEntity> resultSet = commonNurseService.getNurseByIds(nurseIds);
            nurses = entities2Beans(resultSet);
            fillOtherProperties(nurses);
        }
        return nurses;
    }

    public List<Long> getNurseIdsByName(String fuzzyQueryName, int pageIndex, int sizePerPage) {
        List<Long> nurseIds = commonNurseService.getNurseIdsByName(fuzzyQueryName, pageIndex, sizePerPage);
        logger.info("find result set size is {}", nurseIds.size());
        return nurseIds;
    }


    //==============================================================
    //             get used by administrator
    //==============================================================

    public long countByAuthorityAndFuzzyName(String strAuthority, String fuzzyName, String strCanAnswerNursingQuestion, String strCanSeeAllOrder, Integer hospitalId, Integer departmentId, RegisterFrom registerFrom) {
        UserAuthority authority = UserAuthority.parseString(strAuthority);
        YesNoEnum canAnswerNursingQuestion = YesNoEnum.parseString(strCanAnswerNursingQuestion);
        YesNoEnum canSeeAllOrder = YesNoEnum.parseString(strCanSeeAllOrder);
        long count = commonNurseService.countByAuthorityAndFuzzyName(authority, fuzzyName, canAnswerNursingQuestion, canSeeAllOrder, hospitalId, departmentId, registerFrom);
        logger.info("count is {}", count);
        return count;
    }

    public List<NurseBean> getAllByAuthorityAndFuzzyName(String strAuthority, String fuzzyName, String strCanAnswerNursingQuestion, String strCanSeeAllOrder, Integer hospitalId, Integer departmentId, RegisterFrom registerFrom, int pageIndex, int number) {
        // get nurse by authority
        UserAuthority authority = UserAuthority.parseString(strAuthority);
        YesNoEnum canAnswerNursingQuestion = YesNoEnum.parseString(strCanAnswerNursingQuestion);
        YesNoEnum canSeeAllOrder = YesNoEnum.parseString(strCanSeeAllOrder);
        Iterable<NurseEntity> resultSet = commonNurseService.getNurseByAuthorityAndFuzzyName(authority, fuzzyName, canAnswerNursingQuestion, canSeeAllOrder, hospitalId, departmentId, registerFrom, pageIndex, number);
        // parse to bean
        List<NurseBean> beanList = entities2Beans(resultSet);
        fillOtherProperties(beanList);
        logger.info("count is {}", beanList.size());
        return beanList;
    }

    private List<NurseBean> entities2Beans(Iterable<NurseEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        List<NurseBean> beans = new ArrayList<>();
        for (NurseEntity tmp : entities) {
            NurseBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<NurseBean> nurses) {
        if (null==nurses || nurses.isEmpty()) {
            return;
        }

        List<Long> userIds  = new ArrayList<>();
        List<Long> imageIds = new ArrayList<>();
        for (NurseBean bean :nurses) {
            userIds.add(bean.getId());

            long imageId = bean.getProfilePhotoId();
            if (!imageIds.contains(imageId)) {
                imageIds.add(imageId);
            }

            imageId = bean.getBackgroundImageId();
            if (!imageIds.contains(imageId)) {
                imageIds.add(imageId);
            }
        }

        Map<Long, String> imageId2Path = userStorage.getFilePath(imageIds);
        for (NurseBean bean : nurses) {
            long   imageId = bean.getProfilePhotoId();
            String imgPath = imageId2Path.get(imageId);
            if (!VerifyUtil.isStringEmpty(imgPath)) {
                bean.setProfilePhotoUrl(imgPath);
            }

            imageId = bean.getBackgroundImageId();
            imgPath = imageId2Path.get(imageId);
            if (!VerifyUtil.isStringEmpty(imgPath)) {
                bean.setBackgroundImageUrl(imgPath);
            }
        }

        try {
            Map<Long, NurseExtensionBean>        extensionInfo = nurseExtensionService.getExtensionByNurseIds(userIds);
            Map<Long, NurseHospitalRelationBean> hospitals  = hospitalRelationService.getRelationMapByNurseIds(userIds, "");
            Map<Long, Long>                      speakCount = speakService.countByUserIds(userIds);
            for (NurseBean bean : nurses) {
                NurseExtensionBean extension = extensionInfo.get(bean.getId());
                NurseHospitalRelationBean hospital = hospitals.get(bean.getId());
                Long speakNum = speakCount.get(bean.getId());
                speakNum = null == speakNum ? 0L : speakNum;
                bean.setProperty(NurseBean.SPEAK_COUNT, speakNum);
                if (null != hospital) {
                    bean.setProperty(NurseBean.HOSPITAL_DEPARTMENT, hospital);
                }
                if (null!=extension) {
                    bean.setProperty(NurseBean.INFO_EXTENSION, extension);
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
    public NurseBean deleteNurse(long nurseId) {
        NurseEntity entity = commonNurseService.deleteNurse(nurseId);
        return beanConverter.convert(entity);
    }

    //==================================================================
    //         update
    //==================================================================

    @Transactional
    public NurseBean updateNurse(long userId, String name, int age, int iGender) {
        GenderType gender = GenderType.parseInt(iGender);
        NurseEntity entity = commonNurseService.updateBasicInfo(userId, name, age, gender, null, null, null, null, null, null);
        return beanConverter.convert(entity);
    }

    @Transactional
    public String updateHeadPhoto(long userId, String fileName, InputStream inputStream){
        logger.info("add head photo for user "+userId+", fileName="+fileName);
        NurseEntity nurse = repository.findOne(userId);
        long fileId = 0;
        try {
            fileId = userStorage.addFile(nurse.getProfilePhotoId(), fileName, inputStream);
            nurse.setProfilePhotoId(fileId);
        }
        catch (BadRequestException ex) {
            logger.info("Delete file has exception throwing " + ex);
            if (ex.getErrorCode().equals(ErrorCode.FILE_DELETE_FAILED)) {
                throw ex;
            }
        }
        repository.save(nurse);
        return userStorage.getFilePath(fileId);
    }

    @Transactional
    public String updateBackgroundImage(long userId, String fileName, InputStream inputStream){
        NurseEntity nurse = repository.findOne(userId);
        long fileId = 0;
        try {
            fileId = userStorage.addFile(nurse.getBackgroundImageId(), fileName, inputStream);
            nurse.setBackgroundImageId(fileId);
        }
        catch (BadRequestException ex) {
            logger.info("Delete file has exception throwing " + ex);
            if (ex.getErrorCode().equals(ErrorCode.FILE_DELETE_FAILED)) {
                throw ex;
            }
        }
        repository.save(nurse);
        return userStorage.getFilePath(fileId);
    }

    @Transactional
    public NurseBean updateRealNameAndIdentification(long userId, String realName, String identification) {
        if (!commonNurseService.existNurse(userId)) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }

        NurseEntity nurse = commonNurseService.updateBasicInfo(userId,
                null, -1, null, null, null, identification, realName, null, null);
        return beanConverter.convert(nurse);
    }

    @Transactional
    public NurseBean updateShortNote(long userId, String shortNote) {
        logger.info("nurse short note is : " + shortNote);
        if (VerifyUtil.isStringEmpty(shortNote)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (!commonNurseService.existNurse(userId)) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        NurseEntity nurse = commonNurseService.updateBasicInfo(userId,
                null, -1, null, null, null, null, null, shortNote.trim(), null);
        return beanConverter.convert(nurse);
    }

    @Transactional
    public NurseBean updateMobilePassword(long userId, String smsCode, String mobile, String newMobile, String password, String newPassword) {
        logger.info("modify the password and mobile : [smsCode"+smsCode+", mobile=" +mobile+ ", newMobile="+newMobile+", password="+password+", newPassword="+newPassword+"]");

        NurseBean nurse = null;
        if (commonNurseService.existNurse(userId)) {
            nurse = getNurseWithoutOtherInfo(userId);
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
        NurseEntity nurseEntity = commonNurseService.getNurseById(bean.getId());
        if (null==nurseEntity) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
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
            nurseEntity = commonNurseService.updateBasicInfo(nurseEntity.getId()
                    , null, -1, null,
                    nurseEntity.getMobile(), nurseEntity.getPassword(),
                    null, null, null, null);
        }
        return beanConverter.convert(nurseEntity);
    }

    @Transactional
    public List<NurseBean> updateAuthority(String strNurseIds, String strAuthority) {
        logger.info("update nurse {} 's authority property to {}", strNurseIds, strAuthority);

        // check parameters
        UserAuthority authority = UserAuthority.parseString(strAuthority);
        List<NurseEntity> nurses = commonNurseService.updateAuthority(strNurseIds, authority);
        for (NurseEntity nurse : nurses) {
            modifyDenyNurseIds(nurse);
        }
        List<NurseBean> results = new ArrayList<>();
        for (NurseEntity tmp : nurses) {
            NurseBean bean = beanConverter.convert(tmp);
            results.add(bean);
        }
        return results;
    }

    private void modifyDenyNurseIds(NurseEntity nurse) {
        if (null==nurse) {
            return;
        }
        long nurseId = nurse.getId();
        UserAuthority authority = nurse.getAuthority();
        if (UserAuthority.DENY_ALL.equals(authority)) {
            if (!denyNurseIds.contains(nurseId)) {
                denyNurseIds.add(nurseId);
            }
        }
        else if (UserAuthority.AGREE_ALL.equals(authority)) {
            if (denyNurseIds.contains(nurseId)) {
                denyNurseIds.remove(nurseId);
            }
        }
    }
}
