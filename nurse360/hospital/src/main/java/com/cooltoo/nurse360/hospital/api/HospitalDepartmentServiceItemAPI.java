package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.go2nurse.beans.NurseVisitPatientServiceItemBean;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.NurseVisitPatientServiceItemService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 2016/12/11.
 */
@RestController
@RequestMapping("/nurse360_hospital")
public class HospitalDepartmentServiceItemAPI {

    @Autowired private NurseVisitPatientServiceItemService nurseVisitPatientServiceItemService;

    @RequestMapping(path = "/visit/patient/service/item/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countHospitalDepartmentServiceItem(HttpServletRequest request,
                                                   @RequestParam(defaultValue = "", name = "vendor_type") String strVendorType,
                                                   @RequestParam(defaultValue = "", name = "vendor_id")   String strVendorId,
                                                   @RequestParam(defaultValue = "", name = "depart_id")   String strDepartId
    ) {
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        Long vendorId = VerifyUtil.isIds(strVendorId) ? VerifyUtil.parseLongIds(strVendorId).get(0) : 0L;
        Long departId = VerifyUtil.isIds(strDepartId) ? VerifyUtil.parseLongIds(strDepartId).get(0) : 0L;
        long count = 0;
        if (ServiceVendorType.HOSPITAL.equals(vendorType)) {
            count = nurseVisitPatientServiceItemService.countVisitPatientServiceItem(vendorId.intValue(), departId.intValue());
        }
        return count;
    }

    @RequestMapping(path = "/visit/patient/service/item", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<NurseVisitPatientServiceItemBean> getHospitalDepartmentServiceItem(HttpServletRequest request,
                                                                                   @RequestParam(defaultValue = "", name = "vendor_type") String strVendorType,
                                                                                   @RequestParam(defaultValue = "", name = "vendor_id")   String strVendorId,
                                                                                   @RequestParam(defaultValue = "", name = "depart_id")   String strDepartId
    ) {
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        Long vendorId = VerifyUtil.isIds(strVendorId) ? VerifyUtil.parseLongIds(strVendorId).get(0) : 0L;
        Long departId = VerifyUtil.isIds(strDepartId) ? VerifyUtil.parseLongIds(strDepartId).get(0) : 0L;
        List<NurseVisitPatientServiceItemBean> items = new ArrayList<>();
        if (ServiceVendorType.HOSPITAL.equals(vendorType)) {
            items = nurseVisitPatientServiceItemService.getVisitPatientServiceItem(vendorId.intValue(), departId.intValue());
        }
        return items;
    }
}
