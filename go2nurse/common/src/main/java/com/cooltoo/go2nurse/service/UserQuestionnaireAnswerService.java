package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.converter.UserQuestionnaireAnswerBeanConverter;
import com.cooltoo.go2nurse.entities.QuestionEntity;
import com.cooltoo.go2nurse.entities.UserEntity;
import com.cooltoo.go2nurse.entities.UserQuestionnaireAnswerEntity;
import com.cooltoo.go2nurse.repository.QuestionRepository;
import com.cooltoo.go2nurse.repository.UserQuestionnaireAnswerRepository;
import com.cooltoo.go2nurse.repository.UserRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.util.JSONUtil;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import com.qiniu.util.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
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

    @Autowired private UserGo2NurseFileStorageService userFileStorage;
    @Autowired private Go2NurseUtility utility;

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
        List<UserQuestionnaireAnswerBean> allCompleteAnswers = filterOnlyCompletedAnswer(allAnswers);

        List<QuestionnaireBean> questionnaires = fillQuestionnaireAnswer(allCompleteAnswers);
        logger.info("count of user's questionnaire is {}", questionnaires.size());
        return questionnaires;
    }

    @Transactional
    public QuestionnaireBean getUserQuestionnaireWithAnswer(long userId, long groupId, boolean isPatientCompleteQuestionnaire) {
        logger.info("get user{} 's questionnaire={} isPatientCompleteQuestionnaire={}", userId, groupId, isPatientCompleteQuestionnaire);
        List<UserQuestionnaireAnswerBean> questionnaireAnswers = getUserQuestionnaireAnswer(userId, groupId);
        if (!isPatientCompleteQuestionnaire) {
            questionnaireAnswers = filterOnlyCompletedAnswer(questionnaireAnswers);
        }
        List<QuestionnaireBean> questionnaire = fillQuestionnaireAnswer(questionnaireAnswers);
        if (VerifyUtil.isListEmpty(questionnaire)) {
            return null;
        }
        else {
            QuestionnaireBean userQuestionnaire = questionnaire.get(0);
            if (!isPatientCompleteQuestionnaire) {
                // do nothing
            }
            else {
                // set patient questionnaire conclusion, and complete flag
                int lastAnswerIndex = questionnaireAnswers.size() - 1;
                if (!YesNoEnum.YES.equals(questionnaireAnswers.get(lastAnswerIndex).getAnswerCompleted())) {
                    String conclusion = "" + userQuestionnaire.getUserScore();
                    if (null != userQuestionnaire.getUserConclusion()) {
                        conclusion = userQuestionnaire.getUserConclusion().toJson();
                    }
                    patientCompleteQuestionnaire(userId, groupId, conclusion);
                }
            }
            return userQuestionnaire;
        }
    }

    public void getQuestionnaireStatistics(List<QuestionnaireBean> questionnaires) {
        logger.info("get questionnaire statistics by questionnaires size = {}", VerifyUtil.isListEmpty(questionnaires) ? 0 : questionnaires.size());
        if (VerifyUtil.isListEmpty(questionnaires)) {
            return;
        }
        List<Long> questionnaireIds = new ArrayList<>();
        for (QuestionnaireBean questionnaire : questionnaires) {
            if (questionnaireIds.contains(questionnaire.getId())) {
                continue;
            }
            questionnaireIds.add(questionnaire.getId());
        }
        List<UserQuestionnaireAnswerEntity> userAnswerEntities = repository.findByQuestionnaireIdIn(questionnaireIds, sort);
        List<UserQuestionnaireAnswerBean> userAnswers = entitiesToBeans(userAnswerEntities);
        for (QuestionnaireBean questionnaire : questionnaires) {
            fillQuestionnaireStatistics(questionnaire, userAnswers);
        }
        return;
    }

    public String exportQuestionnaireStatisticsToFile(Long userId, Long patientId, GenderType gender,
                                                      Integer hospitalId, Integer departmentId, Long questionnaireId,
                                                      Integer ageStart, Integer ageEnd
                                                    //, Date timeStart, Date timeEnd
    ) {
        logger.info("export answer by userId={} patientId={}, gender={}, hospitalId={}, departId={}, questionnaireId={} ageStart={} ageEnd={}",
                userId, patientId, gender, hospitalId, departmentId, questionnaireId, ageStart, ageEnd);
        List<UserQuestionnaireAnswerEntity> allAnswers = repository.findAnswerToExport(YesNoEnum.YES,
                userId, patientId, gender,
                hospitalId, departmentId, questionnaireId,
                ageStart, ageEnd, sort);
        logger.info("answer count={}", VerifyUtil.isListEmpty(allAnswers) ? 0 : allAnswers.size());
        StringBuilder oneQuestionnaireAnswer = new StringBuilder();
        File statisticsFile = userFileStorage.createFileInBaseStorage("statistics", ".csv");
        JSONUtil jsonUtil = JSONUtil.newInstance();
        // write title
        oneQuestionnaireAnswer.append("\"").append("序号").append("\"\t,");
        oneQuestionnaireAnswer.append("\"").append("医院").append("\"\t,");
        oneQuestionnaireAnswer.append("\"").append("姓名").append("\"\t,");
        oneQuestionnaireAnswer.append("\"").append("性别").append("\"\t,");
        oneQuestionnaireAnswer.append("\"").append("年龄").append("\"\t,");
        oneQuestionnaireAnswer.append("\"").append("手机").append("\"\t,");
        oneQuestionnaireAnswer.append("\"").append("问卷").append("\"\t,");
        oneQuestionnaireAnswer.append("\"").append("问题").append("\"\t,");
        oneQuestionnaireAnswer.append("\"").append("答案").append("\"\t,");
        oneQuestionnaireAnswer.append("\"").append("答案分值").append("\"\t,");
        oneQuestionnaireAnswer.append("\"").append("结论").append("\"\t,");
        oneQuestionnaireAnswer.append("\"").append("结论分值区间").append("\"\t,");
        oneQuestionnaireAnswer.append("\"").append("时间").append("\"\r\n");
        try {
            FileWriter writer = new FileWriter(statisticsFile);
            writer.write(oneQuestionnaireAnswer.toString());
            writer.flush();
            oneQuestionnaireAnswer.setLength(0);
            for (UserQuestionnaireAnswerEntity answer : allAnswers) {
                oneQuestionnaireAnswer.append("\"").append(answer.getGroupId()).append("\"\t,");
                oneQuestionnaireAnswer.append("\"").append(answer.getHospitalName()).append("\"\t,");
                oneQuestionnaireAnswer.append("\"").append(answer.getPatientName()).append("\"\t,");
                oneQuestionnaireAnswer.append("\"").append(answer.getPatientGender()).append("\"\t,");
                oneQuestionnaireAnswer.append("\"").append(answer.getPatientAge()).append("\"\t,");
                oneQuestionnaireAnswer.append("\"").append(answer.getPatientMobile()).append("\"\t,");
                oneQuestionnaireAnswer.append("\"").append(answer.getQuestionnaireName()).append("\"\t,");
                oneQuestionnaireAnswer.append("\"").append(answer.getQuestionContent()).append("\"\t,");
                String userAnswer = answer.getAnswer();
                boolean single = userAnswer.indexOf('{')==userAnswer.lastIndexOf('{');
                if (single) {
                    QuestionOptionBean userOption = jsonUtil.parseJsonBean(userAnswer, QuestionOptionBean.class);
                    if (null==userAnswer) {
                        oneQuestionnaireAnswer.append("\"").append(" ").append("\"\t,");
                        oneQuestionnaireAnswer.append("\"").append(" ").append("\"\t,");
                    }
                    else {
                        oneQuestionnaireAnswer.append("\"").append(userOption.getItem().replace("\"", "\"\"")).append("\"\t,");
                        oneQuestionnaireAnswer.append("\"").append(userOption.getScore()).append("\"\t,");
                    }

                }
                else {
                    List<QuestionOptionBean> userOptions = jsonUtil.parseJsonList(userAnswer, QuestionOptionBean.class);
                    StringBuilder items = new StringBuilder();
                    StringBuilder scores = new StringBuilder();
                    for (QuestionOptionBean userOption : userOptions) {
                        items.append(userOption.getItem()).append(",");
                        scores.append(userOption.getScore()).append(",");
                    }
                    if (items.length()==0) {
                        oneQuestionnaireAnswer.append("\"").append(" ").append("\"\t,");
                        oneQuestionnaireAnswer.append("\"").append(" ").append("\"\t,");
                    }
                    else {
                        oneQuestionnaireAnswer.append("\"").append(items.substring(0, items.length() - 1).replace("\"", "\"\"")).append("\"\t,");
                        oneQuestionnaireAnswer.append("\"").append(scores.substring(0, scores.length() - 1)).append("\"\t,");
                    }
                }
                QuestionnaireConclusionBean conclusion = jsonUtil.parseJsonBean(answer.getQuestionnaireConclusion(), QuestionnaireConclusionBean.class);
                if (null==conclusion) {
                    oneQuestionnaireAnswer.append("\"").append(" ").append("\"\t,");
                    oneQuestionnaireAnswer.append("\"").append(" ").append("\"\t,");
                }
                else {
                    oneQuestionnaireAnswer.append("\"").append(conclusion.getItem().replace("\"", "\"\"")).append("\"\t,");
                    oneQuestionnaireAnswer.append("\"").append(conclusion.getInterval().replace("\"", "\"\"")).append("\"\t,");
                }
                oneQuestionnaireAnswer.append("\"").append(NumberUtil.timeToString(answer.getTime(), NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS)).append("\"\r\n");
                writer.write(oneQuestionnaireAnswer.toString());
                writer.flush();
                oneQuestionnaireAnswer.setLength(0);
            }
            writer.close();
        }
        catch (Exception ex) {
            return "";
        }
        return utility.getHttpPrefix()+statisticsFile.getName();
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

    private void fillQuestionnaireStatistics(QuestionnaireBean questionnaire, List<UserQuestionnaireAnswerBean> userAnswers) {
        if (null==questionnaire || VerifyUtil.isListEmpty(userAnswers)) {
            return;
        }
        QuestionnaireStatisticsBean statistics = new QuestionnaireStatisticsBean();
        int questionNumber = VerifyUtil.isListEmpty(questionnaire.getQuestions()) ? 0 : questionnaire.getQuestions().size();

        JSONUtil jsonUtil = JSONUtil.newInstance();
        // construct statistics conclusion
        List<QuestionnaireConclusionBean> conclusions = jsonUtil.parseJsonList(questionnaire.getConclusion(), QuestionnaireConclusionBean.class);
        List<QuestionnaireStatisticsBean.ConclusionBean> statisticsConclusions = new ArrayList<>();
        for (QuestionnaireConclusionBean conclusion : conclusions) {
            QuestionnaireStatisticsBean.ConclusionBean sc = new QuestionnaireStatisticsBean.ConclusionBean();
            sc.setConclusion(conclusion);
            sc.setResultCount(0);
            statisticsConclusions.add(sc);
        }
        statistics.settConclusions(statisticsConclusions);

        // construct the return value
        boolean needCalculate = false;
        List<String> currentGroupAnswer = new ArrayList<>();
        for (int i=0, count=userAnswers.size(); i<count; i++) {
            UserQuestionnaireAnswerBean currentAnswer = userAnswers.get(i);
            if (currentAnswer.getQuestionnaireId()!=questionnaire.getId()) {
                continue;
            }

            UserQuestionnaireAnswerBean nextAnswer = (i+1<count) ? userAnswers.get(i+1) : null;
            needCalculate = (null==nextAnswer) || (nextAnswer.getGroupId()!=currentAnswer.getGroupId());

            // record answer
            currentGroupAnswer.add(currentAnswer.getAnswer());

            if (needCalculate) {
                // set the answer times and conclusion, when group changed
                if (currentGroupAnswer.size()==questionNumber) {
                    // set answer times
                    statistics.setAnswerTimes(statistics.getAnswerTimes()+1);
                    int totalScore = parseUserAnswersScore(currentGroupAnswer);
                    // set the conclusion
                    for (QuestionnaireStatisticsBean.ConclusionBean conclusion : statisticsConclusions) {
                        if (conclusion.getConclusion().isThisConclusion(totalScore)) {
                            conclusion.setResultCount(conclusion.getResultCount()+1);
                            break;
                        }
                    }
                }

                // reset groupAnswer for another cycle
                currentGroupAnswer.clear();
            }
        }
        return;
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
                long delta = 0;
                if (obj1.getUserId() == obj2.getUserId()) {
                    if (obj1.getGroupId()==obj2.getGroupId()) {
                        delta = (obj1.getQuestionId() - obj2.getQuestionId());
                        return delta>0 ? 1 : (delta==0) ? 0 : -1;
                    }
                    delta = (obj2.getGroupId() - obj1.getGroupId());
                    return delta>0 ? 1 : (delta==0) ? 0 : -1;// 倒序
                }
                delta = (obj1.getUserId() - obj2.getUserId());
                return delta>0 ? 1 : (delta==0) ? 0 : -1;
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
        JSONUtil jsonUtil = JSONUtil.newInstance();
        for (QuestionnaireBean questionnaire : returnValue) {
            List<QuestionnaireConclusionBean> conclusions = jsonUtil.parseJsonList(questionnaire.getConclusion(), QuestionnaireConclusionBean.class);
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

    private List<UserQuestionnaireAnswerBean> filterOnlyCompletedAnswer(List<UserQuestionnaireAnswerBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return null==beans ? new ArrayList<>() : beans;
        }
        List<UserQuestionnaireAnswerBean> completedAnswer = new ArrayList<>();
        for (UserQuestionnaireAnswerBean answer : beans) {
            if (YesNoEnum.YES.equals(answer.getAnswerCompleted())) {
                completedAnswer.add(answer);
            }
        }
        return completedAnswer;
    }

    private int parseUserAnswersScore(List<String> userAnswers) {
        if (VerifyUtil.isListEmpty(userAnswers)) {
            return 0;
        }
        int answerScore = 0;
        for (String answer : userAnswers) {
            answerScore += parseUserAnswerScore(answer);
        }
        return answerScore;
    }

    private int parseUserAnswerScore(String answer) {
        int userScore = 0;
        if (VerifyUtil.isStringEmpty(answer)) {
            return userScore;
        }

        // calculate  score
        JSONUtil jsonUtil = JSONUtil.newInstance();
        boolean single = answer.indexOf('{')==answer.lastIndexOf('{');
        if (single) {
            QuestionOptionBean userOption = jsonUtil.parseJsonBean(answer, QuestionOptionBean.class);
            if (null != userOption) {
                userScore += userOption.getScore();
            }
        }
        else {
            List<QuestionOptionBean> userOptions = jsonUtil.parseJsonList(answer, QuestionOptionBean.class);
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

    /**
     * Invoke just by patient when complete the questionnaire
     */
    @Transactional
    public void patientCompleteQuestionnaire(long userId, long groupId, String questionnaireConclusion) {
        logger.info("patient complete questionnaire answer by userId={} groupId={} questionnaireConclusion={}",
                userId, groupId, questionnaireConclusion);
        if (null==questionnaireConclusion) {
            logger.error("conclusion is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(questionnaireConclusion)) {
            logger.warn("conclusion is empty string");
        }
        // set questionnaire conclusion, and answer completed flag
        int updateCount = repository.completeUserQuestionnaire(userId, groupId, questionnaireConclusion);
        logger.info("completing questionnaire affects count={} records in user_questionnaire_answer table", updateCount);

        // remove all un_completed answer
        repository.deleteByUserIdAndAnswerCompleted(userId, YesNoEnum.NO);
    }

    //==========================================================================
    //                        adding
    //==========================================================================
    public UserQuestionnaireAnswerBean addAnswer(long userId, long patientId, long groupId, long questionId, String answer) {
        logger.info("add user={} patient={} in groupId={} answer question={} with answer={}",
                userId, patientId, groupId, questionId, answer);
        if (!userRepository.exists(userId)) {
            logger.error("user not exists");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        UserEntity user = userRepository.findOne(userId);
        QuestionEntity question = questionRepository.findOne(questionId);
        PatientBean patient = patientService.getOneById(patientId);
        QuestionnaireBean questionnaire = null == question ? null : questionnaireService.getQuestionnaire(question.getQuestionnaireId());
        if (null == question) {
            logger.error("question not exist");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (null == patient) {
            logger.warn("patient not exist, record user information");
        }
        if (null == questionnaire) {
            logger.error("questionnaire not exist");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        groupId = groupId < 0 ? 0 : groupId;

        // calculate age
        int patientAge;
        Calendar currentYear = Calendar.getInstance();
        Calendar birthday = Calendar.getInstance();
        if (null!=patient && patient.getBirthday() != null) {
            birthday.setTime(patient.getBirthday());
        }
        else {
            birthday.setTime(user.getBirthday());
        }
        patientAge = currentYear.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);

        UserQuestionnaireAnswerEntity entity = new UserQuestionnaireAnswerEntity();
        entity.setGroupId(groupId);
        entity.setUserId(userId);
        if (null!=patient) {
            entity.setPatientId(patientId);
            entity.setPatientName(patient.getName());
            entity.setPatientGender(patient.getGender());
            entity.setPatientAge(patientAge);
            entity.setPatientMobile(patient.getMobile());
        }
        else {
            entity.setPatientId(0);
            entity.setPatientName(user.getName());
            entity.setPatientGender(user.getGender());
            entity.setPatientAge(patientAge);
            entity.setPatientMobile(user.getMobile());
        }
        entity.setQuestionnaireId(questionnaire.getId());
        entity.setQuestionnaireName(questionnaire.getTitle());
        entity.setQuestionnaireConclusion(null);
        entity.setQuestionId(question.getId());
        entity.setQuestionContent(question.getContent());
        entity.setAnswer(answer);
        entity.setAnswerCompleted(YesNoEnum.NO);
        if (-1==questionnaire.getHospitalId()) {
            entity.setHospitalId(questionnaire.getHospitalId());
            entity.setHospitalName("cooltoo");
        }
        else if (null != questionnaire.getHospital()) {
            entity.setHospitalId(questionnaire.getHospitalId());
            entity.setHospitalName(questionnaire.getHospital().getName());
        }
        else {
            entity.setHospitalId(questionnaire.getHospitalId());
            entity.setHospitalName("unknown");
        }
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        // TODO -- department information reversed
        entity.setDepartmentId(0);
        entity.setDepartmentName(null);

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