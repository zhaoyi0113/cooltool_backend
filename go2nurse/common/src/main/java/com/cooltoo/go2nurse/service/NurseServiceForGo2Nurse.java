package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseExtensionBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.UserAuthority;
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
        bean.setProperty(NurseBean.INFO_EXTENSION, extension);
        bean.setProperty(NurseBean.HOSPITAL_DEPARTMENT, hospitalDepartment);
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

    public List<NurseBean> getNurseByCanAnswerQuestion(String name, String strCanAnswerQuestion, Integer hospitalId, Integer departmentId, int pageIndex, int sizePerPage) {
        logger.info("get nurse can answer questions at pageIndex={} sizePerPage={}", pageIndex, sizePerPage);
        YesNoEnum canAnswerQuestion = YesNoEnum.parseString(strCanAnswerQuestion);
        Iterable<NurseEntity> nurses = commonNurseService.getNurseByAuthorityAndFuzzyName(UserAuthority.AGREE_ALL, name, canAnswerQuestion, hospitalId, departmentId, pageIndex, sizePerPage);
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
}
