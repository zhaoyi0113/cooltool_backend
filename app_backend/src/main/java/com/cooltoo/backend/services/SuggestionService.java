package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.SuggestionBean;
import com.cooltoo.backend.converter.SuggestionBeanConverter;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.entities.SuggestionEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.repository.SuggestionRepository;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.NumberUtil;
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
    public List<SuggestionBean> getSuggestions(int pageIndex, int number) {
        logger.info("get suggestions on page {}, {} records/page", pageIndex, number);

        PageRequest page = new PageRequest(pageIndex, number, Sort.Direction.DESC, "timeCreated");
        List<Long>  ids  = new ArrayList<Long>();
        List<SuggestionBean> suggestionsB = new ArrayList<SuggestionBean>();
        List<NurseEntity>    users        = null;

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

        users = nurseRepository.findNurseByIdIn(ids);
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
