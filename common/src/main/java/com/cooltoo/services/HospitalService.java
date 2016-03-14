package com.cooltoo.services;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.converter.HospitalBeanConverter;
import com.cooltoo.converter.HospitalEntityConverter;
import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Service("HospitalService")
public class HospitalService {

    @Autowired
    private HospitalRepository repository;
    @Autowired
    private HospitalBeanConverter beanConverter;
    @Autowired
    private HospitalEntityConverter entityConverter;

    public List<HospitalBean> getAll() {
        Iterable<HospitalEntity> iterable = repository.findAll();
        List<HospitalBean> all = new ArrayList<HospitalBean>();
        for (HospitalEntity entity : iterable) {
            HospitalBean bean = beanConverter.convert(entity);
            all.add(bean);
        }
        return all;
    }

    public HospitalBean getOneById(Integer id) {
        HospitalEntity entity = repository.findOne(id);
        if (null == entity) {
            return null;
        }
        return beanConverter.convert(entity);
    }

    @Transactional
    public HospitalBean deleteById(Integer id) {
        HospitalEntity entity = repository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.HOSPITAL_NOT_EXIST);
        }
        repository.delete(id);
        return beanConverter.convert(entity);
    }

    @Transactional
    public HospitalBean update(HospitalBean bean) {
        if (!repository.exists(bean.getId())) {
            throw new BadRequestException(ErrorCode.HOSPITAL_NOT_EXIST);
        }
        String value = bean.getName();
        if (null==value || "".equals(value)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        HospitalEntity entity = repository.findOne(bean.getId());
        if (!value.equals(entity.getName())) {
            entity.setName(value);
        }
        value = bean.getProvince();
        if (null != value && !"".equals(value) && !value.equals(entity.getProvince())) {
            entity.setProvince(value);
        }
        value = bean.getCity();
        if (null != value && !"".equals(value) && !value.equals(entity.getCity())) {
            entity.setCity(value);
        }
        entity = repository.save(entity);
        return beanConverter.convert(entity);
    }

    @Transactional
    public HospitalBean update(int id, String name, String province, String city) {
        HospitalBean bean = new HospitalBean();
        bean.setId(id);
        bean.setName(name);
        bean.setProvince(province);
        bean.setCity(city);
        return update(bean);
    }

    @Transactional
    public Integer newOne(HospitalBean bean) {
        String value = bean.getName();
        if (null==value || "".equals(value)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        HospitalEntity entity = entityConverter.convert(bean);
        entity = repository.save(entity);
        return entity.getId();
    }

    @Transactional
    public Integer newOne(String name, String province, String city) {
        HospitalBean bean = new HospitalBean();
        bean.setName(name);
        bean.setProvince(province);
        bean.setCity(city);
        return newOne(bean);
    }
}
