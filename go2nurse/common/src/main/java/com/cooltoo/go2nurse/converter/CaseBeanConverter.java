package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.CaseBean;
import com.cooltoo.go2nurse.entities.CaseEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by zhaolisong on 2016/10/23.
 */
@Component
public class CaseBeanConverter implements Converter<CaseEntity, CaseBean> {
    @Override
    public CaseBean convert(CaseEntity source) {
        CaseBean bean = new CaseBean();
        bean.setId(source.getId());
        bean.setTime(source.getTime());
        bean.setStatus(source.getStatus());
        bean.setNurseId(source.getNurseId());
        bean.setCasebookId(source.getCasebookId());
        bean.setCaseRecord(source.getCaseRecord());
        return bean;
    }
}
