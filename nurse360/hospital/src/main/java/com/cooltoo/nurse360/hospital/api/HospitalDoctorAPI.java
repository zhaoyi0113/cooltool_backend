package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.DoctorBean;
import com.cooltoo.go2nurse.service.DoctorService;
import com.cooltoo.nurse360.beans.HospitalAdminAuthentication;
import com.cooltoo.nurse360.beans.HospitalAdminBean;
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
        long count = doctorService.countDoctor(hospitalId, departmentId, statuses);
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
        List<DoctorBean> doctors = doctorService.getDoctor(hospitalId, departmentId, statuses, index, number);
        return doctors;
    }


    //=============================================================
    //            Authentication of MANAGER Role
    //=============================================================
    @RequestMapping(path = "/manager/doctor/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countDoctorByManager() {
        return countDoctorByAdmin();
    }

    @RequestMapping(path = "/manager/doctor", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<DoctorBean> getDoctorByManger(@RequestParam(defaultValue = "0",  name = "index")  int index,
                                              @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        return getDoctorByAdmin(index, number);
    }


    //=============================================================
    //            Authentication of NURSE Role
    //=============================================================
    @RequestMapping(path = "/nurse/doctor/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countDoctorByNurse() {
        return countDoctorByAdmin();
    }

    @RequestMapping(path = "/nurse/doctor", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<DoctorBean> getDoctorByNurse(@RequestParam(defaultValue = "0",  name = "index")  int index,
                                             @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        return getDoctorByAdmin(index, number);
    }




    //=============================================================
    //            Common Method
    //=============================================================
    private long countDoctorByAdmin() {
        HospitalAdminAuthentication authentication = (HospitalAdminAuthentication) SecurityContextHolder.getContext().getAuthentication();
        HospitalAdminBean adminBean = (HospitalAdminBean) authentication.getDetails();

        List<CommonStatus> statuses = CommonStatus.getAll();
        statuses.remove(CommonStatus.DELETED);
        long count = doctorService.countDoctor(adminBean.getHospitalId(), adminBean.getDepartmentId(), statuses);
        return count;
    }

    private List<DoctorBean> getDoctorByAdmin(int index, int number) {
        HospitalAdminAuthentication authentication = (HospitalAdminAuthentication) SecurityContextHolder.getContext().getAuthentication();
        HospitalAdminBean adminBean = (HospitalAdminBean) authentication.getDetails();

        List<CommonStatus> statuses = CommonStatus.getAll();
        statuses.remove(CommonStatus.DELETED);
        List<DoctorBean> doctors = doctorService.getDoctor(adminBean.getHospitalId(), adminBean.getDepartmentId(), statuses, index, number);
        return doctors;
    }
}
