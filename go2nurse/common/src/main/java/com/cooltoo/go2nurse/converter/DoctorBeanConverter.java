package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.DoctorBean;
import com.cooltoo.go2nurse.entities.DoctorEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/7/25.
 */
@Component
public class DoctorBeanConverter implements Converter<DoctorEntity, DoctorBean> {
    @Override
    public DoctorBean convert(DoctorEntity source) {
        DoctorBean bean = new DoctorBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setGrade(source.getGrade());
        bean.setName(source.getName());
        bean.setPost(source.getPost());
        bean.setJobTitle(source.getJobTitle());
        bean.setBeGoodAt(source.getBeGoodAt());
        bean.setImageId(source.getImageId());
        bean.setHospitalId(source.getHospitalId());
        bean.setDepartmentId(source.getDepartmentId());
        bean.setIntroduction(null==source.getIntroduction() ? "" : source.getIntroduction());
        return bean;
    }
}
