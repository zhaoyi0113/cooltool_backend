package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.OccupationSkillBean;
import com.cooltoo.backend.converter.OccupationSkillBeanConverter;
import com.cooltoo.backend.entities.OccupationSkillEntity;
import com.cooltoo.constants.OccupationSkillType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.backend.repository.OccupationSkillRepository;
import com.cooltoo.services.StorageService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Service("OccupationSkillService")
public class OccupationSkillService {

    private static final Logger logger = LoggerFactory.getLogger(OccupationSkillService.class.getName());

    @Autowired
    private OccupationSkillRepository skillRepository;
    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;
    @Autowired
    private OccupationSkillBeanConverter beanConverter;

    //=========================================================
    //           get skills
    //=========================================================

    public Map<Integer, OccupationSkillBean> getAllSkillId2BeanMap() {
        Map<Integer, OccupationSkillBean> id2Skills = new HashMap<Integer, OccupationSkillBean>();
        List<OccupationSkillBean>         allSkills = getOccupationSkillList();
        if (null!=allSkills && !allSkills.isEmpty()) {
            for (OccupationSkillBean skill : allSkills) {
                id2Skills.put(skill.getId(), skill);
            }
        }
        return id2Skills;
    }

    public List<OccupationSkillBean> getOccupationSkillList() {
        List<OccupationSkillEntity> skillList = skillRepository.findAll();
        return parseEntity(skillList);
    }

    public OccupationSkillBean getOccupationSkill(int id) {
        OccupationSkillEntity entity = getOccupationSkillEntity(id);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        List<OccupationSkillEntity> skillList = new ArrayList<OccupationSkillEntity>();
        skillList.add(entity);
        List<OccupationSkillBean> skill = parseEntity(skillList);
        return skill.get(0);
    }

    private OccupationSkillEntity getOccupationSkillEntity(int id) {
        OccupationSkillEntity entity = skillRepository.findOne(id);
        if (entity == null) {
            throw new BadRequestException(ErrorCode.NO_SUCH_OCCUPATION);
        }
        return entity;
    }

    @Transactional
    private boolean isSkillNameExist(String name) {
        if (VerifyUtil.isStringEmpty(name)) {
            return false;
        }
        List<OccupationSkillEntity> skills = skillRepository.findByName(name);
        return !skills.isEmpty();
    }

    private List<OccupationSkillBean> parseEntity(List<OccupationSkillEntity> entities) {

        String                    imageUrl = null;
        OccupationSkillBean       bean     = null;
        List<Long>                imageIds = new ArrayList<Long>();
        List<OccupationSkillBean> beans    = new ArrayList<OccupationSkillBean>();

        for (OccupationSkillEntity entity : entities) {
            bean = beanConverter.convert(entity);
            if (bean.getImageId()>0) {
                imageIds.add(bean.getImageId());
            }
            if (bean.getDisableImageId()>0) {
                imageIds.add(bean.getDisableImageId());
            }
            beans.add(bean);
        }

        Map<Long, String> idToPath = storageService.getFilePath(imageIds);
        for (OccupationSkillBean tmp : beans) {
            if (tmp.getImageId()>0) {
                tmp.setImageUrl(idToPath.get(tmp.getImageId()));
            }
            if (tmp.getDisableImageId()>0) {
                tmp.setDisableImageUrl(idToPath.get(tmp.getDisableImageId()));
            }
        }

        return beans;
    }


    //=========================================================
    //           add skill type
    //=========================================================

    @Transactional
    public OccupationSkillBean addNewOccupationSkill(String name, int factor, InputStream image, InputStream disableImage) {
        if (!isSkillNameExist(name)) {
            if (VerifyUtil.isStringEmpty(name)) {
                throw new BadRequestException(ErrorCode.SKILL_NAME_IS_NULL);
            }
            if (factor<=0) {
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }

            OccupationSkillEntity entity = new OccupationSkillEntity();
            if (null!=image) {
                long fileId = storageService.saveFile(0, name, image);
                entity.setImageId(fileId);
            }
            if (null!=disableImage) {
                long disableFileId = storageService.saveFile(0, name + "_disable", disableImage);
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
        OccupationSkillBean bean = getOccupationSkill(id);

        // delete images and file_storage records;
        List<Long> imageIds = new ArrayList();
        imageIds.add(bean.getImageId());
        imageIds.add(bean.getDisableImageId());
        storageService.deleteFiles(imageIds);

        skillRepository.delete(id);
    }


    //=========================================================
    //           update skill type
    //=========================================================

    @Transactional
    public OccupationSkillBean editOccupationSkill(int id, String name, int factor, InputStream imageStream, InputStream disableImageStream) {
        boolean               changed        = false;
        OccupationSkillEntity entity         = editOccupationSkillWithoutImage(id, name, factor);
        String                imgPath        = null;
        String                disableImgPath = null;
        if (imageStream != null) {
            try {
                long fileId = storageService.saveFile(entity.getImageId(), entity.getName(), imageStream);
                entity.setImageId(fileId);
                imgPath     = storageService.getFilePath(fileId);
                changed     = true;
            }
            catch (BadRequestException ex) {
                // do nothing
            }
        }
        if (null!=disableImageStream) {
            try {
                long disableFileId = storageService.saveFile(entity.getDisableImageId(), entity.getName()+"_disable", disableImageStream);
                entity.setDisableImageId(disableFileId);
                disableImgPath     = storageService.getFilePath(disableFileId);
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
        OccupationSkillBean bean = beanConverter.convert(entity);
        bean.setImageUrl(imgPath);
        bean.setDisableImageUrl(disableImgPath);
        return bean;
    }

    @Transactional
    public OccupationSkillEntity editOccupationSkillWithoutImage(int id, String name, int factor) {
        boolean changed = false;

        // get the skill
        OccupationSkillEntity entity = getOccupationSkillEntity(id);
        if (null==entity) {
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
