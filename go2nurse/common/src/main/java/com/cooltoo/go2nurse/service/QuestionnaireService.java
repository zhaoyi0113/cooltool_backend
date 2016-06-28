package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.QuestionBean;
import com.cooltoo.go2nurse.beans.QuestionnaireBean;
import com.cooltoo.go2nurse.constants.QuestionType;
import com.cooltoo.go2nurse.converter.QuestionBeanConverter;
import com.cooltoo.go2nurse.converter.QuestionnaireBeanConverter;
import com.cooltoo.go2nurse.entities.QuestionEntity;
import com.cooltoo.go2nurse.entities.QuestionnaireEntity;
import com.cooltoo.go2nurse.repository.QuestionRepository;
import com.cooltoo.go2nurse.repository.QuestionnaireRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhaolisong on 16/4/6.
 */
@Service("QuestionnaireService")
public class QuestionnaireService {
    private static final Logger logger = LoggerFactory.getLogger(QuestionnaireService.class.getName());

    private static final Sort questionSort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    private static final Sort questionnaireSort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time_created")
    );

    @Autowired private QuestionRepository questionRep;
    @Autowired private QuestionnaireRepository questionnaireRep;
    @Autowired private QuestionBeanConverter questionConverter;
    @Autowired private QuestionnaireBeanConverter questionnaireConverter;

    //=================================================================
    //         getter
    //=================================================================

    public long getQuestionCount() {
        long count = questionRep.count();
        logger.info("question count is {}", count);
        return count;
    }

    public List<QuestionBean> getQuestionByPage(int pageIndex, int sizeOfPage) {
        logger.info("get question by pageIndex={} sizePerPage={}", pageIndex, sizeOfPage);
        PageRequest page = new PageRequest(pageIndex, sizeOfPage, questionSort);
        Page<QuestionEntity> resultSet = questionRep.findAll(page);
        List<QuestionBean> beans = questionEntitiesToBeans(resultSet);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public QuestionBean getQuestion(long questionId) {
        logger.info("get question by id {}", questionId);
        QuestionEntity tag = questionRep.findOne(questionId);
        if (null==tag) {
            logger.info("get question by id, doesn't exist!");
            return null;
        }
        QuestionBean tagB = questionConverter.convert(tag);
        return tagB;
    }

    public List<QuestionBean> getQuestionByIds(String strQuestionIds) {
        logger.info("get question by ids={}", strQuestionIds);

        List<QuestionBean> beans;
        if (VerifyUtil.isIds(strQuestionIds)) {
            List<Long> questionIds = VerifyUtil.parseLongIds(strQuestionIds);
            List<QuestionEntity> resultSet = questionRep.findByIdIn(questionIds, questionSort);
            beans = questionEntitiesToBeans(resultSet);
        }
        else {
            beans = new ArrayList<>();
        }
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<QuestionBean> getQuestionByQuestionnaireId(long questionnaireId) {
        logger.info("get question by questionnaireId={}", questionnaireId);
        List<QuestionEntity> resultSet = questionRep.findByQuestionnaireId(questionnaireId, questionSort);
        List<QuestionBean> beans = questionEntitiesToBeans(resultSet);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public long getQuestionnaireCount() {
        long count = questionnaireRep.count();
        logger.info("questionnaire count is {}", count);
        return count;
    }

    public List<QuestionnaireBean> getQuestionnaireByPage(int pageIndex, int sizeOfPage) {
        // get all questionnaire
        logger.info("get questionnaire at pageIndex={} sizePerPage={}", pageIndex, sizeOfPage);
        PageRequest pageCategory = new PageRequest(pageIndex, sizeOfPage, questionnaireSort);
        Page<QuestionnaireEntity> resultSet = questionnaireRep.findAll(pageCategory);
        List<QuestionnaireBean> beans = questionnaireEntitiesToBeans(resultSet);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public QuestionnaireBean getQuestionnaire(long questionnaireId) {
        logger.info("get questionnaire by id={}", questionnaireId);
        QuestionnaireEntity entity = questionnaireRep.findOne(questionnaireId);
        if (null==entity) {
            logger.info("get question by id, doesn't exist!");
            return null;
        }

        QuestionnaireBean bean = questionnaireConverter.convert(entity);
        return bean;
    }

    public List<QuestionnaireBean> getQuestionnaireByIds(String strQuestionnaireIds) {
        logger.info("get questionnaire by ids={}", strQuestionnaireIds);
        List<Long> questionnaireIds = VerifyUtil.parseLongIds(strQuestionnaireIds);
        List<QuestionnaireBean> beans = getQuestionnaireByIds(questionnaireIds);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<QuestionnaireBean> getQuestionnaireByIds(List<Long> questionnaireIds) {
        logger.info("get questionnaire by ids={}", questionnaireIds);
        List<QuestionnaireBean> beans;
        if (!VerifyUtil.isListEmpty(questionnaireIds)) {
            List<QuestionnaireEntity> resultSet = questionnaireRep.findByIdIn(questionnaireIds, questionnaireSort);
            beans = questionnaireEntitiesToBeans(resultSet);
        }
        else {
            beans = new ArrayList<>();
        }
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<QuestionnaireBean> getQuestionnaireByHospitalId(int hospitalId) {
        logger.info("get questionnaire by hospitalId={}", hospitalId);
        List<QuestionnaireEntity> resultSet = questionnaireRep.findByHospitalId(hospitalId, questionnaireSort);
        List<QuestionnaireBean> beans = questionnaireEntitiesToBeans(resultSet);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public QuestionnaireBean getQuestionnaireWithQuestions(long questionnaireId) {
        logger.info("get questionnaire with questions by questionnaireId={}", questionnaireId);
        QuestionnaireEntity entity = questionnaireRep.findOne(questionnaireId);
        if (null==entity) {
            return null;
        }

        QuestionnaireBean bean = questionnaireConverter.convert(entity);
        List<QuestionBean> questions = getQuestionByQuestionnaireId(questionnaireId);
        bean.setQuestions(questions);
        logger.info("questions count in questionnaire is {}", questions.size());
        return bean;
    }

    public List<QuestionnaireBean> getQuestionnaireWithQuestionsByIds(String strQuestionnaireIds) {
        logger.info("get questionnaire with questions by questionnaireIds={}", strQuestionnaireIds);
        List<QuestionnaireBean> questionnaires;
        if (VerifyUtil.isIds(strQuestionnaireIds)) {
            List<Long> questionnaireIds = VerifyUtil.parseLongIds(strQuestionnaireIds);
            questionnaires = getQuestionnaireWithQuestionsByIds(questionnaireIds);
        }
        else {
            questionnaires = new ArrayList<>();
        }
        logger.info("get questionnaire with question count is {}", questionnaires.size());
        return new ArrayList<>();
    }

    public List<QuestionnaireBean> getQuestionnaireWithQuestionsByIds(List<Long> questionnaireIds) {
        logger.info("get questionnaire with questions by questionnaireIds={}", questionnaireIds);
        List<QuestionnaireBean> questionnaires;
        if (!VerifyUtil.isListEmpty(questionnaireIds)) {
            List<QuestionnaireEntity> questionnaireResultSet = questionnaireRep.findByIdIn(questionnaireIds, questionnaireSort);
            List<QuestionEntity> questionResultSet = questionRep.findByQuestionnaireIdIn(questionnaireIds, questionSort);
            questionnaires = questionnaireEntitiesToBeans(questionnaireResultSet);
            List<QuestionBean> questions = questionEntitiesToBeans(questionResultSet);

            List<QuestionBean> beans;
            for (QuestionnaireBean questionnaire : questionnaires) {
                beans = new ArrayList<>();
                for (QuestionBean question : questions) {
                    if (questionnaire.getId() == question.getQuestionnaireId()) {
                        beans.add(question);
                    }
                }
                questionnaire.setQuestions(beans);
            }
        }
        else {
            questionnaires = new ArrayList<>();
        }
        logger.info("get questionnaire with question count is {}", questionnaires.size());
        return new ArrayList<>();
    }

    private List<QuestionBean> questionEntitiesToBeans(Iterable<QuestionEntity> entities) {
        List<QuestionBean> beans = new ArrayList<>();
        if (null!=entities) {
            for (QuestionEntity entity : entities) {
                QuestionBean bean = questionConverter.convert(entity);
                beans.add(bean);
            }
        }
        return beans;
    }

    private List<QuestionnaireBean> questionnaireEntitiesToBeans(Iterable<QuestionnaireEntity> entities) {
        List<QuestionnaireBean> beans = new ArrayList<>();
        if (null!=entities) {
            for (QuestionnaireEntity entity : entities) {
                QuestionnaireBean bean = questionnaireConverter.convert(entity);
                beans.add(bean);
            }
        }
        return beans;
    }

    //=================================================================
    //         update
    //=================================================================

    @Transactional
    public QuestionBean updateQuestion(long questionId, long questionnaireId, String content, String options, String strType) {
        logger.info("update question={} with questionnaireId={} content={} options={} type={}",
                questionId, questionnaireId, content, options, strType);
        boolean changed = false;
        QuestionEntity entity = questionRep.findOne(questionId);
        if (null==entity) {
            logger.info("question not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (questionnaireRep.exists(questionnaireId)) {
            entity.setQuestionnaireId(questionnaireId);
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(content) && !content.equals(entity.getContent())) {
            entity.setContent(content);
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(options) && !options.equals(entity.getOptions())) {
            entity.setOptions(options);
            changed = true;
        }
        QuestionType type = QuestionType.parseString(strType);
        if (null!=type && !type.equals(entity.getType())) {
            entity.setType(type);
            changed = true;
        }

        if (changed) {
            entity = questionRep.save(entity);
        }
        return questionConverter.convert(entity);
    }

    @Transactional
    public QuestionnaireBean updateQuestionnaire(long questionnaireId, String title, String description, int hospitalId) {
        logger.info("update questionnaire={} with title={} description={} hospitalId={}",
                questionnaireId, title, description, hospitalId);
        boolean changed = false;

        QuestionnaireEntity entity = questionnaireRep.findOne(questionnaireId);
        if (null==entity) {
            logger.info("questionnaire not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!VerifyUtil.isStringEmpty(title) && !title.equals(entity.getTitle())) {
            entity.setTitle(title);
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(description) && !description.equals(entity.getDescription())) {
            entity.setDescription(description);
            changed = true;
        }
        if (hospitalId>0 && hospitalId!=entity.getHospitalId()) {
            entity.setHospitalId(hospitalId);
            changed = true;
        }

        if (changed) {
            entity = questionnaireRep.save(entity);
        }
        return questionnaireConverter.convert(entity);
    }

    //=================================================================
    //         delete
    //=================================================================

    @Transactional
    public long deleteQuestion(long questionId) {
        logger.info("delete question by questionId={}", questionId);
        QuestionEntity entity = questionRep.findOne(questionId);
        if (null!=entity) {
            questionRep.delete(entity);
            return questionId;
        }
        throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
    }

    @Transactional
    public String deleteQuestionByIds(String strQuestionIds) {
        logger.info("delete question by questionIds={}", strQuestionIds);
        if (VerifyUtil.isIds(strQuestionIds)) {
            List<Long> questionIds = VerifyUtil.parseLongIds(strQuestionIds);
            List<QuestionEntity> entities = questionRep.findByIdIn(questionIds, questionSort);
            questionRep.delete(entities);
        }
        throw new BadRequestException(ErrorCode.DATA_ERROR);
    }

    @Transactional
    public long deleteQuestionnaire(long questionnaireId) {
        logger.info("delete questionnaire by questionnaireId={}", questionnaireId);
        QuestionnaireEntity questionnaire = questionnaireRep.findOne(questionnaireId);
        if (null!=questionnaire) {
            // set questionnaire id = 0
            List<QuestionEntity> questions = questionRep.findByQuestionnaireId(questionnaireId, questionSort);
            if (null!=questions) {
                for (QuestionEntity tagE : questions) {
                    tagE.setQuestionnaireId(0);
                }
                questionRep.save(questions);
            }
            // delete questionnaire
            questionnaireRep.delete(questionnaire);
            return questionnaireId;
        }
        throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
    }

    @Transactional
    public String deleteQuestionnaireByIds(String strQuestionnaireIds) {
        logger.info("delete questionnaire by questionnaireIds={}", strQuestionnaireIds);
        if (VerifyUtil.isIds(strQuestionnaireIds)) {
            List<Long> questionnaireIds   = VerifyUtil.parseLongIds(strQuestionnaireIds);
            // set questionnaire id = 0
            List<QuestionEntity> questions = questionRep.findByQuestionnaireIdIn(questionnaireIds, questionSort);
            if (null!=questions) {
                for (QuestionEntity tagE : questions) {
                    tagE.setQuestionnaireId(0);
                }
                questionRep.save(questions);
            }
            // delete questionnaire
            List<QuestionnaireEntity> questionnaires = questionnaireRep.findByIdIn(questionnaireIds, questionnaireSort);
            questionnaireRep.delete(questionnaires);

            return strQuestionnaireIds;
        }
        throw new BadRequestException(ErrorCode.DATA_ERROR);
    }

    @Transactional
    public String deleteQuestionnaireAndQuestionByIds(String strQuestionnaireIds) {
        logger.info("delete questionnaire and questions by questionnaireIds={}", strQuestionnaireIds);
        if (VerifyUtil.isIds(strQuestionnaireIds)) {
            List<Long> questionnaireIds = VerifyUtil.parseLongIds(strQuestionnaireIds);

            List<QuestionnaireEntity> questionnaires = questionnaireRep.findByIdIn(questionnaireIds, questionnaireSort);
            List<QuestionEntity> questions = questionRep.findByQuestionnaireIdIn(questionnaireIds, questionSort);
            questionRep.delete(questions);
            questionnaireRep.delete(questionnaires);

            return strQuestionnaireIds;
        }
        throw new BadRequestException(ErrorCode.DATA_ERROR);
    }


    //=================================================================
    //         add question and questionnaire
    //=================================================================
    @Transactional
    public QuestionBean addQuestion(long questionnaireId, String content, String options, String strType) {
        logger.info("add question : questionnaireId={} content={} options={} strType={}",
                questionnaireId, content, options, strType);

        String imagePath = null;
        QuestionEntity entity = new QuestionEntity();
        if (questionnaireRep.exists(questionnaireId)) {
            entity.setQuestionnaireId(questionnaireId);
        }
        if (!VerifyUtil.isStringEmpty(content)) {
            entity.setContent(content);
        }
        if (!VerifyUtil.isStringEmpty(options)) {
            entity.setOptions(options);
        }
        QuestionType type = QuestionType.parseString(strType);
        if (null!=type) {
            entity.setType(type);
        }
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = questionRep.save(entity);

        return questionConverter.convert(entity);
    }

    @Transactional
    public QuestionnaireBean addQuestionnaire(String title, String description, int hospitalId) {
        logger.info("add questionnaire : title={} description={} hospitalId={}", title, description, hospitalId);

        String imagePath = null;
        QuestionnaireEntity entity = new QuestionnaireEntity();
        if (VerifyUtil.isStringEmpty(title)) {
            logger.error("add questionnaire : title is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        else if (questionnaireRep.countByTitle(title.trim())>0) {
            logger.error("add questionnaire : title is exist");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        else {
            entity.setTitle(title.trim());
        }
        if (!VerifyUtil.isStringEmpty(description)) {
            entity.setDescription(description);
        }
        hospitalId = hospitalId<0 ? 0 : hospitalId;
        entity.setHospitalId(hospitalId);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = questionnaireRep.save(entity);

        return questionnaireConverter.convert(entity);
    }

}
