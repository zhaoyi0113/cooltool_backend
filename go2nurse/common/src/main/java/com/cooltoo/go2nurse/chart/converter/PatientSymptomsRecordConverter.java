package com.cooltoo.go2nurse.chart.converter;

import com.cooltoo.go2nurse.beans.ADLSubmitBean;
import com.cooltoo.go2nurse.chart.generator.PatientSymptomsPagePrinter;
import com.cooltoo.go2nurse.chart.util.TextUtil;
import com.cooltoo.util.NumberUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zhaolisong on 19/01/2017.
 */
@Component
public class PatientSymptomsRecordConverter implements Converter<ADLSubmitBean, PatientSymptomsPagePrinter.Record>{

    private TextUtil textUtil = TextUtil.newInstance();

    @Override
    public PatientSymptomsPagePrinter.Record convert(ADLSubmitBean source) {
        if (null==source) {
            return null;
        }
        PatientSymptomsPagePrinter.Record record = new PatientSymptomsPagePrinter.Record();
        record.setItem(source.getQuestionContent())
              .userSelected(source.getItem())
              .textUtil(textUtil)
        ;
        return record;
    }

    @Override
    public List<PatientSymptomsPagePrinter.Record> convert(List<ADLSubmitBean> sources) {
        List<PatientSymptomsPagePrinter.Record> returnVal = new ArrayList<>();
        if (null==sources || sources.isEmpty()) {
            return returnVal;
        }
        Collections.sort(sources, new Comparator<ADLSubmitBean>() {
            @Override
            public int compare(ADLSubmitBean o1, ADLSubmitBean o2) {
                if (o1==null && o2==null) {
                    return 0;
                }
                if (o1!=null && o2==null) {
                    return 1;
                }
                if (o1==null && o2!=null) {
                    return -1;
                }
                long tmp = (o1.getQuestionId() - o2.getQuestionId());
                return tmp==0 ? 0 : (tmp>0 ? 1 : -1);
            }
        });
        int i = 1;
        for (ADLSubmitBean tmp : sources) {
            PatientSymptomsPagePrinter.Record record = convert(tmp);
            record.id(i++);
            if (null!=record) {
                returnVal.add(record);
            }
        }

        ADLSubmitBean tmp = sources.get(0);

        PatientSymptomsPagePrinter.Record record = new PatientSymptomsPagePrinter.Record();
        record.setItem("总结").userSelected(tmp.getConclusionItem()).textUtil(textUtil);
        record.id(i);
        returnVal.add(record);

        return returnVal;
    }
}
