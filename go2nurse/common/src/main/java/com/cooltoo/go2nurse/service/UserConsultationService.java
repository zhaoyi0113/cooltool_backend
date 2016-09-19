package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.DeviceType;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.converter.UserConsultationBeanConverter;
import com.cooltoo.go2nurse.entities.UserConsultationEntity;
import com.cooltoo.go2nurse.repository.UserConsultationRepository;
import com.cooltoo.go2nurse.service.notification.*;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.*;

/**
 * Created by hp on 2016/8/28.
 */
@Service("UserConsultationService")
public class UserConsultationService {

    private static final Logger logger = LoggerFactory.getLogger(UserConsultationService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private UserConsultationRepository repository;
    @Autowired private UserConsultationBeanConverter beanConverter;
    @Autowired private ImageInUserConsultationService imageService;
    @Autowired private UserConsultationTalkService talkService;

    @Autowired private UserService userService;
    @Autowired private PatientService patientService;
    @Autowired private ConsultationCategoryService categoryService;
    @Autowired private NurseServiceForGo2Nurse nurseService;

    @Autowired private Notifier notifier;

    //===============================================================
    //             get ----  admin using
    //===============================================================

    public long countUserConsultationByCondition(Long userId, Long patientId, Long nurseId, Long categoryId, String contentLike) {
        contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
        long count = repository.countByConditions(userId, patientId, nurseId, categoryId, contentLike);
        logger.info("count consultation user={} patientId={} nurseId={} categoryId={} contentLike={}, count is {}",
                userId, patientId, nurseId, categoryId, contentLike, count);
        return count;
    }

    public List<UserConsultationBean> getUserConsultationByCondition(Long userId, Long patientId, Long nurseId, Long categoryId, String contentLike, int pageIndex, int sizePerPage) {
        logger.info("get consultation user={} patientId={} nurseId={} categoryId={} contentLike={} at page={} sizePerPage={}",
                userId, patientId, nurseId, categoryId, contentLike, pageIndex, sizePerPage);
        List<UserConsultationBean> beans;
        contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
        PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
        Page<UserConsultationEntity> resultSet = repository.findByConditions(userId, patientId, nurseId, categoryId, contentLike, request);
        beans = entitiesToBeansForConsultation(resultSet);
        fillOtherPropertiesForConsultation(beans);

        logger.warn("consultation count={}", beans.size());
        return beans;
    }


    //===============================================================
    //             get ----  nurse using
    //===============================================================

    public List<UserConsultationBean> getUserConsultation(Long userId, String contentLike, int pageIndex, int sizePerPage) {
        logger.info("user={} get consultation (contentLike={}) at page={} sizePerPage={}",
                userId, contentLike, pageIndex, sizePerPage);
        List<UserConsultationBean> beans;
        if (null==userId && VerifyUtil.isStringEmpty(contentLike)) {
            beans = new ArrayList<>();
        }
        else {
            contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
            PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
            Page<UserConsultationEntity> resultSet = repository.findByUserIdAndStatusNotAndContentLike(userId, CommonStatus.DELETED, contentLike, request);
            beans = entitiesToBeansForConsultation(resultSet);
            fillOtherPropertiesForConsultation(beans);
        }
        logger.warn("speak count={}", beans.size());
        return beans;
    }

    public List<UserConsultationBean> getUserConsultation(Long userId, Long nurseId, int pageIndex, int sizePerPage) {
        logger.info("user={} get consultation nurseId={} at page={} sizePerPage={}", userId, nurseId, pageIndex, sizePerPage);
        List<UserConsultationBean> beans;
        PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
        Page<UserConsultationEntity> resultSet = repository.findByUserIdAndStatusNotAndNurseId(userId, CommonStatus.DELETED, nurseId, request);
        beans = entitiesToBeansForConsultation(resultSet);
        fillOtherPropertiesForConsultation(beans);
        logger.warn("speak count={}", beans.size());
        return beans;
    }

    public UserConsultationBean getUserConsultation(long consultationId) {
        logger.info("get consultationId={}", consultationId);
        UserConsultationEntity resultSet = repository.findOne(consultationId);
        if (null==resultSet) {
            logger.info("there is no record");
            return null;
        }
        List<UserConsultationEntity> entities = new ArrayList<>();
        entities.add(resultSet);

        List<UserConsultationBean> userConsultation = entitiesToBeansForConsultation(entities);
        fillOtherPropertiesForConsultation(userConsultation);
        return userConsultation.get(0);
    }

    public UserConsultationBean getUserConsultationWithTalk(Long consultationId) {
        logger.info("get consultation={} with talks", consultationId);
        UserConsultationBean consultation = getUserConsultation(consultationId);
        List<UserConsultationTalkBean> talks = getTalkByConsultationId(consultationId);
        consultation.setTalks(talks);
        return consultation;
    }

    public boolean existsConsultation(long consultationId) {
        return repository.exists(consultationId);
    }

    private List<UserConsultationBean> entitiesToBeansForConsultation(Iterable<UserConsultationEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<UserConsultationBean> beans = new ArrayList<>();
        for(UserConsultationEntity tmp : entities) {
            UserConsultationBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }

        return beans;
    }

    private void fillOtherPropertiesForConsultation(List<UserConsultationBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long> userIds = new ArrayList<>();
        List<Long> patientIds = new ArrayList<>();
        List<Long> nurseIds = new ArrayList<>();
        List<Long> categoryIds = new ArrayList<>();
        List<Long> consultationIds = new ArrayList<>();
        for (UserConsultationBean tmp : beans) {
            if (!userIds.contains(tmp.getUserId())) {
                userIds.add(tmp.getUserId());
            }
            if (!patientIds.contains(tmp.getPatientId())) {
                patientIds.add(tmp.getPatientId());
            }
            if (!categoryIds.contains(tmp.getCategoryId())) {
                categoryIds.add(tmp.getCategoryId());
            }
            if (!consultationIds.contains(tmp.getId())) {
                consultationIds.add(tmp.getId());
            }
            if (!nurseIds.contains(tmp.getNurseId())) {
                nurseIds.add(tmp.getNurseId());
            }
        }

        Map<Long, UserBean> userIdToBean = userService.getUserIdToBean(userIds);
        Map<Long, PatientBean> patientIdToBean = patientService.getPatientIdToBean(patientIds);
        Map<Long, NurseBean> nurseIdToBean = nurseService.getNurseIdToBean(nurseIds);
        Map<Long, ConsultationCategoryBean> categoryIdToBean = categoryService.getCategoryIdToBean(categoryIds);
        Map<Long, List<String>> consultationIdToImagesUrl = imageService.getConsultationIdToImagesUrl(consultationIds);

        // fill talk's nurse information
        nurseIds.clear();
        Map<Long, UserConsultationTalkBean> consultationIdToTalkBean = talkService.getOneTalkByConsultationIds(consultationIds);
        Collection<UserConsultationTalkBean> consultationTalks = consultationIdToTalkBean.values();
        for (UserConsultationTalkBean tmp : consultationTalks) {
            if (!nurseIds.contains(tmp.getNurseId())) {
                nurseIds.add(tmp.getNurseId());
            }
        }
        Map<Long, NurseBean> talkNurseIdToBean = nurseService.getNurseIdToBean(nurseIds);
        for (UserConsultationTalkBean tmp : consultationTalks) {
            NurseBean nurse = talkNurseIdToBean.get(tmp.getNurseId());
            tmp.setNurse(nurse);
        }

        // fill properties
        for (UserConsultationBean tmp : beans) {
            UserBean user = userIdToBean.get(tmp.getUserId());
            tmp.setUser(user);
            PatientBean patient = patientIdToBean.get(tmp.getPatientId());
            tmp.setPatient(patient);
            NurseBean nurse = nurseIdToBean.get(tmp.getNurseId());
            tmp.setNurse(nurse);
            ConsultationCategoryBean category = categoryIdToBean.get(tmp.getCategoryId());
            tmp.setCategory(category);
            List<String> imagesUrl = consultationIdToImagesUrl.get(tmp.getId());
            tmp.setImagesUrl(imagesUrl);

            UserConsultationTalkBean talk = consultationIdToTalkBean.get(tmp.getId());
            List<UserConsultationTalkBean> talkList = new ArrayList<>();
            if (null!=talk) {
                talkList.add(talk);
            }
            tmp.setTalks(talkList);
        }
    }


    //===============================================================
    //             update
    //===============================================================
    @Transactional
    public List<Long> deleteConsultationByIds(long userId, List<Long> consultationIds) {
        logger.info("delete consultation by consultationIds={}.", consultationIds);
        List<Long> retValue = new ArrayList<>();
        if (VerifyUtil.isListEmpty(consultationIds)) {
            return retValue;
        }

        List<UserConsultationEntity> consultations = repository.findAll(consultationIds);
        if (VerifyUtil.isListEmpty(consultations)) {
            logger.info("delete nothing");
            return retValue;
        }

        for (UserConsultationEntity tmp : consultations) {
            if (tmp.getUserId()==userId) {
                continue;
            }
            logger.warn("can not delete consultation that not making by yourself={}", tmp);
            return retValue;
        }

        for (UserConsultationEntity tmp : consultations) {
            tmp.setStatus(CommonStatus.DELETED);
            retValue.add(tmp.getId());
        }
        repository.save(consultations);


        return retValue;
    }

    public CommonStatus updateConsultationStatus(Long consultationId, Long categoryId, Long nurseId, CommonStatus status, YesNoEnum completed) {
        logger.info("update consultation={} with categoryId={} nurseId={} status={}",
                consultationId, categoryId, nurseId, status);
        UserConsultationEntity entity = repository.findOne(consultationId);
        if (null==entity) {
            logger.error("consultation is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;
        if (null!=status && status!=entity.getStatus()) {
            entity.setStatus(status);
            changed = true;
        }
        if (null!=completed && completed!=entity.getCompleted()) {
            entity.setCompleted(completed);
            changed = true;
        }
        if (null!=categoryId && categoryId!=entity.getCategoryId()) {
            entity.setCategoryId(categoryId);
            changed = true;
        }
//        // can not change nurseId
//        if (null!=nurseId && nurseId!=entity.getNurseId()) {
//            entity.setNurseId(nurseId);
//            changed = true;
//        }
        if (changed) {
            entity = repository.save(entity);
        }
        return entity.getStatus();
    }


    //===============================================================
    //             add
    //===============================================================
    @Transactional
    public long addConsultation(long categoryId, long nurseId, long userId, long patientId, String diseaseDescription, String clinicalHistory) {
        logger.info("add consultation categoryId={} nurseId={} userId={} patientId={} diseaseDescription={} clinicalHistory={}",
                categoryId, nurseId, userId, patientId, diseaseDescription, (null!=clinicalHistory));
        if (categoryId>0 && !categoryService.existCategory(categoryId)) {
            logger.info("category not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (nurseId>0 && !nurseService.existsNurse(nurseId)) {
            logger.info("nurse not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!userService.existUser(userId)) {
            logger.info("userId not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!patientService.existPatient(patientId)) {
            logger.info("patientId not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (VerifyUtil.isStringEmpty(diseaseDescription)) {
            logger.info("disease description empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        clinicalHistory = VerifyUtil.isStringEmpty(clinicalHistory) ? "" : clinicalHistory.trim();

        UserConsultationEntity entity = new UserConsultationEntity();
        entity.setCategoryId(categoryId<0 ? 0 : categoryId);
        entity.setNurseId(nurseId<0 ? 0 : nurseId);
        entity.setUserId(userId);
        entity.setPatientId(patientId);
        entity.setDiseaseDescription(diseaseDescription.trim());
        entity.setClinicalHistory(clinicalHistory);
        entity.setCompleted(YesNoEnum.NO);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = repository.save(entity);

        return entity.getId();
    }

    public Map<String, String> addConsultationImage(long userId, long consultationId, String imageName, InputStream image) {
        logger.info("user={} add image to consultation={} name={} image={}", userId, consultationId, imageName, (null!=image));

        // check speak
        UserConsultationEntity speakEntity   = repository.findOne(consultationId);
        if (null==speakEntity) {
            logger.error("consultation is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (userId!=speakEntity.getUserId()) {
            logger.error("user can not modify other's consultation");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        long count = imageService.countImage(consultationId, 0L);
        if (count>=9) {
            logger.warn("the consultation do not need image more than nine");
            return new HashMap<>();
        }

        Map<String, String> idAndUrl = imageService.addImage(consultationId, 0L, imageName, image);
        return idAndUrl;
    }

    //=================================================================================================================
    //*****************************************************************************************************************
    //                                          Talk Service
    //*****************************************************************************************************************
    //=================================================================================================================

    //=======================================
    //           getting
    //=======================================
    private List<UserConsultationTalkBean> getTalkByConsultationId(Long consultationId) {
        List<UserConsultationTalkBean> talksBean = talkService.getTalkByConsultationId(consultationId);
        fillOtherPropertiesForTalk(consultationId, talksBean);
        return talksBean;
    }

    private void fillOtherPropertiesForTalk(Long consultation, List<UserConsultationTalkBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long> nurseIds = new ArrayList<>();
        List<Long> consultationIds = new ArrayList<>();
        for (UserConsultationTalkBean tmp : beans) {
            nurseIds.add(tmp.getNurseId());
            consultationIds.add(tmp.getConsultationId());
        }

        Map<Long, NurseBean> userIdToBean = nurseService.getNurseIdToBean(nurseIds);
        Map<Long, List<String>> consultationIdToImagesUrl = imageService.getTalkIdToImagesUrl(consultation);

        for (UserConsultationTalkBean tmp : beans) {
            NurseBean user = userIdToBean.get(tmp.getNurseId());
            tmp.setNurse(user);
            List<String> imagesUrl = consultationIdToImagesUrl.get(tmp.getId());
            tmp.setImagesUrl(imagesUrl);
        }
    }



    //=======================================
    //           deleting
    //=======================================

    public List<Long> deleteTalk(List<Long> talkIds) {
        List<Long> comments = talkService.deleteByIds(talkIds);
        return comments;
    }

    //=======================================
    //           adding
    //=======================================
    public long addTalk(long consultationId, long nurseId, ConsultationTalkStatus talkStatus, String talkContent) {
        logger.info("add consultation talk, consultationId={} nurseId={} talkStatus={} talkContent={}.",
                consultationId, nurseId, talkStatus, talkContent);
        UserConsultationEntity consultation = repository.findOne(consultationId);
        if (null==consultation) {
            logger.error("consultation is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (0!=consultation.getNurseId() && nurseId!=consultation.getNurseId()) {
            logger.error("consultation not belong this nurse");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (YesNoEnum.YES.equals(consultation.getCompleted())) {
            logger.error("consultation has completed");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        long talkId = talkService.addConsultationTalk(consultationId, nurseId, talkStatus, talkContent);
        if (!ConsultationTalkStatus.USER_SPEAK.equals(talkStatus)) {
            MessageBean message = new MessageBean();
            message.setAlertBody("你有一条回复");
            message.setType(MessageType.CONSULTATION_TALK.name());
            message.setStatus(talkStatus.name());
            message.setRelativeId(consultationId);
            message.setDescription(talkContent);
            notifier.notifyUserPatient(consultation.getUserId(), message);
        }
        return talkId;
    }

    public long updateTalk(long talkId, YesNoEnum isBest) {
        logger.info("update consultation talkId={} isBest={}.", talkId, isBest);
        talkService.updateConsultationTalk(talkId, isBest);
        return talkId;
    }

    public Map<String, String> addTalkImage(long userId, long consultationId, long talkId, String imageName, InputStream image) {
        logger.info("user={} add image to consultation={} talkId={} name={} image={}", userId, consultationId, talkId, imageName, (null!=image));

        UserConsultationEntity consultation = repository.findOne(consultationId);
        if (null==consultation) {
            logger.error("consultation is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (YesNoEnum.YES.equals(consultation.getCompleted())) {
            logger.error("consultation has completed");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        UserConsultationTalkBean talk = talkService.getTalkWithoutInfoById(talkId);
        if (null==talk) {
            logger.error("talk is not exist");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (talk.getConsultationId() != consultationId) {
            logger.error("talk is not belong to consultation");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        long count = imageService.countImage(consultationId, talkId);
        if (count>=9) {
            logger.warn("the consultation do not need image more than nine");
            return new HashMap<>();
        }

        Map<String, String> idAndUrl = imageService.addImage(consultationId, talkId, imageName, image);
        return idAndUrl;
    }
}
