package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.NurseDoctorScoreBean;
import com.cooltoo.go2nurse.entities.NurseDoctorScoreEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 16/9/27.
 */
@Component
public class NurseDoctorScoreBeanConverter implements Converter<NurseDoctorScoreEntity, NurseDoctorScoreBean> {
    @Override
    public NurseDoctorScoreBean convert(NurseDoctorScoreEntity source) {
        NurseDoctorScoreBean bean = new NurseDoctorScoreBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setReceiverType(source.getReceiverType());
        bean.setReceiverId(source.getReceiverId());
        bean.setUserId(source.getUserId());
        bean.setReasonType(source.getReasonType());
        bean.setReasonId(source.getReasonId());
        bean.setScore(source.getScore());
        bean.setWeight(source.getWeight());
        return bean;
    }
}
