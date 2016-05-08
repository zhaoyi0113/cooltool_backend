package com.cooltoo.backend.services;

import com.cooltoo.backend.converter.social_ability.*;
import com.cooltoo.beans.BadgeBean;
import com.cooltoo.beans.SpecificSocialAbility;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.converter.BadgeBeanConverter;
import com.cooltoo.entities.BadgeEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.BadgeRepository;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yzzhao on 2/24/16.
 */
@Service("BadgeService")
public class BadgeService {

    private static final Logger logger = LoggerFactory.getLogger(BadgeService.class.getName());

    @Autowired private BadgeRepository repository;
    @Autowired private BadgeBeanConverter beanConverter;
    @Autowired
    @Qualifier("OfficialFileStorageService")
    private OfficialFileStorageService officialStorage;
    @Autowired private SpeakAbilityTypeConverter speakTypeConverter;
    @Autowired private SkillAbilityTypeConverter skillConverter;
    @Autowired private OccupationAbilityTypeConverter occupationConverter;
    @Autowired private ThumbsUpAbilityTypeConverter thumbsUpConverter;
    @Autowired private CommentAbilityTypeConverter commentConverter;

    //=====================================================
    //                   get
    //=====================================================
    public Map<String, String> getAllAbilityType() {
        return SocialAbilityType.getAllValues();
    }

    public List<SpecificSocialAbility> getItemsOfType(String strAbilityType) {
        logger.info("get abilityType={} 's specific abilities", strAbilityType);
        SocialAbilityType abilityType = SocialAbilityType.parseString(strAbilityType);
        if (null==abilityType) {
            logger.error("invalid type.");
            return new ArrayList<>();
        }
        List<SpecificSocialAbility> retVal;
        if (SocialAbilityType.COMMUNITY==abilityType) {
            retVal = speakTypeConverter.getItems();
        }
        else if (SocialAbilityType.SKILL==abilityType) {
            retVal = skillConverter.getItems();
        }
        else if (SocialAbilityType.OCCUPATION==abilityType) {
            //retVal = occupationConverter.getItems();
            retVal = new ArrayList<>();
            // department do not need to have grade
            logger.warn("social ability type {OCCUPATION} is not valid for Badge-Grade");
        }
        else if (SocialAbilityType.THUMBS_UP==abilityType) {
            retVal = thumbsUpConverter.getItems();
        }
        else if (SocialAbilityType.COMMENT==abilityType) {
            retVal = commentConverter.getItems();
        }
        else {
            retVal = new ArrayList<>();
        }
        logger.info("specific abilities are {}", retVal);
        return retVal;
    }

    public long countByAbilityType(String strAbilityType) {
        logger.info("get abilityType={} count", strAbilityType);
        long count = 0;
        if ("ALL".equalsIgnoreCase(strAbilityType)) {
            count = repository.count();
        }
        else {
            SocialAbilityType abilityType = SocialAbilityType.parseString(strAbilityType);
            count = repository.countByAbilityType(abilityType);
        }
        logger.info("get abilityType={} count={}", strAbilityType, count);
        return count;
    }

    public List<BadgeBean> getBadgeByAbilityType(String strAbilityType, int pageIndex, int number) {
        logger.info("get abilityType={} badge at page={} size/page={}", strAbilityType, pageIndex, number);
        PageRequest       page      = new PageRequest(pageIndex, number, Sort.Direction.ASC, "abilityType", "abilityId", "grade");
        Page<BadgeEntity> resultSet = null;
        if ("ALL".equalsIgnoreCase(strAbilityType)) {
            resultSet = repository.findAll(page);
        }
        else {
            SocialAbilityType abilityType = SocialAbilityType.parseString(strAbilityType);
            resultSet = repository.findByAbilityType(abilityType, page);
        }

        List<BadgeBean> badges = entities2Beans(resultSet);
        fillOtherProperties(badges);
        logger.info("get abilityType={} badge at page={} size/page={}, size={}", strAbilityType, pageIndex, number, badges.size());
        return badges;
    }

    public List<BadgeBean> getBadgeByAbilityIdAndType(int abilityId, String strAbilityType) {
        logger.info("get abilityType={} abilityId={} badge", strAbilityType, abilityId);
        SocialAbilityType abilityType = SocialAbilityType.parseString(strAbilityType);
        Sort              sort        = new Sort(new Sort.Order(Sort.Direction.ASC, "point"));
        List<BadgeEntity> resultSet   = repository.findByAbilityIdAndAbilityType(abilityId, abilityType, sort);
        List<BadgeBean>   badges      = entities2Beans(resultSet);
        fillOtherProperties(badges);
        logger.info("get abilityType={} abilityId={} badge, count={}", strAbilityType, abilityId, badges.size());
        return badges;
    }

