package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.SuggestionBean;
import com.cooltoo.backend.converter.SuggestionBeanConverter;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.entities.SuggestionEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.repository.SuggestionRepository;
import com.cooltoo.constants.SuggestionStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhaolisong on 16/4/6.
 */
@Service("SuggestionService")
public class SuggestionService {

    private static final Logger logger = LoggerFactory.getLogger(SuggestionService.class.getName());

    @Autowired
    private SuggestionRepository repository;
    @Autowired
    private NurseRepository nurseRepository;
    @Autowired
    private SuggestionBeanConverter beanConverter;

    //=========================================================
    //           get suggestion
    //=========================================================

    public long getSuggestionCount(String status) {
        logger.info("get suggestions count by status {}", status);
        if ("ALL".equalsIgnoreCase(status)) {
            return repository.count();
        }
        SuggestionStatus suggStatus = SuggestionStatus.parseString(status);
        if (null==suggStatus) {
            logger.info("suggestions status is invalid");
            return 0;
        }
        return repository.countByStatus(suggStatus);
    }

    public List<SuggestionBean> getSuggestions(String status, int pageIndex, int number) {
        logger.info("get suggestions by status {} on page {}, {} records/page", status, pageIndex, number);
        SuggestionStatus suggStatus = SuggestionStatus.parseString(status);
        if (null==suggStatus) {
            logger.info("suggestions status is invalid");
            return new ArrayList<>();
        }

        PageRequest page = new PageRequest(pageIndex, number, Sort.Direction.DESC, "timeCreated");
        List<SuggestionBean> suggestionsB = new ArrayList<SuggestionBean>();

        Page<SuggestionEntity> suggestions = repository.findByStatus(suggStatus, page);
        for (SuggestionEntity entity : suggestions) {
            SuggestionBean bean = beanConverter.convert(entity);
            suggestionsB.add(bean);
        }

        suggestionsB = fillOtherField(suggestionsB);
        return suggestionsB;
    }

    public List<SuggestionBean> getSuggestions(int pageIndex, int number) {
        logger.info("get suggestions on page {}, {} records/page", pageIndex, number);

        PageRequest page = new PageRequest(pageIndex, number, Sort.Direction.DESC, "timeCreated");
        List<SuggestionBean> suggestionsB = new ArrayList<SuggestionBean>();

        Page<SuggestionEntity> suggestions = repository.findAll(page);
        for (SuggestionEntity entity : suggestions) {
            SuggestionBean bean = beanConverter.convert(entity);
            suggestionsB.add(bean);
        }

        suggestionsB = fillOtherField(suggestionsB);
        return suggestionsB;
    }

    private List<SuggestionBean> fillOtherField(List<SuggestionBean> suggestionsB) {
        if (null==suggestionsB || suggestionsB.isEmpty()) {
            return suggestionsB;
        }

        List<Long>        ids   = new ArrayList<Long>();
        List<NurseEntity> users = null;

        for (SuggestionBean bean : suggestionsB) {
            ids.add(bean.getUserId());
        }

        users = nurseRepository.findByIdIn(ids);
        if (null!=users) {
            for (SuggestionBean bean : suggestionsB) {
                for (NurseEntity user : users) {
                    if (bean.getUserId()==user.getId()) {
                        bean.setUserName(user.getName());
                        if (!VerifyUtil.isStringEmpty(user.getRealName())) {
                            bean.setUserName(user.getRealName());
                        }
                    }
                }
            }
        }
        return suggestionsB;
    }

    private boolean isUserExist(long userId) {
        return nurseRepository.exists(userId);
    }


    //=========================================================
    //           update suggestion
    //=========================================================

    public String updateStatus(String ids, String suggestionType) {
        logger.info("set the suggestion {} status to {}", ids, suggestionType);
        if (!VerifyUtil.isOccupationSkillIds(ids)) {
            logger.warn("the suggestion ids are not valid");
        }
        SuggestionStatus status = SuggestionStatus.parseString(suggestionType);
        if (null==status) {
            logger.warn("the suggestion type is not valid");
        }

        List<Long> suggestIds = new ArrayList<>();
        String[]   strArray  = ids.split(",");
        for (String temp : strArray) {
            long id = Long.parseLong(temp);
            suggestIds.add(id);
        }

        List<SuggestionEntity> entity2Modify = repository.findByIdIn(suggestIds);
        for (SuggestionEntity entity : entity2Modify) {
            entity.setStatus(status);
        }
        repository.save(entity2Modify);
        return ids;
    }

    //=========================================================
    //           add suggestion
    //=========================================================

    public SuggestionBean addSuggestion(long userId, String suggestion) {
        logger.info("time {} user {} add suggestion {}", new Date(), userId, suggestion);
        if (!isUserExist(userId)) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }

        if (VerifyUtil.isStringEmpty(suggestion)) {
            logger.error("suggestion is empty!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        SuggestionEntity entity = new SuggestionEntity();
        entity.setUserId(userId);
        entity.setSuggestion(suggestion);
        entity.setStatus(SuggestionStatus.UNREAD);
        entity.setTimeCreated(new Date());
        entity = repository.save(entity);
        return beanConverter.convert(entity);
    }

    //=========================================================
    //           delete suggestion
    //=========================================================

    public void deleteSuggestion(String ids) {
        logger.info("delete suggestion by id. ids = {}", ids);
        if (!VerifyUtil.isOccupationSkillIds(ids)) {
            logger.error("the ids to delete format is wrong(like '121,122,123' or '123')");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        String[]   arrIds = ids.split(",");
        List<Long> lIds   = new ArrayList<Long>();
        for (String id : arrIds) {
            lIds.add(Long.parseLong(id));
        }

        repository.deleteByIdIn(lIds);
        return;
    }
}
