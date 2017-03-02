package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.PatientSymptomsBean;
import com.cooltoo.go2nurse.converter.PatientSymptomsBeanConverter;
import com.cooltoo.go2nurse.entities.PatientSymptomsEntity;
import com.cooltoo.go2nurse.repository.PatientSymptomsRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.cooltoo.util.JSONUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

/**
 * Created by zhaolisong on 01/03/2017.
 */
@Service("PatientSymptomsService")
public class PatientSymptomsService {

    private static final Logger logger = LoggerFactory.getLogger(PatientSymptomsService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time")
    );

    @Autowired private UserService userService;
    @Autowired private PatientService patientService;
    @Autowired private UserPatientRelationService userPatientRelationService;
    @Autowired private PatientSymptomsBeanConverter beanConverter;
    @Autowired private PatientSymptomsRepository repository;

    @Autowired private UserGo2NurseFileStorageService userFileStorageService;


    //======================================================================
    //                   getter
    //======================================================================
    public Map<Long, PatientSymptomsBean> getPatientsSymptomsByOrderIds(List<Long> orderIds) {
        logger.info("get patient symptoms by orderIds={}", orderIds);
        Map<Long, PatientSymptomsBean> returnVal = new HashMap<>();
        if (VerifyUtil.isListEmpty(orderIds)) {
            return returnVal;
        }

        List<PatientSymptomsEntity> entities = repository.findByOrderIdIn(orderIds);
        List<PatientSymptomsBean> beans = entitiesToBeans(entities);
        if (VerifyUtil.isListEmpty(beans)) {
            return returnVal;
        }

        for (PatientSymptomsBean tmp : beans) {
            returnVal.put(tmp.getOrderId(), tmp);
        }

        logger.info("patient symptoms count is {}", returnVal.size());
        return returnVal;
    }

    public PatientSymptomsBean getLastPatientsSymptoms(long userId, long patientId) {
        logger.info("get last patient symptoms by userId={} patientId={}", userId, patientId);
        List<PatientSymptomsEntity> entities = repository.findByUserIdAndPatientId(userId, patientId, sort);
        if (VerifyUtil.isListEmpty(entities)) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        PatientSymptomsEntity entity = entities.get(0);
        PatientSymptomsBean bean = beanConverter.convert(entity);
        logger.info("last patient symptoms is {}", bean);
        return bean;
    }

    private List<PatientSymptomsBean> entitiesToBeans(List<PatientSymptomsEntity> entities) {
        List<PatientSymptomsBean> beans = new ArrayList<>();
        if (null==entities || entities.isEmpty()) {
            return beans;
        }
        for (PatientSymptomsEntity tmp : entities) {
            PatientSymptomsBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<PatientSymptomsBean> beans) {
    }

    //======================================================================
    //                   update
    //======================================================================
    @Transactional
    public PatientSymptomsBean updatePatientSymptoms(boolean checkUser, long userId,
                                                     long symptomsId,
                                                     String symptoms,
                                                     String symptomsDescription,
                                                     String questionnaire
    ) {
        logger.info("update patient symptoms by symptomsId={} symptoms={} symDesc={} symImgs={} quest={}",
                symptomsId, symptoms, symptomsDescription, questionnaire);
        PatientSymptomsEntity entity = repository.findOne(symptomsId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        if (checkUser) {
            if (userId!=entity.getUserId()) {
                logger.error("patient symptoms not belong to user");
                throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_NOT_EXPECTED);
            }
        }

        boolean change = false;
        if (!VerifyUtil.isStringEmpty(symptoms)) {
            entity.setSymptoms(symptoms);
            change = true;
        }
        if (!VerifyUtil.isStringEmpty(symptomsDescription)) {
            entity.setSymptomsDescription(symptomsDescription);
            change = true;
        }
        if (!VerifyUtil.isStringEmpty(questionnaire)) {
            entity.setQuestionnaire(questionnaire);
            change = true;
        }
        if (change) {
            entity = repository.save(entity);
        }

        PatientSymptomsBean bean = beanConverter.convert(entity);
        logger.info("patient symptoms is {}", bean);
        return bean;
    }

    @Transactional
    public PatientSymptomsBean bindWithOrder(boolean checkUser, long userId, long orderId, long symptomsId) {
        logger.info("bind patient symptoms with order by symptomsId={} orderId={}", symptomsId, orderId);
        PatientSymptomsEntity entity = repository.findOne(symptomsId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        if (checkUser) {
            if (userId!=entity.getUserId()) {
                logger.error("patient symptoms not belong to user");
                throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_NOT_EXPECTED);
            }
        }

        entity.setOrderId(orderId);
        entity = repository.save(entity);

        PatientSymptomsBean bean = beanConverter.convert(entity);
        logger.info("patient symptoms is {}", bean);
        return bean;
    }

    //======================================================================
    //                   add
    //======================================================================
    @Transactional
    public PatientSymptomsBean addSymptomsImage(boolean checkUser, long userId, long symptomsId, InputStream image) {
        logger.info("add image to symptoms={}", symptomsId);
        if (null==image) {
            logger.error("image is null");
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_NOT_EXPECTED);
        }
        if (!repository.exists(symptomsId)) {
            logger.error("patient symptoms is not found");
        }
        PatientSymptomsEntity entity = repository.getOne(symptomsId);
        if (checkUser) {
            if (userId!=entity.getUserId()) {
                logger.error("patient symptoms not belong to user");
                throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_NOT_EXPECTED);
            }
        }

