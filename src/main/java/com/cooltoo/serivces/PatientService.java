package com.cooltoo.serivces;

import com.cooltoo.beans.PatientBean;
import com.cooltoo.converter.PatientBeanConverter;
import com.cooltoo.converter.PatientEntityConverter;
import com.cooltoo.entities.PatientEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by yzzhao on 2/29/16.
 */
@Service("PatientService")
public class PatientService {

    private static final Logger logger = Logger.getLogger(PatientService.class.getName());

    @Autowired
    private PatientRepository repository;

    @Autowired
    private PatientEntityConverter entityConverter;

    @Autowired
    private PatientBeanConverter beanConverter;

    public void createPatient(PatientBean patient){
        PatientEntity entity = entityConverter.convert(patient);
        repository.save(entity);
    }

    public List<PatientBean> getPatients(){
        Iterable<PatientEntity> entities = repository.findAll();
        List<PatientBean> beans = new ArrayList<PatientBean>();
        for(PatientEntity entity: entities){
            PatientBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    public PatientBean getPatientById(long id){
        PatientEntity entity = repository.findOne(id);
        if(entity == null){
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        return beanConverter.convert(entity);
    }

}