    public BadgeBean getBadgeByPointAndAbilityIdAndType(int point, int abilityId, String strAbilityType) {
        logger.info("get abilityType={} abilityId={} point near {} 's badge", strAbilityType, abilityId, point);
        if (point<0) {
            logger.error("point is less than 0!");
            return null;
        }
        SocialAbilityType abilityType = SocialAbilityType.parseString(strAbilityType);
        if (null==abilityType) {
            logger.error("abilityType is invalid!");
            return null;
        }
        List<BadgeEntity> badges = repository.findOneByPoint((long)point, abilityId, abilityType);
        logger.info("the badge is {}", badges);
        if (VerifyUtil.isListEmpty(badges)) {
            return null;
        }
        BadgeBean retVal = beanConverter.convert(badges.get(0));
        retVal.setImageUrl(officialStorage.getFilePath(retVal.getImageId()));
        logger.info("the badge with image is {}", retVal);
        return retVal;
    }

    private List<BadgeBean> entities2Beans(Iterable<BadgeEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<BadgeBean> beans = new ArrayList<>();
        for (BadgeEntity tmp : entities) {
            BadgeBean bean =  beanConverter.convert(tmp);
            beans.add(bean);
        }

        return beans;
    }

    private void fillOtherProperties(List<BadgeBean> beans) {
        if (null==beans||beans.isEmpty()) {
            return;
        }

        List<Long> imageIds = new ArrayList<>();
        for (BadgeBean tmp : beans) {
            imageIds.add(tmp.getImageId());
        }

        Map<Long, String> imageId2Url = officialStorage.getFilePath(imageIds);
        for (BadgeBean tmp : beans) {
            String imageUrl = imageId2Url.get(tmp.getImageId());
            tmp.setImageUrl(imageUrl);
        }
    }

    //=========================================================
    //                delete
    //=========================================================

    @Transactional
    public String deleteBadgeByIds(String ids) {
        logger.info("delete badge by ids={}", ids);
        if (!VerifyUtil.isIds(ids)) {
            logger.error("the ids parameter is invalid");
        }

        List<Integer>     badgeIds  = VerifyUtil.parseIntIds(ids);
        List<BadgeEntity> resultSet = repository.findAll(badgeIds);
        if (null!=resultSet) {
            badgeIds.clear();
            List<Long> imageIds = new ArrayList<>();
            for (BadgeEntity tmp : resultSet) {
                imageIds.add(tmp.getImageId());
                badgeIds.add(tmp.getId());
            }
            officialStorage.deleteFiles(imageIds);
            repository.delete(resultSet);
        }
        logger.info("delete badge by ids={}, delete count={}", ids, badgeIds.size());
        return ids;
    }

    @Transactional
    public List<Integer> deleteByAbilityIdAndType(int abilityId, String strAbilityType) {
        logger.info("delete badge by abilityId={} abilityType={}", abilityId, strAbilityType);

        SocialAbilityType abilityType = SocialAbilityType.parseString(strAbilityType);
        List<BadgeEntity> resultSet = repository.findByAbilityIdAndAbilityType(abilityId, abilityType);
        if (null==resultSet || resultSet.isEmpty()) {
            logger.info("the result is empty");
        }

        List<Long>    imageIds = new ArrayList<>();
        List<Integer> badgeIds = new ArrayList<>();
        for (BadgeEntity tmp : resultSet) {
            imageIds.add(tmp.getImageId());
            badgeIds.add(tmp.getId());
        }

        officialStorage.deleteFiles(imageIds);
        repository.delete(resultSet);

        logger.info("delete badge by abilityId={} abilityType={}, ids={}", abilityId, strAbilityType, badgeIds);
        return badgeIds;
    }

    //=========================================================
    //                add
    //=========================================================

