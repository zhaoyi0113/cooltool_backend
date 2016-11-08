package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.constants.UserType;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.ConsultationCreator;
import com.cooltoo.go2nurse.constants.ConsultationReason;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.constants.ReasonType;
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
    @Autowired private NurseDoctorScoreService nurseDoctorScoreService;

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
        fillOtherPropertiesForConsultation(beans, ConsultationTalkStatus.ADMIN_SPEAK);

        logger.warn("consultation count={}", beans.size());
        return beans;
    }


    //===============================================================
    //             get ----  patient using
    //===============================================================

    public List<UserConsultationBean> getUserConsultation(Long userId, Long nurseId, Long categoryId, String contentLike, ConsultationReason reason, int pageIndex, int sizePerPage, ConsultationTalkStatus talkStatus) {
        logger.info("user={} nusre={} get consultation (contentLike={}) categoryId={} at page={} sizePerPage={}",
                userId, nurseId, contentLike, categoryId, pageIndex, sizePerPage);
        List<UserConsultationBean> beans;
        if (null==userId && VerifyUtil.isStringEmpty(contentLike)) {
            beans = new ArrayList<>();
        }
        else {
            contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
            PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
            Page<UserConsultationEntity> resultSet = repository.findByUserNurseStatusNotAndContentLike(userId, nurseId, categoryId, CommonStatus.DELETED, contentLike, reason, request);
            beans = entitiesToBeansForConsultation(resultSet);
            fillOtherPropertiesForConsultation(beans, talkStatus);
        }
        logger.warn("speak count={}", beans.size());
        return beans;
    }

    public List<UserConsultationBean> getUserConsultation(Long userId, Long nurseId, ConsultationReason reason, int pageIndex, int sizePerPage, ConsultationTalkStatus talkStatus) {
        logger.info("user={} get consultation nurseId={} at page={} sizePerPage={}", userId, nurseId, pageIndex, sizePerPage);
        List<UserConsultationBean> beans;
        PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
        Page<UserConsultationEntity> resultSet = repository.findByUserIdAndStatusNotAndNurseId(userId, CommonStatus.DELETED, nurseId, reason, request);
        beans = entitiesToBeansForConsultation(resultSet);
        fillOtherPropertiesForConsultation(beans, talkStatus);
        logger.warn("speak count={}", beans.size());
        return beans;
    }

    public UserConsultationBean getUserConsultation(long consultationId, ConsultationTalkStatus talkStatus) {
        logger.info("get consultationId={}", consultationId);
        UserConsultationEntity resultSet = repository.findOne(consultationId);
        if (null==resultSet) {
            logger.info("there is no record");
            return null;
        }
        List<UserConsultationEntity> entities = new ArrayList<>();
        entities.add(resultSet);

        List<UserConsultationBean> userConsultation = entitiesToBeansForConsultation(entities);
        fillOtherPropertiesForConsultation(userConsultation, talkStatus);
        return userConsultation.get(0);
    }

    public Map<Long, UserConsultationBean> getUserConsultationIdToBean(List<Long> consultationIds, ConsultationTalkStatus talkStatusNotMatch) {
        Map<Long, UserConsultationBean> map = new HashMap<>();
        if (!VerifyUtil.isListEmpty(consultationIds)) {
            List<UserConsultationEntity> entities = repository.findAll(consultationIds);
            List<UserConsultationBean> beans = entitiesToBeansForConsultation(entities);
            fillOtherPropertiesForConsultation(beans, talkStatusNotMatch);
            for (UserConsultationBean tmp : beans) {
                map.put(tmp.getId(), tmp);
            }
        }
        return map;
    }

    public UserConsultationBean getUserConsultationWithTalk(Long consultationId, ConsultationTalkStatus talkStatus) {
        logger.info("get consultation={} with talks", consultationId);
        UserConsultationBean consultation = getUserConsultation(consultationId, talkStatus);
        if (null!=consultation) {
            List<UserConsultationTalkBean> talks = getTalkByConsultationId(consultationId);
            consultation.setTalks(talks);
        }
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

    private void fillOtherPropertiesForConsultation(List<UserConsultationBean> beans, ConsultationTalkStatus talkStatusNotMatch) {
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
        Map<Long, Long> consultationIdToUnreadTalkSize = talkService.getUnreadTalkSizeByConsultationIds(consultationIds, talkStatusNotMatch);

        // fill talk's nurse information
        nurseIds.clear();
        Map<Long, UserConsultationTalkBean> consultationIdToTalkBean = talkService.getBestTalkByConsultationIds(consultationIds);
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

            Long unreadTalkSize = consultationIdToUnreadTalkSize.get(tmp.getId());
            tmp.setHasUnreadTalk(null==unreadTalkSize ? false : (unreadTalkSize>0));
        }
    }

    private void fillOtherPropertiesForSingleConsultation(UserConsultationBean consultation) {
        if (null==consultation) {
            return;
        }

        Long userId = consultation.getUserId();
        Long patientId = consultation.getPatientId();
        long nurseId = consultation.getNurseId();
        Long categoryId = consultation.getCategoryId();
        Long consultationId = consultation.getId();

        UserBean user = userService.getUser(userId);
        PatientBean patient = patientService.getOneById(patientId);
        NurseBean nurse = nurseId<=0 ? null : nurseService.getNurseById(nurseId);
        ConsultationCategoryBean category = categoryService.getCategoryById(categoryId);
        Map<Long, List<String>> consultationIdToImagesUrl = imageService.getConsultationIdToImagesUrl(Arrays.asList(new Long[]{consultationId}));

        // fill properties
        consultation.setUser(user);
        consultation.setPatient(patient);
        consultation.setNurse(nurse);
        consultation.setCategory(category);
        List<String> imagesUrl = consultationIdToImagesUrl.get(consultation.getId());
        consultation.setImagesUrl(imagesUrl);
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

    @Transactional
    public UserConsultationBean updateConsultationStatus(Long userId, Long consultationId, Long categoryId, Long nurseId, CommonStatus status, YesNoEnum completed) {
        logger.info("update consultation={} with categoryId={} nurseId={} status={} userId={}",
                consultationId, categoryId, nurseId, status, userId);
        UserConsultationEntity entity = repository.findOne(consultationId);
        if (null==entity) {
            logger.error("consultation is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (null!=userId && userId>0 && entity.getUserId()!=userId) {
            logger.info("consultation not belong to user");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
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
        UserConsultationBean bean = beanConverter.convert(entity);
        fillOtherPropertiesForSingleConsultation(bean);
        return bean;
    }

    @Transactional
    public UserConsultationBean scoreConsultation(Long userId, Long nurseId, Long consultationId, float score) {
        logger.info("score consultation={} with score={} userId={}",
                consultationId, score, userId);
        UserConsultationEntity entity = repository.findOne(consultationId);
        if (null==entity) {
            logger.error("consultation is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (null!=userId && userId>0 && entity.getUserId()!=userId) {
            logger.info("consultation not belong to user");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        score = score<0 ? 0 : score;

        entity.setScore(score);
        entity = repository.save(entity);

        if (nurseService.existsNurse(nurseId) && score>0) {
            nurseDoctorScoreService.addScore(UserType.NURSE, nurseId, entity.getUserId(), ReasonType.CONSULTATION, consultationId, score, 1/*weight*/);
        }

        UserConsultationBean bean = beanConverter.convert(entity);
        fillOtherPropertiesForSingleConsultation(bean);
        return bean;
    }


    //===============================================================
    //             add
    //===============================================================
    @Transactional
    public long addConsultation(long categoryId, long nurseId, long userId, long patientId,
                                String diseaseDescription, String clinicalHistory,
                                ConsultationCreator creator,
                                ConsultationReason reason
    ) {
        logger.info("add consultation categoryId={} nurseId={} userId={} patientId={} diseaseDescription={} clinicalHistory={} creator={} reason={}",
                categoryId, nurseId, userId, patientId, diseaseDescription, (null!=clinicalHistory), creator, reason);
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
        if (patientId>0 && !patientService.existPatient(patientId)) {
            logger.info("patientId not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (VerifyUtil.isStringEmpty(diseaseDescription)) {
            logger.info("disease description empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        categoryId = categoryId<0 ? 0 : categoryId;
        patientId  = patientId<0  ? 0 : patientId;
        nurseId    = nurseId<0    ? 0 : nurseId;
        clinicalHistory = VerifyUtil.isStringEmpty(clinicalHistory) ? "" : clinicalHistory.trim();

        UserConsultationEntity entity = new UserConsultationEntity();
        entity.setCategoryId(categoryId<0 ? 0 : categoryId);
        entity.setNurseId(nurseId<0 ? 0 : nurseId);
        entity.setUserId(userId);
        entity.setPatientId(patientId);
        entity.setDiseaseDescription(diseaseDescription.trim());
        entity.setClinicalHistory(clinicalHistory);
        entity.setCompleted(YesNoEnum.NO);
        entity.setCreator(creator);
        entity.setReason(reason);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = repository.save(entity);

        return entity.getId();
    }

    @Transactional
    public Map<String, String> addConsultationImage(long userId, long nurseId, long consultationId, String imageName, InputStream image) {
        logger.info("user={} add image to consultation={} name={} image={}", userId, consultationId, imageName, (null!=image));

        // check speak
        UserConsultationEntity speakEntity   = repository.findOne(consultationId);
        if (null==speakEntity) {
            logger.error("consultation is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (ConsultationCreator.USER.equals(speakEntity.getCreator())
                && userId!=speakEntity.getUserId()) {
            logger.error("user can not modify other's consultation");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (ConsultationCreator.NURSE.equals(speakEntity.getCreator())
                && nurseId!=speakEntity.getNurseId()) {
            logger.error("nurse can not modify other's consultation");
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
    public UserConsultationTalkBean getTalkById(Long talkId) {
        UserConsultationTalkBean talkBean = talkService.getTalkWithoutInfoById(talkId);
        if (null!=talkBean) {
            List<UserConsultationTalkBean> talkBeans = Arrays.asList(new UserConsultationTalkBean[]{talkBean});
            fillOtherPropertiesForTalk(talkBean.getConsultationId(), talkBeans);
        }
        return talkBean;
    }

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
    @Transactional
    public List<Long> deleteTalk(List<Long> talkIds) {
        List<Long> comments = talkService.deleteByIds(talkIds);
        return comments;
    }

    //=======================================
    //           adding
    //=======================================
    @Transactional
    public long addTalk(long consultationId, long nurseId, ConsultationTalkStatus talkStatus, String talkContent) {
        logger.info("add consultation talk, consultationId={} nurseId={} talkStatus={} talkContent={}.",
                consultationId, nurseId, talkStatus, talkContent);
        UserConsultationEntity consultation = repository.findOne(consultationId);
        if (null==consultation) {
            logger.error("consultation is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (nurseId>0 && 0!=consultation.getNurseId() && nurseId!=consultation.getNurseId()) {
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

    //=======================================
    //           updating
    //=======================================
    @Transactional
    public long updateTalk(long talkId, YesNoEnum isBest) {
        logger.info("update consultation talkId={} isBest={}.", talkId, isBest);
        talkService.updateConsultationTalk(talkId, isBest);
        return talkId;
    }

    @Transactional
    public long updateConsultationUnreadTalkStatusToRead(long consultationId, ConsultationTalkStatus talkStatusNotMatch) {
        logger.info("update consultation(Id={}) talkStatusNotMatch={} talks' readingStatus={}.",
                consultationId, talkStatusNotMatch, ReadingStatus.READ);
        talkService.updateConsultationUnreadTalkStatusToRead(consultationId, talkStatusNotMatch);
        return consultationId;
    }

    @Transactional
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
