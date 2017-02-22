package com.cooltoo.nurse360.service;

import com.cooltoo.beans.NurseAuthorizationBean;
import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseExtensionBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.beans.NurseQualificationBean;
import com.cooltoo.constants.*;
import com.cooltoo.converter.NurseBeanConverter;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.service.NurseOrderRelationService;
import com.cooltoo.go2nurse.service.NurseWalletService;
import com.cooltoo.leancloud.LeanCloudService;
import com.cooltoo.nurse360.util.Nurse360Utility;
import com.cooltoo.services.CommonNurseAuthorizationService;
import com.cooltoo.services.CommonNurseHospitalRelationService;
import com.cooltoo.services.CommonNurseService;
import com.cooltoo.services.NurseExtensionService;
import com.cooltoo.services.NurseQualificationService;
import com.cooltoo.services.file.UserFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 16/9/28.
 */
@Service("NurseServiceForNurse360")
public class NurseServiceForNurse360 {

    private static final Logger logger = LoggerFactory.getLogger(NurseServiceForNurse360.class);

    @Autowired private CommonNurseService commonNurseService;
    @Autowired private NurseExtensionService nurseExtensionService;
    @Autowired private CommonNurseHospitalRelationService nurseHospitalRelationService;
    @Autowired private NurseBeanConverter nurseBeanConverter;
    @Autowired private UserFileStorageService nursegoFileStorage;
    @Autowired private Nurse360Utility utility;
    @Autowired private LeanCloudService leanCloudService;
    @Autowired private NurseOrderRelationService nurseOrderService;
    @Autowired private NurseQualificationService nurseQualificationService;
    @Autowired private NurseWalletService walletService;
    @Autowired private CommonNurseAuthorizationService nurseAuthorizationService;

    //===================================================================
    //                     getting
    //===================================================================
    public boolean existsNurse(long nurseId) {
        return commonNurseService.existNurse(nurseId);
    }


    public NurseBean getNurseById(long nurseId) {
        logger.info("get nurse by nurseId={}", nurseId);
        NurseEntity nurse = commonNurseService.getNurseById(nurseId);
        if (null==nurse) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        NurseBean bean = nurseBeanConverter.convert(nurse);
        String profilePath = nursegoFileStorage.getFilePath(bean.getProfilePhotoId());
        String backgroundPath = nursegoFileStorage.getFilePath(bean.getBackgroundImageId());
        if (!VerifyUtil.isStringEmpty(profilePath)) {
            bean.setProfilePhotoUrl(utility.getHttpPrefixForNurseGo() + profilePath);
        }
        if (!VerifyUtil.isStringEmpty(backgroundPath)) {
            bean.setBackgroundImageUrl(utility.getHttpPrefixForNurseGo() + backgroundPath);
        }

        NurseExtensionBean extension = nurseExtensionService.getExtensionByNurseId(nurseId);
        NurseHospitalRelationBean hospitalDepartment = nurseHospitalRelationService.getRelationByNurseId(nurseId, utility.getHttpPrefixForNurseGo());
        List<ServiceOrderBean> orders = nurseOrderService.getOrderByNurseIdAndOrderStatus(nurseId, CommonStatus.ENABLED.name(), OrderStatus.IN_PROCESS, 0, 10);
        List<NurseQualificationBean> qualification = nurseQualificationService.getAllNurseQualifications(nurseId, utility.getHttpPrefixForNurseGo());
        Map<Long, Long> walletBalance = walletService.getNurseWalletBalance(Arrays.asList(new Long[]{nurseId}));
        Long balance = walletBalance.get(nurseId);
        balance = (balance instanceof Long) ? balance : 0L;
        NurseAuthorizationBean authorization = nurseAuthorizationService.getAuthorizationByNurseId(nurseId);

        bean.setProperty(NurseBean.INFO_EXTENSION, extension);
        bean.setProperty(NurseBean.HOSPITAL_DEPARTMENT, hospitalDepartment);
        bean.setProperty(NurseBean.ORDER, orders);
        bean.setProperty(NurseBean.WALLET_BALANCE, VerifyUtil.parsePrice(balance.intValue()));
        if (null!=qualification && !qualification.isEmpty()) {
            bean.setProperty(NurseBean.QUALIFICATION, qualification.get(0));
        }
        bean.setProperty(NurseBean.AUTHORIZATION, authorization);

        return bean;
    }

    public List<NurseBean> getNurseByIds(List<Long> nurseIds) {
        Iterable<NurseEntity> nurses = commonNurseService.getNurseByIds(nurseIds);
        List<NurseBean> beans = entitiesToBeans(nurses);
        fillOtherProperties(beans);
        return beans;
    }

