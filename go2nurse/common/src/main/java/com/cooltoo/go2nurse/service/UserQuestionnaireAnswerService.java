package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
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
            new Sort.Order(Sort.Direction.DESC, "groupId"),
            new Sort.Order(Sort.Direction.ASC, "questionId")
    );

    @Autowired private UserQuestionnaireAnswerRepository repository;
    @Autowired private UserQuestionnaireAnswerBeanConverter beanConverter;

    @Autowired private Go2NurseUtility go2NurseUtility;
    @Autowired private UserRepository userRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private QuestionnaireService questionnaireService;
    @Autowired private PatientService patientService;

    //==========================================================================
    //                        getting
    //==========================================================================
    public long newQuestionnaireGroupId() {
        long groupId = System.currentTimeMillis();
        logger.info("get user-questionnaire-answer group id={}", groupId);
        return groupId;
    }

    public List<QuestionnaireBean> getUserQuestionnaire(long userId) {
        logger.info("get all user={} 's questionnaire", userId);
        // get all answer
        List<UserQuestionnaireAnswerBean> allAnswers = getAllUsersAnswers(userId);
        if (VerifyUtil.isListEmpty(allAnswers)) {
            logger.info("count of user's questionnaire is {}", 0);
            return new ArrayList<>();
        }

        List<QuestionnaireBean> questionnaires = fillQuestionnaireAnswer(allAnswers);
        logger.info("count of user's questionnaire is {}", questionnaires.size());
        return questionnaires;
    }

    public QuestionnaireBean getUserQuestionnaireWithAnswer(long userId, long groupId) {
        logger.info("get user{} 's questionnaire={}", userId, groupId);
        List<UserQuestionnaireAnswerBean> questionnaireAnswers = getUserQuestionnaireAnswer(userId, groupId);
        List<QuestionnaireBean> questionnaire = fillQuestionnaireAnswer(questionnaireAnswers);
        if (VerifyUtil.isListEmpty(questionnaire)) {
            return null;
        }
        else {
            return questionnaire.get(0);
        }
    }

    public List<QuestionnaireStatisticsBean> getQuestionnaireStatistics(long questionnaireId) {
        logger.info("get questionnaire statistics by questionnaireId={}", questionnaireId);
        List<QuestionnaireBean> questionnaires = questionnaireService.getQuestionnaireWithQuestionsByIds(""+questionnaireId);
        if (VerifyUtil.isListEmpty(questionnaires)) {
            logger.warn("there is no questionnaire");
            return new ArrayList<>();
        }
        if (1==questionnaires.size()) {
            logger.warn("there is more than one questionnaire, questionnaire are {}", questionnaires);
            return new ArrayList<>();
        }
        QuestionnaireBean questionnaire = questionnaires.get(0);
        List<UserQuestionnaireAnswerEntity> userAnswerEntities = repository.findByQuestionnaireId(questionnaireId, sort);
        List<UserQuestionnaireAnswerBean> userAnswers = entitiesToBeans(userAnswerEntities);
        List<QuestionnaireStatisticsBean> questionnaireStatistics = fillQuestionnaireStatistics(questionnaire, userAnswers);
        logger.info("get questionnaire statistics, count={}", questionnaireStatistics.size());
        return questionnaireStatistics;
    }

    private List<UserQuestionnaireAnswerBean> getAllUsersAnswers(long userId) {
        logger.info("get all users' answer by userId={}", userId);
        List<UserQuestionnaireAnswerEntity> entities = repository.findByUserId(userId, sort);
        List<UserQuestionnaireAnswerBean> beans = entitiesToBeans(entities);
        logger.info("count is {}", beans.size());
        return beans;
    }

    private List<UserQuestionnaireAnswerBean> getUserQuestionnaireAnswer(long userId, long groupId) {
        logger.info("get user={} answer questionnaire at  groupId={} time's answer", userId, groupId);
        List<UserQuestionnaireAnswerEntity> entities = repository.findByUserIdAndGroupId(userId, groupId, sort);
        List<UserQuestionnaireAnswerBean> beans = entitiesToBeans(entities);
        logger.info("count is {}", beans.size());
        return beans;
    }

    private List<QuestionnaireStatisticsBean> fillQuestionnaireStatistics(QuestionnaireBean questionnaire, List<UserQuestionnaireAnswerBean> userAnswers) {
        if (null==questionnaire || VerifyUtil.isListEmpty(userAnswers)) {
            return new ArrayList<>();
        }
        int questionNumber = VerifyUtil.isListEmpty(questionnaire.getQuestions()) ? 0 : questionnaire.getQuestions().size();
        List<QuestionnaireConclusionBean> conclusions = go2NurseUtility.parseJsonList(questionnaire.getConclusion(), QuestionnaireConclusionBean.class);

        // construct the return value
        List<QuestionnaireStatisticsBean> returnValue = new ArrayList<>();
        QuestionnaireStatisticsBean currentStatistics = null;
        int currentGroupScore = 0;
        int currentGroupAnswer = 0;
        Long currentGroupId = 0L;
        for (int i=0, count=userAnswers.size(); i<count; i++) {
            UserQuestionnaireAnswerBean currentAnswer = userAnswers.get(i);
            currentGroupAnswer ++;
            if (currentGroupId!=currentAnswer.getGroupId()) {
                // set the score and conclusion, when group changed
                if (null!=currentStatistics) {
                    currentStatistics.setAnswerNumber(currentGroupAnswer);
                    currentStatistics.setCompleted(currentGroupAnswer==questionNumber);
                    currentStatistics.setScore(currentGroupScore);
                    for (QuestionnaireConclusionBean conclusion : conclusions) {
                        if (conclusion.isThisConclusion(currentGroupScore)) {
                            currentStatistics.setConclusion(conclusion.getItem());
                            break;
                        }
                    }
                }

                // create a statistics for another cycle
                currentStatistics = new QuestionnaireStatisticsBean();
                currentStatistics.setUserId(currentAnswer.getUserId());
                currentStatistics.setPatientId(currentAnswer.getPatientId());
                currentStatistics.setQuestionNumber(questionNumber);
                returnValue.add(currentStatistics);

                // reset groupId and groupScore and groupAnswer for another cycle
                currentGroupId = currentAnswer.getGroupId();
                currentGroupScore = 0;
                currentGroupAnswer = 0;
            }
            if (null==currentStatistics) {
                continue;
            }

            // calculate  score
            String answer = currentAnswer.getAnswer();
            currentGroupScore += parseUserAnswerScore(answer);
        }

        if (null!=currentStatistics) {
            currentStatistics.setAnswerNumber(currentGroupAnswer);
            currentStatistics.setCompleted(currentGroupAnswer==questionNumber);
            currentStatistics.setScore(currentGroupScore);
            for (QuestionnaireConclusionBean conclusion : conclusions) {
                if (conclusion.isThisConclusion(currentGroupScore)) {
                    currentStatistics.setConclusion(conclusion.getItem());
                    break;
                }
            }
        }

        return returnValue;
    }

    private List<QuestionnaireBean> fillQuestionnaireAnswer(List<UserQuestionnaireAnswerBean> allAnswers) {
        if (VerifyUtil.isListEmpty(allAnswers)) {
            logger.info("count is {}", 0);
            return new ArrayList<>();
        }

        // sort the answer by userId and groupId
        Comparator sorter = new Comparator<UserQuestionnaireAnswerBean>() {
            @Override
            public int compare(UserQuestionnaireAnswerBean obj1, UserQuestionnaireAnswerBean obj2) {
                if (obj1.getUserId() == obj2.getUserId()) {
                    if (obj1.getGroupId()==obj2.getGroupId()) {
                        return (int)(obj1.getQuestionId() - obj2.getQuestionId());
                    }
                    return (int) (obj2.getGroupId() - obj1.getGroupId());// 倒序
                }
                return (int) (obj1.getUserId() - obj2.getUserId());
            }
        };
        Collections.sort(allAnswers, sorter);

        // cache groupId patientId questionnaireId
        List<Long> questionnaireIds = new ArrayList<>();
        List<Long> patientIds = new ArrayList<>();
        for (int i=0, count=allAnswers.size(); i<count; i ++) {
            UserQuestionnaireAnswerBean answer = allAnswers.get(i);
            if (!questionnaireIds.contains(answer.getQuestionnaireId())) {
                questionnaireIds.add(answer.getQuestionnaireId());
            }
            if (!patientIds.contains(answer.getPatientId())) {
                patientIds.add(answer.getPatientId());
            }
        }

        // get questionnaires and patients
        Map<Long, QuestionnaireBean> questionnairesIdToBean;
        questionnairesIdToBean = questionnaireService.getQuestionnaireWithQuestionIdToBeanMapByIds(questionnaireIds);
        Map<Long, PatientBean> patientsIdToBean;
        patientsIdToBean= patientService.getAllIdToBeanByStatusAndIds(patientIds, CommonStatus.ENABLED);

        // construct the return value
        List<QuestionnaireBean> returnValue = new ArrayList<>();
        Long currentGroupId = 0L;
        QuestionnaireBean currentQuestionnaire = null;
        int userScore = 0;
        for (int i=0, count=allAnswers.size(); i<count; i++) {
            UserQuestionnaireAnswerBean currentAnswer = allAnswers.get(i);
            if (currentGroupId!=currentAnswer.getGroupId()) {
                if (null!=currentQuestionnaire) {
                    currentQuestionnaire.setUserScore(userScore);
                }
                currentQuestionnaire = questionnairesIdToBean.get(currentAnswer.getQuestionnaireId());
                if (null!=currentQuestionnaire) {
                    currentQuestionnaire = currentQuestionnaire.clone();
                    currentQuestionnaire.setPatient(patientsIdToBean.get(currentAnswer.getPatientId()));
                    currentQuestionnaire.setGroupId(currentAnswer.getGroupId());
                    returnValue.add(currentQuestionnaire);
                }

                // reset groupId and userScore for another cycle
                currentGroupId = currentAnswer.getGroupId();
                userScore = 0;
            }
            if (null==currentQuestionnaire) {
                continue;
            }

            // calculate  score
            String answer = currentAnswer.getAnswer();
            userScore += parseUserAnswerScore(answer);

            // set answer to question
            long questionId = currentAnswer.getQuestionId();
            List<QuestionBean> questions = currentQuestionnaire.getQuestions();
            if (VerifyUtil.isListEmpty(questions)) {
                continue;
            }
            for (QuestionBean question : questions) {
                if (question.getId() != questionId) {
                    continue;
                }
                question.setUserAnswer(answer);
            }

        }
        if (null!=currentQuestionnaire) {
            currentQuestionnaire.setUserScore(userScore);
        }

        // fill answer to question bean
        for (QuestionnaireBean questionnaire : returnValue) {
            List<QuestionnaireConclusionBean> conclusions = go2NurseUtility.parseJsonList(questionnaire.getConclusion(), QuestionnaireConclusionBean.class);
            boolean set = false;
            for (QuestionnaireConclusionBean conclusion : conclusions) {
                if (conclusion.isThisConclusion(questionnaire.getUserScore())) {
                    questionnaire.setUserConclusion(conclusion);
                    set = true;
                    break;
                }
            }
            if (!set) {
                questionnaire.setUserConclusion(new QuestionnaireConclusionBean());
            }
        }

        logger.info("count is {}", returnValue.size());
        return returnValue;
    }

    private List<UserQuestionnaireAnswerBean> entitiesToBeans(Iterable<UserQuestionnaireAnswerEntity> entities) {
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

    private int parseUserAnswerScore(String answer) {
        int userScore = 0;
        if (VerifyUtil.isStringEmpty(answer)) {
            return userScore;
        }

        // calculate  score
        boolean single = answer.indexOf('{')==answer.lastIndexOf('{');
        if (single) {
            QuestionOptionBean userOption = go2NurseUtility.parseJsonBean(answer, QuestionOptionBean.class);
            if (null != userOption) {
                userScore += userOption.getScore();
            }
        }
        else {
            List<QuestionOptionBean> userOptions = go2NurseUtility.parseJsonList(answer, QuestionOptionBean.class);
            for (QuestionOptionBean userOption : userOptions) {
                userScore += userOption.getScore();
            }
        }

        return userScore;
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

    public List<UserQuestionnaireAnswerBean> deleteByUserIdAndGroupId(long userId, long groupId) {
        logger.info("delete by userId={} and groupId={}", userId, groupId);
        List<UserQuestionnaireAnswerEntity> entities = repository.findByUserIdAndGroupId(userId, groupId, sort);
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
    public UserQuestionnaireAnswerBean updateAnswer(long userId, long groupId, long questionId, String answer, String strStatus) {
        logger.info("update answer by userId={} groupId={} questionId={} answer={} and status={}",
                userId, groupId, questionId, answer, strStatus);
        List<UserQuestionnaireAnswerEntity> entities = repository.findByUserIdAndGroupIdAndQuestionId(userId, groupId, questionId, sort);
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
    public UserQuestionnaireAnswerBean addAnswer(long userId, long patientId, long groupId, long questionId, String answer) {
        logger.info("add user={} patient={} in groupId={} answer question={} with answer={}",
                userId, patientId, groupId, questionId, answer);
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

        groupId = groupId<0 ? 0 : groupId;
        patientId = patientId<0 ? 0 : patientId;

        UserQuestionnaireAnswerEntity entity = new UserQuestionnaireAnswerEntity();
        entity.setUserId(userId);
        entity.setPatientId(patientId);
        entity.setGroupId(groupId);
        entity.setQuestionnaireId(questionnaireId);
        entity.setQuestionId(questionId);
        entity.setAnswer(answer);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        List<UserQuestionnaireAnswerEntity> answers = repository.findByUserIdAndGroupIdAndQuestionId(userId, groupId, questionId, sort);
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