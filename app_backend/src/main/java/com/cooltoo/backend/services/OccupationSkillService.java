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
import java.util.List;

/**
 * Created by yzzhao on 3/10/16.
 */
@Service("OccupationSkillService")
public class OccupationSkillService {

    @Autowired
    private OccupationSkillBeanConverter beanConverter;

    @Autowired
    private OccupationSkillRepository skillRepository;

    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;

    public List<String> getAllSkillTypes() {
        return OccupationSkillType.getAllValues();
    }

    public List<OccupationSkillBean> getOccupationSkillList() {
        Iterable<OccupationSkillEntity> skillList = skillRepository.findAll();
        List<OccupationSkillBean> beanList = new ArrayList<OccupationSkillBean>();
        for (OccupationSkillEntity entity : skillList) {
            beanList.add(beanConverter.convert(entity));
        }
        return beanList;
    }

    public OccupationSkillBean getOccupationSkill(int id) {
        OccupationSkillEntity entity = getOccupationSkillEntity(id);
        return beanConverter.convert(entity);
    }

    private OccupationSkillEntity getOccupationSkillEntity(int id) {
        OccupationSkillEntity entity = skillRepository.findOne(id);
        if (entity == null) {
            throw new BadRequestException(ErrorCode.NO_SUCH_OCCUPATION);
        }
        return entity;
    }

    @Transactional
    public void addNewOccupationSkill(String name, String type, InputStream inputStream) {
        if (!isSkillNameExist(name)) {
            if (VerifyUtil.isStringEmpty(name)) {
                throw new BadRequestException(ErrorCode.SKILL_NAME_IS_NULL);
            }
            OccupationSkillType skillType = OccupationSkillType.parseString(type);
            if (null==type) {
                throw new BadRequestException(ErrorCode.SKILL_TYPE_INVALID);
            }
            if (null==inputStream) {
                throw new BadRequestException(ErrorCode.FILE_UPLOADING_IS_EMPTY);
            }
            long fileId = storageService.saveFile(0, name, inputStream);
            OccupationSkillEntity entity = new OccupationSkillEntity();
            entity.setName(name);
            entity.setType(skillType);
            entity.setImageId(fileId);
            skillRepository.save(entity);
        }
        throw new BadRequestException(ErrorCode.SKILL_EXIST);
    }

    @Transactional
    public void deleteOccupationSkill(int id) {
        skillRepository.delete(id);
    }

    @Transactional
    public void editOccupationSkill(int id, String name, String type, InputStream inputStream) {
        OccupationSkillEntity entity = editOccupationSkillWithoutImage(id, name, type);
        if (inputStream != null) {
            try {
                long fileId = storageService.saveFile(entity.getImageId(), entity.getName(), inputStream);
                entity.setImageId(fileId);
            }
            catch (BadRequestException ex) {
                // do nothing
            }
        }
        skillRepository.save(entity);
    }

    @Transactional
    public OccupationSkillEntity editOccupationSkillWithoutImage(int id, String name, String type) {
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
                }
                else {
                    throw new BadRequestException(ErrorCode.SKILL_EXIST);
                }
            }
        }

        // edit skill type
        OccupationSkillType skillType = OccupationSkillType.parseString(type);
        if (null!=skillType) {
            entity.setType(skillType);
        }

        // save
        entity = skillRepository.save(entity);

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

}
