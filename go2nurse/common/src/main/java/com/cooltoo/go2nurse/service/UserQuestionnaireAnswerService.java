package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.QuestionBean;
import com.cooltoo.go2nurse.beans.QuestionnaireBean;
import com.cooltoo.go2nurse.beans.QuestionnaireConclusionBean;
import com.cooltoo.go2nurse.beans.QuestionOptionBean;
import com.cooltoo.go2nurse.beans.UserQuestionnaireAnswerBean;
import com.cooltoo.go2nurse.constants.QuestionType;
import com.cooltoo.go2nurse.converter.UserQuestionnaireAnswerBeanConverter;
import com.cooltoo.go2nurse.entities.QuestionEntity;
import com.cooltoo.go2nurse.entities.UserQuestionnaireAnswerEntity;
import com.cooltoo.go2nurse.repository.QuestionRepository;
import com.cooltoo.go2nurse.repository.UserQuestionnaireAnswerRepository;
import com.cooltoo.go2nurse.repository.UserRepository;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by hp on 2016/6/28.
 */
@Service("UserQuestionnaireAnswerService")
public class UserQuestionnaireAnswerService {

    private static final Logger logger = LoggerFactory.getLogger(UserQuestionnaireAnswerService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private UserQuestionnaireAnswerRepository repository;
    @Autowired private UserQuestionnaireAnswerBeanConverter beanConverter;

    @Autowired private Go2NurseUtility go2NurseUtility;
    @Autowired private UserRepository userRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private QuestionnaireService questionnaireService;

    //==========================================================================
    //                        getting
    //==========================================================================

    public List<QuestionnaireBean> getUserQuestionnaire(long userId) {
        logger.info("get all user={} 's questionnaire", userId);
        // get all answer
        List<UserQuestionnaireAnswerBean> allAnswers = getAllUsersAnswers(userId);
        if (VerifyUtil.isListEmpty(allAnswers)) {
            logger.info("count of user's questionnaire is {}", 0);
            return new ArrayList<>();
        }

        List<QuestionnaireBean> questionnaires = fillQuestionnaireAnswer(allAnswers, false);
        logger.info("count of user's questionnaire is {}", questionnaires.size());
        return questionnaires;
    }

    public QuestionnaireBean getUserQuestionnaireWithAnswer(long userId, long questionnaireId) {
        logger.info("get user{} 's questionnaire={}", userId, questionnaireId);
        List<UserQuestionnaireAnswerBean> questionnaireAnswers = getUserQuestionnaireAnswer(userId, questionnaireId);
        List<QuestionnaireBean> questionnaire = fillQuestionnaireAnswer(questionnaireAnswers, true);
        if (VerifyUtil.isListEmpty(questionnaire)) {
            return null;
        }
        else {
            return questionnaire.get(0);
        }
    }

    private List<UserQuestionnaireAnswerBean> getAllUsersAnswers(long userId) {
        logger.info("get all users' answer by userId={}", userId);
        List<UserQuestionnaireAnswerEntity> entities = repository.findByUserId(userId, sort);
        List<UserQuestionnaireAnswerBean> beans = entitiesToBeans(entities);
        logger.info("count is {}", beans.size());
        return beans;
    }

    private List<UserQuestionnaireAnswerBean> getUserQuestionnaireAnswer(long userId, long questionnaireId) {
        logger.info("get user={} questionnaire={} 's answer", userId, questionnaireId);
        List<UserQuestionnaireAnswerEntity> entities = repository.findByUserIdAndQuestionnaireId(userId, questionnaireId, sort);
        List<UserQuestionnaireAnswerBean> beans = entitiesToBeans(entities);
        logger.info("count is {}", beans.size());
        return beans;
    }

    private List<QuestionnaireBean> fillQuestionnaireAnswer(List<UserQuestionnaireAnswerBean> allAnswers, boolean fillAnswer) {
        logger.info("fill answer ={}", fillAnswer);
        if (VerifyUtil.isListEmpty(allAnswers)) {
            logger.info("count is {}", 0);
            return new ArrayList<>();
        }
        // cache the answer and questionnaire id
        Map<Long, UserQuestionnaireAnswerBean> questionIdToAnswer = new HashMap<>();
        List<Long> questionnaireIds = new ArrayList<>();
        for (UserQuestionnaireAnswerBean answer : allAnswers) {
            if (!questionIdToAnswer.containsKey(answer.getId())) {
                questionIdToAnswer.put(answer.getQuestionId(), answer);
            }
            if (questionnaireIds.contains(answer.getQuestionnaireId())) {
                continue;
            }
            questionnaireIds.add(answer.getQuestionnaireId());
        }

        // get questionnaires with questions
        List<QuestionnaireBean> questionnaires;
        if (!fillAnswer) {
            questionnaires = questionnaireService.getQuestionnaireByIds(questionnaireIds);
            logger.info("count is {}", questionnaires.size());
            return questionnaires;
        }

        questionnaires = questionnaireService.getQuestionnaireWithQuestionsByIds(questionnaireIds);
        if (VerifyUtil.isListEmpty(questionnaires)) {
            logger.info("count is {}", 0);
            return new ArrayList<>();
        }

        // fill answer to question bean
        int userScore = 0;
        for (QuestionnaireBean questionnaire : questionnaires) {
            List<QuestionnaireConclusionBean> conclusions = go2NurseUtility.parseJsonList(questionnaire.getConclusion(), QuestionnaireConclusionBean.class);
            List<QuestionBean> questions = questionnaire.getQuestions();
            if (VerifyUtil.isListEmpty(questions)) {
                continue;
            }
            for (QuestionBean question : questions) {
                UserQuestionnaireAnswerBean answer = questionIdToAnswer.get(question.getId());
                if(null!=answer) {
                    question.setUserAnswer(answer.getAnswer());
                    if (QuestionType.SINGLE_SELECTION.equals(question.getType())) {
                        QuestionOptionBean userOption = go2NurseUtility.parseJsonBean(answer.getAnswer(), QuestionOptionBean.class);
                        if (null != userOption) {
                            userScore += userOption.getScore();
                        }
                    }
                    else if (QuestionType.MULTI_SELECTION.equals(question.getType())) {
                        List<QuestionOptionBean> userOptions = go2NurseUtility.parseJsonList(answer.getAnswer(), QuestionOptionBean.class);
                        for (QuestionOptionBean userOption : userOptions) {
                            userScore += userOption.getScore();
                        }
                    }
                }
            }
            questionnaire.setUserScore(userScore);
            for (QuestionnaireConclusionBean conclusion : conclusions) {
                if (conclusion.isThisConclusion(userScore)) {
                    questionnaire.setUserConclusion(conclusion);
                    break;
                }
            }
            userScore = 0;
        }

        logger.info("count is {}", questionnaires.size());
        return questionnaires;
    }

    public List<UserQuestionnaireAnswerBean> entitiesToBeans(Iterable<UserQuestionnaireAnswerEntity> entities) {
        List<UserQuestionnaireAnswerBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        for (UserQuestionnaireAnswerEntity entity : entities) {
            UserQuestionnaireAnswerBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    //==========================================================================
    //                        deleting
    //==========================================================================
    public List<UserQuestionnaireAnswerBean> deleteByUserId(long userId) {
        logger.info("delete by userId={}", userId);
        List<UserQuestionnaireAnswerEntity> entities = repository.findByUserId(userId, sort);
        List<UserQuestionnaireAnswerBean> beans = entitiesToBeans(entities);
        if (!VerifyUtil.isListEmpty(entities)) {
            repository.delete(entities);
        }
        return beans;
    }

    public List<UserQuestionnaireAnswerBean> deleteByUserIdAndQuestionIds(long userId, String strQuestionIds) {
        logger.info("delete by userId={} and questionIds={}", userId, strQuestionIds);
        List<Long> questionIds = VerifyUtil.parseLongIds(strQuestionIds);
        List<UserQuestionnaireAnswerEntity> entities = repository.findByUserIdAndQuestionIdIn(userId, questionIds, sort);
        List<UserQuestionnaireAnswerBean> beans = entitiesToBeans(entities);
        if (!VerifyUtil.isListEmpty(entities)) {
            repository.delete(entities);
        }
        return beans;
    }

    public List<UserQuestionnaireAnswerBean> deleteByUserIdAndQuestionnaireId(long userId, long questionnaireId) {
        logger.info("delete by userId={} and questionnaireId={}", userId, questionnaireId);
        List<UserQuestionnaireAnswerEntity> entities = repository.findByUserIdAndQuestionnaireId(userId, questionnaireId, sort);
        List<UserQuestionnaireAnswerBean> beans = entitiesToBeans(entities);
        if (!VerifyUtil.isListEmpty(entities)) {
            repository.delete(entities);
        }
        return beans;
    }

    public List<UserQuestionnaireAnswerBean> deleteByQuestionIds(String strQuestionIds) {
        logger.info("delete by questionIds={}", strQuestionIds);
        List<Long> questionIds = VerifyUtil.parseLongIds(strQuestionIds);
        List<UserQuestionnaireAnswerEntity> entities = repository.findByQuestionIdIn(questionIds, sort);
        List<UserQuestionnaireAnswerBean> beans = entitiesToBeans(entities);
        if (!VerifyUtil.isListEmpty(entities)) {
            repository.delete(entities);
        }
        return beans;
    }

    //==========================================================================
    //                        updating
    //==========================================================================
    public UserQuestionnaireAnswerBean updateAnswer(long userId, long questionId, String answer, String strStatus) {
        logger.info("update answer by userId={} questionId={} answer={} and status={}");
        List<UserQuestionnaireAnswerEntity> entities = repository.findByUserIdAndQuestionId(userId, questionId, sort);
        if (!VerifyUtil.isListEmpty(entities)) {
            CommonStatus status = CommonStatus.parseString(strStatus);
            answer = VerifyUtil.isStringEmpty(answer) ? "" : answer.trim();
            for (UserQuestionnaireAnswerEntity tmp : entities) {
                tmp.setAnswer(answer);
                tmp.setStatus(status);
            }
            repository.save(entities);
            return beanConverter.convert(entities.get(0));
        }
        throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
    }

    //==========================================================================
    //                        adding
    //==========================================================================
    public UserQuestionnaireAnswerBean addAnswer(long userId, long questionId, String answer) {
        logger.info("add user={} answer question={} with answer={}",
                userId, questionId, answer);
        QuestionEntity question = questionRepository.findOne(questionId);
        if (null==question) {
            logger.error("question not exist");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (!userRepository.exists(userId)) {
            logger.error("user not exists");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        long questionnaireId = question.getQuestionnaireId();
        question = null;

        UserQuestionnaireAnswerEntity entity = new UserQuestionnaireAnswerEntity();
        entity.setUserId(userId);
        entity.setQuestionnaireId(questionnaireId);
        entity.setQuestionId(questionId);
        entity.setAnswer(answer);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        List<UserQuestionnaireAnswerEntity> answers = repository.findByUserIdAndQuestionId(userId, questionId, sort);
        if (!VerifyUtil.isListEmpty(answers)) {
            List<UserQuestionnaireAnswerEntity> deleteAnswer = new ArrayList<>();
            for (UserQuestionnaireAnswerEntity tmp : answers) {
                if (tmp.getId()!=entity.getId()) {
                    deleteAnswer.add(tmp);
                }
            }
            repository.delete(deleteAnswer);
        }
        return beanConverter.convert(entity);
    }
}