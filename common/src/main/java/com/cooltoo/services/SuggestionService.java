package com.cooltoo.services;

import com.cooltoo.beans.SuggestionBean;
import com.cooltoo.constants.PlatformType;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.constants.UserType;
import com.cooltoo.converter.SuggestionBeanConverter;
import com.cooltoo.entities.SuggestionEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.SuggestionRepository;
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

    @Autowired private SuggestionRepository repository;
    @Autowired private SuggestionBeanConverter beanConverter;

    //=========================================================
    //           get suggestion
    //=========================================================

    public long countSuggestions(String strUserType, String strPlatform, String version, String status) {
        logger.info("count suggestions by userType={} platform={} version={} status {}",
                strUserType, strPlatform, version, status);
        UserType userType = UserType.parseString(strUserType);
        PlatformType platform = PlatformType.parseString(strPlatform);
        ReadingStatus suggStatus = ReadingStatus.parseString(status);
        version = VerifyUtil.isStringEmpty(version) ? null : version.trim();
        return repository.countByConditions(userType, platform, version, suggStatus);
    }

    public List<SuggestionBean> getSuggestions(String strUserType, String strPlatform, String version, String status, int pageIndex, int number) {
        logger.info("get suggestions by userType={} platform={} version={} status={} at page={} number={}",
                strUserType, strPlatform, version, status, pageIndex, number);
        UserType userType = UserType.parseString(strUserType);
        PlatformType platform = PlatformType.parseString(strPlatform);
        ReadingStatus suggStatus = ReadingStatus.parseString(status);
        version = VerifyUtil.isStringEmpty(version) ? null : version.trim();
        PageRequest page = new PageRequest(pageIndex, number, Sort.Direction.DESC, "timeCreated");
        List<SuggestionBean> suggestionsB = new ArrayList<>();

        Page<SuggestionEntity> suggestions = repository.findByConditions(userType, platform, version, suggStatus, page);
        for (SuggestionEntity entity : suggestions) {
            SuggestionBean bean = beanConverter.convert(entity);
            suggestionsB.add(bean);
        }

        return suggestionsB;
    }

    //=========================================================
    //           update suggestion
    //=========================================================

    public String updateStatus(String ids, String suggestionStatus) {
        logger.info("set the suggestion {} status to {}", ids, suggestionStatus);
        if (!VerifyUtil.isIds(ids)) {
            logger.warn("the suggestion ids are not valid");
        }
        ReadingStatus status = ReadingStatus.parseString(suggestionStatus);
        if (null==status) {
            logger.warn("the suggestion type is not valid");
            return ids;
        }

        List<Long> suggestIds = VerifyUtil.parseLongIds(ids);
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

    public SuggestionBean addSuggestion(long userId, String strUserType, String strUserName, String strPlatform, String version, String suggestion) {
        logger.info("time {} user {} add suggestion {}", new Date(), userId, suggestion);
        UserType userType = UserType.parseString(strUserType);
        PlatformType platform = PlatformType.parseString(strPlatform);
        strUserName = VerifyUtil.isStringEmpty(strUserName) ? null : strUserName.trim();
        version = VerifyUtil.isStringEmpty(version) ? null : version.trim();
        if (null==userType) {
            logger.error("userType is empty!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (null==platform) {
            logger.error("platform is empty!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(suggestion)) {
            logger.error("suggestion is empty!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        SuggestionEntity entity = new SuggestionEntity();
        entity.setUserId(userId);
        entity.setUserType(userType);
        entity.setUserName(strUserName);
        entity.setPlatform(platform);
        entity.setVersion(version);
        entity.setSuggestion(suggestion);
        entity.setStatus(ReadingStatus.UNREAD);
        entity.setTimeCreated(new Date());
        entity = repository.save(entity);
        return beanConverter.convert(entity);
    }

    //=========================================================
    //           delete suggestion
    //=========================================================
    public void deleteSuggestion(String ids) {
        logger.info("delete suggestion by id. ids = {}", ids);
        if (!VerifyUtil.isIds(ids)) {
            logger.error("the ids to delete format is wrong(like '121,122,123' or '123')");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        List<Long> lIds   = VerifyUtil.parseLongIds(ids);
        repository.deleteByIdIn(lIds);
        return;
    }
}
