package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.DoctorBean;
import com.cooltoo.go2nurse.service.DoctorService;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.nurse360.hospital.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/17.
 */
@RestController
@RequestMapping(path = "/nurse360_hospital")
public class HospitalDoctorAPI {

    @Autowired private DoctorService doctorService;


    //=============================================================
    //            Authentication of ADMINISTRATOR Role
    //=============================================================
    @RequestMapping(path = "/admin/doctor/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countDoctor(@RequestParam(name = "hospital_id",   defaultValue = "0") int hospitalId,
                            @RequestParam(name = "department_id", defaultValue = "0") int departmentId
    ) {
        List<CommonStatus> statuses = CommonStatus.getAll();
        statuses.remove(CommonStatus.DELETED);
        long count = doctorService.countDoctor(hospitalId, departmentId, statuses, false);
        return count;
    }

    @RequestMapping(path = "/admin/doctor", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<DoctorBean> getDoctor(@RequestParam(defaultValue = "0",  name = "hospital_id")   int hospitalId,
                                      @RequestParam(defaultValue = "0",  name = "department_id") int departmentId,
                                      @RequestParam(defaultValue = "0",  name = "index")  int index,
                                      @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        List<CommonStatus> statuses = CommonStatus.getAll();
        statuses.remove(CommonStatus.DELETED);
        List<DoctorBean> doctors = doctorService.getDoctor(hospitalId, departmentId, statuses, false, index, number);
        return doctors;
    }


    //=============================================================
    //            Authentication of NURSE/MANAGER Role
    //=============================================================
    @RequestMapping(path = "/doctor/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countDoctor() {
        return countDoctorByAdmin();
    }

    @RequestMapping(path = "/doctor", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<DoctorBean> getDoctor(@RequestParam(defaultValue = "0",  name = "index")  int index,
                                      @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        return getDoctorByAdmin(index, number);
    }




    //=============================================================
    //            Common Method
    //=============================================================
    private long countDoctorByAdmin() {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Integer[] tmp = SecurityUtil.newInstance().getHospitalDepartment("", "", userDetails);
        Integer hospitalId   = tmp[0];
        Integer departmentId = tmp[1];

        List<CommonStatus> statuses = CommonStatus.getAll();
        statuses.remove(CommonStatus.DELETED);
        long count = doctorService.countDoctor(hospitalId, departmentId, statuses, false);
        return count;
    }

    private List<DoctorBean> getDoctorByAdmin(int index, int number) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Integer[] tmp = SecurityUtil.newInstance().getHospitalDepartment("", "", userDetails);
        Integer hospitalId   = tmp[0];
        Integer departmentId = tmp[1];

        List<CommonStatus> statuses = CommonStatus.getAll();
        statuses.remove(CommonStatus.DELETED);
        List<DoctorBean> doctors = doctorService.getDoctor(hospitalId, departmentId, statuses, false, index, number);
        return doctors;
    }
}
