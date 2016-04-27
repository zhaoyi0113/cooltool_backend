package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.SkillBean;
import com.cooltoo.backend.converter.SkillBeanConverter;
import com.cooltoo.backend.entities.SkillEntity;
import com.cooltoo.constants.OccupationSkillStatus;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.backend.repository.SkillRepository;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yzzhao on 3/10/16.
 */
@Service("SkillService")
public class SkillService {

    private static final Logger logger = LoggerFactory.getLogger(SkillService.class.getName());

    @Autowired private SkillRepository skillRepository;
    @Autowired private SkillBeanConverter beanConverter;
    @Autowired private BadgeService badgeService;
    @Autowired
    @Qualifier("OfficialFileStorageService")
    private OfficialFileStorageService officialStorage;

    //=========================================================
    //           get skills
    //=========================================================

    public long getAllSkillCount(String skillStatus) {
        logger.info("get all skill count by status={}", skillStatus);
        long count = 0;
        OccupationSkillStatus status = OccupationSkillStatus.parseStatus(skillStatus);
        if ("ALL".equalsIgnoreCase(skillStatus)) {
            count = skillRepository.count();
        }
        else if (null!=status) {
            count = skillRepository.countByStatus(status);
        }
        logger.info("get all skill count {} by status={}", count, skillStatus);
        return  count;
    }

    public List<SkillBean> getSkillByStatus(String skillStatus, int pageIndex, int number) {
        logger.info("get occupation skill by skill status {} at page {}, {}number/Page", skillStatus, pageIndex, number);

        PageRequest page = new PageRequest(pageIndex, number, Sort.Direction.DESC, "name");

        OccupationSkillStatus status = OccupationSkillStatus.parseStatus(skillStatus);
        Page<SkillEntity> skills = null;
        if ("ALL".equalsIgnoreCase(skillStatus)) {
            skills = skillRepository.findAll(page);
        }
        else if (null!=status) {
            skills = skillRepository.findByStatus(status, page);
        }
        else {
            return new ArrayList<>();
        }

        List<SkillBean> beans = entitiesToBeans(skills);
        fillOtherProperties(beans);

        return beans;
    }

    public List<SkillBean> getSkillByStatus(String skillStatus) {
        logger.info("get occupation skill by skill status {}", skillStatus);
        OccupationSkillStatus status = OccupationSkillStatus.parseStatus(skillStatus);
        if (null==status) {
            logger.error("skill status is invalid");
            return new ArrayList<>();
        }

        List<SkillEntity> allSkill = skillRepository.findByStatus(status);
        List<SkillBean>   beans    = entitiesToBeans(allSkill);
        fillOtherProperties(beans);
        return beans;
    }

