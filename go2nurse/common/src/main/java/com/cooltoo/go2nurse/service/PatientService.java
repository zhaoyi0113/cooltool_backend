package com.cooltoo.go2nurse.service;

import com.cooltoo.go2nurse.repository.PatientRepository;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.converter.PatientBeanConverter;
import com.cooltoo.go2nurse.converter.PatientEntityConverter;
import com.cooltoo.go2nurse.entities.PatientEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yzzhao on 2/29/16.
 */
@Service("PatientService")
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class.getName());

    @Autowired
    private PatientRepository repository;

    @Autowired
    private PatientEntityConverter entityConverter;

    @Autowired
    private PatientBeanConverter beanConverter;

    public List<PatientBean> getAll(){
        Iterable<PatientEntity> entities = repository.findAll();
        List<PatientBean> beans = new ArrayList<PatientBean>();
        for(PatientEntity entity: entities){
            PatientBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    public PatientBean getOneById(long id){
        PatientEntity entity = repository.findOne(id);
        if(entity == null){
            return null;
        }
        return beanConverter.convert(entity);
    }


    @Transactional
    public long create(String name, String nickname, int certificateId, int officeId, String mobile, int age, Date birthday, String usercol) {
        PatientBean bean = new PatientBean();
        bean.setName(name);
        bean.setNickname(nickname);
        bean.setCertificateId(certificateId);
        bean.setOfficeId(officeId);
        bean.setMobile(mobile);
        bean.setAge(age);
        bean.setUsercol(usercol);
        bean.setBirthday(birthday);
        return create(bean);
    }

    @Transactional
    public long create(PatientBean patient){
        PatientEntity entity = entityConverter.convert(patient);
        if (null==entity.getName() || "".equals(entity.getName())){
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        entity = repository.save(entity);
        return entity.getId();
    }

    @Transactional
    public PatientBean delete(long id) {
        PatientEntity entity = repository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        repository.delete(id);
        return beanConverter.convert(entity);
    }

    @Transactional
    public PatientBean update(long id, String name, String nickname, int certificateId, int officeId, String mobile, int age, Date birthday, String usercol) {
        PatientEntity entity = repository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }

        boolean changed = false;
        if (null!=name && !"".equals(name) && !entity.getName().equals(name)) {
            entity.setName(name);
            changed = true;
        }
        if (null!=nickname && !"".equals(nickname) && !nickname.equals(entity.getNickname())) {
            entity.setNickname(nickname);
            changed = true;
        }
        if (entity.getOfficeId()!=officeId && officeId>0) {
            entity.setOfficeId(officeId);
            changed = true;
        }
        if (entity.getCertificateId()!=certificateId && certificateId>0) {
            entity.setCertificateId(certificateId);
            changed = true;
        }
        if (entity.getAge()!=age && age>0) {
            entity.setAge(age);
            changed = true;
        }
        if (null!=birthday && !birthday.equals(entity.getBirthday())) {
            entity.setBirthday(birthday);
            changed = true;
        }
        if (null!=usercol && !"".equals(usercol) && !usercol.equals(entity.getUsercol())) {
            entity.setUsercol(usercol);
            changed = true;
        }
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
