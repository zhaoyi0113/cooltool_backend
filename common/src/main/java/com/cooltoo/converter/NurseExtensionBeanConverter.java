package com.cooltoo.converter;

import com.cooltoo.beans.NurseExtensionBean;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.entities.NurseExtensionEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/8/11.
 */
@Component
public class NurseExtensionBeanConverter implements Converter<NurseExtensionEntity, NurseExtensionBean> {
    @Override
    public NurseExtensionBean convert(NurseExtensionEntity source) {
        NurseExtensionBean bean = new NurseExtensionBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setNurseId(source.getNurseId());
        bean.setGoodAt(source.getGoodAt());
        bean.setJobTitle(source.getJobTitle());
        bean.setAnswerNursingQuestion(source.getAnswerNursingQuestion());
        bean.setIsExpert(YesNoEnum.YES.equals(source.getIsExpert()) ? YesNoEnum.YES : YesNoEnum.NO);
        bean.setCanSeeAllOrder(YesNoEnum.YES.equals(source.getSeeAllOrder()) ? YesNoEnum.YES : YesNoEnum.NO);
        return bean;
    }
}