    @Transactional
    public BadgeBean addBadge(String name, int point, int grade, int abilityId, String strAbilityType, String imageName, InputStream image) {
        logger.info("add badge name={} point={} grade={} abilityId={} abilityType={} imageName={} image={}",
                name, point, grade, abilityId, strAbilityType, imageName, (null!=image));

        // check grade
        if (grade<=0) {
            logger.error("grade is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        // check social ability type
        SocialAbilityType abilityType = SocialAbilityType.parseString(strAbilityType);
        if (!isAbilityIdAndTypeValid(abilityId, abilityType)) {
            logger.error("ability id/type is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        // check record is already exist
        List<BadgeEntity> resultSet   = repository.findByAbilityIdAndAbilityTypeAndGrade(abilityId, abilityType, grade);
        if (null!=resultSet && !resultSet.isEmpty()) {
            logger.error("record is exist == {}", resultSet);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        long   imageId  = 0;
        String imageUrl = null;
        if (null!=image) {
            if (!VerifyUtil.isStringEmpty(imageName)) {
                imageName = "grade_"+System.nanoTime();
            }
            imageId  = officialStorage.addFile(imageId, imageName, image);
            imageUrl = officialStorage.getFilePath(imageId);
        }

        BadgeEntity entity = new BadgeEntity();
        entity.setName(name);
        entity.setGrade(grade);
        entity.setPoint(point);
        entity.setAbilityId(abilityId);
        entity.setAbilityType(abilityType);
        entity.setImageId(imageId);
        entity = repository.save(entity);

        BadgeBean badge = beanConverter.convert(entity);
        badge.setImageUrl(imageUrl);
        return badge;
    }


    //=========================================================
    //                update
    //=========================================================

    @Transactional
    public BadgeBean updateBadge(int id, String name, int point, int grade, int abilityId, String strAbilityType, String imageName, InputStream image) {
        logger.info("update badge id={} name={} point={} grade={} abilityId={} abilityType={} imageName={} image={}",
                id, name, point, grade, abilityId, strAbilityType, imageName, (null!=image));
        if (!repository.exists(id)) {
            throw new BadRequestException(ErrorCode.BADGE_NOT_EXIST);
        }

        boolean     changed = false;
        BadgeEntity entity  = repository.findOne(id);

        SocialAbilityType abilityType = SocialAbilityType.parseString(strAbilityType);

        // check record is already exist
        if (entity.getGrade()!=grade || entity.getAbilityId() != abilityId || !entity.getAbilityType().equals(abilityType)) {
            List<BadgeEntity> resultSet = repository.findByAbilityIdAndAbilityTypeAndGrade(abilityId, abilityType, grade);
            if (null != resultSet && !resultSet.isEmpty()) {
                logger.error("record is exist == {}", resultSet);
                throw new BadRequestException(ErrorCode.RECORD_ALREADY_EXIST);
            }
        }

        if (!VerifyUtil.isStringEmpty(name)) {
            entity.setName(name);
            changed = true;
        }

        if (point>0 && point!=entity.getPoint()) {
            entity.setPoint(point);
            changed = true;
        }

        // check grade
        if (grade>0 && grade!=entity.getGrade()) {
            entity.setGrade(grade);
            changed = true;
        }

        // check social ability type
        if (isAbilityIdAndTypeValid(abilityId, abilityType)) {
            entity.setAbilityType(abilityType);
            entity.setAbilityId(abilityId);
            changed = true;
        }

        String imageUrl = null;
        if (null!=image) {
            long imageId = 0;
            if (!VerifyUtil.isStringEmpty(imageName)) {
                imageName = "grade_"+System.nanoTime();
            }
            imageId  = officialStorage.addFile(imageId, imageName, image);
            imageUrl = officialStorage.getFilePath(imageId);
            entity.setImageId(imageId);
            changed = true;
        }
        if (changed) {
            entity = repository.save(entity);
        }

        BadgeBean bean = beanConverter.convert(entity);
        bean.setImageUrl(imageUrl);
        return bean;
    }


    private boolean isAbilityIdAndTypeValid(int abilityId, SocialAbilityType abilityType) {
        if (SocialAbilityType.SKILL.equals(abilityType)) {
            if (skillConverter.existItem(abilityId)) {
                return true;
            }
            logger.warn("skill id is not exist");
        }
        else if (SocialAbilityType.COMMUNITY.equals(abilityType)) {
            if (speakTypeConverter.existItem(abilityId)) {
                return true;
            }
            logger.warn("speak type id is not exist");
        }
        else if (SocialAbilityType.OCCUPATION.equals(abilityType)) {
            //if (occupationConverter.existItem(abilityId)) {
            //    return true;
            //}
            // department do not need to have grade
            logger.warn("social ability type {OCCUPATION} is not valid for Badge-Grade");
        }
        else if (SocialAbilityType.THUMBS_UP.equals(abilityType)) {
            if (thumbsUpConverter.existItem(abilityId))  {
                return true;
            }
            logger.warn("thumbs up type id is not exist");
        }
        else if (SocialAbilityType.COMMENT.equals(abilityType)) {
            if (commentConverter.existItem(abilityId))  {
                return true;
            }
            logger.warn("comment type id is not exist");
        }
        else {
            logger.warn("social ability type is invalid");
        }
        return false;
    }
}
