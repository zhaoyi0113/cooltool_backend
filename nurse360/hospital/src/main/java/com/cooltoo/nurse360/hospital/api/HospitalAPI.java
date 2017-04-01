package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by zhaolisong on 01/04/2017.
 */
@RestController
@RequestMapping("/nurse360_hospital")
public class HospitalAPI {

    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;

    //=============================================================
    //            Authentication of NURSE/MANAGER Role
    //=============================================================
    @RequestMapping(path = "/hospital/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countHospital(HttpServletRequest request) {
        long count = hospitalService.getHospitalSize();
        return count;
    }

    @RequestMapping(path = "/hospital", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<HospitalBean> getHospital(HttpServletRequest request,
                                          @RequestParam(defaultValue = "0",  name = "index")  int index,
                                          @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        List<HospitalBean> hospitals = hospitalService.getAllByPage(index, number);
        return hospitals;
    }

    @RequestMapping(path = "/hospital/department", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<HospitalDepartmentBean> getAllSecondLevelDepart(@RequestParam(defaultValue = "0", name = "hospital_id") int hospitalId) {
        List<HospitalDepartmentBean> secondLevels = departmentService.getAllSecondLevelDepartmentEnable(hospitalId, "");
        return secondLevels;
    }

    @RequestMapping(path = "/hospital", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public HospitalBean addHospital(HttpServletRequest request,
                                    @RequestParam(defaultValue = "",  name = "name")         String name,
                                    @RequestParam(defaultValue = "",  name = "alias_name")   String aliasName,
                                    @RequestParam(defaultValue = "0", name = "province")     int province,
                                    @RequestParam(defaultValue = "0", name = "city")         int city,
                                    @RequestParam(defaultValue = "0", name = "district")     int district,
                                    @RequestParam(defaultValue = "",  name = "address")      String address,
                                    @RequestParam(defaultValue = "",  name = "phone_number") String phoneNumber,
                                    @RequestParam(defaultValue = "",  name = "zip_code")     String zipCode
    ) {
        name = null==name ? null : name.trim();
        if (VerifyUtil.isStringEmpty(name) || hospitalService.getHospital(name).size()>0) {
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_NOT_EXPECTED);
        }
        int id = hospitalService.newOne(name, aliasName, province, city, district, address, 1, 1, phoneNumber, zipCode);
        HospitalBean hospital = hospitalService.getHospital(id);
        return hospital;
    }

    @RequestMapping(path = "/hospital/department", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public HospitalDepartmentBean addDepartment(HttpServletRequest request,
                                                @RequestParam(defaultValue = "0",name = "hospital_id")       int    hospitalId,
                                                @RequestParam(defaultValue = "", name = "name")              String name,
                                                @RequestParam(defaultValue = "", name = "description")       String description,
                                                @RequestParam(defaultValue = "", name = "address")           String address,
                                                @RequestParam(defaultValue = "", name = "outpatient_address")String outpatientAddress
    ) {
        if (!hospitalService.existHospital(hospitalId) || VerifyUtil.isStringEmpty(name)) {
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_NOT_EXPECTED);
        }

        HospitalDepartmentBean res = null;
        List<HospitalDepartmentBean> departs = departmentService.getByHospitalId(hospitalId, null);
        for (HospitalDepartmentBean tmp : departs) {
            if (tmp.getName().equals(name)) {
                res = tmp;
                break;
            }
        }

        if (null!=res) {
            return res;
        }

        // create parent depart
        int id = departmentService.createHospitalDepartment(hospitalId, name, description, 1, 0, null, null, "", null, null, null, null, address, outpatientAddress, null);
        // create depart
        id = departmentService.createHospitalDepartment(hospitalId, name, description, 1, id, null, null, "", null, null, null, null, address, outpatientAddress, null);

        res = departmentService.getById(id, null);
        return res;
    }
}
