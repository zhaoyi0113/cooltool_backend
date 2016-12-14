package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseExtensionBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.RegisterFrom;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.constants.UserType;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.converter.NurseBeanConverter;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.services.CommonNurseHospitalRelationService;
import com.cooltoo.services.CommonNurseService;
import com.cooltoo.services.NurseExtensionService;
import com.cooltoo.services.file.UserFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/8/12.
 */
@Service("NurseServiceForGo2Nurse")
public class NurseServiceForGo2Nurse {

    private static final Logger logger = LoggerFactory.getLogger(NurseServiceForGo2Nurse.class);

    @Autowired private CommonNurseHospitalRelationService nurseHospitalRelationService;
    @Autowired private NurseExtensionService nurseExtensionService;
    @Autowired private CommonNurseService commonNurseService;
    @Autowired private NurseBeanConverter nurseBeanConverter;
    @Autowired private UserFileStorageService nursegoFileStorage;
    @Autowired private Go2NurseUtility utility;
    @Autowired private NurseDoctorScoreService nurseDoctorScoreService;

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
        Float score = nurseDoctorScoreService.getScoreByReceiverTypeAndId(UserType.NURSE, nurseId);
        bean.setProperty(NurseBean.INFO_EXTENSION, extension);
        bean.setProperty(NurseBean.HOSPITAL_DEPARTMENT, hospitalDepartment);
        bean.setProperty(NurseBean.SCORE, null==score ? 0F : score);
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

    public String getNurseMobile(long nurseId) {
        String mobile = commonNurseService.getNurseMobile(nurseId);
        return mobile;
    }

    public List<Long> getManagerId(int hospitalId, int departmentId) {
        List<Long> managersIdInDepartId = commonNurseService.getManagersId(hospitalId, departmentId);
        return managersIdInDepartId;
    }

    public List<String> getManagerMobiles(int hospitalId, int departmentId) {
        List<String> managersMobileInDepart = commonNurseService.getManagersMobile(hospitalId, departmentId);
        return managersMobileInDepart;
    }

    public long countNurseByCanAnswerQuestion(String name, String strCanAnswerQuestion, String strCanSeeAllOrder, Integer hospitalId, Integer departmentId, RegisterFrom registerFrom) {
        logger.info("count nurse can answer questions");
        YesNoEnum canAnswerQuestion = YesNoEnum.parseString(strCanAnswerQuestion);
        YesNoEnum canSeeAllOrder = YesNoEnum.parseString(strCanSeeAllOrder);
        long count = commonNurseService.countByAuthorityAndFuzzyName(UserAuthority.AGREE_ALL, name, canAnswerQuestion, canSeeAllOrder, hospitalId, departmentId, registerFrom);
        logger.info("count is {}", count);
        return count;
    }

    public List<NurseBean> getNurseByCanAnswerQuestion(String name, String strCanAnswerQuestion, String strCanSeeAllOrder, Integer hospitalId, Integer departmentId, RegisterFrom registerFrom, int pageIndex, int sizePerPage) {
        logger.info("get nurse can answer questions at pageIndex={} sizePerPage={}", pageIndex, sizePerPage);
        YesNoEnum canAnswerQuestion = YesNoEnum.parseString(strCanAnswerQuestion);
        YesNoEnum canSeeAllOrder = YesNoEnum.parseString(strCanSeeAllOrder);
        Iterable<NurseEntity> nurses = commonNurseService.getNurseByAuthorityAndFuzzyName(UserAuthority.AGREE_ALL, name, canAnswerQuestion, canSeeAllOrder, hospitalId, departmentId, registerFrom, pageIndex, sizePerPage);
        List<NurseBean> beans = entitiesToBeans(nurses);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<NurseBean> getNurseByCanAnswerQuestion(String name, String strCanAnswerQuestion, String strCanSeeAllOrder, Integer hospitalId, Integer departmentId, RegisterFrom registerFrom) {
        logger.info("get nurse can answer questions");
        YesNoEnum canAnswerQuestion = YesNoEnum.parseString(strCanAnswerQuestion);
        YesNoEnum canSeeAllOrder = YesNoEnum.parseString(strCanSeeAllOrder);
        Iterable<NurseEntity> nurses = commonNurseService.getNurseByAuthorityAndFuzzyName(UserAuthority.AGREE_ALL, name, canAnswerQuestion, canSeeAllOrder, hospitalId, departmentId, registerFrom);
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
        Map<Long, Float> nurseId2Score = nurseDoctorScoreService.getScoreByReceiverTypeAndIds(UserType.NURSE, nurseIds);
        for (NurseBean bean : beans) {
            long nurseId = bean.getId();
            NurseExtensionBean extension = nurseId2Extension.get(nurseId);
            NurseHospitalRelationBean hospital = nurseId2Hospital.get(nurseId);
            Float score = nurseId2Score.get(nurseId);
            bean.setProperty(NurseBean.INFO_EXTENSION, extension);
            bean.setProperty(NurseBean.HOSPITAL_DEPARTMENT, hospital);
            bean.setProperty(NurseBean.SCORE, null==score ? 0F : score);

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
}
