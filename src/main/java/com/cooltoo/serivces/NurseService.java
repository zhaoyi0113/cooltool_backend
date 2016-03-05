package com.cooltoo.serivces;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.converter.NurseBeanConverter;
import com.cooltoo.converter.NurseEntityConverter;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.util.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lg380357 on 2016/3/2.
 */
@Service("NurseService")
public class NurseService {

    @Autowired
    NurseRepository repository;
    @Autowired
    NurseBeanConverter beanConverter;
    @Autowired
    NurseEntityConverter entityConverter;

    @Transactional
    public long newNurse(String identificationId, String name, int age, int gender, String mobile) {
        NurseBean bean = new NurseBean();
        bean.setIdentificationId(identificationId);
        bean.setName(name);
        bean.setAge(age);
        bean.setGender(gender);
        bean.setMobile(mobile);
        return newNurse(bean);
    }

    @Transactional
    public long newNurse(NurseBean bean) {
        NurseEntity entity = entityConverter.convert(bean);
        if (null==entity.getName() || "".equals(entity.getName())){
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (!NumberUtil.isMobileValid(entity.getMobile())) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        entity = repository.save(entity);
        return entity.getId();
    }

    public NurseBean getNurse(long id) {
        NurseEntity entity = repository.findOne(id);
        if (null == entity) {
            return null;
        }
        return beanConverter.convert(entity);
    }

    public List<NurseBean> getAll() {
        Iterable<NurseEntity> all = repository.findAll();
        List<NurseBean> beanList = new ArrayList<NurseBean>();
        for (NurseEntity entity : all) {
            NurseBean bean = beanConverter.convert(entity);
            beanList.add(bean);
        }
        return beanList;
    }

    @Transactional
    public NurseBean deleteNurse(long id) {
        NurseEntity entity = repository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
        repository.delete(id);
        return beanConverter.convert(entity);
    }

    @Transactional
    public NurseBean updateNurse(long id, String identificationId, String name, int age, int gender, String mobile) {
        NurseEntity entity = repository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
        if (null==name || "".equals(name)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        boolean changed = false;
        // TODO need to add algorithm to verify the identificationId and encrypt it
        if (null!=identificationId && !"".equals(identificationId) && !identificationId.equals(entity.getIdentificationId())) {
            entity.setIdentificationId(identificationId);
            changed = true;
        }
        if (!entity.getName().equals(name)) {
            entity.setName(name);
            changed = true;
        }
        if (entity.getAge()!=age && age>0) {
            entity.setAge(age);
            changed = true;
        }
        if (entity.getGender()!=gender && gender>0) {
            entity.setGender(gender);
            changed = true;
        }
        // TODO need to add algorithm to verify the mobil
        if (NumberUtil.isMobileValid(mobile) && !mobile.equals(entity.getMobile())) {
            entity.setMobile(mobile);
            changed = true;
        }
        if (changed) {
            entity = repository.save(entity);
        }
        return beanConverter.convert(entity);
    }
}
