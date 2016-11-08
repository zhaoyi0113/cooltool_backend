package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.NursePatientFollowUpRecordBean;
import com.cooltoo.go2nurse.entities.NursePatientFollowUpRecordEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/11/8.
 */
@Component
public class NursePatientFollowUpRecordBeanConverter implements Converter<NursePatientFollowUpRecordEntity, NursePatientFollowUpRecordBean> {

    @Override
    public NursePatientFollowUpRecordBean convert(NursePatientFollowUpRecordEntity source) {
        NursePatientFollowUpRecordBean bean = new NursePatientFollowUpRecordBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setFollowUpId(source.getFollowUpId());
        bean.setFollowUpType(source.getFollowUpType());
        bean.setRelativeConsultationId(source.getRelativeConsultationId());
        bean.setRelativeQuestionnaireId(source.getRelativeQuestionnaireId());
        bean.setRelativeQuestionnaireAnswerGroupId(source.getRelativeQuestionnaireAnswerGroupId());
        bean.setPatientReplied(source.getPatientReplied());
        bean.setNurseRead(source.getNurseRead());
        return bean;
    }
}
