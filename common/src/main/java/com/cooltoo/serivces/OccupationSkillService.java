package com.cooltoo.serivces;

import com.cooltoo.beans.OccupationSkillBean;
import com.cooltoo.converter.OccupationSkillBeanConverter;
import com.cooltoo.entities.OccupationSkillEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.OccupationSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private StorageService storageService;

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
    public void addNewOccupationSkill(String name, InputStream inputStream) {
        long fileId = storageService.saveFile(name, inputStream);
        OccupationSkillEntity entity = new OccupationSkillEntity();
        entity.setName(name);
        entity.setImageId(fileId);
        skillRepository.save(entity);
    }

    @Transactional
    public void deleteOccupationSkill(int id) {
        skillRepository.delete(id);
    }

    @Transactional
    public void editOccupationSkill(int id, String name, InputStream inputStream) {
        OccupationSkillEntity entity = editOccupationSkillWithoutImage(id, name);
        if (inputStream != null) {
            long fileId = storageService.saveFile(entity.getName(), inputStream);
            entity.setImageId(fileId);
        }
        skillRepository.save(entity);
    }

    @Transactional
    public OccupationSkillEntity editOccupationSkillWithoutImage(int id, String name) {
        OccupationSkillEntity entity = getOccupationSkillEntity(id);
        if (null != name && !name.isEmpty()) {
            entity.setName(name);
            skillRepository.save(entity);
        }
        return entity;
    }


}