    public SkillBean getOneSkillById(int skillId) {
        SkillEntity entity = skillRepository.findOne(skillId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        List<SkillEntity> skillList = new ArrayList<>();
        skillList.add(entity);
        List<SkillBean> skills = entitiesToBeans(skillList);
        fillOtherProperties(skills);
        return skills.get(0);
    }

    public List<SkillBean> getAllSkill() {
        List<SkillEntity> allSkill = skillRepository.findAll();
        List<SkillBean>   beans    = entitiesToBeans(allSkill);
        fillOtherProperties(beans);
        return beans;
    }

    public Map<Integer, SkillBean> getAllSkillId2BeanMap() {
        Map<Integer, SkillBean> id2Skills = new HashMap<Integer, SkillBean>();
        List<SkillBean>         allSkills = getAllSkill();
        if (null!=allSkills && !allSkills.isEmpty()) {
            for (SkillBean skill : allSkills) {
                id2Skills.put(skill.getId(), skill);
            }
        }
        return id2Skills;
    }

    private boolean isSkillNameExist(String name) {
        if (VerifyUtil.isStringEmpty(name)) {
            return false;
        }
        List<SkillEntity> skills = skillRepository.findByName(name);
        return !skills.isEmpty();
    }

    private List<SkillBean> entitiesToBeans(Iterable<SkillEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<SkillBean> beans = new ArrayList<>();
        for (SkillEntity tmp : entities) {
            SkillBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }

        return beans;
    }

    private void fillOtherProperties(List<SkillBean> skills) {
        if (null==skills || skills.isEmpty()) {
            return;
        }

        List<Long> imageIds = new ArrayList<Long>();

        for (SkillBean bean : skills) {
            if (bean.getImageId()>0) {
                imageIds.add(bean.getImageId());
            }
            if (bean.getDisableImageId()>0) {
                imageIds.add(bean.getDisableImageId());
            }
        }

        Map<Long, String> idToPath = officialStorage.getFilePath(imageIds);
        for (SkillBean tmp : skills) {
            if (tmp.getImageId()>0) {
                tmp.setImageUrl(idToPath.get(tmp.getImageId()));
            }
            if (tmp.getDisableImageId()>0) {
                tmp.setDisableImageUrl(idToPath.get(tmp.getDisableImageId()));
            }
        }
    }


    //=========================================================
    //           add skill type
    //=========================================================

    @Transactional
    public SkillBean addNewOccupationSkill(String name, int factor, InputStream image, InputStream disableImage) {
        if (!isSkillNameExist(name)) {
            if (VerifyUtil.isStringEmpty(name)) {
                throw new BadRequestException(ErrorCode.SKILL_NAME_IS_NULL);
            }
            if (factor<=0) {
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }

            SkillEntity entity = new SkillEntity();
            if (null!=image) {
                long fileId = officialStorage.addFile(0, name, image);
                entity.setImageId(fileId);
            }
            if (null!=disableImage) {
                long disableFileId = officialStorage.addFile(0, name + "_disable", disableImage);
                entity.setDisableImageId(disableFileId);
            }
            entity.setName(name);
            entity.setFactor(factor);
            entity = skillRepository.save(entity);

            return beanConverter.convert(entity);
        }
        throw new BadRequestException(ErrorCode.SKILL_EXIST);
    }


    //=========================================================
    //           delete skill type
    //=========================================================

    @Transactional
    public void deleteOccupationSkill(int id) {
        SkillBean bean = getOneSkillById(id);

        // delete images and file_storage records;
        List<Long> imageIds = new ArrayList();
        imageIds.add(bean.getImageId());
        imageIds.add(bean.getDisableImageId());
        officialStorage.deleteFiles(imageIds);
        skillRepository.delete(id);
        badgeService.deleteByAbilityIdAndType(id, SocialAbilityType.SKILL.name());
    }


    //=========================================================
    //           update skill type
    //=========================================================

    @Transactional
    public SkillBean editOccupationSkill(int id, String name, int factor, String status, InputStream imageStream, InputStream disableImageStream) {
        logger.info("edit occupation skill with image skillId={}, enableImage={} disableImage={}", id, (null!=imageStream), (null!=disableImageStream));
        boolean               changed        = false;
        SkillEntity           entity         = editOccupationSkillWithoutImage(id, name, factor, status);
        String                imgPath        = null;
        String                disableImgPath = null;
        if (imageStream != null) {
            try {
                long fileId = officialStorage.addFile(entity.getImageId(), entity.getName(), imageStream);
                entity.setImageId(fileId);
                imgPath     = officialStorage.getFilePath(fileId);
                changed     = true;
            }
            catch (BadRequestException ex) {
                // do nothing
            }
        }
        if (null!=disableImageStream) {
            try {
                long disableFileId = officialStorage.addFile(entity.getDisableImageId(), entity.getName()+"_disable", disableImageStream);
                entity.setDisableImageId(disableFileId);
                disableImgPath     = officialStorage.getFilePath(disableFileId);
                changed            = true;
            }
            catch (BadRequestException ex) {
                // do nothing
            }
        }
        if (changed) {
            entity = skillRepository.save(entity);
            logger.info("update occupation skill == " + entity);
        }
        else {
            logger.info("no occupation skill(enable/disable image) upated  == " + entity);
        }
        SkillBean bean = beanConverter.convert(entity);
        bean.setImageUrl(imgPath);
        bean.setDisableImageUrl(disableImgPath);
        return bean;
    }

    @Transactional
    public SkillEntity editOccupationSkillWithoutImage(int id, String name, int factor, String status) {
        logger.info("edit occupation skill with skillId={} name={} factor={} status={}", id, name, factor, status);
        boolean changed = false;

        // get the skill
        SkillEntity entity = skillRepository.findOne(id);
        if (entity == null) {
            throw new BadRequestException(ErrorCode.SKILL_NOT_EXIST);
        }

        // edit skill name
        if (!VerifyUtil.isStringEmpty(name)) {
            if (!name.equals(entity.getName())) {
                if (!isSkillNameExist(name)) {
                    entity.setName(name);
                    changed = true;
                }
                else {
                    throw new BadRequestException(ErrorCode.SKILL_EXIST);
                }
            }
        }

        // edit factor
        if (factor>0 && factor!=entity.getFactor()) {
            entity.setFactor(factor);
            changed = true;
        }

        OccupationSkillStatus statusE = OccupationSkillStatus.parseStatus(status);
        if (null!=statusE) {
            entity.setStatus(statusE);
            changed = true;
        }

        // save
        if (changed) {
            entity = skillRepository.save(entity);
            logger.info("update occupation skill == " + entity);
        }
        else {
            logger.info("no occupation skill(basic information) upated  == " + entity);
        }
        return entity;
    }
}