    public List<String> getManagerMobiles(Integer hospitalId, Integer departmentId) {
        logger.info("get nurse manager mobiles by hospital={} department={}", hospitalId, departmentId);
        Iterable<NurseEntity> nurses = commonNurseService.getNurseByAuthorityAndFuzzyName(UserAuthority.AGREE_ALL, null, null, null, hospitalId, departmentId, null);
        List<NurseBean> beans = entitiesToBeans(nurses);
        fillOtherProperties(beans);
        List<String> managersMobile = new ArrayList<>();
        for (NurseBean tmp : beans) {
            NurseExtensionBean extension = (NurseExtensionBean) tmp.getProperty(NurseBean.INFO_EXTENSION);
            if (null==extension) {
                continue;
            }
            String mobile = tmp.getMobile();
            if (VerifyUtil.isStringEmpty(mobile)) {
                continue;
            }
            if (YesNoEnum.YES.equals(extension.getIsManager())) {
                managersMobile.add(tmp.getMobile());
            }
        }
        logger.info("count is {}", managersMobile.size());
        return managersMobile;
    }

    private List<NurseBean> entitiesToBeans(Iterable<NurseEntity> entities) {
        List<NurseBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        for (NurseEntity tmp : entities) {
            NurseBean tmpBean = nurseBeanConverter.convert(tmp);
            beans.add(tmpBean);
        }
        return beans;
    }

    private void fillOtherProperties(List<NurseBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }
        List<Long> imageIds = new ArrayList<>();
        List<Long> nurseIds = new ArrayList<>();
        for (NurseBean bean :beans) {
            long nurseId = bean.getId();
            if (!nurseIds.contains(nurseId)) {
                nurseIds.add(nurseId);
            }
            long imageId = bean.getProfilePhotoId();
            if (!imageIds.contains(imageId)) {
                imageIds.add(imageId);
            }

            imageId = bean.getBackgroundImageId();
            if (!imageIds.contains(imageId)) {
                imageIds.add(imageId);
            }
        }

