package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.go2nurse.beans.NurseVisitPatientBean;
import com.cooltoo.go2nurse.beans.NurseVisitPatientServiceItemBean;
import com.cooltoo.go2nurse.service.NurseVisitPatientService;
import com.cooltoo.go2nurse.service.NurseVisitPatientServiceItemService;
import com.cooltoo.util.JSONUtil;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by zhaolisong on 2016/12/12.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_visit_patient_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_visit_patient_photo_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_visit_patient_service_item_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/hospital_department_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/file_storage_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/user_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/patient_data.xml"),
})
public class NurseVisitPatientServiceTest extends AbstractCooltooTest {

    @Autowired private NurseVisitPatientServiceItemService serviceItemService;
    @Autowired private NurseVisitPatientService nurseVisitPatientService;

    @Test
    public void testUpdateVisitRecord() {
        long visitRecordId = 1;
        String visitRecord = "modify";
        Date visitDate = new Date();
        String address = "Address MODIFY";
        String patientRecordNo = "PRN_MODIFY";
        String note = "Note MODIFY";
        List<NurseVisitPatientServiceItemBean> item = serviceItemService.getVisitPatientServiceItem("1,2");
        String serviceItem = JSONUtil.newInstance().toJsonString(item);
        nurseVisitPatientService.updateVisitRecord(null, visitRecordId, visitRecord, serviceItem, visitDate, address, patientRecordNo, note);
        NurseVisitPatientBean visitBean = nurseVisitPatientService.getVisitRecord(visitRecordId);
        Assert.assertEquals(visitRecord, visitBean.getVisitRecord());
        Assert.assertEquals(visitDate, visitBean.getTime());
        Assert.assertEquals(address, visitBean.getAddress());
        Assert.assertEquals(patientRecordNo, visitBean.getPatientRecordNo());
        Assert.assertEquals(note, visitBean.getNote());
        Assert.assertEquals(serviceItem, visitBean.getServiceItem());
    }
}
