package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.entities.PatientEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yzzhao on 2/29/16.
 */
@Component
public class PatientBeanConverter implements Converter<PatientEntity, PatientBean> {
    @Override
    public PatientBean convert(PatientEntity source) {
        PatientBean bean = new PatientBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setName(source.getName());
        bean.setGender(source.getGender());
        bean.setIdentityCard(source.getIdentityCard());
        bean.setMobile(source.getMobile());
        bean.setIsDefault(source.getIsDefault());
        bean.setBirthday(source.getBirthday());
        if (null==bean.getBirthday()) {
            bean.setAge(0);
        }
        else {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);

            calendar.setTime(source.getBirthday());
            int birth = calendar.get(Calendar.YEAR);

            bean.setAge((year - birth < 0) ? 0 : (year - birth));
        }
        return bean;
    }
}
