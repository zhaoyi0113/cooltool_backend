package com.cooltoo.backend.services;

import com.cooltoo.backend.repository.SkillRepository;
import com.cooltoo.backend.repository.SpeakTypeRepository;
import com.cooltoo.beans.BadgeBean;
import com.cooltoo.constants.BadgeGrade;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.converter.BadgeBeanConverter;
import com.cooltoo.entities.BadgeEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.BadgeRepository;
import com.cooltoo.services.StorageService;
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
    @Autowired private SkillRepository skillRepository;
    @Autowired private SpeakTypeRepository speakTypeRepository;
    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;

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
        PageRequest       page      = new PageRequest(pageIndex, number, Sort.Direction.ASC, "abilityId", "grade");
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

        Map<Long, String> imageId2Url = storageService.getFilePath(imageIds);
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

        String[]      strArray = ids.split(",");
        List<Integer> badgeIds = new ArrayList<>();
        for (String tmp : strArray) {
            Integer id = Integer.parseInt(tmp);
            badgeIds.add(id);
        }

        List<BadgeEntity> resultSet = repository.findAll(badgeIds);
        if (null!=resultSet) {
            badgeIds.clear();
            List<Long> imageIds = new ArrayList<>();
            for (BadgeEntity tmp : resultSet) {
                imageIds.add(tmp.getImageId());
                badgeIds.add(tmp.getId());
            }
            storageService.deleteFiles(imageIds);
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

        storageService.deleteFiles(imageIds);
        repository.delete(resultSet);

        logger.info("delete badge by abilityId={} abilityType={}, ids={}", abilityId, strAbilityType, badgeIds);
        return badgeIds;
    }

    //=========================================================
    //                add
    //=========================================================

    @Transactional
    public BadgeBean addBadge(String name, int point, String strGrade, int abilityId, String strAbilityType, String imageName, InputStream image) {
        logger.info("add badge name={} point={} grade={} abilityId={} abilityType={} imageName={} image={}",
                name, point, strGrade, abilityId, strAbilityType, imageName, (null!=image));

        // check grade
        BadgeGrade grade = BadgeGrade.parseString(strGrade);
        if (null==grade) {
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
            imageId  = storageService.saveFile(imageId, imageName, image);
            imageUrl = storageService.getFilePath(imageId);
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
    public BadgeBean updateBadge(int id, String name, int point, String strGrade, int abilityId, String strAbilityType, String imageName, InputStream image) {
        logger.info("update badge id={} name={} point={} grade={} abilityId={} abilityType={} imageName={} image={}",
                id, name, point, strGrade, abilityId, strAbilityType, imageName, (null!=image));
        if (!repository.exists(id)) {
            throw new BadRequestException(ErrorCode.BADGE_NOT_EXIST);
        }

        boolean     changed = false;
        BadgeEntity entity  = repository.findOne(id);

        BadgeGrade        grade       = BadgeGrade.parseString(strGrade);
        SocialAbilityType abilityType = SocialAbilityType.parseString(strAbilityType);

        // check record is already exist
        List<BadgeEntity> resultSet   = repository.findByAbilityIdAndAbilityTypeAndGrade(abilityId, abilityType, grade);
        if (null!=resultSet && !resultSet.isEmpty()) {
            logger.error("record is exist == {}", resultSet);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
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
        if (null!=grade) {
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
            imageId  = storageService.saveFile(imageId, imageName, image);
            imageUrl = storageService.getFilePath(imageId);
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
            if (skillRepository.exists(abilityId)) {
                return true;
            }
            logger.warn("skill id is not exist");
        }
        else if (SocialAbilityType.COMMUNITY.equals(abilityType)) {
            if (speakTypeRepository.exists(abilityId)) {
                return true;
            }
            logger.warn("speak type id is not exist");
        }
        else if (SocialAbilityType.OCCUPATION.equals(abilityType)) {
            // department do not need to have grade
            logger.warn("social ability type {OCCUPATION} is not valid for Badge-Grade");
        }
        else {
            logger.warn("social ability type is invalid");
        }
        return false;
    }
}
