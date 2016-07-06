package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.QuestionBean;
import com.cooltoo.go2nurse.beans.QuestionnaireBean;
import com.cooltoo.go2nurse.beans.QuestionnaireCategoryBean;
import com.cooltoo.go2nurse.beans.UserHospitalizedRelationBean;
import com.cooltoo.go2nurse.constants.QuestionType;
import com.cooltoo.go2nurse.converter.QuestionBeanConverter;
import com.cooltoo.go2nurse.converter.QuestionnaireBeanConverter;
import com.cooltoo.go2nurse.converter.QuestionnaireCategoryBeanConverter;
import com.cooltoo.go2nurse.entities.QuestionEntity;
import com.cooltoo.go2nurse.entities.QuestionnaireCategoryEntity;
import com.cooltoo.go2nurse.entities.QuestionnaireEntity;
import com.cooltoo.go2nurse.repository.QuestionRepository;
import com.cooltoo.go2nurse.repository.QuestionnaireCategoryRepository;
import com.cooltoo.go2nurse.repository.QuestionnaireRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by zhaolisong on 16/4/6.
 */
@Service("QuestionnaireService")
public class QuestionnaireService {
    private static final Logger logger = LoggerFactory.getLogger(QuestionnaireService.class.getName());

    private static final Sort questionSort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "grade"),
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    private static final Sort questionnaireSort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time")
    );

    private static final Sort questionnaireCategorySort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time")
    );

    @Autowired private QuestionRepository questionRep;
    @Autowired private QuestionnaireRepository questionnaireRep;
    @Autowired private QuestionnaireCategoryRepository questionnaireCategoryRep;
    @Autowired private QuestionBeanConverter questionConverter;
    @Autowired private QuestionnaireBeanConverter questionnaireConverter;
    @Autowired private QuestionnaireCategoryBeanConverter questionnaireCategoryConverter;

    @Autowired private UserGo2NurseFileStorageService userStorageService;

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
        QuestionEntity entity = questionRep.findOne(questionId);
        if (null==entity) {
            logger.info("get question by id, doesn't exist!");
            return null;
        }
        return questionConverter.convert(entity);
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

    public Map<Long, QuestionnaireBean> getQuestionnaireIdToBeanMapByIds(List<Long> questionnaireIds) {
        List<QuestionnaireBean> beans = getQuestionnaireByIds(questionnaireIds);
        Map<Long, QuestionnaireBean> idToBean = new HashMap<>();
        for (QuestionnaireBean bean : beans) {
            idToBean.put(bean.getId(), bean);
        }
        return idToBean;
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

    private List<QuestionnaireBean> getQuestionnaireByHospitalId(int hospitalId) {
        logger.info("get questionnaire by hospitalId={}", hospitalId);
        List<QuestionnaireEntity> resultSet = questionnaireRep.findByHospitalId(hospitalId, questionnaireSort);
        List<QuestionnaireBean> beans = questionnaireEntitiesToBeans(resultSet);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<QuestionnaireBean> getQuestionnaireWithQuestionsByIds(String strQuestionnaireIds) {
        logger.info("get questionnaire with questions by questionnaireIds={}", strQuestionnaireIds);
        List<Long> questionnaireIds = VerifyUtil.parseLongIds(strQuestionnaireIds);
        List<QuestionnaireBean> questionnaires = getQuestionnaireWithQuestionsByIds(questionnaireIds);
        logger.info("get questionnaire with question count is {}", questionnaires.size());
        return questionnaires;
    }

    private List<QuestionnaireBean> getQuestionnaireWithQuestionsByIds(List<Long> questionnaireIds) {
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
        return questionnaires;
    }

    public long getCategoryCount() {
        long count = questionnaireCategoryRep.count();
        logger.info("get questionnaire category count={}", count);
        return count;
    }

    public List<QuestionnaireCategoryBean> getCategoryByPage(int pageIndex, int sizeOfPage) {
        // get all questionnaire category
        logger.info("get questionnaire category at pageIndex={} sizePerPage={}", pageIndex, sizeOfPage);
        PageRequest pageCategory = new PageRequest(pageIndex, sizeOfPage, questionnaireCategorySort);
        Page<QuestionnaireCategoryEntity> resultSet = questionnaireCategoryRep.findAll(pageCategory);
        List<QuestionnaireCategoryBean> beans = questionnaireCategoryEntitiesToBeans(resultSet);
        logger.info("count is {}", beans.size());
        return beans;
    }

    private List<QuestionnaireCategoryBean> getCategoryByIds(List<Long> categoryIds) {
        logger.info("get questionnaire category by category ids={}", categoryIds);
        List<QuestionnaireCategoryEntity> categoryResultSet = questionnaireCategoryRep.findByIdIn(categoryIds, questionnaireCategorySort);
        List<QuestionnaireCategoryBean> categories = questionnaireCategoryEntitiesToBeans(categoryResultSet);
        logger.info("get questionnaire category count is {}", categories.size());
        return categories;
    }

    public List<QuestionnaireCategoryBean> getCategoryWithQuestionnaireByIds(String strCategoriesId) {
        logger.info("get questionnaire category with questionnaire by category ids={}", strCategoriesId);
        List<Long> categoryIds = VerifyUtil.parseLongIds(strCategoriesId);
        List<QuestionnaireCategoryBean> categories = getCategoryWithQuestionnaireByIds(categoryIds);
        logger.info("get questionnaire category with questionnaire count is {}", categories.size());
        return categories;
    }

    public List<QuestionnaireCategoryBean> getCategoryWithQuestionnaireByIds(List<Long> categoriesId) {
        logger.info("get questionnaire category with questionnaire by category ids={}", categoriesId);
        List<QuestionnaireCategoryBean> categories;
        if (!VerifyUtil.isListEmpty(categoriesId)) {
            List<QuestionnaireCategoryEntity> categoryResultSet = questionnaireCategoryRep.findByIdIn(categoriesId, questionnaireCategorySort);
            List<QuestionnaireEntity> questionnaireResultSet = questionnaireRep.findByCategoryIdIn(categoriesId, questionnaireSort);
            categories = questionnaireCategoryEntitiesToBeans(categoryResultSet);
            List<QuestionnaireBean> questionnaires = questionnaireEntitiesToBeans(questionnaireResultSet);

            List<QuestionnaireBean> beans;
            for (QuestionnaireCategoryBean category : categories) {
                beans = new ArrayList<>();
                for (QuestionnaireBean questionnaire : questionnaires) {
                    if (category.getId() == questionnaire.getCategoryId()) {
                        beans.add(questionnaire);
                    }
                }
                category.setQuestionnaires(beans);
            }
        }
        else {
            categories = new ArrayList<>();
        }
        logger.info("get questionnaire category with questionnaire count is {}", categories.size());
        return categories;
    }

    public List<QuestionnaireCategoryBean> getCategoryWithQuestionnaireByUserHospitalizedBean(
            List<UserHospitalizedRelationBean> userHospitalizedBeans) {
        logger.info("get questionnaire category by user hospitalized relation={}", userHospitalizedBeans);
        List<QuestionnaireBean> questionnaires = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(userHospitalizedBeans)) {
            for (UserHospitalizedRelationBean userHospitalized : userHospitalizedBeans) {
                if (YesNoEnum.YES.equals(userHospitalized.getHasLeave())) {
                    continue;
                }
                List<QuestionnaireBean> tmpBeans = getQuestionnaireByHospitalId(userHospitalized.getHospitalId());
                for (QuestionnaireBean tmp : tmpBeans) {
                    questionnaires.add(tmp);
                }
            }
        }
        else {
            questionnaires = getQuestionnaireByHospitalId(0);
            logger.info("get questionnaire without hospital");
        }

        List<Long> categoryIds = new ArrayList<>();
        for (QuestionnaireBean tmp : questionnaires) {
            if (!categoryIds.contains(tmp.getCategoryId())) {
                categoryIds.add(tmp.getCategoryId());
            }
        }

        List<QuestionnaireCategoryBean> categories = getCategoryByIds(categoryIds);
        List<QuestionnaireBean> beans;
        for (QuestionnaireCategoryBean category : categories) {
            beans = new ArrayList<>();
            for (QuestionnaireBean questionnaire : questionnaires) {
                if (category.getId() == questionnaire.getCategoryId()) {
                    beans.add(questionnaire);
                }
            }
            category.setQuestionnaires(beans);
        }

        logger.info("count is {}",categories.size());
        return categories;
    }

    private List<QuestionBean> questionEntitiesToBeans(Iterable<QuestionEntity> entities) {
        List<QuestionBean> beans = new ArrayList<>();
        if (null!=entities) {
            List<Long> imageIds = new ArrayList<>();
            for (QuestionEntity entity : entities) {
                QuestionBean bean = questionConverter.convert(entity);
                beans.add(bean);
                imageIds.add(bean.getImageId());
            }
            Map<Long, String> imageIdToUrl = userStorageService.getFileUrl(imageIds);
            for (QuestionBean bean : beans) {
                String imageUrl = imageIdToUrl.get(bean.getImageId());
                if (!VerifyUtil.isStringEmpty(imageUrl)) {
                    bean.setImageUrl(imageUrl);
                }
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

    private List<QuestionnaireCategoryBean> questionnaireCategoryEntitiesToBeans(Iterable<QuestionnaireCategoryEntity> entities) {
        List<QuestionnaireCategoryBean> beans = new ArrayList<>();
        if (null!=entities) {
            for (QuestionnaireCategoryEntity entity : entities) {
                QuestionnaireCategoryBean bean = questionnaireCategoryConverter.convert(entity);
                beans.add(bean);
            }
        }
        return beans;
    }

    //=================================================================
    //         update
    //=================================================================
    @Transactional
    public QuestionBean updateQuestionImage(long questionId, String imageName, InputStream image){
        logger.info("update question image for questionId={}, imageName={}, image={}", questionId, imageName, image);
        QuestionEntity entity = questionRep.findOne(questionId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (null==image) {
            logger.error("image is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        long imageId = 0;
        String imageUrl = "";
        if (VerifyUtil.isStringEmpty(imageName)) {
            imageName = "question_image_"+System.nanoTime();
        }
        imageId = userStorageService.addFile(entity.getImageId(), imageName, image);
        if (imageId>0) {
            entity.setImageId(imageId);
            imageUrl = userStorageService.getFileURL(imageId);
            entity = questionRep.save(entity);
        }
        QuestionBean bean = questionConverter.convert(entity);
        bean.setImageUrl(imageUrl);
        return bean;
    }

    @Transactional
    public QuestionBean updateQuestion(long questionId, long questionnaireId, String content,
                                       String options, String strType, int grade) {
        logger.info("update question={} with questionnaireId={} content={} options={} type={} grade={}",
                questionId, questionnaireId, content, options, strType, grade);
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
        if (grade>=0 && grade != entity.getGrade()) {
            entity.setGrade(grade);
            changed = true;
        }

        if (changed) {
            entity = questionRep.save(entity);
        }
        return questionConverter.convert(entity);
    }

    @Transactional
    public QuestionnaireBean updateQuestionnaire(long questionnaireId, String title, String description, String conclusion, int hospitalId, long categoryId) {
        logger.info("update questionnaire={} with title={} description={} conclusion={} hospitalId={}",
                questionnaireId, title, description, conclusion, hospitalId);
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
        if (!VerifyUtil.isStringEmpty(conclusion) && !conclusion.equals(entity.getConclusion())) {
            entity.setConclusion(conclusion);
            changed = true;
        }
        if (hospitalId>0 && hospitalId!=entity.getHospitalId()) {
            entity.setHospitalId(hospitalId);
            changed = true;
        }
        if (categoryId>0 && categoryId!=entity.getCategoryId()) {
            entity.setCategoryId(categoryId);
            changed = true;
        }

        if (changed) {
            entity = questionnaireRep.save(entity);
        }
        return questionnaireConverter.convert(entity);
    }

    @Transactional
    public QuestionnaireCategoryBean updateCategory(long questionnaireCategoryId, String name, String introduction) {
        logger.info("update questionnaireCategory={} with name={} introduction={}",
                questionnaireCategoryId, name, introduction);
        boolean changed = false;

        QuestionnaireCategoryEntity entity = questionnaireCategoryRep.findOne(questionnaireCategoryId);
        if (null==entity) {
            logger.info("questionnaire category not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!VerifyUtil.isStringEmpty(name) && !name.trim().equals(entity.getName())) {
            entity.setName(name.trim());
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(introduction) && !introduction.equals(entity.getIntroduction())) {
            entity.setIntroduction(introduction);
            changed = true;
        }

        if (changed) {
            entity = questionnaireCategoryRep.save(entity);
        }
        return questionnaireCategoryConverter.convert(entity);
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
            return questionIds.toString();
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
                for (QuestionEntity tmpE : questions) {
                    tmpE.setQuestionnaireId(0);
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
                for (QuestionEntity tmpE : questions) {
                    tmpE.setQuestionnaireId(0);
                }
                questionRep.save(questions);
            }
            // delete questionnaire
            List<QuestionnaireEntity> questionnaires = questionnaireRep.findByIdIn(questionnaireIds, questionnaireSort);
            questionnaireRep.delete(questionnaires);

            return questionnaireIds.toString();
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

    @Transactional
    public String deleteCategoryByIds(String strQuestionnaireCategoryIds) {
        logger.info("delete questionnaire category by questionnaireCategoryIds={}",
                strQuestionnaireCategoryIds);
        if (VerifyUtil.isIds(strQuestionnaireCategoryIds)) {
            List<Long> questionnaireCategoryIds = VerifyUtil.parseLongIds(strQuestionnaireCategoryIds);
            // set questionnaire id = 0
            List<QuestionnaireEntity> questionnaires = questionnaireRep.findByCategoryIdIn(questionnaireCategoryIds, questionnaireSort);
            if (null!=questionnaires) {
                for (QuestionnaireEntity tmpE : questionnaires) {
                    tmpE.setCategoryId(0);
                }
                questionnaireRep.save(questionnaires);
            }
            // delete questionnaire
            List<QuestionnaireCategoryEntity> questionnaireCategories =
                    questionnaireCategoryRep.findByIdIn(questionnaireCategoryIds, questionnaireCategorySort);
            questionnaireCategoryRep.delete(questionnaireCategories);

            return questionnaireCategoryIds.toString();
        }
        throw new BadRequestException(ErrorCode.DATA_ERROR);
    }


    //=================================================================
    //         add question, questionnaire and questionnaire category
    //=================================================================
    @Transactional
    public QuestionBean addQuestion(long questionnaireId, String content, String options, String strType, int grade) {
        logger.info("add question : questionnaireId={} content={} options={} strType={} grade={}",
                questionnaireId, content, options, strType, grade);

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
        grade = grade<=0 ? 0 : grade;
        entity.setGrade(grade);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = questionRep.save(entity);

        return questionConverter.convert(entity);
    }

    @Transactional
    public QuestionnaireBean addQuestionnaire(String title, String description, String conclusion, int hospitalId, long categoryId) {
        logger.info("add questionnaire : categoryId={} title={} description={} conclusion={} hospitalId={}",
                categoryId, title, description, conclusion, hospitalId);

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
        if (!VerifyUtil.isStringEmpty(conclusion)) {
            entity.setConclusion(conclusion);
        }
        hospitalId = hospitalId<0 ? 0 : hospitalId;
        categoryId = categoryId<0 ? 0 : categoryId;
        entity.setCategoryId(categoryId);
        entity.setHospitalId(hospitalId);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = questionnaireRep.save(entity);

        return questionnaireConverter.convert(entity);
    }

    @Transactional
    public QuestionnaireCategoryBean addCategory(String name, String introduction) {
        logger.info("add questionnaire category by name={} introduction={}", name, introduction);

        QuestionnaireCategoryEntity entity = new QuestionnaireCategoryEntity();
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("add questionnaire category : name is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        else if (questionnaireCategoryRep.countByName(name.trim())>0) {
            logger.error("add questionnaire : title is exist");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        else {
            entity.setName(name.trim());
        }
        if (!VerifyUtil.isStringEmpty(introduction)) {
            entity.setIntroduction(introduction);
        }
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = questionnaireCategoryRep.save(entity);

        return questionnaireCategoryConverter.convert(entity);
    }
}
