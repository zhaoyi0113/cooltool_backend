package com.cooltoo.go2nurse.chart.converter;

import com.cooltoo.go2nurse.beans.NurseVisitPatientBean;
import com.cooltoo.go2nurse.chart.generator.VisitPatientRecordPrinter;
import com.cooltoo.go2nurse.chart.util.TextUtil;
import com.cooltoo.util.NumberUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 19/01/2017.
 */
@Component
public class VisitPatientRecordConverter implements Converter<NurseVisitPatientBean, VisitPatientRecordPrinter.Record>{

    private TextUtil textUtil = TextUtil.newInstance();

    @Override
    public VisitPatientRecordPrinter.Record convert(NurseVisitPatientBean source) {
        if (null==source) {
            return null;
        }
        VisitPatientRecordPrinter.Record record = new VisitPatientRecordPrinter.Record();
        record.id(source.getId())
                .date(NumberUtil.timeToString(source.getTime(), NumberUtil.DATE_YYYY_MM_DD_HH_MM))
                .nurseSignUrl(source.getNurseSignUrl())
                .patientSignUrl(source.getPatientSignUrl())
                .content(source.getVisitRecord())
                .textUtil(textUtil)
        ;
        return record;
    }

    @Override
    public List<VisitPatientRecordPrinter.Record> convert(List<NurseVisitPatientBean> sources) {
        List<VisitPatientRecordPrinter.Record> returnVal = new ArrayList<>();
        if (null==sources) {
            return returnVal;
        }
        for (NurseVisitPatientBean tmp : sources) {
            VisitPatientRecordPrinter.Record record = convert(tmp);
            if (null!=record) {
                returnVal.add(record);
            }
        }
        return returnVal;
    }
}
