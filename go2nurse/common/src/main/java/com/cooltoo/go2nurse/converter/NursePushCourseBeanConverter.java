package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.NursePushCourseBean;
import com.cooltoo.go2nurse.entities.NursePushCourseEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/11/4.
 */
@Component
public class NursePushCourseBeanConverter implements Converter<NursePushCourseEntity, NursePushCourseBean> {

    @Override
    public NursePushCourseBean convert(NursePushCourseEntity source) {
        NursePushCourseBean bean = new NursePushCourseBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setNurseId(source.getNurseId());
        bean.setUserId(source.getUserId());
        bean.setPatientId(source.getPatientId());
        bean.setCourseId(source.getCourseId());
        bean.setRead(source.getRead());
        return bean;
    }
}