        Map<Long, String> imageId2Path = nursegoFileStorage.getFilePath(imageIds);
        Map<Long, NurseExtensionBean> nurseId2Extension = nurseExtensionService.getExtensionByNurseIds(nurseIds);
        Map<Long, NurseHospitalRelationBean> nurseId2Hospital = nurseHospitalRelationService.getRelationMapByNurseIds(nurseIds, utility.getHttpPrefixForNurseGo());
        Map<Long, NurseAuthorizationBean> nurseId2Authorization = nurseAuthorizationService.getAuthorizationByNurseIds(nurseIds);
        for (NurseBean bean : beans) {
            long nurseId = bean.getId();

            NurseExtensionBean extension = nurseId2Extension.get(nurseId);
            NurseHospitalRelationBean hospital = nurseId2Hospital.get(nurseId);
            NurseAuthorizationBean authorization = nurseId2Authorization.get(nurseId);

            bean.setProperty(NurseBean.INFO_EXTENSION, extension);
            bean.setProperty(NurseBean.HOSPITAL_DEPARTMENT, hospital);
            bean.setProperty(NurseBean.AUTHORIZATION, authorization);

            long imageId = bean.getProfilePhotoId();
            String imgPath = imageId2Path.get(imageId);
            if (!VerifyUtil.isStringEmpty(imgPath)) {
                bean.setProfilePhotoUrl(utility.getHttpPrefixForNurseGo()+imgPath);
            }

            imageId = bean.getBackgroundImageId();
            imgPath = imageId2Path.get(imageId);
            if (!VerifyUtil.isStringEmpty(imgPath)) {
                bean.setBackgroundImageUrl(utility.getHttpPrefixForNurseGo()+imgPath);
            }
        }
    }


    //===================================================================
    //                     editing
    //===================================================================
    @Transactional
    public NurseBean editNurse(long nurseId, String realName, int age, int gender,
                               String headName, InputStream head,
                               String backName, InputStream back) {
        logger.info("edit nurse360 realName={} age={} gender={}", realName, age, gender);
        GenderType genderType = GenderType.parseInt(gender);
        NurseEntity nurse = commonNurseService.updateBasicInfo(nurseId, null, age, genderType, null, null, null, realName, null, null);
        updateHeadPhoto(nurse, headName, head);
        updateBackgroundImage(nurse, backName, back);
        return getNurseById(nurseId);
    }

    @Transactional
    private String updateHeadPhoto(NurseEntity nurse, String fileName, InputStream inputStream){
        long fileId = 0;
        try {
            fileId = nursegoFileStorage.addFile(nurse.getProfilePhotoId(), fileName, inputStream);
            commonNurseService.updateHeadPhoto(nurse, fileId);
        }
        catch (BadRequestException ex) {
            logger.info("Delete file has exception throwing " + ex);
            if (ex.getErrorCode().equals(ErrorCode.NURSE360_FILE_OPERATION_FAILED)) {
                throw ex;
            }
        }
        if (fileId<0) {
            return "";
        }
        return nursegoFileStorage.getFilePath(fileId);
    }

    @Transactional
    private String updateBackgroundImage(NurseEntity nurse, String fileName, InputStream inputStream){
        long fileId = 0;
        try {
            fileId = nursegoFileStorage.addFile(nurse.getBackgroundImageId(), fileName, inputStream);
            commonNurseService.updateBackgroundImage(nurse, fileId);
        }
        catch (BadRequestException ex) {
            logger.info("Delete file has exception throwing " + ex);
            if (ex.getErrorCode().equals(ErrorCode.NURSE360_FILE_OPERATION_FAILED)) {
                throw ex;
            }
        }
        if (fileId<0) {
            return "";
        }
        return nursegoFileStorage.getFilePath(fileId);
    }

    @Transactional
    public NurseBean modifyMobile(long nurseId, String smsCode, String mobile) {
        logger.info("modify nurse={}'s mobile={} smsCode={}", nurseId, mobile, smsCode);
        if (!commonNurseService.existNurse(nurseId)) {
            logger.error("nurse not exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        leanCloudService.verifySmsCode(smsCode, mobile);

        NurseBean nurse = nurseBeanConverter.convert(commonNurseService.getNurseById(nurseId));
        if (!VerifyUtil.isStringEmpty(mobile) && !mobile.trim().equals(nurse.getMobile())) {
            commonNurseService.updateBasicInfo(nurseId, null, -1, null, mobile.trim(), null, null, null, null, null);
            nurse.setMobile(mobile.trim());
        }

        return nurse;
    }

    @Transactional
    public NurseBean modifyPassword(long nurseId, String password, String newPassword) {
        logger.info("modify nurse={}'s password={} newPassword={}", nurseId, password, newPassword);
        if (!commonNurseService.existNurse(nurseId)) {
            logger.error("nurse not exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        NurseBean nurse = nurseBeanConverter.convert(commonNurseService.getNurseById(nurseId));

        if (VerifyUtil.isStringEmpty(password)) {
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_IS_EMPTY);
        }
        if (VerifyUtil.isStringEmpty(newPassword)) {
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_IS_EMPTY);
        }
        if (!password.equals(nurse.getPassword())) {
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_NOT_EXPECTED);
        }
        if (newPassword.equals(password)) {
            logger.info("the new password is same with old password.");
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_NOT_EXPECTED);
        }

        commonNurseService.updateBasicInfo(nurseId, null, -1, null, null, newPassword.trim(), null, null, null, null);
        nurse.setMobile(newPassword.trim());

        return nurse;
    }

    @Transactional
    public NurseBean resetPassword(String smsCode, String mobile, String newPassword) {
        logger.info("reset password : mobile={} newPassword={} smsCode={}", mobile, newPassword, smsCode);
        List<NurseEntity> nurses= commonNurseService.getNurseByMobile(mobile);
        if (VerifyUtil.isListEmpty(nurses) || nurses.size()!=1) {
            logger.error("nurse not exist or mobile has more people--{}", nurses);
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        leanCloudService.verifySmsCode(smsCode, mobile);

        if (VerifyUtil.isStringEmpty(newPassword)) {
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_IS_EMPTY);
        }

        NurseBean nurse = nurseBeanConverter.convert(nurses.get(0));
        if (!newPassword.trim().equals(nurse.getPassword())) {
            commonNurseService.updateBasicInfo(nurse.getId(), null, -1, null, null, newPassword.trim(), null, null, null, null);
            nurse.setPassword(newPassword.trim());
        }

        return nurse;
    }


    //===================================================================
    //                     adding
    //===================================================================

    @Transactional
    public long addNurse(String realName, int age, int gender, String mobile, String password, String smsCode) {
        GenderType genderType = GenderType.parseInt(gender);
        genderType = null==genderType ? GenderType.SECRET : genderType;
        logger.info("register new nurse360 realName={} age={} gender={} mobile={} sms={} password={}",
                realName, age, gender, mobile, smsCode, password);

        leanCloudService.verifySmsCode(smsCode, mobile);

        if (VerifyUtil.isStringEmpty(mobile) || VerifyUtil.isStringEmpty(password)){
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_IS_EMPTY);
        }

        mobile = mobile.trim();
        password = password.trim();
        List<NurseEntity> nurses = commonNurseService.getNurseByMobile(mobile);
        if(!nurses.isEmpty()){
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_EXISTS_ALREADY);
        }

        NurseEntity entity = new NurseEntity();
        entity.setMobile(mobile);
        entity.setPassword(password);
        entity.setGender(genderType);
        entity.setRealName(realName);
        entity.setAge(age);
        entity.setAuthority(UserAuthority.AGREE_ALL);
        entity.setRegisterFrom(RegisterFrom.GO2NURSE);
        entity = commonNurseService.registerNurse(entity.getName(), entity.getAge(), entity.getGender(),
                entity.getMobile(), entity.getPassword(),
                entity.getIdentification(), entity.getRealName(), entity.getShortNote(), entity.getRegisterFrom());
        return entity.getId();
    }

    @Transactional
    public void setHospitalDepartmentAndJobTitle(long nurseId, int hospitalId, int departmentId, String jobTitle) {
        logger.info("nurse={} set hospital={} department={} and jobTitle={}",
                nurseId, hospitalId, departmentId, jobTitle);
        nurseHospitalRelationService.setRelation(nurseId, hospitalId, departmentId);
        nurseExtensionService.setExtension(nurseId, null, null, jobTitle, null, null, null);
    }
}
