package com.cooltoo.go2nurse.converter;

import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.beans.ReExaminationStrategyBean;
import com.cooltoo.go2nurse.entities.ReExaminationStrategyEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/8/26.
 */
@Component
public class ReExaminationStrategyBeanConverter implements Converter<ReExaminationStrategyEntity, ReExaminationStrategyBean> {
    @Override
    public ReExaminationStrategyBean convert(ReExaminationStrategyEntity source) {
        ReExaminationStrategyBean bean = new ReExaminationStrategyBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setDepartmentId(source.getDepartmentId());
        bean.setReExaminationDay(source.getReExaminationDay());
        bean.setRecycled(YesNoEnum.YES.equals(source.getRecycled()) ? true : false);
        return bean;
    }
}
