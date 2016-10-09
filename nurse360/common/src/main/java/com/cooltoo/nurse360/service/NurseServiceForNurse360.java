package com.cooltoo.nurse360.service;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseExtensionBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.*;
import com.cooltoo.converter.NurseBeanConverter;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.leancloud.LeanCloudService;
import com.cooltoo.nurse360.util.Nurse360Utility;
import com.cooltoo.services.CommonNurseHospitalRelationService;
import com.cooltoo.services.CommonNurseService;
import com.cooltoo.services.NurseExtensionService;
import com.cooltoo.services.file.UserFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
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
    @Autowired private NurseOrderRelationServiceForNurse360 nurseOrderService;

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
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        NurseBean bean = nurseBeanConverter.convert(nurse);
        String profilePath = nursegoFileStorage.getFilePath(bean.getProfilePhotoId());
        String backgroundPath = nursegoFileStorage.getFilePath(bean.getBackgroundImageId());
        bean.setProfilePhotoUrl(utility.getHttpPrefixForNurseGo()+profilePath);
        bean.setBackgroundImageUrl(utility.getHttpPrefixForNurseGo()+backgroundPath);
        NurseExtensionBean extension = nurseExtensionService.getExtensionByNurseId(nurseId);
        NurseHospitalRelationBean hospitalDepartment = nurseHospitalRelationService.getRelationByNurseId(nurseId, utility.getHttpPrefixForNurseGo());
        List<ServiceOrderBean> orders = nurseOrderService.getOrderByNurseIdAndOrderStatus(nurseId, CommonStatus.ENABLED.name(), OrderStatus.IN_PROCESS);
        bean.setProperty(NurseBean.INFO_EXTENSION, extension);
        bean.setProperty(NurseBean.HOSPITAL_DEPARTMENT, hospitalDepartment);
        bean.setProperty(NurseBean.ORDER, orders);
        return bean;
    }

    public Map<Long, NurseBean> getNurseIdToBean(List<Long> nurseIds) {
        logger.info("get nurse by nurseId={}", nurseIds);
        List<NurseEntity> nurses = commonNurseService.getNurseByIds(nurseIds);
        List<NurseBean> beans = entitiesToBeans(nurses);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        Map<Long, NurseBean> nurseIdToBean = new HashMap<>();
        for (NurseBean tmp : beans) {
            nurseIdToBean.put(tmp.getId(), tmp);
        }
        return nurseIdToBean;
    }

    public List<NurseBean> getNurseByCanAnswerQuestion(String name, String strCanAnswerQuestion, Integer hospitalId, Integer departmentId, RegisterFrom registerFrom, int pageIndex, int sizePerPage) {
        logger.info("get nurse can answer questions at pageIndex={} sizePerPage={}", pageIndex, sizePerPage);
        YesNoEnum canAnswerQuestion = YesNoEnum.parseString(strCanAnswerQuestion);
        Iterable<NurseEntity> nurses = commonNurseService.getNurseByAuthorityAndFuzzyName(UserAuthority.AGREE_ALL, name, canAnswerQuestion, hospitalId, departmentId, registerFrom, pageIndex, sizePerPage);
        List<NurseBean> beans = entitiesToBeans(nurses);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<NurseBean> getNurseByQueryString(String strCanAnswerQuestion, String name, int pageIndex, int sizePerPage) {
        logger.info("get nurse can answer questions at pageIndex={} sizePerPage={}", pageIndex, sizePerPage);
        YesNoEnum canAnswerQuestion = YesNoEnum.parseString(strCanAnswerQuestion);
        Iterable<NurseEntity> nurses = commonNurseService.getNurseByQueryString(UserAuthority.AGREE_ALL, canAnswerQuestion, name, name, name, pageIndex, sizePerPage);
        List<NurseBean> beans = entitiesToBeans(nurses);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
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
        for (NurseBean bean : beans) {
            long nurseId = bean.getId();
            NurseExtensionBean extension = nurseId2Extension.get(nurseId);
            NurseHospitalRelationBean hospital = nurseId2Hospital.get(nurseId);
            bean.setProperty(NurseBean.INFO_EXTENSION, extension);
            bean.setProperty(NurseBean.HOSPITAL_DEPARTMENT, hospital);

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
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        mobile = mobile.trim();
        password = password.trim();
        List<NurseEntity> nurses = commonNurseService.getNurseByMobile(mobile);
        if(!nurses.isEmpty()){
            throw new BadRequestException(ErrorCode.NURSE_ALREADY_EXISTED);
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
        nurseExtensionService.setExtension(nurseId, null, null, jobTitle, null);
    }
}
