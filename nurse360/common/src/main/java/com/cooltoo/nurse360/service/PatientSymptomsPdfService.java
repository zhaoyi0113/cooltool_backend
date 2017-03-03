package com.cooltoo.nurse360.service;

import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.chart.converter.PatientSymptomsRecordConverter;
import com.cooltoo.go2nurse.chart.generator.PatientSymptomsPagePrinter;
import com.cooltoo.go2nurse.chart.ui.layout.Page;
import com.cooltoo.go2nurse.chart.util.DpiUtil;
import com.cooltoo.go2nurse.chart.util.FontUtil;
import com.cooltoo.go2nurse.chart.util.PageSize;
import com.cooltoo.go2nurse.chart.util.TextUtil;
import com.cooltoo.go2nurse.service.ServiceOrderService;
import com.cooltoo.go2nurse.service.UserAddressService;
import com.cooltoo.go2nurse.service.UserService;
import com.cooltoo.nurse360.service.file.TemporaryFileStorageServiceForNurse360;
import com.cooltoo.nurse360.util.Nurse360Utility;
import com.cooltoo.util.JSONUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by zhaolisong on 03/03/2017.
 */
@Service("PatientSymptomsPdfService")
public class PatientSymptomsPdfService {
    public static final float   CONTENT_ROW_HEIGHT = 10f;
    public static final int     CONTENT_ROW_SIZE   = 36;
    public static final float[] PAGE_PADDING       = new float[]{11, 11, 11, 11};
    public static final String  BASE_FONT          = "simsun.ttf";

    private static final Logger logger = LoggerFactory.getLogger(PatientSymptomsPdfService.class);

    @Autowired private ServiceOrderService orderService;
    @Autowired private UserService userService;
    @Autowired private UserAddressService userAddressService;
    @Autowired private TemporaryFileStorageServiceForNurse360 temporaryFileStorageService;
    @Autowired private Nurse360Utility nurse360Utility;




    public List<String> createOrderADLEvaluate(Long orderId) {
        logger.info("create order ADL evaluate, orderId={}", orderId);

        List<ServiceOrderBean> orderBeans = orderService.getOrderByOrderId(orderId);
        ServiceOrderBean orderBean = orderBeans.get(0);
        ServiceItemBean serviceItemBean = orderBean.getServiceItem();
        if (null==serviceItemBean || YesNoEnum.NO.equals(serviceItemBean.getNeedSymptoms())) {
            return Arrays.asList(new String[]{"NO_URL"});
        }
        PatientSymptomsBean patientSymptoms = (PatientSymptomsBean) orderBean.getProperty(ServiceOrderBean.PATIENT_SYMPTOMS);
        if (null==patientSymptoms) {
            logger.error("patient symptoms is null");
            return Arrays.asList(new String[]{"SYMPTOMS_NOT_SUBMIT"});
        }
        if (VerifyUtil.isStringEmpty(patientSymptoms.getQuestionnaire())) {
            logger.error("questionnaire is null");
            return Arrays.asList(new String[]{"ADL_NOT_SUBMIT"});
        }

        FontUtil.loadBaseFont(nurse360Utility.getFontSimsun());

        long startRecordId = 0;
        int  startRecordLine = 0;
        int  startPageIndex = 1;


        // get information show in top region
        UserBean user = userService.getUser(orderBean.getUserId());
        UserAddressBean userAddress = userAddressService.getUserDefaultAddress(orderBean.getUserId());
        PatientBean patient = orderBean.getPatient();


        // convert records to show in content region
        String questionnaire = patientSymptoms.getQuestionnaire();
        List<ADLSubmitBean> adlSubmits = JSONUtil.newInstance().parseJsonList(questionnaire, ADLSubmitBean.class);
        PatientSymptomsRecordConverter recordConverter = new PatientSymptomsRecordConverter();
        List<PatientSymptomsPagePrinter.Record> records = recordConverter.convert(adlSubmits);

        // create page printer
        PatientSymptomsPagePrinter patientSymptomsPagePrinter = new PatientSymptomsPagePrinter(
                DpiUtil.DPI_300, PageSize.A4,
                PAGE_PADDING,
                BASE_FONT,
                CONTENT_ROW_HEIGHT,
                CONTENT_ROW_SIZE,
                serviceItemBean.getName(),
                patient.getName(),
                GenderType.genderInfo(patient.getGender()),
                patient.getAge() + "",
                user.getName(),
                userAddress.toAddress(),
                user.getMobile()
        );
        // create page top region
        Page page = patientSymptomsPagePrinter.pageTop();
        // fill page content region
        // add save to temporary path
        List<String> savedPageFilePaths = patientSymptomsPagePrinter.pageContent(
                orderBean.getUserId(), orderBean.getPatientId(), orderId,
                page, records,
                temporaryFileStorageService.getStoragePath(),
                startRecordId,
                startRecordLine,
                startPageIndex,
                false);

        return savedPageFilePaths;
    }

    private List<PatientSymptomsPagePrinter.Record> mock() {
        PatientSymptomsPagePrinter.Record re = null;
        List<PatientSymptomsPagePrinter.Record> records = new ArrayList<>();

        re = new PatientSymptomsPagePrinter.Record();
        re.textUtil(TextUtil.newInstance()).id(1).setItem("大便").userSelected("偶尔几次（每周<1次）");
        records.add(re);

        re = new PatientSymptomsPagePrinter.Record();
        re.textUtil(TextUtil.newInstance()).id(2).setItem("小便").userSelected("偶尔失禁（每24小时<1次，每周>1次）");
        records.add(re);

        re = new PatientSymptomsPagePrinter.Record();
        re.textUtil(TextUtil.newInstance()).id(3).setItem("修饰").userSelected("独立洗脸、梳头、刷牙、剃须");
        records.add(re);

        re = new PatientSymptomsPagePrinter.Record();
        re.textUtil(TextUtil.newInstance()).id(4).setItem("上厕所").userSelected("需要部分帮助");
        records.add(re);

        re = new PatientSymptomsPagePrinter.Record();
        re.textUtil(TextUtil.newInstance()).id(5).setItem("吃饭").userSelected("需部分帮助（夹饭、盛饭、切面包）");
        records.add(re);

        re = new PatientSymptomsPagePrinter.Record();
        re.textUtil(TextUtil.newInstance()).id(6).setItem("转移（床<---->椅）").userSelected("需要大量帮助（2人），能坐");
        records.add(re);

        re = new PatientSymptomsPagePrinter.Record();
        re.textUtil(TextUtil.newInstance()).id(7).setItem("活动（主要指步行，即在病房及其周围，不包括走远路）").userSelected("需1人帮助步行（体力或语言指导）需1人帮助步行（体力或语言指导）需1人帮助步行（体力或语言指导）");
        records.add(re);

        re = new PatientSymptomsPagePrinter.Record();
        re.textUtil(TextUtil.newInstance()).id(8).setItem("穿衣").userSelected("需要部分帮助");
        records.add(re);

        re = new PatientSymptomsPagePrinter.Record();
        re.textUtil(TextUtil.newInstance()).id(9).setItem("上楼梯（上下一段楼梯，用手杖也算独立）").userSelected("需要帮助（体力或语言指导）");
        records.add(re);

        re = new PatientSymptomsPagePrinter.Record();
        re.textUtil(TextUtil.newInstance()).id(10).setItem("洗澡").userSelected("依赖别人");
        records.add(re);

        return records;
    }
}