        JSONUtil jsonUtil = JSONUtil.newInstance();
        String imagesUrls = entity.getSymptomsImages();
        List<String> listImages = null;
        if (null==imagesUrls || imagesUrls.trim().isEmpty()) {
            listImages = new ArrayList<>();
        }
        else {
            listImages = jsonUtil.parseJsonList(imagesUrls, String.class);
        }
        String newImagePath = (String) userFileStorageService.addFile(0, "imageInPatientSymptoms", image, false);
        listImages.add(newImagePath);

        entity.setSymptomsImages(jsonUtil.toJsonString(listImages));
        entity = repository.save(entity);


        PatientSymptomsBean bean = beanConverter.convert(entity);
        logger.info("patient symptoms is {}", bean);
        return bean;
    }

    @Transactional
    public PatientSymptomsBean addPatientSymptoms(long userId, long patientId,
                                                  String symptoms,
                                                  String symptomsDescription
    ) {
        logger.info("add patient symptoms by userId={} patientId={} symptoms={} symDesc={}",
                userId, patientId, symptoms, symptomsDescription);
        if (!userService.existUser(userId)) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        if (!patientService.existPatient(patientId)) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        if (!userPatientRelationService.existRelation(userId, patientId)) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        List<PatientSymptomsEntity> entities = repository.findByUserIdAndPatientId(userId, patientId, sort);
        List<PatientSymptomsEntity> unused = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(entities)) {
            for (PatientSymptomsEntity tmp : entities) {
                if (tmp.getOrderId()>0) {
                    continue;
                }
                unused.add(tmp);
            }
        }

        PatientSymptomsEntity entity = new PatientSymptomsEntity();
        if (!unused.isEmpty()) {
            deleteSymptomsImages(unused);
            entity = unused.remove(0);
            repository.delete(unused);
        }
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity.setUserId(userId);
        entity.setPatientId(patientId);
        entity.setSymptoms(symptoms);
        entity.setSymptomsDescription(null==symptomsDescription ? "" : symptomsDescription);
        entity.setSymptomsImages(null);
        entity.setQuestionnaire(null);

        entity = repository.save(entity);

        PatientSymptomsBean bean = beanConverter.convert(entity);
        logger.info("patient symptoms is {}", bean);
        return bean;
    }

    private void deleteSymptomsImages(List<PatientSymptomsEntity> entities) {
        if (null==entities || entities.isEmpty()) {
            return;
        }
        for (PatientSymptomsEntity tmp : entities) {
            String tmpImageUrls = tmp.getSymptomsImages();
            if (null==tmpImageUrls || tmpImageUrls.trim().isEmpty()) {
                continue;
            }
            List<String> tmpImageList = JSONUtil.newInstance().parseJsonList(tmpImageUrls, String.class);
            userFileStorageService.deleteFileByPaths(tmpImageList);
        }
    }

}
