package com.cooltoo.go2nurse.converter;

import com.cooltoo.go2nurse.beans.NurseWalletBean;
import com.cooltoo.go2nurse.entities.NurseWalletEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by zhaolisong on 12/12/2016.
 */
@Component
public class NurseWalletBeanConverter implements Converter<NurseWalletEntity, NurseWalletBean> {
    @Override
    public NurseWalletBean convert(NurseWalletEntity source) {
        NurseWalletBean bean = new NurseWalletBean();
        bean.setId(source.getId());
        bean.setStatus(source.getStatus());
        bean.setTime(source.getTime());
        bean.setNurseId(source.getNurseId());
        bean.setSummary(source.getSummary());
        bean.setAmountCent(source.getAmount());
        bean.setReason(source.getReason());
        bean.setReasonId(source.getReasonId());
        bean.setProcess(source.getProcess());

        String processRecord = source.getProcessRecord();
        bean.setProcessRecord(null==processRecord ? "" : processRecord);

        Date processTime = source.getProcessTime();
        bean.setProcessTime(null==processTime ? new Date(0) : processTime);
        return bean;
    }
}
